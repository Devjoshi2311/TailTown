# Deployment Architecture

## Current Deployment State

The backend is configured to run locally on port `8080` with PostgreSQL at `localhost:5432/tailtown`. The Android app points Retrofit to a hardcoded LAN IP. There is no visible deployment configuration, CI/CD pipeline, container configuration, staging environment, or infrastructure-as-code in the current project.

## Environments

Required environments:

- Local: developer machine.
- Development: shared integration environment.
- Staging: production-like, used for release validation.
- Production: customer-facing.

Each environment must have:

- Separate database.
- Separate object storage bucket.
- Separate Firebase project or environment-specific FCM configuration.
- Separate payment provider account or mode.
- Separate secrets.
- Separate analytics stream.

## Backend Deployment

Recommended runtime:

- Containerized Spring Boot application.
- Java 17 runtime.
- Horizontal scaling behind load balancer.
- PostgreSQL managed database.
- Redis managed cache.
- Queue/worker infrastructure.

Minimum production topology:

- Load balancer with TLS.
- 2+ backend application instances.
- Managed PostgreSQL with automated backups and PITR.
- Redis.
- Object storage.
- Worker process for async jobs.
- Monitoring/alerting.

## Configuration

All environment-specific values must come from environment variables or secret manager:

- Database URL/user/password.
- JWT signing keys.
- Firebase credentials.
- Payment provider keys and webhook secrets.
- Object storage credentials.
- Redis URL.
- Queue URL.
- CORS origins.
- Admin SSO config.
- Analytics keys.

No production secret should have a default fallback.

## Database Deployment

Required changes:

- Introduce Flyway migrations.
- Disable Hibernate schema update in production.
- Run migrations as a release step before app rollout.
- Use backward-compatible migrations for rolling deploys.

Migration process:

1. Backup or snapshot database.
2. Run Flyway validation.
3. Apply migrations.
4. Deploy application.
5. Verify health and metrics.
6. Roll forward on failure whenever possible.

## Android Release Deployment

Required:

- Product flavors or build variants for dev/staging/prod.
- BuildConfig-based API base URL.
- HTTPS-only production networking.
- Release signing through Play App Signing.
- R8/minification enabled.
- Crashlytics enabled.
- Analytics enabled.
- Body logging disabled.
- Versioning strategy for app updates.

Recommended release tracks:

- Internal testing.
- Closed testing.
- Open/beta testing.
- Production staged rollout.

## CI/CD Pipeline

Backend pipeline:

- Static checks.
- Unit tests.
- Integration tests with PostgreSQL.
- Security dependency scan.
- Build container image.
- Run database migration validation.
- Deploy to staging.
- Smoke test staging.
- Manual approval for production.
- Deploy production with rolling strategy.

Android pipeline:

- Static analysis.
- Unit tests.
- Compose UI tests where feasible.
- Build debug/staging APK.
- Build signed release bundle.
- Upload to internal testing track.
- Promote through release tracks.

## Health Checks

Backend health checks:

- Liveness: application process is running.
- Readiness: database, Redis, required config loaded.
- Deep health: external integrations checked asynchronously, not blocking readiness.

Expose:

- `/actuator/health`
- `/actuator/metrics`
- `/actuator/prometheus` if using Prometheus

Restrict actuator endpoints to internal networks or authenticated admin access.

## Observability Deployment

Required:

- Centralized structured logging.
- Metrics collection.
- Distributed tracing.
- Error reporting.
- Audit log storage.
- Dashboard and alerting.

Production alerts:

- Backend 5xx rate above threshold.
- Auth failure spike.
- P95 latency breach.
- DB CPU/connection saturation.
- Redis unavailable.
- Queue backlog.
- Payment webhook failures.
- Notification delivery failures.
- Android crash-free sessions below target.

## Backup and Disaster Recovery

Database:

- Continuous backups with PITR.
- Daily snapshots.
- Monthly restore drill.

Object storage:

- Versioning enabled for sensitive buckets.
- Lifecycle policies.
- Cross-region backup if required.

Recovery targets:

- RPO: 15 minutes or better for transactional DB.
- RTO: 1-4 hours depending on business maturity.

## Scaling Strategy

Backend:

- Scale stateless API horizontally.
- Tune database connection pool.
- Add read replicas for analytics/admin reads when needed.
- Cache product, promotion, and vet discovery reads.
- Move heavy work to async workers.

Database:

- Add indexes for high-volume queries.
- Monitor slow queries.
- Partition large event/audit/notification tables if needed.
- Archive old chat/notification events based on retention policy.

Android:

- Cache data with Room.
- Paginate lists.
- Avoid polling where push or real-time transport is available.

## Production Launch Checklist

- Flyway migrations in place.
- Hibernate production DDL mode set to validate.
- Secrets in secret manager.
- HTTPS enforced.
- Android cleartext disabled.
- Token storage encrypted.
- Admin MFA enabled.
- Payment webhooks verified.
- FCM production configured.
- Backups enabled and restore-tested.
- Monitoring dashboards live.
- On-call rotation defined.
- Incident runbooks created.
- Privacy policy and terms version tracked.

