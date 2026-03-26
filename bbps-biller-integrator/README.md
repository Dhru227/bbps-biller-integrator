# BBPS Biller Integrator

Spring Boot service that receives BBPS bill fetch / bill payment requests, generates the corresponding BBPS XML responses, and posts them back to BBPS (with OU XML signing).

## Prerequisites

1. Java 17+
2. PostgreSQL
3. BBPS signing keystore
   - Set environment variable `BBPS_HOME` to the BBPS installation directory.
   - The code expects: `$BBPS_HOME/config/certificate/OU/ousigner.p12`
   - Keystore password is read from system property `keystorepwd` (default: `npciupi`)

## Configuration

Edit `src/main/resources/application.properties` as needed:

1. Database
   - `spring.datasource.url`
   - `spring.datasource.username`
   - `spring.datasource.password`

2. CU / BBPS URLs (used to construct callback endpoints)
   - `cu.domain`
   - `cu.billfetchresponse.url`
   - `cu.billpaymentresponse.url`

3. Callback base URL
   - `bbps.ip` (defaults to `${cu.domain}`)
   - `bbps.billfetchresponse.url` (defaults to `${cu.billfetchresponse.url}`)
   - `bbps.billpaymentresponse.url` (defaults to `${cu.billpaymentresponse.url}`)

## How to run

From the project root (`bbps-biller-integrator`):

```bash
mvn spring-boot:run
```

Server starts on:

```text
server.port=8111
```

## API Endpoints

### 1) Bill Fetch Request

`POST /BillFetchRequest/1.0/urn:referenceId:{referenceId}`

- `Content-Type: application/xml`
- Returns: `Ack` (quick acknowledgement)

Example:

```bash
curl -X POST "http://localhost:8111/BillFetchRequest/1.0/urn:referenceId:demoRef" \
  -H "Content-Type: application/xml" \
  --data-binary "@billFetchRequest.xml"
```

Behavior:
1. Controller immediately returns `Ack`.
2. Async service finds the latest unpaid bill from DB using `BillDetails -> CustomerParams -> Tags`.
3. It generates `BillFetchResponse` XML.
4. It POSTs `BillFetchResponse` to BBPS using `OuRestTemplate` (OU XML signature).
   - Callback URL format:
     - `bbps.ip + bbps.billfetchresponse.url + <refId>`

### 2) Bill Payment Request

`POST /BillPaymentRequest/1.0/urn:referenceId:{referenceId}`

- `Content-Type: application/xml`
- Returns: `Ack` (quick acknowledgement)

Example:

```bash
curl -X POST "http://localhost:8111/BillPaymentRequest/1.0/urn:referenceId:demoRef" \
  -H "Content-Type: application/xml" \
  --data-binary "@billPaymentRequest.xml"
```

Behavior:
1. Controller immediately returns `Ack`.
2. Async service finds the latest unpaid bill from DB using the same customer param tags.
3. It records payment in DB (`payment_transactions` insert + `bill_details.bill_status` -> `PAID`) before sending a success response.
4. It generates `BillPaymentResponse` XML.
5. It POSTs `BillPaymentResponse` to BBPS using `OuRestTemplate` (OU XML signature).
   - Callback URL format:
     - `bbps.ip + bbps.billpaymentresponse.url + <refId>`
6. It logs the BBPS ACK status.

### 3) Heartbeat

`GET /heartbeat`

Returns a heartbeat response from the configured `HeartbeatService`.

## Logging (for request/response tracing)

For both flows (bill fetch and bill payment), logs print:

- Incoming request XML (the request payload received by the controller)
- Immediate `Ack` XML returned by the controller
- Generated response XML (`BillFetchResponse` / `BillPaymentResponse`) before POSTing to BBPS
- BBPS callback ACK status and the full ACK XML body (if present)

These prints are currently unconditional to help debugging.

## Database assumptions

The implementation expects `bill_details.bill_status` values:
- `UNPAID` (used to find bills for fetch/payment)
- `PAID` (set after payment callback ACK is received successfully)

