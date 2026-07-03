# TailTown Production Gaps

## Severity Scale

- Critical: Blocks production launch or creates severe security/data-loss/payment risk.
- High: Must be fixed before scaling beyond controlled beta.
- Medium: Important for reliability, maintainability, or user experience.
- Low: Should be scheduled, but does not block early production if monitored.

Effort scale:

- S: 1-3 engineering days.
- M: 1-2 engineering weeks.
- L: 3-6 engineering weeks.
- XL: Multi-team or multi-month initiative.

## Executive Summary

TailTown has broad product surface area, but the implementation is still prototype-to-MVP maturity. The Android app contains screens and Retrofit integrations across most domains, and the backend exposes Kotlin/Spring REST modules for auth, pets, vets, bookings, shop, orders, notifications, chat, referrals, and subscriptions. However, the system is not yet production-ready for 100,000+ users.

The highest-risk gaps are authentication hardening, authorization consistency, payment and order correctness, booking slot safety, database migration discipline, observability, secure mobile storage, CI/CD, and deployment automation.

## Authentication

### Gap: Firebase phone auth is synced to backend through synthetic email/password

- Severity: Critical
- Why it is a problem: The Android app currently derives backend credentials from Firebase UID. This is not a production-grade identity exchange and makes account linking, revocation, provider validation, and audit weak.
- Recommended fix: Add backend Firebase ID token exchange. Android sends Firebase ID token; backend verifies it with Firebase Admin SDK; backend links/creates a TailTown user and issues TailTown access/refresh tokens.
- Effort: M

### Gap: Refresh tokens are not persisted, rotated, or revocable

- Severity: Critical
- Why it is a problem: A stolen refresh token can remain valid until expiry. Logout cannot reliably revoke sessions. Replay detection is impossible.
- Recommended fix: Add `refresh_tokens` or `sessions` table storing token hashes, device id, expiry, revocation state, and token family. Rotate refresh token on every refresh and revoke token family on replay.
- Effort: M

### Gap: JWT access token lifetime is too long for production defaults

- Severity: High
- Why it is a problem: Current access token expiry is 24 hours. Long-lived bearer tokens increase blast radius if leaked from device logs, memory, proxy tooling, or compromised storage.
- Recommended fix: Use 10-15 minute access tokens and long-lived rotated refresh tokens.
- Effort: S

### Gap: Android stores tokens in SharedPreferences

- Severity: Critical
- Why it is a problem: Plain SharedPreferences is not appropriate for bearer tokens on a production mobile app.
- Recommended fix: Use EncryptedSharedPreferences, encrypted DataStore, or Android Keystore-backed session storage. Clear cached sensitive data on logout.
- Effort: S-M

### Gap: Logout does not revoke backend sessions

- Severity: High
- Why it is a problem: Android Firebase sign-out and local token clearing do not invalidate backend refresh tokens.
- Recommended fix: Add `/auth/logout` and `/auth/logout-all` endpoints that revoke current or all refresh token sessions.
- Effort: M

### Gap: No account recovery, password reset, or session management

- Severity: Medium
- Why it is a problem: Users cannot safely recover accounts, view active devices, or revoke old sessions.
- Recommended fix: Add password reset flow, device session listing, and session revocation.
- Effort: M

## Authorization

### Gap: No role-based access control

- Severity: Critical
- Why it is a problem: The product needs customers, vets, support agents, catalog managers, finance, operations, and admins. Current backend effectively treats authenticated users as customers.
- Recommended fix: Add roles/permissions model with method-level authorization. Separate customer APIs from admin/provider APIs.
- Effort: L

### Gap: Ownership checks are inconsistent across modules

- Severity: Critical
- Why it is a problem: Some services enforce ownership, but notification mark-read and other resource operations need consistent user scoping. Any missing ownership check can expose user data.
- Recommended fix: Create shared authorization helpers/repository methods like `findByIdAndUserId`. Add security tests for every user-owned resource.
- Effort: M

### Gap: No admin audit trail

- Severity: Critical
- Why it is a problem: Support/admin workflows will access personal, medical, order, chat, and payment-adjacent data. Without audit logs, abuse and accidental access are invisible.
- Recommended fix: Add immutable `audit_logs` table and enforce audit reason for sensitive admin reads/writes.
- Effort: M-L

### Gap: Vets have no authenticated provider identity

- Severity: High
- Why it is a problem: Vets cannot manage schedules, bookings, prescriptions, or chat securely as first-class actors.
- Recommended fix: Add provider accounts linked to vet profiles, provider roles, and provider-scoped APIs.
- Effort: L

## Database

### Gap: Existing JPA entities are not fully aligned with production schema

- Severity: High
- Why it is a problem: A production schema migration now exists, but the current entity model still reflects MVP assumptions and compatibility columns. This can cause drift between database design and domain code.
- Recommended fix: Align entities with the production schema in phases. Add explicit `@Version`, soft-delete filters, normalized models, and migration-backed constraints.
- Effort: L

### Gap: Optimistic locking is not implemented in JPA entities

- Severity: High
- Why it is a problem: Concurrent updates can overwrite booking, cart, subscription, profile, inventory, and order state.
- Recommended fix: Add `@Version` fields to mutable entities and surface `409 Conflict` on version mismatch.
- Effort: M

### Gap: Soft delete is not enforced in repositories

- Severity: High
- Why it is a problem: Schema supports `deleted_at`, but application queries can still return deleted records unless repository methods filter them.
- Recommended fix: Add repository conventions, query filters, and service-level guarantees for active records only.
- Effort: M

### Gap: Missing status history and ledger tables

- Severity: High
- Why it is a problem: Orders, bookings, payments, inventory, subscriptions, and referrals need auditable state transitions. Current models mostly mutate status in place.
- Recommended fix: Add status history tables and immutable ledgers for inventory, rewards, payments, and refunds.
- Effort: L

### Gap: Seed data runs at application startup

- Severity: Medium
- Why it is a problem: Startup seeders can mutate production unexpectedly and mix demo data management with runtime behavior.
- Recommended fix: Move seed/reference data into Flyway migrations, admin tooling, or environment-gated dev fixtures.
- Effort: S-M

### Gap: No backup, restore, retention, or purge implementation

- Severity: Critical
- Why it is a problem: Medical records, orders, chat, and identity data need recoverability and retention discipline.
- Recommended fix: Configure PITR backups, restore drills, retention policies, and account deletion/anonymization workflows.
- Effort: M-L

## Booking Flow

### Gap: Booking creation does not use slot inventory

- Severity: Critical
- Why it is a problem: Current booking accepts provider name and scheduled time, allowing double-bookings and bookings outside vet availability.
- Recommended fix: Use `booking_slots` with available/held/booked state. Booking creation must atomically reserve a slot.
- Effort: L

### Gap: No booking hold flow

- Severity: High
- Why it is a problem: Users can select the same slot concurrently during checkout or confirmation.
- Recommended fix: Add slot hold endpoint with TTL, Redis or DB-backed hold state, and cleanup job.
- Effort: M

### Gap: Cancellation and reschedule policies are incomplete

- Severity: High
- Why it is a problem: Production booking needs clear windows, fees/refunds, provider notifications, and status history.
- Recommended fix: Add booking state machine, cancellation policy service, reschedule endpoint, and status history.
- Effort: L

### Gap: Booking payments are not connected

- Severity: High
- Why it is a problem: Paid bookings cannot be safely confirmed, refunded, or reconciled.
- Recommended fix: Add booking payment intent, payment callbacks, refund flow, and booking/payment state coordination.
- Effort: L

### Gap: No vet-side acceptance or provider operations

- Severity: Medium
- Why it is a problem: Vets and operations teams cannot manage availability, cancellations, no-shows, or completion.
- Recommended fix: Add provider/admin booking workflows and APIs.
- Effort: L

## Notifications

### Gap: Notifications are stored but not dispatched

- Severity: High
- Why it is a problem: Persisted notifications alone do not deliver appointment reminders, medication reminders, order updates, or chat alerts.
- Recommended fix: Add FCM token registration, notification dispatch worker, provider integration, delivery status, and retries.
- Effort: L

### Gap: No scheduled reminder engine

- Severity: High
- Why it is a problem: Vaccinations, medications, bookings, and subscriptions require reliable time-based notifications.
- Recommended fix: Add scheduler/worker backed by DB or queue. Emit reminder events from prescriptions, vaccines, bookings, and subscriptions.
- Effort: L

### Gap: Preferences are too coarse and not enforced at send time

- Severity: Medium
- Why it is a problem: Users can set preferences, but send logic must enforce them consistently while preserving mandatory transactional messages.
- Recommended fix: Centralize notification policy checks before dispatch.
- Effort: M

### Gap: No notification templates or campaign controls

- Severity: Medium
- Why it is a problem: Hardcoded notification copy cannot support localization, experiments, or marketing approvals.
- Recommended fix: Add templates, template variables, campaign approval, and rate-limited sends.
- Effort: M-L

## Chat

### Gap: Chat is REST-only and not real-time

- Severity: High
- Why it is a problem: Support and vet chat needs timely delivery, typing/read state, and push notifications.
- Recommended fix: Add WebSocket/SSE or managed real-time provider. Keep REST pagination as fallback.
- Effort: L

### Gap: Conversations support only simple ownership model

- Severity: High
- Why it is a problem: Real chat needs participants, support assignment, vet participation, and admin workflows.
- Recommended fix: Add conversation participants, assignment, status, escalation, and permissions.
- Effort: L

### Gap: No attachment handling

- Severity: Medium
- Why it is a problem: Users need to share prescriptions, reports, invoices, and images. Direct URLs without object storage controls are unsafe.
- Recommended fix: Add signed upload/download URLs, object storage, file type/size validation, malware scanning where applicable.
- Effort: M-L

### Gap: No moderation or abuse controls

- Severity: Medium
- Why it is a problem: Chat can be abused through spam, harassment, or unsafe content.
- Recommended fix: Add rate limits, reporting, block/escalation controls, and moderation audit trail.
- Effort: M

## Orders

### Gap: Checkout creates orders directly from cart without payment confirmation

- Severity: Critical
- Why it is a problem: Orders can be created without confirmed payment, inventory reservation, or reliable idempotency.
- Recommended fix: Implement checkout sessions, payment intents, webhook confirmation, order placement after payment success, and idempotency keys.
- Effort: L

### Gap: No inventory reservation or ledger

- Severity: Critical
- Why it is a problem: Concurrent orders can oversell products. Stock changes are not auditable.
- Recommended fix: Add inventory ledger and reservation model. Deduct or reserve stock atomically during checkout/payment confirmation.
- Effort: L

### Gap: Order state machine is incomplete

- Severity: High
- Why it is a problem: Production fulfillment needs placed, packed, shipped, out-for-delivery, delivered, cancelled, returned, refunded states and transition validation.
- Recommended fix: Add order status transition service and `order_status_history`.
- Effort: M-L

### Gap: No delivery/shipment tracking

- Severity: Medium
- Why it is a problem: Users and support cannot track ecommerce fulfillment.
- Recommended fix: Add shipment table, tracking provider integration, and order tracking endpoint.
- Effort: M-L

### Gap: No returns, cancellations, refunds, or invoices

- Severity: High
- Why it is a problem: Commerce support and compliance need post-order workflows.
- Recommended fix: Add cancellation/refund/return APIs, invoice generation, and payment provider reconciliation.
- Effort: L

## Payments

### Gap: No payment provider integration

- Severity: Critical
- Why it is a problem: Ecommerce, bookings, and subscriptions cannot be production-grade without payment authorization, capture, webhook verification, refunds, and reconciliation.
- Recommended fix: Integrate a payment provider such as Razorpay, Stripe, Cashfree, or PayU depending on market. Store provider tokens and payment metadata only.
- Effort: L

### Gap: Payment methods are locally modeled without provider tokenization flow

- Severity: Critical
- Why it is a problem: Storing or simulating payment methods without PCI-safe tokenization is dangerous and not usable for real charges.
- Recommended fix: Use provider setup intents/tokens. Store masked label, provider, token reference, and default flag only.
- Effort: M-L

### Gap: No webhook signature verification

- Severity: Critical
- Why it is a problem: Fake payment callbacks could mark unpaid orders or bookings as paid.
- Recommended fix: Verify webhook signatures, store webhook events, process idempotently, and reconcile provider state.
- Effort: M

### Gap: No refund or settlement model

- Severity: High
- Why it is a problem: Cancellations, returns, failed deliveries, and booking cancellations require traceable financial operations.
- Recommended fix: Add `payments`, `refunds`, `payment_events`, and settlement reconciliation jobs.
- Effort: L

## Analytics

### Gap: No analytics event taxonomy or implementation

- Severity: High
- Why it is a problem: Product decisions and funnel health cannot be measured across onboarding, booking, checkout, subscriptions, and health engagement.
- Recommended fix: Define client and backend event schema. Implement Android analytics SDK and backend domain event outbox.
- Effort: M-L

### Gap: No conversion funnel tracking

- Severity: Medium
- Why it is a problem: Vet discovery and ecommerce funnels cannot be optimized or debugged.
- Recommended fix: Track booking funnel, checkout funnel, subscription funnel, health engagement, notification opens, and referral attribution.
- Effort: M

### Gap: No privacy governance for analytics

- Severity: High
- Why it is a problem: Analytics must not leak medical details, chat content, addresses, tokens, or payment data.
- Recommended fix: Add event schema review, PII classification, allowlist properties, and deletion/export support.
- Effort: M

### Gap: No warehouse or reporting pipeline

- Severity: Medium
- Why it is a problem: Operational metrics across orders, bookings, providers, and support need reliable reporting.
- Recommended fix: Send backend domain events to warehouse via queue/outbox. Build BI dashboards.
- Effort: L

## Caching

### Gap: No Redis/cache layer

- Severity: High
- Why it is a problem: Rate limiting, idempotency, booking holds, product catalog cache, vet discovery cache, and session metadata all need fast shared state.
- Recommended fix: Add Redis with clear ownership: rate limits, idempotency keys, slot holds, short-lived catalog/vet caches.
- Effort: M

### Gap: No HTTP or data cache strategy on Android

- Severity: Medium
- Why it is a problem: Product, vet, notification, order, and health screens will feel slow and brittle on weak networks.
- Recommended fix: Use Room-backed local cache, cache headers where appropriate, and stale-while-refresh UI.
- Effort: L

### Gap: No cache invalidation strategy

- Severity: Medium
- Why it is a problem: Stale product prices, vet availability, or promotions can create bad UX and transactional failures.
- Recommended fix: Define TTLs and event-based invalidation for products, categories, promotions, vets, and slots.
- Effort: M

## Offline Support

### Gap: Room is not implemented despite being part of target stack

- Severity: High
- Why it is a problem: Health, pet profiles, orders, notifications, and chat history should remain readable offline.
- Recommended fix: Add Room database with DAOs for pets, health records, products, cart snapshot, orders, notifications, conversations, and messages.
- Effort: L

### Gap: No sync conflict policy

- Severity: Medium
- Why it is a problem: Offline edits to pets, addresses, weight records, or preferences can conflict with server changes.
- Recommended fix: Use version fields and define per-resource merge/replace behavior. Surface conflicts in UI when needed.
- Effort: M-L

### Gap: Offline writes are not categorized by safety

- Severity: Medium
- Why it is a problem: Some operations are safe to queue, but booking, checkout, and payment must not be queued blindly.
- Recommended fix: Categorize operations: offline-readable, queued-idempotent writes, and online-only transactional writes.
- Effort: M

### Gap: No local sensitive data cleanup policy

- Severity: High
- Why it is a problem: Medical records, prescriptions, chat, and profile data may remain on device after logout.
- Recommended fix: Clear or re-key encrypted local database on logout/account switch.
- Effort: M

## Monitoring

### Gap: Backend lacks production observability setup

- Severity: Critical
- Why it is a problem: Without metrics, structured logs, traces, and alerts, production incidents cannot be detected or debugged.
- Recommended fix: Add Spring Boot Actuator, Micrometer, Prometheus/OpenTelemetry, structured JSON logs, request IDs, and dashboard alerts.
- Effort: M

### Gap: No Android crash and performance monitoring

- Severity: High
- Why it is a problem: Crashes, ANRs, slow startup, and network failures will be invisible.
- Recommended fix: Add Firebase Crashlytics and Performance Monitoring or equivalent. Include app version and backend request ID correlation where possible.
- Effort: S-M

### Gap: No domain health metrics

- Severity: High
- Why it is a problem: API uptime alone does not show checkout failure, payment webhook failure, slot conflicts, notification failures, or subscription renewal failures.
- Recommended fix: Emit business metrics for auth, booking, checkout, orders, payments, notifications, chat, subscriptions, and referrals.
- Effort: M

### Gap: No alerting or on-call runbooks

- Severity: High
- Why it is a problem: Incidents will rely on users reporting failures.
- Recommended fix: Define alerts, escalation policy, runbooks, and incident severity process.
- Effort: M

## Deployment

### Gap: No production deployment topology

- Severity: Critical
- Why it is a problem: The backend is configured for local PostgreSQL and local port use. There is no container, infrastructure, or runtime environment definition.
- Recommended fix: Containerize backend, deploy behind TLS load balancer, use managed PostgreSQL, Redis, object storage, and worker runtime.
- Effort: L

### Gap: Android uses hardcoded LAN API URL

- Severity: Critical
- Why it is a problem: Production builds cannot point to a developer machine IP. This also encourages cleartext traffic.
- Recommended fix: Add build flavors and BuildConfig base URLs for dev, staging, and prod.
- Effort: S

### Gap: Android cleartext traffic is enabled

- Severity: Critical
- Why it is a problem: Production user traffic must use HTTPS. Cleartext bearer token transport is unacceptable.
- Recommended fix: Disable `usesCleartextTraffic` for release and configure network security config for dev-only exceptions.
- Effort: S

### Gap: No object storage deployment for files/images

- Severity: High
- Why it is a problem: Medical records, chat attachments, pet avatars, vet avatars, and product images need durable, secure storage.
- Recommended fix: Add S3/GCS/Azure Blob storage with signed URLs and lifecycle policies.
- Effort: M

### Gap: No environment separation

- Severity: High
- Why it is a problem: Dev, staging, and production can accidentally share data, Firebase projects, analytics, or payment credentials.
- Recommended fix: Create separate environment configs, databases, Firebase projects, payment modes, object buckets, and secrets.
- Effort: M

## CI/CD

### Gap: No visible CI pipeline

- Severity: High
- Why it is a problem: Builds, tests, linting, and deployment checks are manual and error-prone.
- Recommended fix: Add CI for backend compile/test, Android assemble/test, static analysis, migration validation, dependency scanning.
- Effort: M

### Gap: No automated database migration validation

- Severity: High
- Why it is a problem: Flyway migrations can fail only at deployment time if not validated in CI.
- Recommended fix: Run migrations against ephemeral PostgreSQL in CI using Testcontainers or service containers.
- Effort: M

### Gap: No release promotion workflow

- Severity: Medium
- Why it is a problem: Production deploys and Android releases need controlled staging, smoke tests, and rollback/roll-forward plans.
- Recommended fix: Add staging deployment, smoke tests, manual approval, production rollout, and Android internal/closed track promotion.
- Effort: M-L

### Gap: No dependency/security scanning

- Severity: Medium
- Why it is a problem: Vulnerable libraries can enter production unnoticed.
- Recommended fix: Add Dependabot/Renovate, Gradle dependency checks, secret scanning, and container image scanning.
- Effort: S-M

## Security

### Gap: Default JWT secret exists in configuration

- Severity: Critical
- Why it is a problem: Production could accidentally run with a known/default signing key.
- Recommended fix: Remove default production secret. Load secrets from secret manager and fail startup if missing/weak.
- Effort: S

### Gap: HTTP body logging is enabled in Android networking

- Severity: High
- Why it is a problem: Tokens, profile data, addresses, prescriptions, and chat content can leak into logs.
- Recommended fix: Disable body logging in release builds. Redact authorization and sensitive fields.
- Effort: S

### Gap: No rate limiting

- Severity: Critical
- Why it is a problem: Auth, OTP exchange, chat, checkout, referral, and booking endpoints are vulnerable to brute force and abuse.
- Recommended fix: Add Redis-backed rate limiting by IP, user id, device id, email/phone, and endpoint group.
- Effort: M

### Gap: No CORS/security header policy

- Severity: Medium
- Why it is a problem: Admin panel and API exposure need controlled origins and browser security headers.
- Recommended fix: Configure CORS per environment and add gateway/security headers.
- Effort: S

### Gap: No file upload security

- Severity: High
- Why it is a problem: Medical/chat attachments can carry malware or oversized files.
- Recommended fix: Enforce signed uploads, size/type limits, content scanning, private buckets, and expiring download URLs.
- Effort: M-L

### Gap: No privacy/account deletion workflow

- Severity: High
- Why it is a problem: Users need data export/deletion, and the business needs retention rules for medical, order, chat, and payment records.
- Recommended fix: Add account deletion request flow, anonymization, retention matrix, and export endpoint/process.
- Effort: L

### Gap: No secrets management

- Severity: Critical
- Why it is a problem: Database passwords, JWT keys, Firebase credentials, payment keys, and webhook secrets must not live in code or local config defaults.
- Recommended fix: Use cloud secret manager or Vault. Inject secrets at runtime. Rotate secrets periodically.
- Effort: M

## Priority Launch Plan

### Must Fix Before Any Public Production Launch

- Firebase backend token exchange.
- Refresh token persistence, rotation, and revocation.
- Encrypted Android token storage.
- Disable release cleartext traffic and hardcoded LAN URL.
- Remove default JWT secret.
- Add RBAC foundation and ownership tests.
- Flyway migration validation in CI.
- Booking slot reservation.
- Payment provider integration.
- Idempotent checkout and order creation.
- Inventory reservation/ledger.
- Backend observability and alerts.
- Production deployment topology and backups.

### Must Fix Before Scaling to 100,000+ Users

- Redis caching/rate limiting/idempotency.
- Room offline cache.
- Notification dispatch pipeline.
- Real-time chat or managed chat provider.
- Analytics event pipeline.
- Admin panel with audit logs.
- Object storage with signed URLs.
- Subscription billing lifecycle.
- Order fulfillment/refund workflows.
- Status history and ledgers.

### Can Follow After Stable Production Foundation

- Advanced campaign tooling.
- Provider-side mobile/web portal.
- Fine-grained analytics warehouse models.
- Search/geospatial optimization.
- Feature flags and experimentation platform.
- Advanced fraud/risk scoring.

