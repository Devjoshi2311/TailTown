# Production Android Architecture

## Goals

TailTown Android must support a production pet care platform with authentication, pet profiles, vet discovery, booking, health records, ecommerce, subscriptions, notifications, chat, referral, and account management.

The target Android stack:

- Kotlin
- Jetpack Compose
- MVVM
- Hilt
- Retrofit
- Room
- WorkManager
- Kotlin Coroutines and Flow
- Navigation Compose
- Firebase Cloud Messaging
- Analytics and crash reporting

The architecture must support:

- 100,000+ registered users.
- Reliable behavior on poor networks.
- Secure token and sensitive data storage.
- Offline-first reads for core user data.
- Online-only transactional operations for booking, payment, and checkout.
- Testable ViewModels, repositories, sync workers, and data mappers.

## Architecture Overview

Use a layered, feature-oriented architecture:

- UI layer: Compose screens, navigation, UI state, UI events.
- Presentation layer: ViewModels, state reducers, one-time effects.
- Domain layer: use cases, domain models, validation, business rules.
- Data layer: repositories, remote data sources, local data sources, mappers.
- Platform layer: network, database, secure storage, WorkManager, notifications, analytics.

Dependency direction:

- Feature UI depends on feature ViewModels.
- ViewModels depend on use cases or repository interfaces.
- Repositories depend on Retrofit services, Room DAOs, and sync orchestration.
- Platform implementations are injected through Hilt.

No Compose screen should call Retrofit, Room, Firebase, or WorkManager directly.

## Feature Modules

For early production, a single Gradle `:app` module with strict package boundaries is acceptable. For scale, split into modules as build times and ownership grow.

Recommended modular target:

- `:app`
- `:core:common`
- `:core:designsystem`
- `:core:network`
- `:core:database`
- `:core:datastore`
- `:core:analytics`
- `:core:notifications`
- `:core:sync`
- `:feature:auth`
- `:feature:home`
- `:feature:pets`
- `:feature:health`
- `:feature:vets`
- `:feature:booking`
- `:feature:shop`
- `:feature:cart`
- `:feature:orders`
- `:feature:subscriptions`
- `:feature:account`
- `:feature:notifications`
- `:feature:chat`
- `:feature:referral`

Module rules:

- Feature modules cannot depend on each other directly.
- Cross-feature navigation uses route contracts exposed by each feature.
- Shared UI components live in `core:designsystem`.
- Shared API/session/database infrastructure lives in `core`.
- Domain models can live in feature modules unless reused widely.

## Package Structure

Recommended package layout inside each feature:

```text
com.tailtown.pawcare
  app
    MainActivity
    TailTownApp
    AppNavHost
    AppState

  core
    common
      result
      error
      dispatcher
      time
      validation
    designsystem
      component
      icon
      theme
    network
      ApiResult
      AuthInterceptor
      TokenAuthenticator
      NetworkModule
      dto
    database
      TailTownDatabase
      dao
      entity
      relation
      migration
    datastore
      SessionDataStore
      UserPreferencesStore
    analytics
      AnalyticsLogger
      AnalyticsEvent
      CrashReporter
    notifications
      FcmService
      NotificationRouter
      PushTokenRegistrar
    sync
      SyncManager
      SyncWorker
      OutboxWorker

  feature
    auth
      data
        AuthRepositoryImpl
        AuthRemoteDataSource
      domain
        AuthRepository
        LoginUseCase
        ExchangeFirebaseTokenUseCase
      presentation
        AuthViewModel
        AuthUiState
        AuthEffect
      ui
        LoginScreen
        OtpScreen

    pets
      data
      domain
      presentation
      ui
      navigation

    booking
      data
      domain
      presentation
      ui
      navigation
```

Feature package convention:

- `data`: DTO mappers, repository implementation, remote/local data sources.
- `domain`: repository interface, use cases, domain models.
- `presentation`: ViewModels, UI state, UI events, effects.
- `ui`: Compose screens and UI-only models.
- `navigation`: route definitions and graph builder.

## Hilt Architecture

Use Hilt for all construction and dependency wiring.

Application:

- `@HiltAndroidApp` on `TailTownApp`.
- `@AndroidEntryPoint` on `MainActivity`.
- `@HiltViewModel` for every ViewModel.

Core Hilt modules:

- `NetworkModule`: Retrofit, OkHttp, API services, interceptors.
- `DatabaseModule`: Room database and DAOs.
- `RepositoryModule`: binds repository interfaces to implementations.
- `DataStoreModule`: session and preferences storage.
- `WorkerModule`: WorkManager worker factories.
- `AnalyticsModule`: analytics logger and crash reporter.
- `DispatcherModule`: IO/default/main dispatchers.

Rules:

- No singleton service locators.
- No manual ViewModel factories for production features.
- Inject repository interfaces into ViewModels or use cases.
- Inject `CoroutineDispatcher` qualifiers for testability.
- Use Hilt test replacements for repository/data source tests.

## Navigation Architecture

Use Navigation Compose with feature-owned route contracts.

Recommended graph shape:

- Root graph:
  - `auth_graph`
  - `main_graph`

- Main graph:
  - bottom tab: Home
  - bottom tab: Vets
  - bottom tab: Shop
  - bottom tab: Inbox
  - bottom tab: Account

- Nested feature graphs:
  - Pets: pet profile, edit pet, health entry points.
  - Health: vaccinations, prescriptions, weight, medical records.
  - Booking: vet detail, slot selection, booking review, booking result, booking detail.
  - Shop: product list, category, product detail.
  - Cart/Checkout: cart, checkout, order placed.
  - Orders: order history, order detail, tracking.
  - Chat: conversation list, chat detail.
  - Notifications: notification center.
  - Subscriptions: list, detail, manage.

Route rules:

- Each feature exposes route builders, not raw string concatenation.
- Use typed arguments where possible.
- Validate all deep link arguments before using them.
- Auth guard protects main graph.
- Unknown or unauthorized deep links route to a safe fallback.
- Preserve bottom-tab state using `saveState` and `restoreState`.

Deep links:

- `tailtown://booking/{bookingId}`
- `tailtown://order/{orderId}`
- `tailtown://pet/{petId}`
- `tailtown://prescription/{prescriptionId}`
- `tailtown://notification/{notificationId}`
- `tailtown://chat/{conversationId}`
- `tailtown://product/{productId}`
- `tailtown://subscription/{subscriptionId}`

## State Management

Use MVVM with unidirectional data flow.

ViewModel responsibilities:

- Own screen UI state.
- Call use cases/repositories.
- Combine local cache and remote refresh state.
- Emit one-time effects for navigation, snackbars, dialogs, and permission prompts.
- Never expose mutable state to UI.

Recommended screen contract:

```text
UiState:
  loading
  refreshing
  content
  empty
  offline
  error

UiEvent:
  user actions from the screen

UiEffect:
  one-time actions like navigate, show snackbar, request permission
```

Preferred APIs:

- `StateFlow<UiState>` for state.
- `SharedFlow<UiEffect>` or `Channel` for one-time effects.
- `collectAsStateWithLifecycle()` in Compose.
- Immutable Kotlin data classes for state.

State rules:

- Compose screens render state only.
- ViewModels do not hold Android `Context` except where Hilt-safe abstractions are used.
- Long-running work runs in repositories, workers, or use cases.
- Every async operation maps to loading/content/error states.
- Empty state is separate from error state.

## Repository Layer

Repositories are the single source of truth for feature data.

Repository responsibilities:

- Expose domain models as `Flow`.
- Decide remote vs local source strategy.
- Persist remote responses into Room.
- Read UI data from Room for offline-first screens.
- Coordinate WorkManager sync for queued writes.
- Map network/database errors into domain errors.

Repository pattern:

- `RemoteDataSource`: Retrofit calls only.
- `LocalDataSource`: Room DAOs only.
- `RepositoryImpl`: orchestration, sync policy, mapping.
- `Mapper`: DTO/entity/domain conversions.

Example repository categories:

- `AuthRepository`
- `ProfileRepository`
- `PetRepository`
- `HealthRepository`
- `VetRepository`
- `BookingRepository`
- `ProductRepository`
- `CartRepository`
- `OrderRepository`
- `SubscriptionRepository`
- `NotificationRepository`
- `ChatRepository`
- `ReferralRepository`

Rules:

- UI never consumes DTOs.
- Room entities are not exposed to UI.
- Domain models are stable and independent from network schema.
- All writes return typed result objects.
- Transactional operations are online-only unless explicitly designed for outbox sync.

## Retrofit and Network Layer

Network components:

- Retrofit service interfaces per module.
- OkHttp auth interceptor.
- OkHttp token authenticator.
- Request ID interceptor.
- App metadata interceptor.
- Network connectivity monitor.
- Error response parser.

Headers:

- `Authorization: Bearer <accessToken>`
- `X-Request-Id`
- `X-App-Version`
- `X-Platform: android`
- `X-Device-Id`
- `Idempotency-Key` for booking, checkout, and subscription creation.

Token refresh:

- Access token refresh is centralized in `TokenAuthenticator`.
- Only one refresh call should run at a time.
- Refresh failure clears session and routes to auth.
- Refresh token is rotated and persisted atomically.

Network error mapping:

- `400`: validation/domain input error.
- `401`: session expired or unauthorized.
- `403`: forbidden.
- `404`: missing resource.
- `409`: conflict/version/idempotency/slot conflict.
- `422`: business rule failure.
- `429`: rate limited.
- `5xx`: server unavailable.
- IO timeout/no network: offline or retryable error.

Release rules:

- HTTPS only.
- No body logging in release.
- Redact authorization and sensitive headers.
- Use build flavors for environment base URLs.
- Optional certificate pinning only with a safe rotation process.

## Room Database

Room is used for offline-first reads, local cache, and sync state.

Recommended local entities:

- `UserProfileEntity`
- `PetEntity`
- `WeightRecordEntity`
- `PrescriptionEntity`
- `VaccineEntity`
- `MedicalRecordEntity`
- `VetEntity`
- `BookingEntity`
- `CategoryEntity`
- `ProductEntity`
- `CartEntity`
- `CartItemEntity`
- `OrderEntity`
- `OrderItemEntity`
- `SubscriptionEntity`
- `NotificationEntity`
- `ConversationEntity`
- `MessageEntity`
- `ReferralSummaryEntity`
- `OutboxEntity`
- `SyncMetadataEntity`

Database rules:

- Use explicit Room migrations.
- Never use destructive migration in production.
- Keep sensitive cached data encrypted at rest where feasible.
- Clear user-scoped tables on logout.
- Use user id as part of cache scoping.
- Store remote `version` fields for conflict detection.
- Store `lastSyncedAt` for cache freshness.

DAO rules:

- DAO read methods return `Flow`.
- DAO write methods are `suspend`.
- Multi-table writes use Room transactions.
- Queries filter by current user id where applicable.

## WorkManager Architecture

Use WorkManager for guaranteed background work.

Workers:

- `PushTokenSyncWorker`: registers FCM token with backend.
- `ProfileSyncWorker`: refreshes user profile and preferences.
- `PetHealthSyncWorker`: syncs pets, prescriptions, vaccines, weight records.
- `CatalogSyncWorker`: refreshes categories/products.
- `NotificationSyncWorker`: refreshes notification center.
- `ChatSyncWorker`: syncs recent conversations/messages.
- `OutboxWorker`: sends queued safe offline writes.
- `SubscriptionRefreshWorker`: refreshes subscription status.

WorkManager rules:

- Use constraints for network-required work.
- Use unique work names for sync categories.
- Use exponential backoff.
- Use idempotency keys for queued writes.
- Do not queue payments, checkout, or booking confirmation blindly.
- Workers use injected repositories through Hilt Worker integration.

Online-only operations:

- Login/register/token refresh.
- Booking slot hold and booking confirmation.
- Checkout/order placement.
- Payment actions.
- Subscription creation/cancellation if billing affected.

Queueable operations:

- Weight record creation.
- Notification read state.
- Chat messages if product accepts delayed sending.
- Profile preference updates.
- Draft medical notes/uploads after explicit UX support.

## Offline-First Strategy

Offline-first means core screens read from Room and refresh in background.

Offline-readable:

- Home summary.
- Pet profiles.
- Weight history.
- Vaccination schedule.
- Prescriptions.
- Medical timeline metadata.
- Product catalog cache.
- Cart snapshot.
- Order history snapshot.
- Subscriptions.
- Notifications.
- Conversations and recent messages.

Online-required:

- Auth.
- Vet slot availability.
- Booking confirmation.
- Checkout.
- Payment.
- Refund/cancel payment-sensitive operations.
- Real-time chat delivery confirmation.

Data flow:

1. UI observes Room through repository `Flow`.
2. Repository triggers remote refresh when data is stale.
3. Remote response is persisted to Room.
4. UI updates from Room automatically.
5. Errors are surfaced without deleting stale content.

Conflict strategy:

- Use server `version` values.
- If local queued write conflicts, mark outbox item as `CONFLICT`.
- UI shows conflict resolution for editable user data.
- Transactional operations fail fast and ask user to retry online.

## Caching Strategy

Cache types:

- Memory cache: short-lived in repositories for current screen/session.
- Room cache: persistent user-scoped cache.
- HTTP cache: limited use for public-ish catalog and static content.
- Image cache: Coil memory/disk cache.

Recommended TTLs:

- User profile: 24 hours, refresh on app start.
- Pets: 24 hours, refresh on app start and after edits.
- Health records: 6-24 hours, refresh on health tab open.
- Vet list: 15-60 minutes.
- Vet slots: no persistent cache beyond short UI session; always revalidate before booking.
- Categories/products: 1-6 hours.
- Cart: refresh on cart open and after every mutation.
- Orders: 15-60 minutes, refresh on order tab open.
- Notifications: 5-15 minutes plus FCM-triggered refresh.
- Chat: refresh on conversation open and push/new-message event.

Invalidation:

- Auth logout clears all user-scoped cache.
- Product/cart mutations refresh cart.
- Booking create/cancel refresh bookings, slots, notifications.
- Order create refresh cart and orders.
- Notification push invalidates notification cache.
- Chat push invalidates conversation/message cache.

## Error Handling

Use typed errors across data/domain layers.

Error categories:

- `NetworkUnavailable`
- `Timeout`
- `Unauthorized`
- `Forbidden`
- `NotFound`
- `Validation`
- `Conflict`
- `RateLimited`
- `ServerUnavailable`
- `PaymentFailed`
- `SlotUnavailable`
- `OutOfStock`
- `Unknown`

UI behavior:

- Preserve stale content when refresh fails.
- Show inline field errors for validation.
- Show retry actions for network/server failures.
- Route to auth on unrecoverable `401`.
- Show conflict-specific messages for booking slot, cart stock, and version conflicts.
- Avoid exposing raw backend stack traces or technical messages.

Retry rules:

- Retry safe GET refreshes.
- Retry idempotent writes with idempotency keys.
- Do not retry payments unless provider flow supports it safely.
- Use exponential backoff for WorkManager.

## Analytics Integration

Use a central `AnalyticsLogger` interface injected into ViewModels/use cases.

Client event requirements:

- No raw medical notes.
- No chat message body.
- No address text.
- No tokens.
- No payment credentials.
- No prescription free-text instructions.

Core events:

- `app_opened`
- `login_started`
- `login_completed`
- `login_failed`
- `pet_created`
- `pet_updated`
- `weight_record_added`
- `prescription_viewed`
- `dose_marked`
- `vaccine_added`
- `vet_search_viewed`
- `vet_profile_viewed`
- `booking_slot_selected`
- `booking_created`
- `booking_cancelled`
- `product_list_viewed`
- `product_viewed`
- `cart_item_added`
- `cart_viewed`
- `checkout_started`
- `order_created`
- `subscription_created`
- `subscription_paused`
- `notification_opened`
- `conversation_opened`
- `message_sent`
- `referral_shared`

Required properties:

- `user_id` after login.
- `anonymous_id` before login.
- `session_id`.
- `app_version`.
- `screen_name`.
- Domain ids only when safe.
- Funnel step names for booking and checkout.

Crash reporting:

- Set user id after login.
- Log non-sensitive breadcrumbs.
- Attach backend request id to network failure breadcrumbs.
- Track ANRs and startup performance.

## Notification Architecture

Use Firebase Cloud Messaging for push notifications.

Components:

- `FcmService`: receives token updates and push messages.
- `PushTokenRegistrar`: sends FCM token to backend.
- `NotificationRouter`: maps deep links to app routes.
- `NotificationPermissionManager`: handles Android 13+ runtime permission.
- `NotificationRepository`: stores notification center state in Room.

Notification flow:

1. Firebase issues or refreshes FCM token.
2. App stores token locally and enqueues `PushTokenSyncWorker`.
3. Backend sends push notification.
4. App receives push and displays system notification if appropriate.
5. User taps notification.
6. `NotificationRouter` validates deep link and navigates.
7. App marks notification read through repository.

Notification categories:

- Appointment reminder.
- Booking update.
- Medication reminder.
- Vaccination due.
- Order status.
- Subscription delivery.
- Chat message.
- Referral/reward.
- Promotion.
- System/account.

Rules:

- Respect backend notification preferences.
- Transactional notifications may be mandatory.
- Deep links must be validated before navigation.
- Notification payloads must contain ids, not sensitive content.
- Chat notification body should be privacy-safe.
- Push token is user/device scoped.

## Security Architecture

Mobile security requirements:

- HTTPS-only release traffic.
- Encrypted token storage.
- Clear local cache on logout.
- Disable request/response body logs in release.
- Protect exported activities.
- Validate deep links.
- Use Play Integrity or device attestation for high-risk flows if fraud grows.
- Avoid storing raw payment data.
- Use signed URLs for uploads/downloads.

Sensitive local data:

- Tokens.
- User profile.
- Addresses.
- Medical records.
- Prescriptions.
- Chat messages.
- Payment method metadata.

Recommended handling:

- Tokens in Android Keystore-backed encrypted storage.
- Room database encrypted if threat model requires it.
- User-scoped cache wipe on logout.
- Avoid screenshots on high-risk payment/auth screens if needed.

## Testing Strategy

Unit tests:

- ViewModels with fake repositories.
- Use cases.
- Mappers.
- Validators.
- Error mapping.
- Sync conflict logic.

Repository tests:

- Fake Retrofit service.
- In-memory Room.
- Network/local source merge behavior.
- Cache invalidation.
- Offline fallback.

Worker tests:

- WorkManager test driver.
- Retry/backoff.
- Network constraints.
- Outbox success/failure/conflict.

UI tests:

- Compose screen rendering for loading/content/empty/error/offline states.
- Navigation routes.
- Form validation.
- Critical user journeys.

End-to-end smoke tests:

- Login.
- Add pet.
- Add weight record.
- View vet and select slot.
- Create booking.
- Add product to cart.
- Checkout/order creation in staging.
- Open notification deep link.
- Send chat message.

Test infrastructure:

- Hilt test modules.
- MockWebServer for API tests.
- Room in-memory DB.
- Turbine for Flow tests.
- Compose UI test framework.
- Firebase emulator where applicable.

## Performance and Scale

For 100,000+ users, the Android app must minimize backend and device load.

Requirements:

- Paginate product, vet, notification, order, and chat lists.
- Debounce search.
- Avoid duplicate network calls across recomposition.
- Use Room as single source of truth for frequently viewed data.
- Use image loading with size constraints and Coil cache.
- Use stable Compose keys in lazy lists.
- Avoid large object graphs in navigation arguments.
- Use baseline profiles for startup and scrolling performance.
- Use background sync with constraints instead of aggressive polling.

Performance targets:

- Cold start P50 under 2 seconds on common devices.
- Main screen first content from cache under 500 ms when warm.
- Product/vet list pagination under 300 ms local render after data arrival.
- Crash-free sessions above 99.5%.
- ANR rate below Play Console bad behavior thresholds.

## Release Architecture

Build variants:

- `devDebug`
- `stagingDebug`
- `stagingRelease`
- `prodRelease`

Each variant configures:

- API base URL.
- Firebase project.
- Analytics stream.
- Crash reporting flag.
- Logging level.
- Feature flags.

Release requirements:

- R8/minification enabled.
- Resource shrinking enabled.
- Debug logs disabled.
- Cleartext disabled.
- Signing through Play App Signing.
- Crashlytics enabled.
- Strict versioning and staged rollout.

## Migration From Current App

Current gaps visible in the Android project:

- Hilt is not wired.
- Room is not implemented.
- WorkManager is not implemented.
- Retrofit is a singleton.
- Token storage uses SharedPreferences.
- Base URL is hardcoded to a LAN IP.
- Cleartext traffic is enabled.
- Manual ViewModel factories are used.
- Some repositories still use stub data or direct Retrofit access.

Recommended migration plan:

1. Add build flavors and secure network config.
2. Introduce Hilt and migrate Retrofit/session dependencies.
3. Replace SharedPreferences token store with encrypted storage.
4. Add Room database and DAOs for profile, pets, products, cart, orders, notifications.
5. Convert repositories to offline-first one feature at a time.
6. Add WorkManager sync and push token registration.
7. Standardize ViewModel state/effect contracts.
8. Add analytics and crash reporting.
9. Add test coverage for critical flows.
10. Remove stub repositories from production builds.

