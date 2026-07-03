# TailTown System Architecture

## Purpose

TailTown is a pet care platform combining consumer pet health, vet discovery and booking, ecommerce, subscription delivery, notifications, chat, and referral workflows. The current system consists of:

- Android client: Kotlin, Jetpack Compose, Navigation Compose, Retrofit, MVVM-style ViewModels.
- Backend: Kotlin, Spring Boot 3, JPA/Hibernate, PostgreSQL, JWT authentication.

The production target is a reliable, observable, secure platform serving 100,000+ registered users with clear separation between customer-facing mobile flows, backend domain services, administrative operations, analytics, and operational infrastructure.

## Current Implementation Snapshot

The current Android application has screens and ViewModels for onboarding, home, pet profile, vet discovery, bookings, health, shop, cart, checkout, orders, account, subscriptions, referral, inbox, chat, and notifications. It uses a singleton Retrofit client, SharedPreferences-backed token storage, Firebase phone authentication, backend email/password authentication, and manual ViewModel factories.

The current backend exposes REST endpoints for authentication, users, pets, vets, bookings, products, cart, orders, subscriptions, promotions, notifications, conversations, and referral. It uses Spring Security with a JWT filter, JPA repositories, PostgreSQL configuration, and boot-time seed data.

Important deltas from the stated target stack:

- Hilt is not currently wired into the Android app.
- Room is not currently present for offline storage or caching.
- Flyway is not currently configured with migration files; Hibernate `ddl-auto: update` is enabled.
- Admin panel APIs and roles are not yet implemented.
- Payment, inventory reservation, real-time chat, notification dispatch, appointment slot inventory, and subscription billing are not yet production-grade.

## High-Level Architecture

Recommended production architecture:

- Android App
  - Presentation: Jetpack Compose screens, navigation graphs, UI state models.
  - ViewModel layer: business-facing screen state, intent handling, coroutine orchestration.
  - Domain layer: use cases for auth, pets, booking, health, shop, chat, notifications.
  - Data layer: repositories combining Retrofit APIs, Room cache, DataStore or encrypted token storage.
  - Device services: FCM, location, camera/file picker, deep links, analytics.

- API Backend
  - REST API service using Spring Boot.
  - Stateless JWT authentication for mobile clients.
  - Domain modules: auth, users, pets, vets, booking, health, shop, orders, subscriptions, notifications, chat, referral.
  - PostgreSQL primary transactional store.
  - Redis for cache, rate limits, token/session metadata, idempotency, and ephemeral chat/notification state.
  - Object storage for medical records, prescriptions, pet images, product images, and chat attachments.
  - Queue/event bus for async notification, email/SMS, order, subscription, and analytics processing.

- Admin Panel
  - Separate web application or secured backend module.
  - Role-based access for support, operations, catalog, vet operations, finance, and super admin.
  - Uses admin-only APIs with audit logs and stricter controls.

- Analytics Platform
  - Client analytics events from Android.
  - Backend domain events from transactional workflows.
  - Event pipeline to warehouse/lakehouse.
  - Dashboards for growth, retention, commerce, bookings, health engagement, support, and reliability.

## Recommended Service Boundaries

At 100,000+ users, a modular monolith is appropriate initially if module boundaries are disciplined. Split into microservices only when load, team ownership, or operational isolation justifies the cost.

Recommended modular monolith domains:

- Identity and Access: users, credentials, tokens, roles, device sessions.
- Pet Profile and Health: pets, weight logs, prescriptions, dose logs, vaccinations, medical records.
- Vet Marketplace: vet profiles, clinics, service areas, schedules, reviews, availability.
- Booking: appointment lifecycle, slot reservation, cancellation, reschedule, reminders.
- Commerce: catalog, cart, checkout, payment intents, order lifecycle, inventory.
- Subscription Delivery: recurring plans, billing cycles, delivery schedule, pause/resume.
- Notifications: preferences, templates, dispatch, delivery logs.
- Chat and Support: conversations, messages, attachments, read state, escalation.
- Referral and Rewards: referral codes, attribution, reward ledger.
- Admin and Audit: operational workflows and immutable audit trails.

## Request Flow

1. Android obtains authentication through Firebase phone auth or backend email/password.
2. Android stores access and refresh tokens securely.
3. Android calls backend REST endpoints with `Authorization: Bearer <token>`.
4. Backend JWT filter validates the token, loads the user, and binds the principal.
5. Controllers validate requests and delegate to domain services.
6. Services enforce ownership, status transitions, and business rules.
7. Repositories persist state in PostgreSQL.
8. Domain events are emitted for analytics, notifications, reminders, fulfillment, or audit.
9. Android receives normalized API responses and updates UI state.

## Scalability Targets

For 100,000+ users, design for:

- 10,000-25,000 daily active users.
- 500-2,000 peak concurrent users depending on marketing and notification campaigns.
- Read-heavy catalog, vet discovery, notifications, and home feed traffic.
- Write-sensitive booking, checkout, payment, prescription, and chat flows.
- P95 API latency under 300 ms for cached/read flows and under 800 ms for transactional writes.
- P99 latency tracked separately for checkout, booking, and login.

## Production Architecture Principles

- Keep mobile and backend contracts versioned.
- Make all payment, checkout, booking, and subscription operations idempotent.
- Use database migrations only; disable schema mutation by Hibernate in production.
- Treat medical records and prescriptions as sensitive user data.
- Validate resource ownership at the service/repository boundary, not only in controllers.
- Emit domain events after every meaningful state transition.
- Build administrative actions with audit logs from day one.
- Prefer eventual consistency for notifications, analytics, reminders, and fulfillment integrations.
- Preserve a single source of truth for orders, payments, appointments, subscriptions, and inventory.

## Critical Cross-Cutting Components

- API Gateway or Load Balancer: TLS termination, WAF, request size limits, routing, health checks.
- Authentication: JWT access tokens, refresh token rotation, token revocation support.
- Authorization: customer, vet, admin, support, catalog, operations roles.
- Caching: Redis for frequently-read public-ish data like vet lists, product catalog, promotions.
- Asynchronous Jobs: reminders, subscription renewals, notification dispatch, payment reconciliation.
- Object Storage: signed upload/download URLs for medical records, prescriptions, chat attachments, images.
- Observability: metrics, logs, traces, audit events, crash reporting, ANR tracking.
- Configuration: environment-specific secrets and feature flags.

## Recommended Evolution Path

Phase 1: Harden the modular monolith.

- Add Flyway migrations.
- Add Hilt, Room, encrypted token storage, and repository interfaces on Android.
- Add roles, admin audit, request validation, pagination metadata, idempotency keys.
- Add production payment and notification integrations.

Phase 2: Add operational infrastructure.

- Redis cache and rate limiting.
- Queue workers for notifications, reminders, analytics, subscription jobs.
- Object storage for files.
- Admin panel.
- CI/CD, staging, production environments, backups, monitoring.

Phase 3: Split selectively if needed.

- Commerce/order service if checkout load or integrations grow.
- Chat service if real-time requirements become significant.
- Notification service if delivery volume and provider failover need independent scaling.
- Analytics pipeline separated from transactional API.

