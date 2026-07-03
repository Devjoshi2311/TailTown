# Production Backend Architecture

## Goals

TailTown backend must support a production pet care platform with authentication, pet profiles, vet discovery, booking, health records, ecommerce, subscriptions, push notifications, chat, referral, and account management.

The target backend stack:

- Kotlin
- Spring Boot 3
- PostgreSQL 16
- Flyway
- Spring Security with JWT (RS256)
- Redis 7
- Docker
- Spring Data JPA / Hibernate
- Quartz Scheduler (JDBC store)
- Micrometer + Prometheus + Grafana
- WebSocket (STOMP) for chat

The architecture must support:

- 100,000+ registered users.
- Horizontally scalable, stateless API nodes behind a load balancer.
- Sub-200 ms P95 response times for read-heavy endpoints.
- Idempotent writes for booking, checkout, and subscription creation.
- Distributed caching and session-free JWT authentication.
- Structured audit logging for health records and financial operations.
- Zero-downtime deployments with rolling Flyway migrations.

---

## Architecture Overview

Use a layered, feature-oriented modular monolith. Logical boundaries must be clean enough to extract into services if scaling demands it later.

Layers:

- API layer: controllers, request/response DTOs, input validation, versioning.
- Application layer: service classes, orchestration, transaction management, business rules.
- Domain layer: domain models, domain events, domain-level validation.
- Infrastructure layer: JPA repositories, Redis clients, external HTTP clients, file storage.
- Platform layer: security, caching, job scheduling, observability, Docker config.

Dependency direction:

- Controllers depend on application services only.
- Application services depend on domain models and infrastructure interfaces.
- Infrastructure implementations are injected through Spring's IoC container.
- Domain models have no dependency on Spring or infrastructure.

No controller calls a JPA repository directly. No repository contains business logic.

---

## Package Structure

```text
com.tailtown.backend
  Application.kt

  api
    v1
      auth
        AuthController
        dto
          LoginRequest
          OtpVerifyRequest
          AuthResponse
          RefreshTokenRequest
          TokenResponse
          FirebaseTokenRequest
      profile
        ProfileController
        dto
          UpdateProfileRequest
          ProfileResponse
          AddressRequest
          AddressResponse
      pets
        PetController
        dto
          CreatePetRequest
          UpdatePetRequest
          PetResponse
      health
        HealthController
        dto
          AddWeightRequest
          WeightResponse
          AddVaccineRequest
          VaccineResponse
          AddPrescriptionRequest
          PrescriptionResponse
          LogDoseRequest
          MedicalRecordResponse
      vets
        VetController
        dto
          VetResponse
          VetSearchRequest
          SlotResponse
          SlotAvailabilityResponse
      booking
        BookingController
        dto
          CreateBookingRequest
          BookingResponse
          CancelBookingRequest
      shop
        ProductController
        CategoryController
        dto
          ProductResponse
          CategoryResponse
          ProductListResponse
          ProductSearchRequest
      cart
        CartController
        dto
          AddCartItemRequest
          UpdateCartItemRequest
          CartResponse
          CartItemResponse
      orders
        OrderController
        dto
          CheckoutRequest
          OrderResponse
          OrderItemResponse
          OrderStatusResponse
      subscriptions
        SubscriptionController
        dto
          CreateSubscriptionRequest
          SubscriptionResponse
          PauseSubscriptionRequest
      notifications
        NotificationController
        dto
          NotificationResponse
          MarkReadRequest
          PushTokenRequest
      chat
        ChatController
        ChatWebSocketController
        dto
          ConversationResponse
          MessageResponse
          SendMessageRequest
      referral
        ReferralController
        dto
          ReferralSummaryResponse
          ReferralCodeResponse

  application
    auth
      AuthService
      OtpService
      TokenService
      FirebaseAuthService
    profile
      ProfileService
    pets
      PetService
    health
      HealthService
      PrescriptionService
      VaccinationService
    vets
      VetService
      SlotService
    booking
      BookingService
      BookingConflictResolver
    shop
      ProductService
      CategoryService
    cart
      CartService
    orders
      OrderService
      OrderFulfillmentService
    subscriptions
      SubscriptionService
    notifications
      NotificationService
      PushNotificationService
    chat
      ChatService
      ConversationService
    referral
      ReferralService
    event
      BookingCreatedEventHandler
      OrderPlacedEventHandler
      SubscriptionActivatedEventHandler
      ReferralCompletedEventHandler

  domain
    auth
      OtpCredential
      RefreshToken
      TokenPair
    profile
      UserProfile
      Address
    pets
      Pet
      PetSpecies
      PetGender
    health
      WeightRecord
      Vaccine
      Prescription
      PrescriptionDose
      MedicalRecord
    vets
      Vet
      VetSpecialization
      AvailabilitySlot
    booking
      Booking
      BookingStatus
      BookingSlot
    shop
      Product
      Category
      ProductVariant
    cart
      Cart
      CartItem
    orders
      Order
      OrderItem
      OrderStatus
      FulfillmentStatus
    subscriptions
      Subscription
      SubscriptionPlan
      SubscriptionStatus
    notifications
      Notification
      NotificationType
      PushToken
    chat
      Conversation
      Message
    referral
      ReferralSummary
      ReferralReward
    event
      BookingCreatedEvent
      OrderPlacedEvent
      SubscriptionActivatedEvent
      PrescriptionDueEvent
      ReferralCompletedEvent

  infrastructure
    persistence
      auth
        RefreshTokenEntity
        RefreshTokenRepository
        OtpCredentialEntity
        OtpCredentialRepository
      profile
        UserProfileEntity
        UserProfileRepository
      pets
        PetEntity
        PetRepository
      health
        WeightRecordEntity
        WeightRecordRepository
        VaccineEntity
        VaccineRepository
        PrescriptionEntity
        PrescriptionRepository
        PrescriptionDoseEntity
        PrescriptionDoseRepository
        MedicalRecordEntity
        MedicalRecordRepository
      vets
        VetEntity
        VetRepository
        AvailabilitySlotEntity
        AvailabilitySlotRepository
      booking
        BookingEntity
        BookingRepository
      shop
        ProductEntity
        ProductRepository
        CategoryEntity
        CategoryRepository
      cart
        CartEntity
        CartItemEntity
        CartRepository
      orders
        OrderEntity
        OrderItemEntity
        OrderRepository
      subscriptions
        SubscriptionEntity
        SubscriptionRepository
      notifications
        NotificationEntity
        NotificationRepository
        PushTokenEntity
        PushTokenRepository
      chat
        ConversationEntity
        MessageEntity
        ConversationRepository
        MessageRepository
      referral
        ReferralEntity
        ReferralRepository
    cache
      RedisConfig
      CacheKeyBuilder
      UserCacheService
      ProductCacheService
      SessionCacheService
    jobs
      scheduler
        SubscriptionRenewalJob
        AppointmentReminderJob
        PrescriptionDueJob
        VaccinationDueJob
        ExpiredSlotCleanupJob
        StaleOtpCleanupJob
        NotificationDispatchJob
        AbandonedCartNotifyJob
        OrderTimeoutJob
        RefreshTokenPurgeJob
      config
        QuartzConfig
        JobFactory
    push
      FcmClient
      FcmPayloadBuilder
    storage
      FileStorageService
      S3StorageService
      SignedUrlService
    http
      FirebaseAuthClient
      PaymentGatewayClient
    mapper
      PetMapper
      HealthMapper
      BookingMapper
      OrderMapper
      SubscriptionMapper
      ChatMapper

  platform
    security
      SecurityConfig
      JwtAuthFilter
      JwtTokenProvider
      UserDetailsServiceImpl
      RateLimitFilter
      IdempotencyFilter
    config
      RedisConfig
      JpaConfig
      WebSocketConfig
      AsyncConfig
      ObjectMapperConfig
    exception
      GlobalExceptionHandler
      TailTownException
      ErrorResponse
      ErrorCode
    logging
      RequestLoggingFilter
      AuditLogger
      CorrelationIdFilter
    observability
      MetricsConfig
      HealthIndicators
      TracingConfig
    ratelimit
      RateLimitService
      RateLimitProperties
      RateLimitKeyResolver

  db
    migration
      V1__create_users.sql
      V2__create_pets.sql
      V3__create_health_records.sql
      V4__create_vets_and_slots.sql
      V5__create_bookings.sql
      V6__create_shop.sql
      V7__create_cart_orders.sql
      V8__create_subscriptions.sql
      V9__create_notifications.sql
      V10__create_chat.sql
      V11__create_referrals.sql
      V12__add_refresh_tokens.sql
```

---

## API Versioning

All production endpoints are versioned by URI path prefix.

```
/api/v1/auth/**
/api/v1/profile/**
/api/v1/pets/**
/api/v1/health/**
/api/v1/vets/**
/api/v1/bookings/**
/api/v1/shop/**
/api/v1/cart/**
/api/v1/orders/**
/api/v1/subscriptions/**
/api/v1/notifications/**
/api/v1/chat/**
/api/v1/referral/**
/api/internal/**
/actuator/**
```

Versioning rules:

- URI path versioning is preferred over header versioning for discoverability and cache-friendliness.
- A new version (`/api/v2/`) is introduced only when breaking changes are unavoidable.
- Old versions are supported for at least one deprecation cycle before removal.
- Version routing uses separate controller classes, not conditional logic inside one method.
- Deprecated endpoints include the response header `Deprecation: date="..."`.
- Actuator endpoints are never under `/api/`.
- `/api/internal/**` endpoints are restricted to internal network or a shared secret header; never exposed to mobile clients.

---

## DTO Strategy

Three distinct object types travel through the system:

- **Request DTOs**: incoming data from the client. Carry input validation annotations (`@NotBlank`, `@Size`, `@Email`, `@Min`) only. No business logic.
- **Response DTOs**: outgoing data to the client. Serialized by Jackson. Never expose internal entity ids as a stable contract unless the id is genuinely stable.
- **Domain models**: internal representations used in application and domain layers. No Jackson annotations. No JPA annotations.

Mapping rules:

- Entity → domain model: done in infrastructure mappers, called by the repository adapter before returning to the application layer.
- Domain model → response DTO: done in the controller or a dedicated `ResponseAssembler`. Never inside a service.
- Request DTO → service input / command object: done in the controller before calling the service.
- No JPA entity is ever serialized to JSON directly.
- No request DTO is persisted to the database directly.

Shared response conventions:

- List responses: `{ "items": [...], "total": 100, "page": 0, "size": 20 }` using a shared generic `PageResponse<T>` wrapper.
- Single resource responses: flat JSON, no nested envelope.
- Error responses: `{ "code": "SLOT_UNAVAILABLE", "message": "...", "requestId": "...", "timestamp": "..." }`.
- Timestamps: ISO 8601 strings in UTC (`2025-06-10T14:30:00Z`).
- Currency: integer minor units (paise for INR). Accompany every amount with a `currency` field.
- Sensitive fields (passwords, OTP values, raw tokens) are never included in responses.

---

## Service Layer

Application services coordinate business operations. They own transaction boundaries and call domain objects, repositories, and infrastructure adapters.

Service rules:

- Services are `@Service`. Mutating services are `@Transactional` at class or method level. Read services use `@Transactional(readOnly = true)`.
- Services do not call other feature services directly. Cross-feature coordination uses domain events or shared infrastructure services (e.g. `NotificationService`, `PushNotificationService`).
- Services never return JPA entities. They return domain models.
- Services do not call controllers or produce HTTP responses.
- Validation requiring persistence (duplicate phone, existing slot reservation) is the service's responsibility, not the controller's.

Transaction rules:

- Booking creation: one transaction covering slot pessimistic lock, booking insert, and notification outbox enqueue.
- Order placement: one transaction covering inventory decrement, order insert, cart clear, and payment reference persist.
- Subscription creation: one transaction covering subscription record, first billing reference, and notification enqueue.
- Idempotency: services accept an optional `idempotencyKey` parameter for mutating operations. The key is checked in Redis before executing. On a cache hit the prior result is returned immediately without re-executing.

Avoid transactions that span external calls (Firebase, payment gateway, FCM). Instead:

1. Execute the local transaction.
2. Enqueue an outbox record for the external side effect.
3. A background job or `@TransactionalEventListener(AFTER_COMMIT)` dispatches the external call.
4. Reconciliation jobs detect and repair divergent state.

Key service contracts:

**AuthService**:
- Initiate OTP, validate OTP, issue JWT access + refresh pair.
- Exchange Firebase ID token for a TailTown JWT pair.
- Refresh access token via refresh token with rotation.
- Revoke single or all sessions.

**BookingService**:
- Acquire a short-duration pessimistic lock on the slot row.
- Create booking and decrement slot capacity atomically.
- Cancel within the cancellation window and release the slot.
- Detect double-booking and return `SlotUnavailableException`.

**OrderService**:
- Validate cart items, check stock, compute totals, apply promotions.
- Reserve inventory, create order, clear cart atomically.
- Emit `OrderPlacedEvent` after commit for fulfillment.
- Advance order status on payment webhook callback.

**SubscriptionService**:
- Create with plan, billing anchor, and first delivery date.
- Process renewal via `SubscriptionRenewalJob`.
- Handle pause, resume, and cancellation with pro-ration rules.

---

## Repository Layer

Use Spring Data JPA for standard CRUD. Use `@Query` with JPQL or native SQL for complex queries. Use `Specification` for dynamic filters. Use native queries sparingly and only when JPQL is insufficient.

Repository rules:

- Each aggregate root has one repository interface.
- Repositories return entities or projections. Mappers in the infrastructure layer convert to domain models before the result leaves the infrastructure package.
- No business logic inside repositories.
- Pagination uses Spring's `Pageable` and `Page<T>`.
- Soft deletes use an `is_deleted` flag. Entities annotated with `@Where(clause = "is_deleted = false")` automatically exclude deleted rows.
- All user-scoped queries include `userId` as a filter parameter to prevent data leakage across accounts.
- High-write tables (messages, notifications) use batch inserts where appropriate.

Custom query patterns:

```kotlin
interface BookingListProjection {
    val id: UUID
    val vetName: String
    val slotTime: Instant
    val status: String
}

@Repository
interface BookingRepository : JpaRepository<BookingEntity, UUID> {

    @Query("""
        SELECT b.id as id, v.name as vetName, b.slot_time as slotTime, b.status as status
        FROM bookings b JOIN vets v ON b.vet_id = v.id
        WHERE b.user_id = :userId AND b.is_deleted = false
        ORDER BY b.slot_time DESC
    """, nativeQuery = true)
    fun findSummariesForUser(userId: UUID, pageable: Pageable): Page<BookingListProjection>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM AvailabilitySlotEntity s WHERE s.id = :slotId")
    fun findSlotForUpdate(slotId: UUID): AvailabilitySlotEntity?
}
```

Recommended index strategy (defined in Flyway migrations, not Hibernate annotations):

- `users`: `email` (unique), `phone` (unique), `firebase_uid` (unique).
- `pets`: `(user_id, is_deleted)`.
- `bookings`: `(user_id, status)`, `(vet_id, slot_time, status)`.
- `availability_slots`: `(vet_id, start_time, status)`.
- `orders`: `(user_id, created_at DESC)`, `status`.
- `messages`: `(conversation_id, created_at DESC)`.
- `notifications`: `(user_id, is_read, created_at DESC)`.
- `prescriptions`: `(pet_id, is_deleted)`.

---

## Exception Handling

Use a single `@RestControllerAdvice` to handle all exceptions. No stack trace ever reaches a client.

Exception hierarchy:

```kotlin
sealed class TailTownException(
    val errorCode: ErrorCode,
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

class ResourceNotFoundException(resource: String, id: Any) :
    TailTownException(ErrorCode.NOT_FOUND, "$resource not found: $id")

class ValidationException(
    message: String,
    val fieldErrors: Map<String, String> = emptyMap()
) : TailTownException(ErrorCode.VALIDATION_ERROR, message)

class ConflictException(errorCode: ErrorCode, message: String) :
    TailTownException(errorCode, message)

class SlotUnavailableException :
    ConflictException(ErrorCode.SLOT_UNAVAILABLE, "Slot is no longer available")

class IdempotencyConflictException :
    ConflictException(ErrorCode.IDEMPOTENCY_CONFLICT, "Duplicate request in progress")

class OutOfStockException(productId: UUID) :
    ConflictException(ErrorCode.OUT_OF_STOCK, "Product $productId is out of stock")

class AuthenticationException(message: String) :
    TailTownException(ErrorCode.UNAUTHORIZED, message)

class ForbiddenException(message: String = "Access denied") :
    TailTownException(ErrorCode.FORBIDDEN, message)

class RateLimitExceededException :
    TailTownException(ErrorCode.RATE_LIMITED, "Too many requests")

class ExternalServiceException(service: String, cause: Throwable) :
    TailTownException(ErrorCode.EXTERNAL_SERVICE_ERROR, "External service failed: $service", cause)
```

Error codes:

```kotlin
enum class ErrorCode {
    VALIDATION_ERROR,
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    SLOT_UNAVAILABLE,
    OUT_OF_STOCK,
    BOOKING_CONFLICT,
    VERSION_CONFLICT,
    IDEMPOTENCY_CONFLICT,
    OTP_EXPIRED,
    OTP_INVALID,
    OTP_MAX_ATTEMPTS,
    REFRESH_TOKEN_INVALID,
    REFRESH_TOKEN_EXPIRED,
    RATE_LIMITED,
    PAYMENT_DECLINED,
    PAYMENT_ALREADY_CAPTURED,
    SUBSCRIPTION_ALREADY_ACTIVE,
    INTERNAL_ERROR,
    EXTERNAL_SERVICE_ERROR,
}
```

HTTP status mapping:

- `VALIDATION_ERROR`, `OTP_*`, `REFRESH_TOKEN_*`: 400
- `UNAUTHORIZED`: 401
- `FORBIDDEN`: 403
- `NOT_FOUND`: 404
- `SLOT_UNAVAILABLE`, `OUT_OF_STOCK`, `BOOKING_CONFLICT`, `VERSION_CONFLICT`, `IDEMPOTENCY_CONFLICT`: 409
- `RATE_LIMITED`: 429
- `INTERNAL_ERROR`, `EXTERNAL_SERVICE_ERROR`: 500

Global handler:

```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(TailTownException::class)
    fun handleDomain(ex: TailTownException, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val status = ex.errorCode.httpStatus()
        log.warn("Domain error [{}] {} - {}", ex.errorCode, req.requestURI, ex.message)
        return ResponseEntity.status(status).body(ErrorResponse.from(ex, req))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val fieldErrors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "invalid") }
        return ResponseEntity.badRequest().body(ErrorResponse.validation(fieldErrors, req))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error on {}", req.requestURI, ex)
        return ResponseEntity.internalServerError().body(ErrorResponse.internal(req))
    }
}
```

Error response shape:

```json
{
  "code": "SLOT_UNAVAILABLE",
  "message": "Slot is no longer available",
  "requestId": "req-abc123",
  "timestamp": "2025-06-10T14:30:00Z",
  "fieldErrors": {}
}
```

Rules:

- Stack traces are never included in responses.
- Field-level errors are only included for `VALIDATION_ERROR`.
- Unexpected exceptions are logged at ERROR with full stack trace.
- Domain exceptions are logged at WARN.
- `requestId` is drawn from the MDC correlation ID set by `CorrelationIdFilter`.

---

## JWT and Authentication

Token design:

- Access token: short-lived JWT (15 minutes), signed RS256 with a private key stored outside the application binary.
- Refresh token: opaque UUID, stored in PostgreSQL (`refresh_tokens` table) and Redis with a 30-day TTL.
- No server-side session state for access tokens. Refresh token state is minimal (user id, device id, expiry).

Access token claims:

```json
{
  "sub": "<userId>",
  "iat": 1718020200,
  "exp": 1718021100,
  "roles": ["USER"],
  "jti": "<unique token id>"
}
```

Token lifecycle:

1. Client authenticates via OTP or Firebase token exchange.
2. Server issues an access token + refresh token pair.
3. Refresh token is stored in `refresh_tokens` and Redis under `refresh:<token>` → `userId`.
4. Client sends `Authorization: Bearer <accessToken>` on every request.
5. On expiry, client calls `POST /api/v1/auth/refresh`.
6. Server validates the refresh token from Redis, rotates it (old invalidated, new issued), returns a new pair.
7. On logout, server deletes the refresh token from Redis and PostgreSQL.
8. On forced logout (all devices), all refresh tokens for the user are deleted.

Filter chain order (outermost to innermost):

1. `CorrelationIdFilter`: assigns `X-Request-Id` if absent; stores in MDC.
2. `RateLimitFilter`: checks per-IP and per-user Redis counters before routing.
3. `IdempotencyFilter`: checks idempotency key for mutating endpoints.
4. `JwtAuthFilter`: validates JWT, populates `SecurityContextHolder`.
5. `RequestLoggingFilter`: logs method, URI, status, and duration after the response is committed.

---

## Caching

Use Redis as the distributed cache. Use Spring Cache abstraction (`@Cacheable`, `@CacheEvict`, `@CachePut`) backed by `RedisCacheManager` with per-cache TTL configuration.

Cache names and TTLs:

| Cache name           | Key pattern                     | TTL        | Eviction trigger                         |
|----------------------|---------------------------------|------------|------------------------------------------|
| `user-profile`       | `user:<userId>`                 | 24 hours   | Profile update, logout                   |
| `pets`               | `pets:<userId>`                 | 24 hours   | Pet create/update/delete                 |
| `vet-list`           | `vets:<city>:<page>`            | 30 minutes | Admin invalidation                       |
| `vet-profile`        | `vet:<vetId>`                   | 1 hour     | Vet profile update                       |
| `vet-slots`          | `slots:<vetId>:<date>`          | 5 minutes  | Slot booked/released/created             |
| `product-catalog`    | `products:<categoryId>:<page>`  | 1 hour     | Product update, stock change             |
| `product-detail`     | `product:<productId>`           | 1 hour     | Product update                           |
| `subscription-plans` | `plans`                         | 6 hours    | Plan update                              |
| `referral-summary`   | `referral:<userId>`             | 15 minutes | Referral event                           |

Caching rules:

- Slot availability is cached for 5 minutes maximum. Always revalidate with a DB read (inside the pessimistic lock) before committing a booking.
- User-scoped caches are always namespaced by `userId` to prevent cross-user leakage.
- Cache keys include a schema version suffix when entity shape changes (`product:v2:<id>`).
- Cache-aside is the default pattern: cache miss → DB read → cache write.
- Write-through for critical items (user profile) keeps the cache fresh immediately after mutation.
- Redis connections use a Lettuce pool; configure pool size to match expected concurrency.
- Cache serialization uses JSON, not Java serialization.
- Protect against cache stampede on cold start using a distributed lock (`lock:catalog-cache-rebuild`, TTL 60 s) before bulk catalog loads.

---

## Rate Limiting

Redis-backed sliding window counters implemented in `RateLimitFilter`, which runs before routing.

Default limits:

| Endpoint pattern            | Limit              | Window    | Key    |
|-----------------------------|--------------------|-----------|--------|
| `POST /api/v1/auth/otp`     | 5 requests         | 15 min    | IP     |
| `POST /api/v1/auth/verify`  | 10 requests        | 15 min    | IP     |
| `POST /api/v1/auth/refresh` | 20 requests        | 1 hour    | IP     |
| `POST /api/v1/bookings`     | 20 requests        | 1 hour    | userId |
| `POST /api/v1/orders`       | 10 requests        | 1 hour    | userId |
| All authenticated endpoints | 300 requests       | 1 min     | userId |
| All unauthenticated endpoints| 60 requests       | 1 min     | IP     |

Sliding window implementation using a Redis sorted set:

```kotlin
fun isAllowed(key: String, limit: Int, windowSeconds: Long): Boolean {
    val now = System.currentTimeMillis()
    val windowStart = now - (windowSeconds * 1000)
    val redisKey = "ratelimit:$key"

    return redis.execute { conn ->
        conn.zRemRangeByScore(redisKey, Range.closed(Double.NEGATIVE_INFINITY, windowStart.toDouble()))
        val count = conn.zCard(redisKey) ?: 0L
        if (count < limit) {
            conn.zAdd(redisKey, now.toDouble(), now.toString())
            conn.expire(redisKey, windowSeconds)
            true
        } else {
            false
        }
    }
}
```

Response headers included on every response:

```
X-RateLimit-Limit: 300
X-RateLimit-Remaining: 42
X-RateLimit-Reset: 1718020260
```

On limit exceeded: `429 Too Many Requests` with `Retry-After` header and `ErrorCode.RATE_LIMITED`.

---

## Logging

Use structured JSON logging via Logback + `logstash-logback-encoder`.

Every log line includes:

```json
{
  "timestamp": "2025-06-10T14:30:00.123Z",
  "level": "INFO",
  "logger": "com.tailtown.backend.application.booking.BookingService",
  "message": "Booking created",
  "requestId": "req-abc123",
  "userId": "usr-xyz789",
  "traceId": "trace-def456",
  "spanId": "span-ghi012",
  "bookingId": "bkg-jkl345",
  "duration_ms": 42
}
```

MDC fields propagated to all log lines in a request context:

- `requestId`: set by `CorrelationIdFilter` from `X-Request-Id` or generated.
- `userId`: set by `JwtAuthFilter` after successful token validation.
- `traceId` / `spanId`: set by Micrometer Tracing.

Log levels by environment:

- Production: `INFO` for `com.tailtown.**`, `WARN` for third-party libraries.
- Staging: `DEBUG` for `com.tailtown.**`.
- Development: `DEBUG` for `com.tailtown.**`, `DEBUG` for Hibernate SQL.

Audit logging:

- Health record creates/updates/deletes are written to an `audit_log` table: `userId`, `entityType`, `entityId`, `action`, `beforeJson`, `afterJson`, `timestamp`.
- Financial operations (order placement, subscription charge, refund) are also written to the audit log.
- Audit log writes happen within the same transaction as the primary operation.
- `AuditLogger` is an injected service; never called from repositories.

Security logging rules:

- Never log passwords, OTP values, JWT tokens, payment card details, raw health notes, or chat message body.
- Log the `requestId` at every service operation entry and exit.
- Log duration for all external HTTP calls and database batch operations.
- In production, log request/response bodies only on error responses (4xx/5xx); never on successful responses.

---

## Monitoring and Observability

Use Spring Boot Actuator + Micrometer + Prometheus + Grafana.

Actuator endpoints:

- `/actuator/health`: liveness and readiness. Used by Docker/Kubernetes health checks.
- `/actuator/prometheus`: Prometheus scrape endpoint. Restricted to internal network.
- `/actuator/info`: application version, git commit SHA.
- All other actuator endpoints disabled in production.

Custom health indicators:

- `DatabaseHealthIndicator`: `SELECT 1` against PostgreSQL.
- `RedisHealthIndicator`: `PING` to Redis.
- `ExternalServiceHealthIndicator`: lightweight status check against payment gateway and Firebase.

Custom metrics:

```kotlin
// Booking funnel
Counter.builder("tailtown.bookings.created").register(meterRegistry)
Counter.builder("tailtown.bookings.cancelled").register(meterRegistry)
Counter.builder("tailtown.bookings.slot_conflict").register(meterRegistry)

// Order funnel
Counter.builder("tailtown.orders.placed").register(meterRegistry)
Counter.builder("tailtown.orders.payment_failed").register(meterRegistry)

// Auth
Counter.builder("tailtown.auth.otp_sent").register(meterRegistry)
Counter.builder("tailtown.auth.otp_verified").register(meterRegistry)
Counter.builder("tailtown.auth.otp_failed").register(meterRegistry)
Counter.builder("tailtown.auth.token_refreshed").register(meterRegistry)

// Rate limiting
Counter.builder("tailtown.ratelimit.blocked").tag("endpoint", endpoint).register(meterRegistry)

// Cache
Gauge.builder("tailtown.cache.hit_rate") { cacheStats.hitRate() }.register(meterRegistry)
```

SLO targets:

- P50 API latency: under 50 ms.
- P95 API latency: under 200 ms.
- P99 API latency: under 500 ms.
- Error rate (5xx): below 0.1%.
- Availability: 99.9% monthly.

Distributed tracing:

- Micrometer Tracing with OpenTelemetry exporter.
- Trace context propagates via `traceparent` / `tracestate` headers.
- Sample 100% in development, 5–10% in production (configurable per environment).
- Trace spans cover: HTTP request lifecycle, service method, repository query, and external HTTP call.

Grafana alerts:

- P95 API latency > 500 ms for 5 consecutive minutes.
- 5xx rate > 1% for 2 consecutive minutes.
- PostgreSQL connection pool saturation above 90%.
- Redis connection errors.
- Scheduled job failure rate > 5% in any 10-minute window.
- Auth failure spike above 3× baseline.

---

## Background Jobs

Use Quartz Scheduler with a JDBC job store (PostgreSQL) for distributed, cluster-safe scheduling. With JDBC clustering, a job runs on exactly one node even when multiple API instances are deployed.

Quartz tables are created by a dedicated Flyway migration. Table prefix: `QRTZ_`.

Scheduled jobs:

| Job                          | Schedule         | Description                                                           |
|------------------------------|------------------|-----------------------------------------------------------------------|
| `SubscriptionRenewalJob`     | Every 15 min     | Find subscriptions due for renewal, trigger billing, send receipts    |
| `AppointmentReminderJob`     | Every 30 min     | Push reminder for bookings within the next 24 hours                   |
| `PrescriptionDueJob`         | Daily 08:00 UTC  | Find active prescriptions with doses due today; notify owners         |
| `VaccinationDueJob`          | Daily 08:00 UTC  | Find vaccinations due in the next 7 days; notify owners               |
| `ExpiredSlotCleanupJob`      | Every 1 hour     | Archive or delete vet availability slots older than 30 days           |
| `StaleOtpCleanupJob`         | Every 30 min     | Delete OTP records expired more than 1 hour ago                       |
| `AbandonedCartNotifyJob`     | Every 4 hours    | Notify users with non-empty carts idle more than 24 hours             |
| `SubscriptionDeliveryJob`    | Every 1 hour     | Queue order creation for subscriptions with a delivery date of today  |
| `NotificationDispatchJob`    | Every 5 min      | Flush pending push notifications from outbox to FCM                   |
| `OrderTimeoutJob`            | Every 15 min     | Cancel unpaid orders older than the payment timeout window            |
| `RefreshTokenPurgeJob`       | Daily 03:00 UTC  | Delete expired refresh token rows from PostgreSQL                     |

Job rules:

- All jobs are idempotent. Running a job twice must not double-charge, double-send, or duplicate records.
- Jobs acquire a row-level lock (or claim a status transition) on affected records to be safe under concurrent instances.
- Jobs process records in pages of 100–500 rows to avoid long-running transactions.
- Failed individual items are logged and skipped; the job does not abort on a single bad record.
- Job execution time and items-processed count are recorded as Micrometer timers/counters.

Intra-process async events:

Use `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` for side effects that must fire only after the transaction commits.

- `BookingCreatedEvent` → send booking confirmation push notification.
- `OrderPlacedEvent` → trigger inventory reservation and fulfillment.
- `SubscriptionActivatedEvent` → send welcome notification.
- `ReferralCompletedEvent` → credit referrer reward.

---

## Idempotency

Idempotency keys prevent duplicate execution of mutating transactional endpoints.

Covered endpoints:

- `POST /api/v1/bookings`
- `POST /api/v1/orders`
- `POST /api/v1/subscriptions`
- `POST /api/v1/cart/checkout`
- `POST /api/v1/referral/apply`

Request header: `Idempotency-Key: <client-generated UUID>`

Server behavior:

1. `IdempotencyFilter` checks Redis key `idempotency:<userId>:<key>` before the controller.
2. On a cache hit, return the stored response immediately with header `Idempotency-Replayed: true`.
3. On a cache miss, proceed; after the response is committed, store the serialized response in Redis with a 24-hour TTL.
4. If a request with the same key is currently in-flight, return `409 IDEMPOTENCY_CONFLICT`.

---

## Database Schema Conventions

Flyway manages all schema changes. Hibernate `ddl-auto` is set to `validate` in all environments. No DDL is generated automatically.

Schema rules:

- All primary keys are `UUID`, generated by the application using `UUID.randomUUID()`.
- All tables have `created_at TIMESTAMPTZ NOT NULL DEFAULT now()` and `updated_at TIMESTAMPTZ NOT NULL DEFAULT now()`.
- `updated_at` is maintained by a PostgreSQL trigger; do not rely solely on Hibernate's `@UpdateTimestamp`.
- Soft deletes use `is_deleted BOOLEAN NOT NULL DEFAULT false` and `deleted_at TIMESTAMPTZ`.
- Monetary amounts are stored as `BIGINT` in minor units (paise). Every amount column is accompanied by a `currency VARCHAR(3)` column.
- Flyway migrations are versioned sequentially (`V1__`, `V2__`, …). No out-of-order migrations.
- All migrations are reviewed for safe execution under live traffic: prefer additive changes (nullable new column, `CREATE INDEX CONCURRENTLY`) over destructive changes in a single migration step.

Naming conventions:

- Table names: plural snake_case (`user_profiles`, `booking_slots`).
- Column names: snake_case.
- Index names: `idx_<table>_<columns>`.
- Foreign key names: `fk_<child_table>_<parent_table>`.
- Unique constraint names: `uq_<table>_<columns>`.

---

## Security

Authentication:

- All endpoints except `/api/v1/auth/**` and explicitly whitelisted public paths require a valid JWT.
- JWT is validated on every request. No server-side lookup is needed for access token validation; the RS256 public key is loaded at startup.
- Public endpoints (actuator health, app config) are whitelisted in `SecurityConfig`.

Authorization:

- Use Spring Security method security (`@PreAuthorize`) for resource ownership checks.
- Every endpoint accessing user-scoped data checks `userId` from the JWT subject against the resource owner stored in the database.
- Admin endpoints under `/api/internal/**` require the `ADMIN` role.

Input security:

- All string inputs are length-capped via `@Size` validation.
- File upload endpoints validate MIME type and impose a configurable size limit (default 10 MB).
- The backend issues pre-signed S3 URLs for uploads; the client uploads directly to S3. No file bytes pass through the API server.

Data protection:

- PostgreSQL connections use TLS.
- Redis connections use TLS.
- Sensitive fields (FCM tokens, payment method tokens) are encrypted at rest with AES-256 before storage.
- No raw payment card data is stored or logged. Only provider-issued tokenized references.
- Health notes and prescription instructions are never logged above DEBUG level.

CORS:

- Allowed origins are configured per environment.
- Production allows only registered app origins and the TailTown mobile URI scheme.
- `Authorization` and `Idempotency-Key` are declared as allowed request headers.

Webhook security:

- Payment and shipping provider webhooks are verified using HMAC signature validation before processing.
- Firebase push callbacks are verified using Firebase Admin SDK.

---

## Docker and Deployment

Multi-stage Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Docker Compose for local development:

```yaml
services:
  app:
    build: .
    ports: ["8080:8080"]
    environment:
      SPRING_PROFILES_ACTIVE: dev
      DB_HOST: postgres
      REDIS_HOST: redis
    depends_on: [postgres, redis]

  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: tailtown
      POSTGRES_USER: tailtown
      POSTGRES_PASSWORD: tailtown
    ports: ["5432:5432"]
    volumes: ["pgdata:/var/lib/postgresql/data"]

  redis:
    image: redis:7-alpine
    ports: ["6379:6379"]

volumes:
  pgdata:
```

Spring profiles:

- `dev`: local development, verbose Hibernate logging, relaxed CORS.
- `staging`: staging environment, Testcontainers not used, real Redis/PostgreSQL.
- `prod`: production, no Hibernate SQL logging, strict CORS, monitoring enabled.

Environment variable conventions:

- Database: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`.
- Connection pool: `DB_POOL_SIZE` (default 20).
- Redis: `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`.
- JWT: `JWT_PRIVATE_KEY`, `JWT_PUBLIC_KEY` (PEM, base64-encoded).
- Firebase: `FIREBASE_CREDENTIALS_JSON` (base64-encoded service account JSON).
- S3: `AWS_BUCKET`, `AWS_REGION`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`.
- Payment: `PAYMENT_API_KEY`, `PAYMENT_WEBHOOK_SECRET`.
- Feature flags: `FEATURE_CHAT_ENABLED`, `FEATURE_SUBSCRIPTIONS_ENABLED`.

All secrets are injected at runtime via environment variables or a secrets manager (AWS Secrets Manager, HashiCorp Vault). No secrets in source code or Docker images.

---

## Testing Strategy

Unit tests:

- Service classes with mocked repositories and infrastructure clients.
- Domain model validation logic.
- JWT token provider (sign and verify).
- Rate limit sliding window counter.
- Idempotency filter behavior.
- Entity-to-domain mapper conversions.
- Error code HTTP status mapping.

Integration tests (Testcontainers):

- Full Spring context with real PostgreSQL 16 and Redis 7 containers.
- Flyway migrations run in the test context.
- Transactional boundaries: booking slot pessimistic lock, order inventory decrement.
- Cache behavior: cache hit, cache miss, eviction after mutation.
- Rate limiting with real Redis counters.
- Idempotency key deduplication.

Controller tests (`@WebMvcTest`):

- Request validation rejection (missing fields, out-of-range values).
- Authentication guard: unauthenticated request returns `401`.
- Ownership guard: user A cannot access user B's resource.
- Error response shape for all exception types.
- Pagination parameter handling.

Job tests:

- Each Quartz job runs against a Testcontainers PostgreSQL.
- Verify idempotency: running a job twice produces the same result.
- Verify pagination: jobs process all records when the count exceeds one page.
- Verify failure isolation: one bad record does not abort the job.

End-to-end smoke tests (staging):

- Full OTP login and token refresh cycle.
- Add pet, add weight record, fetch health summary.
- Search vets, get slot availability, create booking, cancel booking.
- Add product to cart, checkout, verify order created and cart cleared.
- Create subscription, pause, resume.
- Send chat message via WebSocket.
- Apply referral code.
- Idempotent checkout: same `Idempotency-Key` returns prior response on second call.
- Double booking: second booking attempt on same slot returns `409 SLOT_UNAVAILABLE`.

Critical test cases that must always pass:

- A user cannot read another user's pets, orders, bookings, notifications, or conversations.
- Checkout cannot oversell inventory past zero.
- The same `Idempotency-Key` cannot create a duplicate order.
- The same slot cannot be double-booked concurrently.
- A used refresh token cannot be reused after rotation.
- Admin-role restrictions are enforced on `/api/internal/**`.

Test infrastructure:

- Testcontainers for PostgreSQL 16 and Redis 7.
- WireMock for FCM, payment gateway, and Firebase Admin.
- Spring Security test support for mocking authenticated users and roles.
- AssertJ for fluent assertions.
- Awaitility for async job and event assertions.

---

## Scalability Targets

For 100,000+ registered users:

- Stateless API nodes behind a load balancer. Scale by adding instances; no sticky sessions required.
- PostgreSQL connection pooling via PgBouncer in transaction mode. Pool size: 20–50 connections per app node.
- Redis Cluster or Redis Sentinel for high-availability caching and rate limit state.
- Quartz JDBC clustering ensures each scheduled job runs on exactly one node across the fleet.
- All list queries are paginated. No endpoint returns an unbounded collection.
- WebSocket connections for chat are handled by a single service at initial scale. At higher scale, use Redis Pub/Sub or Redis Streams to relay messages across nodes.
- S3 (or compatible) for file and image storage. The API issues signed URLs; it never proxies file bytes.
- CDN in front of product images and static content.
- PostgreSQL read replicas for read-heavy query load if primary throughput is saturated.
- Background jobs run on a dedicated worker profile (separate Docker container) to isolate job CPU and I/O from API request latency.

Baseline capacity estimates at 100k users, 10k DAU:

- Peak API RPS: ~500–1,000 on two standard app nodes.
- PostgreSQL storage: ~50 GB at two years of health records and chat history.
- Redis memory: ~2–4 GB for caches, rate limit counters, and session metadata.
- Background job throughput: ~10,000 push notifications per hour is well within one job node.
