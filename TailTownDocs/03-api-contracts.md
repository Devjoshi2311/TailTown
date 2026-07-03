# REST API Contracts

## API Standards

Base path:

- Production: `/api/v1`
- Current backend compatibility path: `/api`

Content type:

- Request: `application/json`
- Response: `application/json`

Authentication:

- Customer endpoints require `Authorization: Bearer <accessToken>`.
- Public endpoints explicitly say `Authentication: Public`.
- Admin-only endpoints are not included in this contract.

Standard response envelope:

```yaml
ApiResponse:
  type: object
  required: [success]
  properties:
    success:
      type: boolean
    message:
      type: string
      nullable: true
    data:
      nullable: true
```

Standard error response:

```yaml
ErrorResponse:
  type: object
  required: [success, message]
  properties:
    success:
      type: boolean
      example: false
    message:
      type: string
      example: Validation failed
    errorCode:
      type: string
      example: VALIDATION_ERROR
    fieldErrors:
      type: object
      additionalProperties:
        type: string
      nullable: true
    requestId:
      type: string
      example: req_01JZ8E6K9SZ8F5XQ9W2T4X9S4Z
```

Standard errors:

- `400 Bad Request`: malformed request, invalid state transition, invalid parameters.
- `401 Unauthorized`: missing, expired, or invalid token.
- `403 Forbidden`: authenticated but not allowed to access the resource.
- `404 Not Found`: resource does not exist or is not visible to caller.
- `409 Conflict`: duplicate resource, optimistic lock conflict, idempotency conflict, slot conflict.
- `422 Unprocessable Entity`: valid JSON but business validation failed.
- `429 Too Many Requests`: rate limit exceeded.
- `500 Internal Server Error`: unexpected server failure.

Common headers:

```yaml
X-Request-Id:
  in: header
  schema:
    type: string
  required: false

Idempotency-Key:
  in: header
  schema:
    type: string
    minLength: 16
    maxLength: 128
  required: false
  description: Required for booking creation, checkout/order creation, subscription creation, and payment-sensitive actions.
```

Pagination:

```yaml
PageMeta:
  type: object
  properties:
    page:
      type: integer
      example: 0
    size:
      type: integer
      example: 20
    totalElements:
      type: integer
      example: 145
    totalPages:
      type: integer
      example: 8
```

## Auth

### POST `/auth/register`

Authentication: Public

Request DTO:

```yaml
RegisterRequest:
  type: object
  required: [email, password, name]
  properties:
    email:
      type: string
      format: email
      example: maya@example.com
    password:
      type: string
      format: password
      minLength: 10
      example: Str0ngPass!23
    name:
      type: string
      minLength: 2
      maxLength: 160
      example: Maya Rao
    referralCode:
      type: string
      nullable: true
      example: TAIL9F3A
```

Response DTO:

```yaml
AuthResponse:
  type: object
  properties:
    accessToken:
      type: string
    refreshToken:
      type: string
    expiresIn:
      type: integer
      example: 900
    refreshExpiresIn:
      type: integer
      example: 2592000
    user:
      $ref: '#/components/schemas/UserProfile'
```

Validation rules:

- `email` must be valid and unique among active users.
- `password` must be at least 10 characters and include sufficient complexity.
- `name` must be 2-160 characters.
- `referralCode`, when provided, must exist and must not belong to the registering user/device.

Error responses:

- `400 VALIDATION_ERROR`
- `409 EMAIL_ALREADY_REGISTERED`
- `422 INVALID_REFERRAL_CODE`
- `429 RATE_LIMITED`

Example:

```yaml
request:
  email: maya@example.com
  password: Str0ngPass!23
  name: Maya Rao
  referralCode: TAIL9F3A
response:
  success: true
  data:
    accessToken: eyJhbGciOi...
    refreshToken: rft_...
    expiresIn: 900
    refreshExpiresIn: 2592000
    user:
      id: 5c047a34-6c10-4f93-9f3c-f901ab1ee1b5
      email: maya@example.com
      name: Maya Rao
      phone: null
      avatarUrl: null
      referralCode: TAIL7K2Q
```

### POST `/auth/login`

Authentication: Public

Request DTO:

```yaml
LoginRequest:
  type: object
  required: [email, password]
  properties:
    email:
      type: string
      format: email
    password:
      type: string
      format: password
```

Response DTO:

- `AuthResponse`

Validation rules:

- `email` must be valid.
- `password` must be non-blank.
- Account must be active.

Error responses:

- `400 VALIDATION_ERROR`
- `401 INVALID_CREDENTIALS`
- `403 ACCOUNT_BLOCKED`
- `429 RATE_LIMITED`

### POST `/auth/refresh`

Authentication: Public

Request DTO:

```yaml
RefreshTokenRequest:
  type: object
  required: [refreshToken]
  properties:
    refreshToken:
      type: string
```

Response DTO:

- `AuthResponse`

Validation rules:

- Refresh token must be valid, unexpired, not revoked, and match a known device session.
- Refresh token rotation must revoke the previous refresh token.

Error responses:

- `400 INVALID_REFRESH_TOKEN`
- `401 REFRESH_TOKEN_EXPIRED`
- `409 REFRESH_TOKEN_REPLAY_DETECTED`

### POST `/auth/firebase`

Authentication: Public

Request DTO:

```yaml
FirebaseAuthRequest:
  type: object
  required: [idToken]
  properties:
    idToken:
      type: string
    displayName:
      type: string
      nullable: true
```

Response DTO:

- `AuthResponse`

Validation rules:

- `idToken` must verify against Firebase Admin SDK.
- Firebase subject must be linked to one TailTown user.

Error responses:

- `400 VALIDATION_ERROR`
- `401 INVALID_FIREBASE_TOKEN`
- `409 IDENTITY_ALREADY_LINKED`

### POST `/auth/logout`

Authentication: Customer

Request DTO:

```yaml
LogoutRequest:
  type: object
  properties:
    refreshToken:
      type: string
      nullable: true
    allDevices:
      type: boolean
      default: false
```

Response DTO:

```yaml
EmptyResponse:
  type: object
  nullable: true
```

Validation rules:

- Current access token must be valid.
- If `allDevices=true`, revoke all refresh tokens for the user.

Error responses:

- `401 UNAUTHORIZED`

## Profile

### GET `/profile/me`

Authentication: Customer

Request DTO:

- None

Response DTO:

```yaml
UserProfile:
  type: object
  properties:
    id:
      type: string
      format: uuid
    email:
      type: string
      format: email
    phone:
      type: string
      nullable: true
    name:
      type: string
    avatarUrl:
      type: string
      nullable: true
    referralCode:
      type: string
    emailVerified:
      type: boolean
    phoneVerified:
      type: boolean
```

Validation rules:

- Caller can only read their own profile.

Error responses:

- `401 UNAUTHORIZED`
- `404 USER_NOT_FOUND`

### PATCH `/profile/me`

Authentication: Customer

Request DTO:

```yaml
UpdateProfileRequest:
  type: object
  properties:
    name:
      type: string
      minLength: 2
      maxLength: 160
    phone:
      type: string
      pattern: '^\\+?[1-9][0-9]{7,14}$'
      nullable: true
    avatarUrl:
      type: string
      format: uri
      nullable: true
    version:
      type: integer
      format: int64
```

Response DTO:

- `UserProfile`

Validation rules:

- At least one mutable field must be present.
- `phone` must be unique if provided.
- `version` must match current profile version when provided.

Error responses:

- `400 VALIDATION_ERROR`
- `401 UNAUTHORIZED`
- `409 VERSION_CONFLICT`
- `409 PHONE_ALREADY_USED`

### GET `/profile/addresses`

Authentication: Customer

Request DTO:

- None

Response DTO:

```yaml
Address:
  type: object
  properties:
    id:
      type: string
      format: uuid
    label:
      type: string
    recipientName:
      type: string
      nullable: true
    phone:
      type: string
      nullable: true
    line1:
      type: string
    line2:
      type: string
      nullable: true
    landmark:
      type: string
      nullable: true
    city:
      type: string
    state:
      type: string
    pincode:
      type: string
    country:
      type: string
      example: IN
    latitude:
      type: number
      nullable: true
    longitude:
      type: number
      nullable: true
    isDefault:
      type: boolean
    version:
      type: integer
      format: int64
```

Validation rules:

- Return only active addresses owned by caller.

Error responses:

- `401 UNAUTHORIZED`

### POST `/profile/addresses`

Authentication: Customer

Request DTO:

```yaml
AddressRequest:
  type: object
  required: [label, line1, city, state, pincode]
  properties:
    label:
      type: string
      minLength: 1
      maxLength: 80
    recipientName:
      type: string
      maxLength: 160
      nullable: true
    phone:
      type: string
      nullable: true
    line1:
      type: string
      minLength: 3
    line2:
      type: string
      nullable: true
    landmark:
      type: string
      nullable: true
    city:
      type: string
      minLength: 2
    state:
      type: string
      minLength: 2
    pincode:
      type: string
      pattern: '^[0-9]{6}$'
    country:
      type: string
      default: IN
    latitude:
      type: number
      minimum: -90
      maximum: 90
      nullable: true
    longitude:
      type: number
      minimum: -180
      maximum: 180
      nullable: true
    isDefault:
      type: boolean
      default: false
```

Response DTO:

- `Address`

Validation rules:

- Label must be unique for the user among active addresses.
- Only one default address may exist per user.
- Pincode must be serviceable for delivery-dependent flows.

Error responses:

- `400 VALIDATION_ERROR`
- `401 UNAUTHORIZED`
- `409 ADDRESS_LABEL_EXISTS`
- `422 UNSERVICEABLE_PINCODE`

### PATCH `/profile/addresses/{addressId}`

Authentication: Customer

Request DTO:

- `AddressRequest` plus required `version`.

Response DTO:

- `Address`

Validation rules:

- Address must belong to caller.
- `version` must match current address version.

Error responses:

- `401 UNAUTHORIZED`
- `403 FORBIDDEN`
- `404 ADDRESS_NOT_FOUND`
- `409 VERSION_CONFLICT`

### DELETE `/profile/addresses/{addressId}`

Authentication: Customer

Request DTO:

- None

Response DTO:

- `EmptyResponse`

Validation rules:

- Address must belong to caller.
- Cannot delete address if it is the active address on an active subscription unless replacement is provided.

Error responses:

- `401 UNAUTHORIZED`
- `403 FORBIDDEN`
- `404 ADDRESS_NOT_FOUND`
- `422 ADDRESS_IN_USE`

## Pets

### GET `/pets`

Authentication: Customer

Request DTO:

- Query: `includeDeleted=false`

Response DTO:

```yaml
Pet:
  type: object
  properties:
    id:
      type: string
      format: uuid
    name:
      type: string
    species:
      type: string
      example: dog
    breed:
      type: string
      nullable: true
    gender:
      type: string
      enum: [MALE, FEMALE, UNKNOWN]
    dateOfBirth:
      type: string
      format: date
      nullable: true
    age:
      type: integer
      nullable: true
    weightKg:
      type: number
      nullable: true
    avatarUrl:
      type: string
      nullable: true
    microchipId:
      type: string
      nullable: true
    allergies:
      type: string
      nullable: true
    medicalNotes:
      type: string
      nullable: true
    version:
      type: integer
      format: int64
```

Validation rules:

- Return only caller-owned pets.

Error responses:

- `401 UNAUTHORIZED`

### POST `/pets`

Authentication: Customer

Request DTO:

```yaml
CreatePetRequest:
  type: object
  required: [name, species]
  properties:
    name:
      type: string
      minLength: 1
      maxLength: 120
    species:
      type: string
      enum: [dog, cat, bird, rabbit, other]
    breed:
      type: string
      maxLength: 120
      nullable: true
    gender:
      type: string
      enum: [MALE, FEMALE, UNKNOWN]
      default: UNKNOWN
    dateOfBirth:
      type: string
      format: date
      nullable: true
    weightKg:
      type: number
      minimum: 0.1
      maximum: 200
      nullable: true
    microchipId:
      type: string
      nullable: true
```

Response DTO:

- `Pet`

Validation rules:

- `dateOfBirth` cannot be in the future.
- `microchipId` must be unique if provided.
- Max pets per user may be enforced by plan or abuse controls.

Error responses:

- `400 VALIDATION_ERROR`
- `401 UNAUTHORIZED`
- `409 MICROCHIP_ALREADY_EXISTS`
- `422 PET_LIMIT_REACHED`

### GET `/pets/{petId}`

Authentication: Customer

Request DTO:

- Path: `petId uuid`

Response DTO:

- `Pet`

Validation rules:

- Pet must belong to caller.

Error responses:

- `401 UNAUTHORIZED`
- `403 FORBIDDEN`
- `404 PET_NOT_FOUND`

### PATCH `/pets/{petId}`

Authentication: Customer

Request DTO:

```yaml
UpdatePetRequest:
  type: object
  required: [version]
  properties:
    name:
      type: string
      minLength: 1
      maxLength: 120
    species:
      type: string
    breed:
      type: string
      nullable: true
    gender:
      type: string
      enum: [MALE, FEMALE, UNKNOWN]
    dateOfBirth:
      type: string
      format: date
      nullable: true
    weightKg:
      type: number
      minimum: 0.1
      maximum: 200
    avatarUrl:
      type: string
      nullable: true
    allergies:
      type: string
      nullable: true
    medicalNotes:
      type: string
      nullable: true
    version:
      type: integer
      format: int64
```

Response DTO:

- `Pet`

Validation rules:

- Pet must belong to caller.
- `version` must match.

Error responses:

- `400 VALIDATION_ERROR`
- `403 FORBIDDEN`
- `404 PET_NOT_FOUND`
- `409 VERSION_CONFLICT`

### DELETE `/pets/{petId}`

Authentication: Customer

Request DTO:

- None

Response DTO:

- `EmptyResponse`

Validation rules:

- Pet must belong to caller.
- Active bookings may block deletion.

Error responses:

- `403 FORBIDDEN`
- `404 PET_NOT_FOUND`
- `422 PET_HAS_ACTIVE_BOOKINGS`

### GET `/pets/{petId}/weight-records`

Authentication: Customer

Request DTO:

- Query: `from date`, `to date`

Response DTO:

```yaml
WeightRecord:
  type: object
  properties:
    id:
      type: string
      format: uuid
    petId:
      type: string
      format: uuid
    weightKg:
      type: number
    recordedOn:
      type: string
      format: date
    source:
      type: string
      enum: [USER, VET, IMPORT]
    notes:
      type: string
      nullable: true
    version:
      type: integer
      format: int64
```

Validation rules:

- Pet must belong to caller.
- Date range cannot exceed 5 years.

Error responses:

- `403 FORBIDDEN`
- `404 PET_NOT_FOUND`

### POST `/pets/{petId}/weight-records`

Authentication: Customer

Request DTO:

```yaml
CreateWeightRecordRequest:
  type: object
  required: [weightKg, recordedOn]
  properties:
    weightKg:
      type: number
      minimum: 0.1
      maximum: 200
    recordedOn:
      type: string
      format: date
    notes:
      type: string
      nullable: true
```

Response DTO:

- `WeightRecord`

Validation rules:

- Pet must belong to caller.
- `recordedOn` cannot be in the future.
- One active weight record per pet per date.

Error responses:

- `400 VALIDATION_ERROR`
- `403 FORBIDDEN`
- `409 WEIGHT_RECORD_EXISTS`

### GET `/pets/{petId}/prescriptions`

Authentication: Customer

Request DTO:

- Query: `status`, `page`, `size`

Response DTO:

```yaml
Prescription:
  type: object
  properties:
    id:
      type: string
      format: uuid
    petId:
      type: string
      format: uuid
    vetId:
      type: string
      format: uuid
      nullable: true
    medicationName:
      type: string
    dosage:
      type: string
    frequency:
      type: string
    instructions:
      type: string
      nullable: true
    startDate:
      type: string
      format: date
    endDate:
      type: string
      format: date
      nullable: true
    status:
      type: string
      enum: [ACTIVE, COMPLETED, CANCELLED]
    documentUrl:
      type: string
      nullable: true
    version:
      type: integer
      format: int64
```

Validation rules:

- Pet must belong to caller.

Error responses:

- `403 FORBIDDEN`
- `404 PET_NOT_FOUND`

### POST `/pets/{petId}/prescriptions`

Authentication: Customer

Request DTO:

```yaml
CreatePrescriptionRequest:
  type: object
  required: [medicationName, dosage, frequency, startDate]
  properties:
    medicationName:
      type: string
      minLength: 1
      maxLength: 180
    dosage:
      type: string
      minLength: 1
      maxLength: 120
    frequency:
      type: string
      minLength: 1
      maxLength: 120
    instructions:
      type: string
      nullable: true
    startDate:
      type: string
      format: date
    endDate:
      type: string
      format: date
      nullable: true
    documentUrl:
      type: string
      nullable: true
```

Response DTO:

- `Prescription`

Validation rules:

- Pet must belong to caller.
- `endDate` must be greater than or equal to `startDate`.

Error responses:

- `400 VALIDATION_ERROR`
- `403 FORBIDDEN`
- `404 PET_NOT_FOUND`

### POST `/prescriptions/{prescriptionId}/doses`

Authentication: Customer

Request DTO:

```yaml
MarkDoseRequest:
  type: object
  properties:
    takenAt:
      type: string
      format: date-time
      nullable: true
    note:
      type: string
      nullable: true
```

Response DTO:

```yaml
DoseLog:
  type: object
  properties:
    id:
      type: string
      format: uuid
    prescriptionId:
      type: string
      format: uuid
    takenAt:
      type: string
      format: date-time
    note:
      type: string
```

Validation rules:

- Prescription must belong to caller through the pet.
- `takenAt` cannot be far in the future.

Error responses:

- `403 FORBIDDEN`
- `404 PRESCRIPTION_NOT_FOUND`

### GET `/pets/{petId}/vaccines`

Authentication: Customer

Request DTO:

- Query: `status`

Response DTO:

```yaml
Vaccine:
  type: object
  properties:
    id:
      type: string
      format: uuid
    petId:
      type: string
      format: uuid
    vaccineName:
      type: string
    doseLabel:
      type: string
      nullable: true
    dueDate:
      type: string
      format: date
      nullable: true
    administeredDate:
      type: string
      format: date
      nullable: true
    status:
      type: string
      enum: [DUE, UPCOMING, COMPLETED, OVERDUE, SKIPPED]
    providerName:
      type: string
      nullable: true
    certificateUrl:
      type: string
      nullable: true
    version:
      type: integer
      format: int64
```

Validation rules:

- Pet must belong to caller.

Error responses:

- `403 FORBIDDEN`
- `404 PET_NOT_FOUND`

### POST `/pets/{petId}/vaccines`

Authentication: Customer

Request DTO:

```yaml
CreateVaccineRequest:
  type: object
  required: [vaccineName]
  properties:
    vaccineName:
      type: string
      minLength: 1
      maxLength: 180
    doseLabel:
      type: string
      nullable: true
    dueDate:
      type: string
      format: date
      nullable: true
    administeredDate:
      type: string
      format: date
      nullable: true
    status:
      type: string
      enum: [DUE, UPCOMING, COMPLETED, OVERDUE, SKIPPED]
      default: DUE
    providerName:
      type: string
      nullable: true
    certificateUrl:
      type: string
      nullable: true
    notes:
      type: string
      nullable: true
```

Response DTO:

- `Vaccine`

Validation rules:

- Pet must belong to caller.
- Completed vaccines require `administeredDate`.

Error responses:

- `400 VALIDATION_ERROR`
- `403 FORBIDDEN`
- `409 VACCINE_ALREADY_EXISTS`

## Vets

### GET `/vets`

Authentication: Customer

Request DTO:

```yaml
VetSearchQuery:
  type: object
  properties:
    city:
      type: string
    specialty:
      type: string
    serviceType:
      type: string
    homeVisit:
      type: boolean
    minRating:
      type: number
      minimum: 0
      maximum: 5
    latitude:
      type: number
    longitude:
      type: number
    page:
      type: integer
      default: 0
    size:
      type: integer
      default: 20
      maximum: 100
```

Response DTO:

```yaml
Vet:
  type: object
  properties:
    id:
      type: string
      format: uuid
    displayName:
      type: string
    specialty:
      type: string
    bio:
      type: string
      nullable: true
    avatarUrl:
      type: string
      nullable: true
    rating:
      type: number
    reviewCount:
      type: integer
    yearsExperience:
      type: integer
    homeVisitAvailable:
      type: boolean
    clinicName:
      type: string
      nullable: true
    city:
      type: string
      nullable: true
    state:
      type: string
      nullable: true
    pincode:
      type: string
      nullable: true
```

Validation rules:

- `size` max 100.
- `latitude` and `longitude` must be supplied together.

Error responses:

- `400 VALIDATION_ERROR`
- `401 UNAUTHORIZED`

### GET `/vets/{vetId}`

Authentication: Customer

Request DTO:

- Path: `vetId uuid`

Response DTO:

- `Vet` plus certifications, services, availability summary.

Validation rules:

- Vet must be active or visible to caller by existing booking context.

Error responses:

- `404 VET_NOT_FOUND`

### GET `/vets/{vetId}/slots`

Authentication: Customer

Request DTO:

- Query: `from date-time`, `to date-time`, `serviceType`

Response DTO:

```yaml
BookingSlot:
  type: object
  properties:
    id:
      type: string
      format: uuid
    vetId:
      type: string
      format: uuid
    serviceType:
      type: string
    startsAt:
      type: string
      format: date-time
    endsAt:
      type: string
      format: date-time
    status:
      type: string
      enum: [AVAILABLE, HELD, BOOKED, CANCELLED]
    price:
      type: number
```

Validation rules:

- `to` must be after `from`.
- Range cannot exceed 31 days.
- Only available slots should be returned to customers by default.

Error responses:

- `400 VALIDATION_ERROR`
- `404 VET_NOT_FOUND`

## Bookings

### GET `/bookings`

Authentication: Customer

Request DTO:

- Query: `status`, `from`, `to`, `page`, `size`

Response DTO:

```yaml
Booking:
  type: object
  properties:
    id:
      type: string
      format: uuid
    userId:
      type: string
      format: uuid
    pet:
      $ref: '#/components/schemas/PetSummary'
    vet:
      $ref: '#/components/schemas/VetSummary'
    slotId:
      type: string
      format: uuid
    serviceType:
      type: string
    visitType:
      type: string
      enum: [CLINIC, HOME, VIDEO]
    scheduledStart:
      type: string
      format: date-time
    scheduledEnd:
      type: string
      format: date-time
    status:
      type: string
      enum: [PENDING_PAYMENT, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW, REFUNDED]
    addressSnapshot:
      type: string
      nullable: true
    notes:
      type: string
      nullable: true
    version:
      type: integer
      format: int64
```

Validation rules:

- Return only bookings owned by caller.

Error responses:

- `401 UNAUTHORIZED`

### POST `/bookings`

Authentication: Customer

Headers:

- `Idempotency-Key`: required

Request DTO:

```yaml
CreateBookingRequest:
  type: object
  required: [petId, vetId, slotId, serviceType, visitType]
  properties:
    petId:
      type: string
      format: uuid
    vetId:
      type: string
      format: uuid
    slotId:
      type: string
      format: uuid
    serviceType:
      type: string
      enum: [VET_VISIT, GROOMING, TRAINING, BOARDING]
    visitType:
      type: string
      enum: [CLINIC, HOME, VIDEO]
    addressId:
      type: string
      format: uuid
      nullable: true
    notes:
      type: string
      maxLength: 1000
      nullable: true
```

Response DTO:

- `Booking`

Validation rules:

- Pet must belong to caller.
- Vet must be active.
- Slot must belong to vet, match service type, and be available or held by caller.
- Home visit requires address.
- Slot cannot be double-booked.
- Idempotency key must be unique per user/action.

Error responses:

- `400 VALIDATION_ERROR`
- `403 PET_NOT_OWNED`
- `404 SLOT_NOT_FOUND`
- `409 SLOT_UNAVAILABLE`
- `409 IDEMPOTENCY_CONFLICT`
- `422 ADDRESS_REQUIRED`

### GET `/bookings/{bookingId}`

Authentication: Customer

Request DTO:

- Path: `bookingId uuid`

Response DTO:

- `Booking`

Validation rules:

- Booking must belong to caller.

Error responses:

- `403 FORBIDDEN`
- `404 BOOKING_NOT_FOUND`

### PATCH `/bookings/{bookingId}/cancel`

Authentication: Customer

Request DTO:

```yaml
CancelBookingRequest:
  type: object
  required: [reason, version]
  properties:
    reason:
      type: string
      minLength: 3
      maxLength: 500
    version:
      type: integer
      format: int64
```

Response DTO:

- `Booking`

Validation rules:

- Booking must belong to caller.
- Booking must be cancellable.
- Cancellation policy window must allow cancellation.
- `version` must match.

Error responses:

- `403 FORBIDDEN`
- `404 BOOKING_NOT_FOUND`
- `409 VERSION_CONFLICT`
- `422 BOOKING_NOT_CANCELLABLE`

## Products

### GET `/products`

Authentication: Customer

Request DTO:

- Query: `categoryId`, `category`, `search`, `brand`, `minPrice`, `maxPrice`, `inStock`, `page`, `size`, `sort`

Response DTO:

```yaml
Product:
  type: object
  properties:
    id:
      type: string
      format: uuid
    categoryId:
      type: string
      format: uuid
      nullable: true
    sku:
      type: string
    name:
      type: string
    slug:
      type: string
    brand:
      type: string
    subtitle:
      type: string
    description:
      type: string
    price:
      type: number
    mrp:
      type: number
    currency:
      type: string
      example: INR
    stockQty:
      type: integer
    isActive:
      type: boolean
    isBestseller:
      type: boolean
    rating:
      type: number
    reviewCount:
      type: integer
    imageUrl:
      type: string
      nullable: true
    subscriptionEligible:
      type: boolean
```

Validation rules:

- `size` max 100.
- `minPrice <= maxPrice`.
- Customer API returns only active, non-deleted products.

Error responses:

- `400 VALIDATION_ERROR`

### GET `/products/{productId}`

Authentication: Customer

Request DTO:

- Path: `productId uuid`

Response DTO:

- `Product`

Validation rules:

- Product must be active.

Error responses:

- `404 PRODUCT_NOT_FOUND`

### GET `/categories`

Authentication: Customer

Request DTO:

- None

Response DTO:

```yaml
Category:
  type: object
  properties:
    id:
      type: string
      format: uuid
    parentId:
      type: string
      format: uuid
      nullable: true
    name:
      type: string
    slug:
      type: string
    description:
      type: string
      nullable: true
    sortOrder:
      type: integer
```

Validation rules:

- Return active categories only.

Error responses:

- `401 UNAUTHORIZED`

## Cart

### GET `/cart`

Authentication: Customer

Request DTO:

- None

Response DTO:

```yaml
Cart:
  type: object
  properties:
    id:
      type: string
      format: uuid
    items:
      type: array
      items:
        $ref: '#/components/schemas/CartItem'
    subtotal:
      type: number
    deliveryFee:
      type: number
    discountTotal:
      type: number
    taxTotal:
      type: number
    total:
      type: number
    currency:
      type: string
    version:
      type: integer
      format: int64

CartItem:
  type: object
  properties:
    id:
      type: string
      format: uuid
    product:
      $ref: '#/components/schemas/Product'
    quantity:
      type: integer
    lineTotal:
      type: number
    version:
      type: integer
      format: int64
```

Validation rules:

- Return or create active cart for caller.

Error responses:

- `401 UNAUTHORIZED`

### POST `/cart/items`

Authentication: Customer

Request DTO:

```yaml
AddCartItemRequest:
  type: object
  required: [productId, quantity]
  properties:
    productId:
      type: string
      format: uuid
    quantity:
      type: integer
      minimum: 1
      maximum: 99
```

Response DTO:

- `Cart`

Validation rules:

- Product must exist and be active.
- Quantity must not exceed available stock or product purchase limit.
- Existing cart line should increment quantity.

Error responses:

- `404 PRODUCT_NOT_FOUND`
- `409 OUT_OF_STOCK`
- `422 QUANTITY_LIMIT_EXCEEDED`

### PATCH `/cart/items/{cartItemId}`

Authentication: Customer

Request DTO:

```yaml
UpdateCartItemRequest:
  type: object
  required: [quantity, version]
  properties:
    quantity:
      type: integer
      minimum: 0
      maximum: 99
    version:
      type: integer
      format: int64
```

Response DTO:

- `Cart`

Validation rules:

- Cart item must belong to caller's active cart.
- Quantity `0` removes item.
- `version` must match.

Error responses:

- `403 FORBIDDEN`
- `404 CART_ITEM_NOT_FOUND`
- `409 VERSION_CONFLICT`
- `409 OUT_OF_STOCK`

### DELETE `/cart/items/{cartItemId}`

Authentication: Customer

Request DTO:

- None

Response DTO:

- `Cart`

Validation rules:

- Cart item must belong to caller's active cart.

Error responses:

- `403 FORBIDDEN`
- `404 CART_ITEM_NOT_FOUND`

## Orders

### POST `/orders`

Authentication: Customer

Headers:

- `Idempotency-Key`: required

Request DTO:

```yaml
CreateOrderRequest:
  type: object
  required: [addressId]
  properties:
    addressId:
      type: string
      format: uuid
    paymentMethodId:
      type: string
      format: uuid
      nullable: true
    notes:
      type: string
      nullable: true
```

Response DTO:

```yaml
Order:
  type: object
  properties:
    id:
      type: string
      format: uuid
    orderNumber:
      type: string
    status:
      type: string
      enum: [PENDING_PAYMENT, PLACED, PACKED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, REFUNDED]
    paymentStatus:
      type: string
      enum: [PENDING, AUTHORIZED, PAID, FAILED, REFUNDED]
    items:
      type: array
      items:
        $ref: '#/components/schemas/OrderItem'
    subtotal:
      type: number
    discountTotal:
      type: number
    deliveryFee:
      type: number
    taxTotal:
      type: number
    grandTotal:
      type: number
    currency:
      type: string
    deliveryAddressSnapshot:
      type: string
    placedAt:
      type: string
      format: date-time
      nullable: true
    version:
      type: integer
      format: int64

OrderItem:
  type: object
  properties:
    productId:
      type: string
      format: uuid
      nullable: true
    sku:
      type: string
    productName:
      type: string
    quantity:
      type: integer
    unitPrice:
      type: number
    lineTotal:
      type: number
```

Validation rules:

- Active cart must not be empty.
- Address must belong to caller and be serviceable.
- Product prices and stock must be revalidated at order creation.
- Idempotency key must prevent duplicate orders.

Error responses:

- `400 CART_EMPTY`
- `403 ADDRESS_NOT_OWNED`
- `409 IDEMPOTENCY_CONFLICT`
- `409 OUT_OF_STOCK`
- `422 PRICE_CHANGED`
- `422 UNSERVICEABLE_PINCODE`

### GET `/orders`

Authentication: Customer

Request DTO:

- Query: `status`, `page`, `size`

Response DTO:

- Array of `Order` plus `PageMeta`

Validation rules:

- Return only caller-owned orders.

Error responses:

- `401 UNAUTHORIZED`

### GET `/orders/{orderId}`

Authentication: Customer

Request DTO:

- Path: `orderId uuid`

Response DTO:

- `Order`

Validation rules:

- Order must belong to caller.

Error responses:

- `403 FORBIDDEN`
- `404 ORDER_NOT_FOUND`

### POST `/orders/{orderId}/reorder`

Authentication: Customer

Request DTO:

```yaml
ReorderRequest:
  type: object
  properties:
    replaceCart:
      type: boolean
      default: false
```

Response DTO:

- `Cart`

Validation rules:

- Order must belong to caller.
- Only active/in-stock products are re-added.

Error responses:

- `403 FORBIDDEN`
- `404 ORDER_NOT_FOUND`
- `422 NO_REORDERABLE_ITEMS`

## Notifications

### GET `/notifications`

Authentication: Customer

Request DTO:

- Query: `unreadOnly`, `page`, `size`

Response DTO:

```yaml
Notification:
  type: object
  properties:
    id:
      type: string
      format: uuid
    type:
      type: string
      enum: [APPOINTMENT, MEDICATION, DELIVERY, PROMO, CHAT, SYSTEM]
    title:
      type: string
    body:
      type: string
    deepLink:
      type: string
      nullable: true
    priority:
      type: string
      enum: [LOW, NORMAL, HIGH]
    isRead:
      type: boolean
    createdAt:
      type: string
      format: date-time
    version:
      type: integer
      format: int64
```

Validation rules:

- Return only caller-owned notifications.

Error responses:

- `401 UNAUTHORIZED`

### PATCH `/notifications/{notificationId}/read`

Authentication: Customer

Request DTO:

```yaml
MarkNotificationReadRequest:
  type: object
  properties:
    version:
      type: integer
      format: int64
      nullable: true
```

Response DTO:

- `Notification`

Validation rules:

- Notification must belong to caller.

Error responses:

- `403 FORBIDDEN`
- `404 NOTIFICATION_NOT_FOUND`
- `409 VERSION_CONFLICT`

### GET `/notifications/preferences`

Authentication: Customer

Request DTO:

- None

Response DTO:

```yaml
NotificationPreferences:
  type: object
  properties:
    appointments:
      type: boolean
    medications:
      type: boolean
    orders:
      type: boolean
    promos:
      type: boolean
    chat:
      type: boolean
    version:
      type: integer
      format: int64
```

Validation rules:

- Return caller preferences; create defaults if missing.

Error responses:

- `401 UNAUTHORIZED`

### PUT `/notifications/preferences`

Authentication: Customer

Request DTO:

```yaml
UpdateNotificationPreferencesRequest:
  type: object
  required: [appointments, medications, orders, promos, chat, version]
  properties:
    appointments:
      type: boolean
    medications:
      type: boolean
    orders:
      type: boolean
    promos:
      type: boolean
    chat:
      type: boolean
    version:
      type: integer
      format: int64
```

Response DTO:

- `NotificationPreferences`

Validation rules:

- `version` must match.
- Some transactional notifications may remain mandatory even if category disabled.

Error responses:

- `409 VERSION_CONFLICT`

## Chat

### GET `/conversations`

Authentication: Customer

Request DTO:

- Query: `status`, `page`, `size`

Response DTO:

```yaml
Conversation:
  type: object
  properties:
    id:
      type: string
      format: uuid
    type:
      type: string
      enum: [SUPPORT, VET, ORDER, BOOKING]
    status:
      type: string
      enum: [OPEN, CLOSED, ARCHIVED]
    subject:
      type: string
      nullable: true
    participantName:
      type: string
    participantAvatarUrl:
      type: string
      nullable: true
    lastMessagePreview:
      type: string
      nullable: true
    lastMessageAt:
      type: string
      format: date-time
      nullable: true
    unreadCount:
      type: integer
    version:
      type: integer
      format: int64
```

Validation rules:

- Return only caller-owned conversations.

Error responses:

- `401 UNAUTHORIZED`

### POST `/conversations`

Authentication: Customer

Request DTO:

```yaml
CreateConversationRequest:
  type: object
  required: [type]
  properties:
    type:
      type: string
      enum: [SUPPORT, VET, ORDER, BOOKING]
    vetId:
      type: string
      format: uuid
      nullable: true
    bookingId:
      type: string
      format: uuid
      nullable: true
    orderId:
      type: string
      format: uuid
      nullable: true
    subject:
      type: string
      maxLength: 180
      nullable: true
    initialMessage:
      type: string
      maxLength: 5000
      nullable: true
```

Response DTO:

- `Conversation`

Validation rules:

- Referenced booking/order must belong to caller.
- Vet conversation requires an active booking or allowed support context.

Error responses:

- `403 FORBIDDEN`
- `404 RELATED_RESOURCE_NOT_FOUND`
- `409 CONVERSATION_ALREADY_EXISTS`

### GET `/conversations/{conversationId}/messages`

Authentication: Customer

Request DTO:

- Path: `conversationId uuid`
- Query: `before`, `after`, `size`

Response DTO:

```yaml
Message:
  type: object
  properties:
    id:
      type: string
      format: uuid
    conversationId:
      type: string
      format: uuid
    senderType:
      type: string
      enum: [USER, VET, SUPPORT, SYSTEM]
    messageType:
      type: string
      enum: [TEXT, IMAGE, FILE, SYSTEM]
    body:
      type: string
      nullable: true
    attachmentUrl:
      type: string
      nullable: true
    sentAt:
      type: string
      format: date-time
    readAt:
      type: string
      format: date-time
      nullable: true
    version:
      type: integer
      format: int64
```

Validation rules:

- Conversation must belong to caller.

Error responses:

- `403 FORBIDDEN`
- `404 CONVERSATION_NOT_FOUND`

### POST `/conversations/{conversationId}/messages`

Authentication: Customer

Request DTO:

```yaml
SendMessageRequest:
  type: object
  required: [messageType]
  properties:
    messageType:
      type: string
      enum: [TEXT, IMAGE, FILE]
    body:
      type: string
      maxLength: 5000
      nullable: true
    attachmentUrl:
      type: string
      nullable: true
```

Response DTO:

- `Message`

Validation rules:

- Conversation must belong to caller and be open.
- Text messages require non-blank `body`.
- File/image messages require `attachmentUrl`.
- Sender must pass chat rate limits.

Error responses:

- `400 VALIDATION_ERROR`
- `403 FORBIDDEN`
- `404 CONVERSATION_NOT_FOUND`
- `422 CONVERSATION_CLOSED`
- `429 RATE_LIMITED`

### PATCH `/conversations/{conversationId}/read`

Authentication: Customer

Request DTO:

```yaml
MarkConversationReadRequest:
  type: object
  properties:
    readUntil:
      type: string
      format: date-time
      nullable: true
```

Response DTO:

- `Conversation`

Validation rules:

- Conversation must belong to caller.

Error responses:

- `403 FORBIDDEN`
- `404 CONVERSATION_NOT_FOUND`

## Referral

### GET `/referral`

Authentication: Customer

Request DTO:

- None

Response DTO:

```yaml
ReferralSummary:
  type: object
  properties:
    code:
      type: string
    referrerReward:
      type: number
    refereeReward:
      type: number
    referralsMade:
      type: integer
    rewardsEarned:
      type: number
    currency:
      type: string
```

Validation rules:

- Return caller referral code and aggregate reward state.

Error responses:

- `401 UNAUTHORIZED`

### POST `/referral/claim`

Authentication: Customer

Request DTO:

```yaml
ClaimReferralRequest:
  type: object
  required: [referralCode]
  properties:
    referralCode:
      type: string
      minLength: 4
      maxLength: 40
```

Response DTO:

```yaml
Referral:
  type: object
  properties:
    id:
      type: string
      format: uuid
    referralCode:
      type: string
    referrerUserId:
      type: string
      format: uuid
    referredUserId:
      type: string
      format: uuid
    status:
      type: string
      enum: [PENDING, QUALIFIED, REWARDED, REJECTED]
    referrerRewardAmount:
      type: number
    referredRewardAmount:
      type: number
    currency:
      type: string
```

Validation rules:

- User can claim only once.
- User cannot claim own referral code.
- Referral code must be active.
- Abuse/fraud checks may reject claim.

Error responses:

- `400 VALIDATION_ERROR`
- `409 REFERRAL_ALREADY_CLAIMED`
- `422 SELF_REFERRAL_NOT_ALLOWED`
- `422 INVALID_REFERRAL_CODE`

## Subscriptions

### GET `/subscriptions`

Authentication: Customer

Request DTO:

- Query: `status`

Response DTO:

```yaml
Subscription:
  type: object
  properties:
    id:
      type: string
      format: uuid
    product:
      $ref: '#/components/schemas/Product'
    addressId:
      type: string
      format: uuid
      nullable: true
    status:
      type: string
      enum: [ACTIVE, PAUSED, PAYMENT_FAILED, CANCELLED]
    quantity:
      type: integer
    cadence:
      type: string
      enum: [WEEKLY, BIWEEKLY, MONTHLY]
    pricePerCycle:
      type: number
    currency:
      type: string
    nextBillingDate:
      type: string
      format: date
      nullable: true
    nextDeliveryDate:
      type: string
      format: date
    pausedUntil:
      type: string
      format: date
      nullable: true
    version:
      type: integer
      format: int64
```

Validation rules:

- Return only caller-owned subscriptions.

Error responses:

- `401 UNAUTHORIZED`

### POST `/subscriptions`

Authentication: Customer

Headers:

- `Idempotency-Key`: required

Request DTO:

```yaml
CreateSubscriptionRequest:
  type: object
  required: [productId, addressId, quantity, cadence]
  properties:
    productId:
      type: string
      format: uuid
    addressId:
      type: string
      format: uuid
    quantity:
      type: integer
      minimum: 1
      maximum: 20
    cadence:
      type: string
      enum: [WEEKLY, BIWEEKLY, MONTHLY]
    firstDeliveryDate:
      type: string
      format: date
      nullable: true
```

Response DTO:

- `Subscription`

Validation rules:

- Product must be active and subscription eligible.
- Address must belong to caller and be serviceable.
- One active subscription per user/product unless variants are introduced.
- First delivery date must be serviceable and not in the past.

Error responses:

- `404 PRODUCT_NOT_FOUND`
- `403 ADDRESS_NOT_OWNED`
- `409 SUBSCRIPTION_ALREADY_EXISTS`
- `422 PRODUCT_NOT_SUBSCRIBABLE`
- `422 UNSERVICEABLE_PINCODE`

### PATCH `/subscriptions/{subscriptionId}/pause`

Authentication: Customer

Request DTO:

```yaml
PauseSubscriptionRequest:
  type: object
  required: [pausedUntil, version]
  properties:
    pausedUntil:
      type: string
      format: date
    version:
      type: integer
      format: int64
```

Response DTO:

- `Subscription`

Validation rules:

- Subscription must belong to caller.
- Subscription must be active.
- `pausedUntil` must be in the future.
- `version` must match.

Error responses:

- `403 FORBIDDEN`
- `404 SUBSCRIPTION_NOT_FOUND`
- `409 VERSION_CONFLICT`
- `422 SUBSCRIPTION_NOT_ACTIVE`

### PATCH `/subscriptions/{subscriptionId}/resume`

Authentication: Customer

Request DTO:

```yaml
ResumeSubscriptionRequest:
  type: object
  required: [version]
  properties:
    version:
      type: integer
      format: int64
```

Response DTO:

- `Subscription`

Validation rules:

- Subscription must belong to caller.
- Subscription must be paused or payment failed.
- `version` must match.

Error responses:

- `403 FORBIDDEN`
- `404 SUBSCRIPTION_NOT_FOUND`
- `409 VERSION_CONFLICT`
- `422 SUBSCRIPTION_NOT_RESUMABLE`

### PATCH `/subscriptions/{subscriptionId}/skip-next`

Authentication: Customer

Request DTO:

```yaml
SkipNextDeliveryRequest:
  type: object
  required: [version]
  properties:
    version:
      type: integer
      format: int64
```

Response DTO:

- `Subscription`

Validation rules:

- Subscription must belong to caller.
- Subscription must be active.
- Delivery cannot be skipped after fulfillment cutoff.

Error responses:

- `403 FORBIDDEN`
- `404 SUBSCRIPTION_NOT_FOUND`
- `409 VERSION_CONFLICT`
- `422 SKIP_WINDOW_CLOSED`

### DELETE `/subscriptions/{subscriptionId}`

Authentication: Customer

Request DTO:

```yaml
CancelSubscriptionRequest:
  type: object
  required: [reason, version]
  properties:
    reason:
      type: string
      minLength: 3
      maxLength: 500
    version:
      type: integer
      format: int64
```

Response DTO:

- `Subscription`

Validation rules:

- Subscription must belong to caller.
- Already shipped upcoming delivery cannot be cancelled through this endpoint.
- `version` must match.

Error responses:

- `403 FORBIDDEN`
- `404 SUBSCRIPTION_NOT_FOUND`
- `409 VERSION_CONFLICT`
- `422 CANCELLATION_WINDOW_CLOSED`

