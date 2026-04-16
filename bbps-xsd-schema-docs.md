# BBPS Biller Integrator — XSD Schema Documentation

> Auto-generated from XSD files.  
> **Total schemas:** 52

## Table of Contents

1. [BBPS-Common](#bbps-common)
2. [UPMSCommon](#upmscommon)
3. [402Request](#402request)
4. [402Response](#402response)
5. [Ack](#ack)
6. [AgentFetchRequest](#agentfetchrequest)
7. [AgentFetchResponse](#agentfetchresponse)
8. [AgentInstFetchRequest](#agentinstfetchrequest)
9. [AgentInstFetchResponse](#agentinstfetchresponse)
10. [AlertNotificationDetail](#alertnotificationdetail)
11. [AlertNotificationServiceObjects](#alertnotificationserviceobjects)
12. [BBPSPlanMDMPull](#bbpsplanmdmpull)
13. [BBPSPlanMDMPush](#bbpsplanmdmpush)
14. [BillerActivationCheckRequest](#billeractivationcheckrequest)
15. [BillerActivationCheckResponse](#billeractivationcheckresponse)
16. [BillerFetchRequest](#billerfetchrequest)
17. [BillerFetchResponse](#billerfetchresponse)
18. [BillerStatusRequest](#billerstatusrequest)
19. [BillerStatusResponse](#billerstatusresponse)
20. [BillerStatusUpdate](#billerstatusupdate)
21. [BillFetchRequest](#billfetchrequest)
22. [BillFetchResponse](#billfetchresponse)
23. [BillPaymentRequest](#billpaymentrequest)
24. [BillPaymentResponse](#billpaymentresponse)
25. [BillPaymentResponseLite](#billpaymentresponselite)
26. [BillPaymentValidationResponse](#billpaymentvalidationresponse)
27. [BillRefNumberStatusUpdateRequest](#billrefnumberstatusupdaterequest)
28. [BillValidationRequest](#billvalidationrequest)
29. [BillValidationResponse](#billvalidationresponse)
30. [BulkAck](#bulkack)
31. [BulkBillPaymentRequest](#bulkbillpaymentrequest)
32. [BulkBillPaymentResponse](#bulkbillpaymentresponse)
33. [BulkBillPresentmentConfirmation](#bulkbillpresentmentconfirmation)
34. [BulkBillPresentmentRequest](#bulkbillpresentmentrequest)
35. [BulkBillPresentmentResponse](#bulkbillpresentmentresponse)
36. [EditNotificationRequest](#editnotificationrequest)
37. [EditNotificationResponse](#editnotificationresponse)
38. [EFRMResponse](#efrmresponse)
39. [GenerateBillRefNumberRequest](#generatebillrefnumberrequest)
40. [NewNotificationRequest](#newnotificationrequest)
41. [NewNotificationResponse](#newnotificationresponse)
42. [NotificationExceptionResponse](#notificationexceptionresponse)
43. [ReqDiagnostic](#reqdiagnostic)
44. [ResDiagnostic](#resdiagnostic)
45. [SearchNotificationRequest](#searchnotificationrequest)
46. [SearchNotificationResponse](#searchnotificationresponse)
47. [SwitchoverRequest](#switchoverrequest)
48. [SwitchoverResponse](#switchoverresponse)
49. [TicketRequest](#ticketrequest)
50. [TicketResponse](#ticketresponse)
51. [TxnStatusComplainRequest](#txnstatuscomplainrequest)
52. [TxnStatusComplainResponse](#txnstatuscomplainresponse)

---

## BBPS-Common
**File:** `BBPS-Common.xsd`  
**Namespace:** `http://bbps.org/schema`  

### Complex Types
#### `headType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `ver` | `string` | required |
| `ts` | `string` | required |
| `origInst` | `string` | required |
| `refId` | `string` | required |
| `origRefId` | `string` | optional |
| `orderConfirmationId` | `string` | optional |
| `siTxn` | `siTxnType` | optional |
| `callbackEndpointIP` | `string` | optional |
| `response` | `string` | optional |

#### `analyticsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `(inline complex type)` | 2..2 |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `name` | `string` | required |
| `value` | `string` | required |

#### `riskScoresType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Score` | `(inline complex type)` | Optional, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `provider` | `string` | required |
| `type` | `string` | required |
| `value` | `string` | required |

#### `txnType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `RiskScores` | `riskScoresType` | Optional |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `ts` | `string` | required |
| `type` | `string` | optional |
| `msgId` | `string` | optional |
| `txnReferenceId` | `string` | optional |
| `xchangeId` | `string` | optional |
| `directBillChannel` | `directBillChannelType` | optional |
| `directBillContentId` | `string` | optional |
| `paymentRefId` | `string` | optional |
| `tId` | `string` | optional |

#### `customerDtlsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `(inline complex type)` | Optional, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `name` | `string` | required |
| `value` | `string` | required |
| `mobile` | `string` | required |

#### `deviceType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `(inline complex type)` | Required, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `name` | `deviceTagNameType` | required |
| `value` | `string` | required |

#### `agentType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Device` | `deviceType` | Required |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `id` | `string` | required |

#### `billerType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `id` | `string` | required |

#### `customerParamsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `(inline complex type)` | Required, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `name` | `string` | required |
| `value` | `string` | required |

#### `planInfoType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `(inline complex type)` | Required, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `name` | `string` | required |
| `value` | `string` | required |
| `type` | `planType` | required |

#### `planDetailsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `PlanDetail` | `planDetailType` | Required, unbounded |

#### `planDetailType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `(inline complex type)` | Required, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `name` | `string` | required |
| `value` | `string` | required |
| `type` | `planType` | required |
| `Id` | `string` | required |

#### `billDetailsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Biller` | `billerType` | Required |
| `CustomerParams` | `customerParamsType` | Required |

#### `billerResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `(inline complex type)` | Optional, unbounded |
| `BillerSpecificInfo` | `BillerSpecificInfoType` | 0..1000 |
| `CCF1` | `(inline complex type)` | Optional |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `name` | `string` | required |
| `value` | `string` | required |
| `minAmount` | `string` | optional |
| `maxAmount` | `string` | optional |
| `amountMultiple` | `string` | optional |
| `type` | `string` | required |
| `charges` | `string` | required |
| `GST` | `string` | required |
| `totalCharges` | `string` | required |
| `remarks` | `string` | optional |
| `customerName` | `string` | optional |
| `amount` | `string` | required |
| `dueDate` | `string` | optional |
| `custConvFee` | `string` | optional |
| `label` | `string` | optional |
| `amountMultiple` | `string` | optional |
| `minAmount` | `string` | optional |
| `maxAmount` | `string` | optional |
| `billDate` | `string` | optional |
| `billNumber` | `string` | optional |
| `billPeriod` | `string` | optional |
| `billExpiry` | `string` | optional |

#### `BillerSpecificInfoType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `(inline complex type)` | Optional, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `name` | `string` | required |
| `value` | `string` | required |

#### `billerPlanResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `PlanInfo` | `planInfoType` | 0..15 |

#### `reasonType`
*Extends:* `string`

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `approvalRefNum` | `string` | required |
| `responseCode` | `string` | required |
| `responseReason` | `string` | required |
| `complianceRespCd` | `string` | optional |
| `complianceReason` | `string` | optional |

#### `CCF1Type`
*Extends:* `string`

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `type` | `string` | required |
| `charges` | `string` | required |
| `GST` | `string` | required |
| `totalCharges` | `string` | required |
| `remarks` | `string` | optional |

#### `additionalInfoType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `(inline complex type)` | Optional, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `name` | `string` | required |
| `value` | `string` | required |

#### `pmtMtdType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `quickPay` | `qckPayType` | optional |
| `splitPay` | `spltPayType` | optional |
| `OFFUSPay` | `offUsPayType` | optional |
| `paymentMode` | `string` | optional |

#### `amtType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `amount` | `string` | optional |
| `custConvFee` | `string` | optional |
| `currency` | `string` | optional |
| `COUcustConvFee` | `string` | optional |
| `billNumber` | `string` | optional |

#### `amountType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Amt` | `amtType` | Required |
| `AmountBreakUp` | `amtBreakUpType` | Required |
| `SplitPayAmount` | `string` | Required |
| `Tag` | `(inline complex type)` | Optional, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `name` | `string` | required |
| `value` | `string` | required |
| `minAmount` | `string` | optional |
| `maxAmount` | `string` | optional |
| `amountMultiple` | `string` | optional |

#### `pymntInfType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `(inline complex type)` | Required, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `name` | `string` | required |
| `value` | `string` | required |

#### `errorMessage`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `errorCd` | `string` | Required |
| `errorDtl` | `string` | Required |

#### `search`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `billerId` | `string` | Required, unbounded |

#### `upmsType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `txnType` | `upmsTxnType` | optional |
| `cprn` | `string` | optional |
| `bprn` | `string` | optional |
| `isBulk` | `boolean` | optional |

#### `amt`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `amount` | `string` | optional |

#### `amtBreakUpType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `BillAmt` | `billAmountType` | Optional, unbounded |
| `TDSDeductedAmt` | `TDSDeductedAmountType` | Optional, unbounded |

#### `billAmountType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `billNumber` | `string` | optional |
| `Amount` | `string` | optional |

#### `TDSDeductedAmountType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `billNumber` | `string` | optional |
| `Amount` | `string` | optional |

#### `billerCategoryType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `category` | `string` | required |

### Simple Types (Enumerations)
#### `custIdentityConstant`
*Base type:* `string`

**Allowed values:**

- `EMAIL`
- `PAN`
- `AADHAAR`
- `AUTHMETHOD`
- `LOAN_ACCOUNT`
- `CAN`
- `MOBILE`
- `ACCOUNT_HOLDER_NAME`
- `ACCOUNT_TYPE`
- `REMITTER_NAME`
- `ADDRESS`
- `DATEANDPLACEOFBIRTH`
- `NATIONALIDENTITYNUMBER`
- `CUSTOMERIDENTIFICATIONNUMBER`

#### `transactionType`
*Base type:* `string`

**Allowed values:**

- `FORWARD TYPE REQUEST`
- `REVERSAL TYPE REQUEST`
- `FORWARD TYPE RESPONSE`
- `REVERSAL TYPE RESPONSE`

#### `deviceTagNameType`
*Base type:* `string`

**Allowed values:**

- `MOBILE`
- `GEOCODE`
- `POSTAL_CODE`
- `IP`
- `INITIATING_CHANNEL`
- `TERMINAL_ID`
- `IMEI`
- `IFSC`
- `MAC`
- `OS`
- `APP`

#### `initiatingChannelType`
*Base type:* `string`

**Allowed values:**

- `INT`
- `MOB`
- `POS`
- `KIOSK`
- `MPOS`
- `ATM`
- `BNKBRNCH`

#### `qckPayType`
*Base type:* `string`

**Allowed values:**

- `Yes`
- `No`

#### `spltPayType`
*Base type:* `string`

**Allowed values:**

- `Yes`
- `No`

#### `offUsPayType`
*Base type:* `string`

**Allowed values:**

- `Yes`
- `No`

#### `planType`
*Base type:* `string`

**Allowed values:**

- `NEW`
- `EXISTING`
- `RECOMMENDED`
- `CURATED`
- `ACTIVATED`

#### `siTxnType`
*Base type:* `string`

**Allowed values:**

- `Yes`
- `No`

#### `analyticsFetchTypeInstance`
*Base type:* `string`

**Allowed values:**

- `FETCHREQUESTSTART`
- `FETCHREQUESTEND`

#### `analyticsPaymentTypeInstance`
*Base type:* `string`

**Allowed values:**

- `PAYREQUESTSTART`
- `PAYREQUESTEND`

#### `upmsTxnType`
*Base type:* `string`

**Allowed values:**

- `PRESENTMENT_REGISTRATION`
- `BILL_PRESENTMENT`
- `BILL_PAYMENT`

#### `directBillChannelType`
*Base type:* `string`

**Allowed values:**

- `L1QR`
- `L2QR`
- `L3QR`
- `L1PL`
- `L2PL`
- `L3PL`

---

## UPMSCommon
**File:** `UPMSCommon.xsd`  
**Namespace:** `http://upms.org/schema`  

### Complex Types
#### `headType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `ts` | `string` | required |
| `origInst` | `string` | required |
| `refId` | `string` | required |

#### `tagType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `name` | `string` | required |
| `value` | `string` | required |

#### `customerDtlsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Mobile` | `string` | Required |
| `Email` | `string` | Optional |
| `Aadhaar` | `string` | Optional |

#### `mandateType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `RegistrationType` | `string` | Optional |
| `BillPeriod` | `string` | Optional |
| `ToDate` | `string` | Optional |
| `DebitInfo` | `debitInfoType` | Optional |
| `Cprn` | `string` | Optional |
| `Bprn` | `string` | Optional |
| `MultipleRegistrationExists` | `string` | Optional |
| `Status` | `string` | Optional |

#### `debitInfoType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Type` | `string` | Optional |
| `Amount` | `string` | Optional |
| `Currency` | `string` | Optional |
| `PaymentMode` | `string` | Optional |
| `DebitDate` | `string` | Optional |

#### `billerParamType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `tagType` | Required, unbounded |

#### `customerParamsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `tagType` | Required, unbounded |

#### `deviceType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `tagType` | Required, unbounded |

#### `reasonType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `ResponseCode` | `string` | Required |
| `ResponseReason` | `string` | Required |

#### `billerResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `tagType` | Required, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `customerName` | `string` | optional |
| `amount` | `string` | optional |
| `dueDate` | `string` | optional |
| `custConvFee` | `string` | optional |
| `billDate` | `string` | optional |
| `billNumber` | `string` | optional |
| `billPeriod` | `string` | optional |

#### `additionalInfoType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `tagType` | Required, unbounded |

#### `authType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `AccValidationRefNum` | `string` | Required |
| `AccHolderName` | `string` | Required |
| `AccType` | `string` | Required |

#### `parameterType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `PresentmentRegRefId` | `string` | Optional |
| `Cprn` | `string` | Optional |
| `Bprn` | `string` | Optional |

#### `presentmentStatusType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `PresentmentRegRefId` | `string` | Required |
| `RegistrationCreationDate` | `string` | Required |
| `BillerId` | `string` | Required |
| `BillerInitiated` | `string` | Required |
| `IssuerId` | `string` | Optional |
| `AgentId` | `string` | Optional |
| `Customer` | `customerDtlsType` | Required |
| `Mandate` | `mandateType` | Required |
| `BillerParams` | `billerParamType` | Required |
| `CustomerParams` | `customerParamsType` | Required |
| `BillerResponse` | `billerResponseType` | Optional |
| `AdditionalInfo` | `additionalInfoType` | Optional |

#### `recordsType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `limit` | `string` | optional |
| `remaning` | `string` | optional |
| `fetched` | `string` | optional |

#### `paymentsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `BillPayment` | `(inline complex type)` | Required, unbounded |
| `BillerId` | `string` | Required |
| `PresentmentRefId` | `string` | Required |
| `DateOfPayment` | `string` | Required |
| `Mandate` | `mandateType` | Required |

#### `billsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `BillPresentments` | `(inline complex type)` | Required, unbounded |
| `RefId` | `string` | Required |
| `Ts` | `string` | Required |
| `BillDetails` | `billDetailsType` | Required |
| `BillInfo` | `billInfoType` | Required |
| `AdditionalInfo` | `additionalInfoType` | Required |
| `Mandate` | `mandateType` | Required |

#### `billDetailsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Biller` | `billerType` | Required |
| `CustomerParams` | `customerParamsType` | Optional |

#### `billerType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `id` | `string` | required |

#### `billInfoType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `tagType` | Required, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `customerName` | `string` | optional |
| `amount` | `string` | required |
| `dueDate` | `string` | optional |
| `billDate` | `string` | optional |
| `billNumber` | `string` | optional |
| `billPeriod` | `string` | optional |

#### `errorMessage`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `errorCd` | `string` | Required |
| `errorDtl` | `string` | Required |

#### `billNotify`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `BillPaymentNotifications` | `(inline complex type)` | Required, unbounded |
| `RefId` | `string` | Required |
| `Ts` | `string` | Required |
| `BillerId` | `string` | Required |
| `PresentmentRefId` | `string` | Required |
| `DateOfPayment` | `string` | Required |
| `Mandate` | `mandateType` | Required |

#### `Ack`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `errorMessages` | `errorMessage` | Optional, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `refId` | `string` | optional |
| `api` | `string` | optional |
| `ts` | `string` | optional |
| `RspCd` | `string` | optional |
| `cprn` | `string` | optional |

### Simple Types (Enumerations)
#### `DeviceTagNameType`
*Base type:* `string`

**Allowed values:**

- `MOBILE`
- `GEOCODE`
- `POSTAL_CODE`
- `IP`
- `INITIATING_CHANNEL`
- `TERMINAL_ID`
- `IMEI`
- `IFSC`
- `MAC`
- `OS`
- `APP`

#### `BillerParamNameType`
*Base type:* `string`

**Allowed values:**

- `BILL_TYPE`

#### `DebitType`
*Base type:* `string`

**Allowed values:**

- `FIXED_AMOUNT`
- `MAX_AMOUNT`

#### `BillPeriodType`
*Base type:* `string`

**Allowed values:**

- `DAILY`
- `WEEKLY`
- `BIMONTHLY`
- `MONTHLY`
- `QUARTERLY`
- `HALFYEARLY`
- `YEARLY`
- `ASPRESENTED`

#### `RegistrationType`
*Base type:* `string`

**Allowed values:**

- `VIEW_N_PAY`
- `AUTO_PAY`

---

## 402Request
**File:** `402Request.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `TxnStatusRequest` | `TxnStatusRequest` | 402 API Request |

### Complex Types
#### `TxnStatusRequest`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Txn` | `txnType` | Required |
| `TxnStatusReq` | `TxnStatusReq` | Required |
| `Upms` | `upmsType` | Optional |

#### `TxnStatusReq`
*Extends:* `string`

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `msgId` | `string` | optional |
| `txnReferenceId` | `string` | required |

---

## 402Response
**File:** `402Response.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `TxnStatusResponse` | `TxnStatusResponse` | 402 API Response |

### Complex Types
#### `TxnStatusResponse`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Reason` | `reasonType` | Required |
| `Txn` | `txnType` | Required |
| `BillDetails` | `billDetailsType` | Required |
| `BillerResponse` | `billerResponseType` | Required, unbounded |
| `BillerPlanResponse` | `billerPlanResponseType` | Optional |
| `AdditionalInfo` | `additionalInfoType` | Optional |
| `Upms` | `upmsType` | Optional |

---

## Ack
**File:** `Ack.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `Ack` | `Ack` | Ack |

### Complex Types
#### `Ack`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `errorMessages` | `errorMessage` | Optional, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `api` | `string` | optional |
| `refId` | `string` | optional |
| `msgId` | `string` | optional |
| `RspCd` | `string` | optional |
| `ts` | `string` | optional |
| `id` | `string` | optional |

---

## AgentFetchRequest
**File:** `AgentFetchRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`, `BillerFetchRequest.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `AgentFetchRequest` | `AgentFetchRequestType` | Agent Fetch Request |

### Complex Types
#### `AgentFetchRequestType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Search` | `searchTypeForAgent` | Optional |
| `SearchByTime` | `searchByTime` | Optional |

#### `searchTypeForAgent`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `agentId` | `string` | Optional, unbounded |

---

## AgentFetchResponse
**File:** `AgentFetchResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`, `BillerFetchResponse.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `AgentFetchResponse` | `AgentFetchResponseType` | Agent Fetch Response |

### Complex Types
#### `AgentFetchResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Agent` | `agent` | Optional, unbounded |
| `searchResult` | `SearchResult` | Optional |

#### `agent`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `agentId` | `string` | Required |
| `agentBusnsType` | `string` | Required |
| `agentName` | `string` | Required |
| `agentAliasName` | `string` | Required |
| `agentLinkedAgentInst` | `string` | Required |
| `agentGeoCode` | `string` | Required |
| `agent_shop_name` | `string` | Required |
| `agent_mobile_no` | `string` | Required |
| `agentDummy` | `string` | Required |
| `agentPaymentModes` | `PaymentModeLimit` | Required, unbounded |
| `agentPaymentChannels` | `PaymentChannelLimit` | Required, unbounded |
| `agentEffctvFrom` | `string` | Required |
| `agentEffctvTo` | `string` | Required |
| `agentStatus` | `string` | Required |
| `agentTempDeactivationStart` | `string` | Optional |
| `agentTempDeactivationEnd` | `string` | Optional |
| `agentRefId` | `string` | Required |
| `agentBulk` | `string` | Required |
| `agentPinCode` | `string` | Required |
| `agentRegisteredCity` | `string` | Required |
| `agentRegisteredState` | `string` | Required |
| `agentRegisteredAddress` | `string` | Required |
| `agentRegisteredCountry` | `string` | Required |

---

## AgentInstFetchRequest
**File:** `AgentInstFetchRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`, `BillerFetchRequest.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `AgentInstituteFetchRequest` | `AgentInstituteFetchRequestType` | Agent Institute Fetch Request |

### Complex Types
#### `AgentInstituteFetchRequestType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Search` | `agentSearchType` | Optional |
| `SearchByTime` | `searchByTime` | Optional |

#### `agentSearchType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `agentInstituteId` | `string` | Optional, unbounded |
| `agentInstituteName` | `string` | Optional, unbounded |

---

## AgentInstFetchResponse
**File:** `AgentInstFetchResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`, `BillerFetchResponse.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `AgentInstituteFetchResponse` | `AgentInstituteFetchResponseType` | Agent Institute Fetch Response |

### Complex Types
#### `AgentInstituteFetchResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `agentInstitutes` | `AgentInstitute` | Optional, unbounded |
| `searchResult` | `SearchResult` | Optional |

#### `AgentInstitute`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `agentInstituteId` | `string` | Required |
| `agentInstType` | `string` | Required |
| `agentInstBusnsType` | `string` | Required |
| `agentInstName` | `string` | Required |
| `agentInstAliasName` | `string` | Required |
| `agentInstLinkedOU` | `string` | Required |
| `agentInstTanNo` | `string` | Required |
| `agentInstEffctvFrom` | `string` | Required |
| `agentInstEffctvTo` | `string` | Required |
| `agentInstUaadhaar` | `string` | Required |
| `agentInstRocUin` | `string` | Required |
| `agentInstStatus` | `string` | Required |
| `agentInstTempDeactivationStart` | `string` | Optional |
| `agentinstTempDeactivationEnd` | `string` | Optional |

---

## AlertNotificationDetail
**File:** `AlertNotificationDetail.xsd`  
**Namespace:** `http://bbps.org/schema`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `NotificationDetailType` | `notificationDetailType` | NotificaitonAlertDetails |

### Complex Types
#### `notificationDetailType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `notificationId` | `long` | Optional |
| `creatorId` | `string` | Required |
| `message` | `string` | Required |
| `startDate` | `long` | Required |
| `endDate` | `long` | Required |
| `purpose` | `string` | Required |
| `remarks` | `string` | Optional |
| `targetGroup` | `NotificationTargetGroupTypes` | Required |
| `targetUserIds` | `string` | Required |
| `stakeHolder` | `NotificationStakeHolderTypes` | Required |
| `commChannel` | `NotificationCommChannelTypes` | Optional |
| `active` | `boolean` | Required |
| `deleted` | `boolean` | Required |
| `repetInterval` | `NotificationRepetIntervelTypes` | Optional |
| `triggerTime` | `string` | Optional |
| `notificationType` | `NotificationTypes` | Optional |
| `severity` | `NotificationSeverityTypes` | Required |
| `alertType` | `NotificationAlertTypes` | Required |
| `customParams` | `string` | Required |
| `templateId` | `int` | Optional |
| `targetEmails` | `string` | Optional |
| `targetMobiles` | `string` | Optional |
| `name` | `string` | Optional |
| `attachmentFilePathName` | `string` | Optional |
| `processed` | `boolean` | Optional |
| `sourceApp` | `string` | Required |
| `targetEntity` | `string` | Optional |
| `additionalEmail` | `string` | Optional |

### Simple Types (Enumerations)
#### `NotificationAlertTypes`
*Base type:* `string`

**Allowed values:**

- `ADHOC`
- `STATIC`
- `REPETITIVE`
- `BROADCAST`

#### `NotificationPurposeTypes`
*Base type:* `string`

**Allowed values:**

- `Downtime`
- `Field Information`
- `New Launches`
- `Scheme`
- `Others`

#### `NotificationStakeHolderTypes`
*Base type:* `string`

**Allowed values:**

- `BBPOUS`
- `BBPCU`
- `CUSTOMERS`
- `BILLERS`
- `AGENT_INSTITUTIONS`
- `AGENTS`
- `OTHERS`

#### `NotificationCommChannelTypes`
*Base type:* `string`

**Allowed values:**

- `SMS`
- `Email`
- `Message Box`

#### `NotificationTargetGroupTypes`
*Base type:* `string`

**Allowed values:**

- `BBPOUS`
- `BBPCU`
- `CUSTOMERS`
- `BILLERS`
- `AGENT_INSTITUTIONS`
- `AGENTS`
- `OTHERS`

#### `NotificationRepetIntervelTypes`
*Base type:* `string`

**Allowed values:**

- `Once`
- `Daily`
- `AlternateDay`
- `Weekly`
- `BiWeekly`
- `Monthly`
- `SemiMonthly`

#### `NotificationSeverityTypes`
*Base type:* `string`

**Allowed values:**

- `HIGH`
- `MEDIUM`
- `LOW`

#### `NotificationTypes`
*Base type:* `string`

**Allowed values:**

- `SMS`
- `EMAIL`
- `ONSCREEN`

---

## AlertNotificationServiceObjects
**File:** `AlertNotificationServiceObjects.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `AlertNotificationDetail.xsd`  

### Complex Types
#### `AlertMessage`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `alertId` | `string` | Optional |
| `creatorId` | `string` | Required |
| `message` | `string` | Required |
| `name` | `string` | Required |
| `startDate` | `date` | Optional |
| `endDate` | `date` | Optional |
| `purpose` | `string` | Required |
| `remarks` | `string` | Optional |
| `customParams` | `string` | Required |
| `targetGroup` | `NotificationTargetGroupTypes` | Required |
| `targetUserIds` | `string` | Optional |
| `targetEmails` | `string` | Optional |
| `targetMobiles` | `string` | Optional |
| `stakeHolder` | `NotificationStakeHolderTypes` | Required |
| `commChannel` | `NotificationCommChannelTypes` | Optional |
| `active` | `boolean` | Required |
| `deleted` | `boolean` | Required |
| `alertType` | `NotificationAlertTypes` | Required |
| `repetInterval` | `NotificationRepetIntervelTypes` | Optional |
| `repetTime` | `string` | Optional |
| `sourceApp` | `string` | Optional |
| `targetEntity` | `string` | Optional |
| `additionalEmail` | `string` | Optional |

#### `NotificationMessage`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `senderId` | `string` | Optional |
| `targetGroup` | `NotificationTargetGroupTypes` | Required |
| `targetEmails` | `string` | Required |
| `targetMobiles` | `string` | Required |
| `message` | `string` | Required |
| `purpose` | `string` | Required |
| `remarks` | `string` | Optional |
| `name` | `string` | Required |
| `customParams` | `string` | Required |
| `emailTemplateId` | `int` | Required |
| `stakeHolder` | `NotificationStakeHolderTypes` | Required |
| `sourceApp` | `string` | Required |
| `targetEntity` | `string` | Optional |
| `additionalEmail` | `string` | Optional |

---

## BBPSPlanMDMPull
**File:** `BBPSPlanMDMPull.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPSPlanMDMPush.xsd`, `BillerFetchRequest.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BBPSPlanMDMPull` | `BBPSPlanMDMPullType` | BBPS Plan MDM Pull |

### Complex Types
#### `BBPSPlanMDMPullType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `PlanHeadType` | Required |
| `Search` | `SearchByBiller` | Optional |
| `SearchByTime` | `searchByTime` | Optional |

#### `SearchByBiller`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `billerId` | `string` | Optional, unbounded |

---

## BBPSPlanMDMPush
**File:** `BBPSPlanMDMPush.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BillerFetchResponse.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BBPSPlanMDMPush` | `BBPSPlanMDMPushType` | BBPS Plan MDM Push |

### Complex Types
#### `BBPSPlanMDMPushType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `PlanHeadType` | Required |
| `PlanDetails` | `PlanDetail` | Optional, unbounded |
| `searchResult` | `SearchResult` | Optional |
| `totalRecords` | `string` | 0..0 |
| `pageNumber` | `string` | 0..0 |

#### `PlanDetail`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Id` | `string` | Required |
| `billerId` | `string` | Required |
| `categoryType` | `string` | Required |
| `categorySubType` | `CategorySubType` | Required |
| `amountInRupees` | `double` | Required |
| `planDescription` | `string` | Required |
| `planAdditionalInfo` | `PlanAdditionalInfo` | Optional |
| `effctvFrom` | `string` | Required |
| `effctvTo` | `string` | Optional |
| `status` | `string` | Required |
| `supportRecurringMandate` | `SupportRecurringMandate` | Optional |

#### `CategorySubType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `subType` | `string` | Optional, unbounded |

#### `PlanAdditionalInfo`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Tag` | `(inline complex type)` | Required, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `paramName` | `string` | required |
| `paramValue` | `string` | required |

#### `PlanHeadType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `ver` | `string` | required |
| `ts` | `string` | required |
| `origInst` | `string` | required |
| `refId` | `string` | required |
| `type` | `HeadRequestType` | required |

### Simple Types (Enumerations)
#### `HeadRequestType`
*Base type:* `string`

**Allowed values:**

- `REQUEST`
- `RESPONSE`

#### `SupportRecurringMandate`
*Base type:* `string`

**Allowed values:**

- `MANDATORY`
- `OPTIONAL`
- `NOT_SUPPORTED`

---

## BillerActivationCheckRequest
**File:** `BillerActivationCheckRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillerActivationCheckRequest` | `ActivationCheckRequest` | ActivationCheckRequest |

### Complex Types
#### `ActivationCheckRequest`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Search` | `search` | Required |

---

## BillerActivationCheckResponse
**File:** `BillerActivationCheckResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillerActivationCheckResponse` | `ActivationCheckResponse` | Activation Check Response |

### Complex Types
#### `ActivationCheckResponse`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Biller` | `ActivationBiller` | Required, unbounded |

#### `ActivationBiller`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `id` | `string` | optional |
| `extendDownTime` | `string` | optional |
| `durationInMinutes` | `string` | optional |

---

## BillerFetchRequest
**File:** `BillerFetchRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillerFetchRequest` | `BillerFetchRequestType` | BBPS Biller Fetch Request |

### Complex Types
#### `BillerFetchRequestType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `SearchMyBiller` | `searchMyBiller` | Optional |
| `Search` | `searchType` | Optional |
| `SearchByTime` | `searchByTime` | Optional |
| `SearchByFeature` | `searchByFeature` | Optional |
| `Paging` | `paging` | Optional |
| `afterbillerid` | `string` | Optional |
| `beforebillerid` | `string` | Optional |
| `limit` | `integer` | Optional |

#### `searchType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `billerId` | `string` | Optional, unbounded |
| `billerName` | `string` | Optional, unbounded |
| `billerCategoryName` | `string` | Optional, unbounded |
| `paymentMode` | `string` | Optional, unbounded |
| `paymentChannel` | `string` | Optional, unbounded |

#### `searchByTime`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `time` | `string` | Optional |

#### `searchMyBiller`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `mybiller` | `myBiller` | optional |

#### `paging`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `afterbillerid` | `string` | Optional |
| `beforebillerid` | `string` | Optional |
| `limit` | `integer` | Optional |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `id` | `string` | required |

### Simple Types (Enumerations)
#### `myBiller`
*Base type:* `string`

**Allowed values:**

- `Yes`
- `No`

#### `searchByFeature`
*Base type:* `string`

**Allowed values:**

- `Upms`
- `Clickpay`
- `Upms,Clickpay`
- `Clickpay,Upms`
- `Corporate`

---

## BillerFetchResponse
**File:** `BillerFetchResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillerFetchResponse` | `BillerFetchResponseType` | BBPS Biller Fetch Response |

### Complex Types
#### `BillerFetchResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `biller` | `Biller` | Optional, unbounded |
| `searchResult` | `SearchResult` | Optional |

#### `Biller`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `billerId` | `string` | Required |
| `billerName` | `string` | Required |
| `billerAliasName` | `string` | Required |
| `billerCategoryName` | `string` | Required |
| `billerSubCategoryName` | `string` | Optional |
| `billerMode` | `string` | Required |
| `billerResponseType` | `string` | Required |
| `billerAcceptsAdhoc` | `boolean` | Required |
| `parentBiller` | `boolean` | Required |
| `parentBillerId` | `string` | Required |
| `billerOwnerShp` | `string` | Required |
| `billerCoverage` | `string` | Required |
| `fetchRequirement` | `FetchRequirement` | Required |
| `authRequirement` | `AuthRequirement` | Required |
| `paymentAmountExactness` | `string` | Optional |
| `supportBillValidation` | `string` | Optional |
| `billerEffctvFrom` | `string` | Required |
| `billerEffctvTo` | `string` | Required |
| `billerTempDeactivationStart` | `string` | Optional |
| `billerTempDeactivationEnd` | `string` | Optional |
| `billerPaymentModes` | `PaymentModeLimit` | Required, unbounded |
| `billerPaymentChannels` | `PaymentChannelLimit` | Required, unbounded |
| `billerCustomerParams` | `ParamConfig` | Required, unbounded |
| `customerParamGroups` | `CustomerParamGroups` | Optional |
| `billerSpecificInfos` | `ParamConfig` | Optional, unbounded |
| `billerResponseParams` | `BillerResponseParams` | Required |
| `billerPlanResponseParams` | `BillerPlanResponseParams` | Required |
| `billerAdditionalInfo` | `ParamConfig` | Required, unbounded |
| `billerAdditionalInfoPayment` | `ParamConfig` | Required, unbounded |
| `interchangeFeeConf` | `InterchangeFeeConf` | Required, unbounded |
| `interchangeFee` | `InterchangeFee` | Required, unbounded |
| `Status` | `string` | Optional |
| `billerDescription` | `string` | Optional |
| `supportDeemed` | `string` | Optional |
| `supportPendingStatus` | `string` | Optional |
| `billerTimeOut` | `string` | Optional |
| `planMdmRequirement` | `PlanMDMRequirement` | Optional |
| `planAdditionalInfo` | `ParamConfig` | Optional, unbounded |
| `upmsBillerPaymentModes` | `PaymentModeLimit` | Optional, unbounded |
| `upmsBillerPaymentChannels` | `PaymentChannelLimit` | Optional, unbounded |
| `upmsEnabled` | `boolean` | Optional |
| `upmsbillerParams` | `ParamConfig` | Optional, unbounded |
| `upmsBillerType` | `UpmsBillerType` | Required |
| `selectionType` | `SelectionType` | Required |
| `clickpayEnabled` | `boolean` | Optional |
| `clickpayValue` | `string` | Optional |
| `additionalFeesEnabled` | `boolean` | Optional |
| `additionalFees` | `ParamConfig` | Optional, unbounded |
| `supportsCalendarDayFetchRetention` | `boolean` | Optional |
| `fetchRetentionInHours` | `string` | Optional |
| `deemedSuccessEnabled` | `boolean` | Optional |
| `valAddFlag` | `boolean` | Optional |
| `valAddCustParams` | `ValAddCustomerParams` | Required, unbounded |
| `corporateFlag` | `boolean` | Optional |
| `billAdjustmentFrequency` | `string` | Required |
| `mandateRequirement` | `MandateRequirement` | Required |
| `blrReqResFormat` | `string` | Required |
| `pseudoBiller` | `boolean` | Required |
| `supportCallBack` | `boolean` | Required |
| `ccfResponseRemarks` | `ccfResponseRemarks` | Optional, unbounded |

#### `PaymentModeLimit`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `paymentMode` | `string` | Required |
| `maxLimit` | `long` | Optional |
| `minLimit` | `long` | Optional |
| `supportPendingStatus` | `string` | Optional |

#### `PaymentChannelLimit`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `paymentChannel` | `string` | Required |
| `maxLimit` | `long` | Optional |
| `minLimit` | `long` | Optional |
| `supportPendingStatus` | `string` | Optional |

#### `ParamConfig`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `paramName` | `string` | Required |
| `dataType` | `DataType` | Required |
| `optional` | `boolean` | Required |
| `minValue` | `long` | Optional |
| `maxValue` | `long` | Optional |
| `minLength` | `int` | Optional |
| `maxLength` | `int` | Optional |
| `regex` | `string` | Optional |
| `values` | `string` | Optional |
| `visibility` | `boolean` | Optional |
| `encryptionType` | `EncryptionType` | Required |
| `unique` | `boolean` | Optional |

#### `ValAddCustomerParams`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `requestType` | `string` | Optional, unbounded |
| `customerParams` | `ParamConfig` | Required, unbounded |

#### `ccfResponseRemarks`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `ccfSource` | `string` | Optional |
| `ccfRemarks` | `string` | Optional |

#### `BillerResponseParams`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `params` | `ParamConfig` | Required, unbounded |
| `amountOptions` | `AmountOption` | Required, unbounded |

#### `BillerPlanResponseParams`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `params` | `ParamConfig` | Required, unbounded |
| `amountOptions` | `AmountOption` | Required, unbounded |

#### `CustomerParamGroups`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `group` | `Group` | Required, unbounded |

#### `Group`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `param` | `string` | Required, unbounded |
| `group` | `Group` | Optional, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `name` | `string` | required |
| `input` | `string` | optional |

#### `AmountOption`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `amountBreakupSet` | `string` | Required, unbounded |

#### `InterchangeFeeConf`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `mti` | `string` | Required |
| `paymentMode` | `string` | Required |
| `paymentChannel` | `string` | Required |
| `responseCode` | `string` | Required |
| `fees` | `string` | Required, unbounded |
| `defaultFee` | `boolean` | Required |
| `effctvFrom` | `string` | Optional |
| `effctvTo` | `string` | Optional |

#### `InterchangeFee`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `feeCode` | `string` | Required |
| `feeDesc` | `string` | Required |
| `feeDirection` | `InterchangeFeeDirection` | Required |
| `interchangeFeeDetails` | `InterchangeFeeDetailsType` | Required, unbounded |

#### `InterchangeFeeDetailsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `tranAmtRangeMax` | `long` | Required |
| `tranAmtRangeMin` | `long` | Required |
| `percentFee` | `decimal` | Required |
| `flatFee` | `decimal` | Required |
| `effctvFrom` | `string` | Required |
| `effctvTo` | `string` | Required |

#### `SearchResult`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `result` | `string` | Required |

### Simple Types (Enumerations)
#### `DataType`
*Base type:* `string`

**Allowed values:**

- `NUMERIC`
- `ALPHANUMERIC`

#### `EncryptionType`
*Base type:* `string`

**Allowed values:**

- `NONE`
- `MASK`
- `ENCRYPT`

#### `InterchangeFeeDirection`
*Base type:* `string`

**Allowed values:**

- `B2C`
- `C2B`

#### `FetchRequirement`
*Base type:* `string`

**Allowed values:**

- `MANDATORY`
- `OPTIONAL`
- `NOT_SUPPORTED`

#### `AuthRequirement`
*Base type:* `string`

**Allowed values:**

- `MANDATORY`
- `NOT_SUPPORTED`

#### `PlanMDMRequirement`
*Base type:* `string`

**Allowed values:**

- `MANDATORY`
- `OPTIONAL`
- `CONDITIONAL`
- `NOT_SUPPORTED`

#### `UpmsBillerType`
*Base type:* `string`

**Allowed values:**

- `SINGLE`
- `MULTIPLE`
- `SPECIAL`
- `NOT_SUPPORTED`

#### `SelectionType`
*Base type:* `string`

**Allowed values:**

- `SINGLE`
- `MULTIPLE`
- `ALL`

#### `MandateRequirement`
*Base type:* `string`

**Allowed values:**

- `MANDATORY`
- `OPTIONAL`
- `NOT_SUPPORTED`

---

## BillerStatusRequest
**File:** `BillerStatusRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillerStatusRequest` | `BillerStatusRequestType` | Biller Status Request |

### Complex Types
#### `BillerStatusRequestType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Search` | `search` | Optional |

---

## BillerStatusResponse
**File:** `BillerStatusResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillerStatusResponse` | `BillerStatusResponse` | Biller Status Response |

### Complex Types
#### `BillerStatusResponse`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Biller` | `BillerStatusType` | Optional, unbounded |

#### `BillerStatusType`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `id` | `string` | optional |
| `xchangeId` | `string` | optional |
| `status` | `string` | optional |
| `startTime` | `string` | optional |
| `description` | `string` | optional |

---

## BillerStatusUpdate
**File:** `BillerStatusUpdate.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillerStatusUpdate` | `BillerStatusUpdate` | Biller Status Update |

### Complex Types
#### `BillerStatusUpdate`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Biller` | `billerDetails` | Optional, unbounded |

#### `billerDetails`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `id` | `string` | optional |
| `xchangeId` | `string` | optional |
| `status` | `string` | optional |
| `startTime` | `string` | optional |
| `description` | `string` | optional |
| `durationInMinutes` | `string` | optional |

---

## BillFetchRequest
**File:** `BillFetchRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillFetchRequest` | `BillFetchRequestType` | BBPS Bill Request |

### Complex Types
#### `BillFetchRequestType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Analytics` | `analyticsType` | Optional |
| `Txn` | `txnType` | Required |
| `Customer` | `customerDtlsType` | Required |
| `Agent` | `agentType` | Required |
| `BillDetails` | `billDetailsType` | Required |
| `Upms` | `upmsType` | Optional |

---

## BillFetchResponse
**File:** `BillFetchResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillFetchResponse` | `BillFetchResponseType` | BBPS Bill Fetch Response |

### Complex Types
#### `BillFetchResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Reason` | `reasonType` | Required |
| `Txn` | `txnType` | Required |
| `BillDetails` | `billDetailsType` | Required |
| `BillerResponse` | `billerResponseType` | Optional, unbounded |
| `BillerPlanResponse` | `billerPlanResponseType` | Optional |
| `AdditionalInfo` | `additionalInfoType` | Optional |
| `Upms` | `upmsType` | Optional |

---

## BillPaymentRequest
**File:** `BillPaymentRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillPaymentRequest` | `BillPaymentRequestType` | BBPS Bill Payment Request |

### Complex Types
#### `BillPaymentRequestType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Analytics` | `analyticsType` | Optional |
| `Txn` | `txnType` | Required |
| `Customer` | `customerDtlsType` | Required |
| `Agent` | `agentType` | Required |
| `BillDetails` | `billDetailsType` | Required |
| `BillerCategory` | `billerCategoryType` | Required |
| `PlanDetails` | `planDetailsType` | Optional |
| `BillerResponse` | `billerResponseType` | Optional, unbounded |
| `AdditionalInfo` | `additionalInfoType` | Optional |
| `PaymentMethod` | `pmtMtdType` | Required |
| `Amount` | `amountType` | Required |
| `PaymentInformation` | `pymntInfType` | Required |
| `Upms` | `upmsType` | Optional |

---

## BillPaymentResponse
**File:** `BillPaymentResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillPaymentResponse` | `BillPaymentResponseType` | BBPS Bill Fetch Response |

### Complex Types
#### `BillPaymentResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Reason` | `reasonType` | Required |
| `Txn` | `txnType` | Required |
| `BillDetails` | `billDetailsType` | Required |
| `BillerResponse` | `billerResponseType` | Optional, unbounded |
| `BillerPlanResponse` | `billerPlanResponseType` | Optional |
| `AdditionalInfo` | `additionalInfoType` | Optional |
| `Upms` | `upmsType` | Optional |

---

## BillPaymentResponseLite
**File:** `BillPaymentResponseLite.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillPaymentResponseLite` | `BillPaymentResponseTypeLite` | BBPS Bill Payment Response Lite |

### Complex Types
#### `BillPaymentResponseTypeLite`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `billerId` | `string` | required |
| `ts` | `string` | required |
| `refId` | `string` | required |
| `approvalRefNum` | `string` | required |
| `responseCode` | `string` | required |
| `responseReason` | `string` | required |
| `txnReferenceId` | `string` | optional |
| `directBillContentId` | `string` | optional |
| `paymentRefId` | `string` | optional |

---

## BillPaymentValidationResponse
**File:** `BillPaymentValidationResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillPaymentValidationResponse` | `BillPaymentValidationResponseType` | UPMS Bill Payment Validation Response |

### Complex Types
#### `BillPaymentValidationResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Reason` | `reasonType` | Required |
| `Upms` | `upmsType` | Required |

---

## BillRefNumberStatusUpdateRequest
**File:** `BillRefNumberStatusUpdateRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillRefNumberStatusUpdateRequest` | `BillRefNumberStatusUpdateRequestType` | BillReferenceNumber Update Request |

### Complex Types
#### `BillRefNumberStatusUpdateRequestType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Txn` | `txnType` | Required |
| `Reason` | `statusType` | Required |

#### `statusType`
*Extends:* `string`

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `responseCode` | `string` | required |
| `responseReason` | `string` | required |
| `complianceRespCd` | `string` | optional |
| `complianceReason` | `string` | optional |

---

## BillValidationRequest
**File:** `BillValidationRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillValidationRequest` | `BillValidationRequestType` | Bill Validation Request |

### Complex Types
#### `BillValidationRequestType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Agent` | `agentType` | Required |
| `BillDetails` | `billDetailsType` | Required |
| `Amount` | `amt` | Optional |
| `Upms` | `upmsType` | Optional |

---

## BillValidationResponse
**File:** `BillValidationResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillValidationResponse` | `BillValidationResponseType` | Bill Validation Response |

### Complex Types
#### `BillValidationResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Reason` | `reasonType` | Required |
| `BillerPlanResponse` | `billerPlanResponseType` | Optional |
| `AdditionalInfo` | `additionalInfoType` | Optional |
| `Upms` | `upmsType` | Optional |
| `CCF1` | `CCF1Type` | Optional |

---

## BulkAck
**File:** `BulkAck.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `Ack.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BulkAck` | `BulkAckType` | BBPS Bulk Ack |

### Complex Types
#### `BulkAckType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `ParentRefId` | `string` | Required |
| `OuId` | `string` | Optional |
| `Api` | `string` | Optional |
| `Ts` | `string` | Required |
| `RspCd` | `string` | Required |
| `Acks` | `Ack` | Required, unbounded |

---

## BulkBillPaymentRequest
**File:** `BulkBillPaymentRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BulkBillPaymentRequest` | `BulkBillPaymentRequestType` | UPMS Bulk Bill Payment Request |

### Complex Types
#### `BulkBillPaymentRequestType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `ParentRefId` | `string` | Required |
| `OrigInstId` | `string` | Optional |
| `Ts` | `string` | Required |
| `BillPaymentRequest` | `BillPaymentBulkRequestType` | Required, unbounded |

#### `BillPaymentBulkRequestType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Analytics` | `analyticsType` | Optional |
| `Txn` | `txnType` | Required |
| `Customer` | `customerDtlsType` | Required |
| `Agent` | `agentType` | Required |
| `BillDetails` | `billDetailsType` | Required |
| `BillerResponse` | `billerResponseType` | Optional |
| `AdditionalInfo` | `additionalInfoType` | Optional |
| `PaymentMethod` | `pmtMtdType` | Required |
| `Amount` | `amountType` | Required |
| `PaymentInformation` | `pymntInfType` | Required |
| `Upms` | `upmsType` | Optional |

---

## BulkBillPaymentResponse
**File:** `BulkBillPaymentResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BulkBillPaymentResponse` | `BulkBillPaymentResponseType` | UPMS Bulk Bill Payment Response |

### Complex Types
#### `BulkBillPaymentResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `ParentRefId` | `string` | Required |
| `OrigInstId` | `string` | Optional |
| `Ts` | `string` | Required |
| `BillPaymentResponse` | `BillPaymentBulkResponseType` | Required, unbounded |

#### `BillPaymentBulkResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Reason` | `reasonType` | Required |
| `Txn` | `txnType` | Required |
| `BillDetails` | `billDetailsType` | Required |
| `BillerResponse` | `billerResponseType` | Required |
| `AdditionalInfo` | `additionalInfoType` | Optional |
| `Upms` | `upmsType` | Optional |

---

## BulkBillPresentmentConfirmation
**File:** `BulkBillPresentmentConfirmation.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillPresentmentConfirmations` | `BillPresentmentConfirmationsType` | BBPS Bulk Bill Presentment Confirmations |
| `BillPresentmentConfirmation` | `BillPresentmentConfirmationType` | Bill Presentment Confirmation |

### Complex Types
#### `BillPresentmentConfirmationsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `billPresentmentConfirmation` | `BillPresentmentConfirmationType` | Required, unbounded |

#### `BillPresentmentConfirmationType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Reason` | `reasonType` | Required |
| `Txn` | `txnType` | Required |

---

## BulkBillPresentmentRequest
**File:** `BulkBillPresentmentRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BillFetchRequest.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillPresentmentRequests` | `BillFetchRequestsType` | BBPS Bulk Bill Presentment Requests |

### Complex Types
#### `BillFetchRequestsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `billPresentmentRequest` | `BillFetchRequestType` | Required, unbounded |

---

## BulkBillPresentmentResponse
**File:** `BulkBillPresentmentResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BillFetchResponse.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `BillPresentmentResponses` | `BillFetchResponsesType` | BBPS Bulk Bill Presentment Responses |

### Complex Types
#### `BillFetchResponsesType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `billPresentmentResponse` | `BillFetchResponseType` | Required, unbounded |

---

## EditNotificationRequest
**File:** `EditNotificationRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `AlertNotificationDetail.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `EditNotificationRequest` | — | — |

---

## EditNotificationResponse
**File:** `EditNotificationResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `AlertNotificationDetail.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `EditNotificationResponse` | — | — |

---

## EFRMResponse
**File:** `EFRMResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `EFRMResponse` | `EFRMResponseType` | BBPS EFRM Response |

### Complex Types
#### `EFRMResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Txn` | `txnType` | Required |

---

## GenerateBillRefNumberRequest
**File:** `GenerateBillRefNumberRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `GenerateBillRefNumberRequest` | `GenerateBillRefNumberRequestType` | Generate BillRefNumber Request |

### Complex Types
#### `GenerateBillRefNumberRequestType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Analytics` | `analyticsType` | Optional |
| `Txn` | `txnType` | Required |
| `Customer` | `customerDtlsType` | Required |
| `Agent` | `agentType` | Required |
| `BillDetails` | `billDetailsType` | Required |
| `PlanDetails` | `planDetailsType` | Optional |
| `BillerResponse` | `billerResponseType` | Optional, unbounded |
| `AdditionalInfo` | `additionalInfoType` | Optional |
| `PaymentMethod` | `pmtMtdType` | Required |
| `Amount` | `amountType` | Required |
| `PaymentInformation` | `pymntInfType` | Required |
| `Upms` | `upmsType` | Optional |

---

## NewNotificationRequest
**File:** `NewNotificationRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `AlertNotificationDetail.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `NewNotificationRequest` | — | — |

---

## NewNotificationResponse
**File:** `NewNotificationResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `AlertNotificationDetail.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `NewNotificationResponse` | — | — |

---

## NotificationExceptionResponse
**File:** `NotificationExceptionResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `NotificationExceptionResponse` | — | — |

### Complex Types
#### `ValidationError`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `fieldName` | `string` | Optional |
| `errorMessage` | `string` | Optional |

---

## ReqDiagnostic
**File:** `ReqDiagnostic.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `ReqDiagnostic` | `ReqDiagnosticType` | BBPS Diagnostic Request |

### Complex Types
#### `ReqDiagnosticType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `mediaType` | `string` | Optional |

---

## ResDiagnostic
**File:** `ResDiagnostic.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `ResDiagnostic` | `ResDiagnosticType` | BBPS Diagnostic Response |

### Complex Types
#### `ResDiagnosticType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `errorMessages` | `errorMessage` | Optional, unbounded |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `responseReason` | `string` | optional |

---

## SearchNotificationRequest
**File:** `SearchNotificationRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `AlertNotificationDetail.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `SearchNotificationRequest` | — | — |

### Simple Types (Enumerations)
#### `NotificationOrderDirection`
*Base type:* `string`

**Allowed values:**

- `ASC`
- `DESC`

#### `NotificationOrderBy`
*Base type:* `string`

**Allowed values:**

- `notification_type`
- `user_type`
- `severity`

---

## SearchNotificationResponse
**File:** `SearchNotificationResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `AlertNotificationDetail.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `SearchNotificationResponse` | — | — |

---

## SwitchoverRequest
**File:** `SwitchoverRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `SwitchoverRequest` | `SwitchoverRequestType` | Switchover Request |

### Complex Types
#### `SwitchoverRequestType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `SwitchoverDetails` | `SwitchoverDetailsType` | Required |

#### `SwitchoverDetailsType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `isPlanned` | `string` | Required |
| `ouEndPointURL` | `string` | Required |
| `description` | `string` | Required |

---

## SwitchoverResponse
**File:** `SwitchoverResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `SwitchoverResponse` | `SwitchoverResponseType` | Switchover Response |

### Complex Types
#### `SwitchoverResponseType`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Reason` | `reasonType` | Required |

---

## TicketRequest
**File:** `TicketRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `TicketRequest` | `TicketRequest` | BBPS API request |

### Complex Types
#### `TicketRequest`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Txn` | `txnType` | Required |
| `TicketReq` | `TicketReq` | Required |

#### `TicketReq`
*Extends:* `string`

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `ticketId` | `string` | optional |
| `txnReferenceId` | `string` | optional |
| `disposition` | `string` | optional |
| `description` | `string` | optional |
| `remarks` | `string` | optional |
| `customerMobile` | `string` | optional |
| `customerName` | `string` | optional |
| `customerEmailId` | `string` | optional |
| `ticketTat` | `string` | optional |

---

## TicketResponse
**File:** `TicketResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `TicketResponse` | `TicketResponse` | BBPS API response |

### Complex Types
#### `TicketResponse`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Txn` | `txnType` | Required |
| `TicketResp` | `TicketResp` | Required |

#### `TicketResp`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `ticketId` | `string` | optional |
| `description` | `string` | optional |
| `ticketStatus` | `string` | optional |
| `assigned` | `string` | optional |
| `responseCode` | `string` | optional |
| `responseReason` | `string` | optional |
| `remarks` | `string` | optional |
| `txnReferenceId` | `string` | optional |
| `disposition` | `string` | optional |
| `ticketType` | `string` | optional |

---

## TxnStatusComplainRequest
**File:** `TxnStatusComplainRequest.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `TxnStatusComplainRequest` | `TxnStatusComplainRequest` | BBPS API request |

### Complex Types
#### `TxnStatusComplainRequest`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Txn` | `txnType` | Required |
| `TxnStatusComplainReq` | `TxnStatusComplainReq` | Required |
| `TxnSearchDateCriteria` | `TxnSearchDateCriteria` | Optional |

#### `TxnStatusComplainReq`
*Extends:* `string`

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `msgId` | `string` | required |
| `complaintId` | `string` | optional |
| `servReason` | `string` | optional |
| `participationType` | `string` | optional |
| `agentId` | `string` | optional |
| `billerId` | `string` | optional |
| `mobile` | `string` | optional |
| `txnReferenceId` | `string` | optional |
| `ticketId` | `string` | optional |
| `category` | `string` | optional |
| `disposition` | `string` | optional |
| `description` | `string` | optional |
| `complaintType` | `string` | optional |
| `complaintStatus` | `string` | optional |
| `assigned` | `string` | optional |
| `superLevelEsc` | `string` | optional |
| `estimatedTAT` | `string` | optional |
| `reopenComplaint` | `string` | optional |

#### `TxnSearchDateCriteria`
*Extends:* `string`

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `fromDate` | `string` | required |
| `toDate` | `string` | required |

---

## TxnStatusComplainResponse
**File:** `TxnStatusComplainResponse.xsd`  
**Namespace:** `http://bbps.org/schema`  
**Includes:** `BBPS-Common.xsd`  

### Root Elements
| Element | Type | Description |
| --- | --- | --- |
| `TxnStatusComplainResponse` | `TxnStatusComplainResponse` | BBPS API response |

### Complex Types
#### `TxnStatusComplainResponse`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `Head` | `headType` | Required |
| `Txn` | `txnType` | Required |
| `TxnStatusComplainResp` | `TxnStatusComplainResp` | Required |

#### `TxnStatusComplainResp`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `TxnList` | `TxnList` | Required |
| `Tickets` | `Tickets` | Optional |
| `CreditAdjustments` | `CreditAdjustments` | Optional |
| `ForcePaymentDetails` | `ForcePaymentDetails` | Optional |
| `CustomerDetails` | `CustomerDetails` | Optional |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `msgId` | `string` | required |
| `complaintId` | `string` | optional |
| `description` | `string` | optional |
| `openComplaint` | `string` | optional |
| `reopenComplaint` | `string` | optional |
| `complaintStatus` | `string` | optional |
| `assigned` | `string` | optional |
| `responseCode` | `string` | optional |
| `responseReason` | `string` | optional |
| `remarks` | `string` | optional |
| `txnReferenceId` | `string` | optional |
| `disposition` | `string` | optional |
| `description` | `string` | optional |
| `billNumber` | `string` | optional |

#### `TxnList`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `TxnDetail` | `TxnDetail` | Optional, unbounded |

#### `TxnDetail`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `AdditionalInfo` | `additionalInfoType` | Optional |

**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `agentId` | `string` | optional |
| `billerId` | `string` | optional |
| `refId` | `string` | optional |
| `approvalRefNum` | `string` | optional |
| `txnReferenceId` | `string` | required |
| `mti` | `string` | optional |
| `txnDate` | `string` | required |
| `amount` | `string` | required |
| `txnStatus` | `string` | required |
| `complianceRespCd` | `string` | optional |
| `complianceReason` | `string` | optional |
| `disputeId` | `string` | optional |
| `disputeDate` | `string` | optional |
| `disputeType` | `string` | optional |
| `disputeStatus` | `string` | optional |
| `disputeAmount` | `string` | optional |
| `caId` | `string` | optional |
| `caDate` | `string` | optional |
| `caStatus` | `string` | optional |
| `caAmount` | `string` | optional |
| `caPenalty` | `string` | optional |
| `fpId` | `string` | optional |
| `fpInitiatedDate` | `string` | optional |
| `fpStatus` | `string` | optional |
| `fpClosureDate` | `string` | optional |
| `refundedTxnAmount` | `string` | optional |
| `paymentRefId` | `string` | optional |
| `billNumber` | `string` | optional |

#### `Tickets`
**Child Elements:**

| Element | Type | Occurrence |
| --- | --- | --- |
| `TicketDetails` | `TicketDetails` | Optional, unbounded |

#### `TicketDetails`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `ticketId` | `string` | optional |
| `ticketType` | `string` | optional |
| `description` | `string` | optional |
| `ticketStatus` | `string` | optional |
| `ticketInitiatedDate` | `string` | optional |
| `ticketClosureDate` | `string` | optional |
| `ticketEstimatedTat` | `string` | optional |
| `refundedTxnAmount` | `string` | optional |
| `refundedPenaltyAmount` | `string` | optional |

#### `CreditAdjustments`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `caId` | `string` | optional |
| `caInitiatedDate` | `string` | optional |
| `caStatus` | `string` | optional |
| `caClosureDate` | `string` | optional |
| `refundedTxnAmount` | `string` | optional |
| `refundedPenaltyAmount` | `string` | optional |

#### `ForcePaymentDetails`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `fpId` | `string` | optional |
| `fpInitiatedDate` | `string` | optional |
| `fpStatus` | `string` | optional |
| `fpClosureDate` | `string` | optional |
| `refundedTxnAmount` | `string` | optional |

#### `CustomerDetails`
**Attributes:**

| Attribute | Type | Use |
| --- | --- | --- |
| `mobile` | `string` | required |

---
