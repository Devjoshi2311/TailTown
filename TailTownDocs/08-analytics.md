# Analytics Architecture

## Goals

Analytics should help TailTown understand product adoption, user retention, pet health engagement, booking conversion, ecommerce revenue, subscription retention, support quality, and operational reliability.

Analytics must not leak sensitive data. Medical record details, prescriptions, chat text, payment identifiers, raw addresses, and tokens must not be sent to analytics tools.

## Event Sources

### Android Client Events

Client events capture user behavior and UX performance:

- App opened.
- Login started/completed/failed.
- Pet profile created/updated.
- Vet search viewed.
- Vet filter applied.
- Vet profile viewed.
- Booking slot selected.
- Booking created/cancelled.
- Product list viewed.
- Product viewed.
- Add to cart.
- Cart viewed.
- Checkout started/completed/failed.
- Order viewed.
- Prescription viewed.
- Dose marked.
- Weight logged.
- Notification opened.
- Chat opened/message sent.
- Referral shared.
- Subscription viewed/paused/resumed.

### Backend Domain Events

Backend events are authoritative for state changes:

- User registered.
- Token refreshed.
- Pet created.
- Weight logged.
- Prescription created.
- Dose logged.
- Booking created/cancelled/completed.
- Cart item added/updated/removed.
- Checkout session created.
- Payment succeeded/failed/refunded.
- Order placed/shipped/delivered/cancelled.
- Subscription created/renewed/paused/cancelled/payment_failed.
- Notification queued/sent/delivered/opened.
- Conversation created/message sent.
- Referral attributed/reward granted.

## Event Design

Every event should include:

- event_name
- event_id
- occurred_at
- source: android, backend, admin, worker
- user_id when available
- anonymous_id or device_id for pre-login client events
- session_id
- app_version
- platform
- request_id for backend events
- entity ids relevant to the event
- non-sensitive properties

Do not include:

- access tokens
- refresh tokens
- passwords
- OTPs
- raw chat text
- medical record body text
- prescription notes unless explicitly anonymized
- raw address
- payment credentials

## Key Metrics

### Acquisition and Activation

- New registrations.
- Phone auth conversion rate.
- Email/password conversion rate.
- Onboarding completion rate.
- First pet creation rate.
- Time to first pet.

### Engagement

- DAU, WAU, MAU.
- Sessions per user.
- Pet profile views.
- Health feature usage.
- Dose marking adherence.
- Weight logging frequency.
- Notification open rate.

### Vet Marketplace

- Vet search count.
- Vet profile view rate.
- Booking funnel conversion:
  - search viewed
  - vet profile viewed
  - slot selected
  - booking started
  - booking confirmed
- Booking cancellation rate.
- Booking completion rate.
- Average booking lead time.
- Provider utilization.

### Ecommerce

- Product impressions.
- Product detail views.
- Add-to-cart rate.
- Cart abandonment rate.
- Checkout conversion rate.
- Average order value.
- GMV.
- Repeat purchase rate.
- Reorder rate.
- Refund/cancellation rate.

### Subscriptions

- New subscriptions.
- Active subscriptions.
- Pause/resume rate.
- Renewal success rate.
- Payment failure rate.
- Churn rate.
- Average subscription lifetime.

### Notifications

- Sent count by type.
- Delivery rate.
- Open rate.
- Conversion after open.
- Opt-out rate.
- Failure rate by provider.

### Chat and Support

- Conversation volume.
- First response time.
- Resolution time.
- Reopen rate.
- CSAT if collected.
- Messages per conversation.

### Reliability

- API P50/P95/P99 latency by endpoint.
- Error rate by endpoint.
- Crash-free sessions.
- ANR rate.
- Network failure rate.
- Payment webhook failure rate.
- Queue backlog.

## Funnel Definitions

### Booking Funnel

1. `vet_search_viewed`
2. `vet_profile_viewed`
3. `booking_slot_selected`
4. `booking_started`
5. `booking_payment_started` if paid booking is introduced
6. `booking_confirmed`

### Checkout Funnel

1. `product_list_viewed`
2. `product_viewed`
3. `cart_item_added`
4. `cart_viewed`
5. `checkout_started`
6. `payment_started`
7. `order_placed`

### Health Engagement Funnel

1. `pet_created`
2. `health_dashboard_viewed`
3. `prescription_created` or `vaccination_added`
4. `notification_reminder_sent`
5. `dose_marked` or `vaccination_completed`

## Analytics Pipeline

Recommended pipeline:

- Android sends client analytics to analytics SDK and selected backend endpoints.
- Backend writes domain events to transactional outbox.
- Worker publishes events to queue/stream.
- Stream processor loads events into warehouse.
- BI dashboards consume warehouse tables.

Recommended destinations:

- Product analytics: Firebase Analytics, Amplitude, Mixpanel, or PostHog.
- Crash reporting: Firebase Crashlytics.
- Warehouse: BigQuery, Snowflake, Redshift, or Postgres analytics replica for early stage.
- Operational dashboards: Grafana/Prometheus or cloud equivalent.

## Data Governance

Required:

- Event naming convention.
- Event schema registry.
- PII classification.
- Retention policy.
- Consent enforcement for marketing analytics where required.
- User deletion/export handling.
- Debug mode for development events.
- Environment separation for dev/staging/prod analytics.

## Dashboard Set

Minimum dashboards:

- Executive: DAU, registrations, bookings, GMV, subscriptions, retention.
- Booking Operations: booking funnel, cancellations, provider utilization, no-shows.
- Commerce: product funnel, cart, checkout, GMV, AOV, order status.
- Health Engagement: pets, prescriptions, doses, vaccinations, weight logs.
- Notifications: send volume, delivery, opens, opt-outs.
- Support: open conversations, SLA, resolution.
- Reliability: API, app crashes, queues, DB.

