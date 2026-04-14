# Project Structure: BBPS Biller Integrator

## 1. Project Purpose
The `bbps-biller-integrator` is a Spring Boot service that acts as an integration layer between billers and the Bharat Bill Payment System (BBPS). It receives BBPS bill fetch and bill payment requests as XML payloads, provides immediate fast-track acknowledgements (`Ack`), processes the requests asynchronously by interacting either with a local cache/database or external Mock Biller Stubs, generates signed XML responses using organizational unit (OU) certificates, and asynchronous POSTs the results back to the BBPS callbacks.

## 2. Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 2.7.2
- **Build Tool**: Maven
- **Database**: PostgreSQL
- **XML Processing / Serialization**: JAXB (`javax.xml.bind:jaxb-api` 2.3.1)
- **JWT & Encryption**: Nimbus JOSE JWT 9.8.1
- **Boilerplate Reduction**: Lombok 1.18.30
- **Utilities**: Apache Commons Lang3 3.12.0

## 3. Directory Structure
```text
bbps-biller-integrator/
├── pom.xml                                   # Maven dependencies and build plugins
├── README.md                                 # Core project documentation and execution instructions
├── config/
│   └── certificate/                          # Configuration directory expecting OU certs (ousigner.p12)
└── src/
    └── main/
        ├── resources/
        │   ├── application.properties        # Env vars, DB connection details, Callback URLs
        │   ├── schema.sql                    # Initial database tables for PostgreSQL (billerdb)
        │   └── xsd/                          # BBPS XML Schema Definitions for JAXB generation
        └── java/bharat/connect/biller/
            ├── ApplicationContext.java       # Main Spring Boot entry point
            ├── cache/                        # Application level caching (BillerRoutingCache)
            ├── common/                       # Shared Enums (API) and CommonUtils
            ├── config/                       # Application configuration (DatabaseConfig)
            ├── controller/
            │   ├── BbpsRequestController.java # Main BBPS XML request receiving endpoints
            │   ├── BillerController.java      # Basic CRUD endpoint for Users (Internal Use)
            │   ├── BillerRegistrationController.java # Registers new billers via JSON payload
            │   └── MockBillerController.java  # Mock endpoints for dynamic bill fetch/payment testing
            ├── dao/                          # Database Access Objects interfaces
            │   └── impl/                     # JDBC/Database implementations (BillFetchDaoImpl, etc.)
            ├── dto/                          # Data Transfer Objects for JSON requests
            ├── model/                        # Domain entities (BillDetails, User)
            ├── rest/                         # Custom RestTemplates (OuRestTemplate for digital signing)
            └── service/                      # Core asynchronous business logic interfaces
                └── impl/                     # Concrete business logic (BillFetchServiceImpl, etc.)
```

## 4. Entry Points
- **Startup Class**: `bharat.connect.biller.ApplicationContext.java`
- **Execution Command**: `mvn spring-boot:run` from the project root. The server starts on port `8111` by default.

## 5. Environment Variables
- **`BBPS_HOME`**: Points to the base directory containing the `config/certificate` folder. Used to locate the `OU/ousigner.p12` keystore for XML signing. (Required)
- **`keystorepwd`** (System Property): Keystore password for the `.p12` file. (Optional; falls back to `"npciupi"`). Example: `-Dkeystorepwd=mypassword`
- **`spring.datasource.url`** (Properties): JDBC PostgreSQL URL. Example: `jdbc:postgresql://localhost:5432/ayushman?currentSchema=billerdb` (Required)
- **`spring.datasource.username`** (Properties): Database user username. Example: `postgres` (Required)
- **`spring.datasource.password`** (Properties): Database user password. (Required)
- **`cu.domain`**, **`bbps.ip`** (Properties): Domains for BBPS & CU callback resolution. Examples: `http://localhost:8112` (Required)
- **`ou.id`** (Properties): Originating Unit identifier. Example: `BBPS001` (Required)

## 6. API Endpoints / Routes

### BBPS Webhooks
- **`POST /BillFetchRequest/1.0/urn:referenceId:{referenceId}`**
  - **Purpose**: Receives BBPS bill fetch requests.
  - **Request Shape**: Signed XML `BillFetchRequest`.
  - **Response Shape**: Signed XML `Ack`.
  - **Middleware/Auth**: Handled implicitly through Spring's filter context and OU signature validation (if configured).

- **`POST /BillPaymentRequest/1.0/urn:referenceId:{referenceId}`**
  - **Purpose**: Receives BBPS bill payment requests.
  - **Request Shape**: Signed XML `BillPaymentRequest`.
  - **Response Shape**: Signed XML `Ack`.

### External Mock Endpoints
- **`POST /mock-biller/{billerId}/fetch`**
  - **Purpose**: Serves dynamically generated dummy bills for testing without actual CBS integrations.
  - **Request Shape**: JSON key-value map.
  - **Response Shape**: JSON (status, billerId, billAmount, dueDate, billNumber, customerName).

- **`POST /mock-biller/{billerId}/payment`**
  - **Purpose**: Confirms mock bill payment processing.
  - **Request Shape**: JSON key-value map.
  - **Response Shape**: JSON mock receipt confirming 'PAID' status.

### Administrative Endpoints
- **`POST /api/v1/biller/register`**
  - **Purpose**: Registers a new biller into the system cache.
  - **Request Shape**: JSON `BillerRegistrationRequest` (EntityName, RefId). Header `X-API-Key` required.
  - **Response Shape**: JSON `BillerRegistrationResponse`.
  - **Middleware/Auth**: Hardcoded manual API Key validation (`X-API-Key: POC-SECRET-KEY-123`).

- **`GET /heartbeat`**
  - **Purpose**: Health check endpoint.
  - **Response Shape**: Handled by `HeartbeatService` string response.

- **`GET / POST / PUT / DELETE /users`** 
  - **Purpose**: Boilerplate CRUD functionality for internal Users mapped in `model/User.java`.

## 7. Core Business Logic
- **`BillFetchServiceImpl.processBillFetchAsync`**
  - **Purpose**: Fetches bill data asynchronously. It either interrogates the routing cache to perform a dynamic JSON REST request against `MockBillerController` or accesses the `billerdb.bill_details` table to find an `UNPAID` bill matching customer params.
  - **Inputs**: `BillFetchRequest`, Reference ID (String).
  - **Outputs**: None.
  - **Side Effects**: Generates XML `BillFetchResponse`, digitally signs it via `OuRestTemplate`, and HTTP POSTs to the external BBPS callback endpoint.

- **`BillPaymentServiceImpl.processBillPaymentAsync`**
  - **Purpose**: Processes a payment request against an existing bill. Looks up the bill from `bill_details`, safely updates its status to `PAID`, inserts a transaction row in `payment_transactions`.
  - **Inputs**: `BillPaymentRequest`, Reference ID (String).
  - **Outputs**: None.
  - **Side Effects**: DB write for status update and transaction insert. Generates signed XML `BillPaymentResponse` and HTTP POSTs to the BBPS payment callback endpoint.

## 8. Data Models / Schemas
Database tables are present in `schema.sql` under the `billerdb` schema:
- **`bbps_master`**: Tracks registered billers.
  - `biller_id` (PK), `biller_name`, `category`, `bbps_biller_id` (Unique), `is_active`, `bbps_endpoint_url`.
- **`customer_params_master`**: Tracks query parameters configured for customer lookups.
  - `param_id` (PK), `param_name`, `param_type`, `param_value`, `validation_regex`.
- **`bill_details`**: Stores simulated unpaid customer bills.
  - `bill_id` (PK), `customer_param_name`, `customer_param_value` (Indexed pair to identify), `bill_amount` (Decimal), `due_date`, `bill_status` (Defaults to 'UNPAID', shifts to 'PAID' via payment endpoint).
- **`payment_transactions`**: Audit trail of completed payments.
  - `txn_id` (PK), `bill_id` (FK), `bbps_txn_ref` (Unique ID from BBPS request), `amount_paid`, `payment_mode`, `payment_status`.

## 9. External Dependencies
- **BBPS Gateway Core**: Async calls are fired to `${bbps.ip}${bbps.bill(fetch|payment)response.url}` appending the `refId`. E.g., `http://localhost:8112/bbps/BillFetchResponse/1.0/urn:referenceId:xyz`.
- **Mock Biller Systems**: Called via `RestTemplate` when a request matches a dynamically configured cache route (`MockBillerController`).

## 10. Error Handling
- **Flow**: Application fails gracefully using Java try/catch exception handling specifically geared around HTTP or `JAXBException` serialization errors.
- **Reporting**: Exceptions are predominantly logged as stack traces to console input (`e.printStackTrace()`).
- **Web Validations**: Missing payload components (like null `CustomerParams` or bad API keys) generally result in early return aborts (doing nothing silently) or HTTP 400 Bad Request / 401 Unauthorized codes.

## 11. Admin / UI Surface
- This project lacks a graphical user interface (GUI). All interactions occur programmatically via XML HTTP integration layers (BBPS standard) and REST endpoints (`api/v1/biller`). 

## 12. Tests
- **Existence**: While `spring-boot-starter-test` is populated within the `pom.xml`, there is currently no `src/test` directory available within the project structure, implying tests have not yet been developed for the business logic or controllers.

## 13. Known Gaps / TODOs
- **Security**: Endpoint `BillerRegistrationController` leverages a statically hardcoded `"POC-SECRET-KEY-123"` token for `X-API-Key` verification instead of robust role-based security.
- **Logging Infrastructure**: Deep usage of pure `System.out.println` and `e.printStackTrace()` as primary logging vectors in `BbpsRequestController` and `*ServiceImpl`. A full logging library (SLF4J + Logback/Log4j2) mapped to a file appender should be implemented.
- **Error Silencing**: Functions like `processBillFetchAsync` will abort silently if a mock stub crashes or returns a miss on a bill.
- **Test Coverage**: Complete absence of structural tests (JUnit integration/unit coverage).
- **Hardcoded Fallbacks**: Use of Mock controllers directly interacting dynamically via REST inside the exact same codebase instead of clean interface segregation and integration with actual Biller Core Banking Systems (CBS).
