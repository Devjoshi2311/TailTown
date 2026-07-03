# Security Architecture

## Current Security State

The backend uses Spring Security with stateless JWT authentication. Public endpoints are:

- `/api/auth/**`
- Swagger UI
- API docs

All other endpoints require authentication. JWT tokens contain the user id as the subject and email as a claim. Passwords are hashed with BCrypt. The Android app uses Firebase phone authentication and also supports backend email/password login. Tokens are stored in SharedPreferences.

Current security risks:

- JWT secret has a default fallback value in configuration.
- Refresh tokens are not persisted, rotated, or revocable.
- Android token storage is not encrypted.
- Cleartext HTTP traffic is enabled in Android manifest.
- Retrofit base URL is hardcoded to a LAN IP.
- HTTP body logging is enabled in the Android client.
- Role-based authorization is not implemented.
- Admin access model is absent.
- Notification ownership validation is incomplete for marking notifications read.
- Rate limiting and abuse controls are absent.

## Identity Model

Production identity should support multiple auth providers:

- Email/password.
- Firebase phone auth.
- Optional Google/Apple sign-in.
- Admin SSO for internal users.

Recommended model:

- User account is the business profile.
- Auth identity represents a login provider.
- Device session represents a mobile installation.
- Refresh token belongs to a device session.
- Admin identity is separate from customer identity.

## Authentication Requirements

### Access Tokens

- Short-lived JWT, recommended 15 minutes.
- Contains subject, issuer, audience, issued-at, expiry, token id.
- Contains minimal claims only.
- Signed using a strong secret or asymmetric key pair.
- Key rotation plan required.

### Refresh Tokens

- Long-lived opaque token or JWT with server-side persistence.
- Store only token hashes.
- Rotate on every refresh.
- Revoke old token after successful rotation.
- Detect replay and revoke the token family.
- Bind to device id where feasible.

### Password Security

- BCrypt or Argon2.
- Minimum length 10+ for email/password accounts.
- Block common compromised passwords.
- Login throttling and account lockout with progressive delay.
- Password reset tokens must be single-use and short-lived.

### Firebase Phone Auth

The current Android app maps Firebase UID to backend email and password. This is acceptable only as a prototype.

Production flow:

1. Android obtains Firebase ID token.
2. Android sends Firebase ID token to backend.
3. Backend verifies token with Firebase Admin SDK.
4. Backend links or creates TailTown user.
5. Backend issues TailTown access and refresh tokens.

Never derive backend passwords from Firebase UID.

## Authorization

Required roles:

- `CUSTOMER`
- `VET`
- `SUPPORT_AGENT`
- `CATALOG_MANAGER`
- `OPERATIONS_MANAGER`
- `FINANCE_MANAGER`
- `ADMIN`
- `SUPER_ADMIN`

Authorization rules:

- Customers can access only their own pets, addresses, orders, subscriptions, notifications, conversations, and bookings.
- Vets can access assigned bookings, their own availability, and permitted customer/pet context for active appointments.
- Support can access customer data only with ticket context and audit reason.
- Finance can access payment and refund metadata but not medical details unless explicitly authorized.
- Admin panel actions require role checks and audit logs.

Use method-level authorization for sensitive service methods, not only URL rules.

## Data Protection

Sensitive data categories:

- Personal identity: name, phone, email, address.
- Pet health data: medical records, prescriptions, vaccination records.
- Payment metadata: tokenized payment identifiers, invoices, refunds.
- Chat: support/vet messages and attachments.
- Location: delivery and vet discovery location.

Requirements:

- Encrypt data in transit with TLS 1.2+.
- Encrypt database volumes and backups.
- Use object storage encryption for files.
- Use signed URLs for file access.
- Avoid storing raw payment credentials.
- Redact tokens, passwords, OTPs, payment data, and medical attachment URLs from logs.

## Android Security

Current risks:

- `android:usesCleartextTraffic="true"`.
- Access and refresh tokens stored in SharedPreferences.
- Retrofit logs full request/response bodies.
- Base URL is hardcoded to a private IP.
- Release minification is disabled.

Production requirements:

- Use HTTPS only.
- Disable cleartext traffic.
- Store tokens in EncryptedSharedPreferences or Jetpack Security, or use encrypted DataStore.
- Disable body logging in release.
- Use environment-based base URLs through build config.
- Enable R8/minification and resource shrinking for release.
- Add certificate pinning only if the operations team can safely manage rotation.
- Protect deep links from open redirects and invalid route parameters.

## Backend Security

Required controls:

- Strong JWT secret from secret manager; no production default.
- CORS explicitly configured for admin panel origins.
- CSRF disabled only for stateless APIs; admin web flows may need CSRF protection depending on auth mechanism.
- Request body size limits.
- Rate limits for auth, OTP verification, checkout, booking, chat, and referral claim.
- Idempotency keys for state-changing payment/order/booking flows.
- Bean validation on all request DTOs.
- Centralized authorization helper for ownership checks.
- Security headers at gateway/load balancer.

## Admin Security

Admin panel must have:

- SSO or strong email/password with MFA.
- Role-based access control.
- IP allowlisting or zero-trust access for sensitive environments.
- Session timeout.
- Step-up auth for refunds, account blocking, medical record access, and campaign sends.
- Audit log for every read/write of sensitive records.

Audit log fields:

- actor id
- actor role
- action
- resource type
- resource id
- reason
- source IP
- request id
- before/after snapshot for writes where appropriate
- timestamp

## API Abuse Controls

Rate limit dimensions:

- IP
- user id
- device id
- email/phone
- endpoint group

High-risk endpoints:

- register
- login
- refresh
- Firebase token exchange
- OTP flows
- checkout
- booking creation
- referral claim
- chat send
- file upload

Abuse detection:

- Multiple accounts per device.
- Self-referral attempts.
- High failed login counts.
- Repeated payment failures.
- High chat/message velocity.
- Suspicious coupon/referral usage.

## Compliance and Privacy

TailTown handles sensitive personal and pet health information. Even where pet records are not regulated as human health records, customers will expect medical-grade privacy.

Required policies:

- Data retention and deletion policy.
- User data export.
- Account deletion.
- Consent for marketing notifications.
- Consent for location access.
- Consent and audit for medical record sharing with vets.
- Privacy policy version tracking.

## Security Testing

Required test coverage:

- Authentication success/failure.
- Token expiry and refresh.
- Token replay/revocation.
- Ownership checks for every user-owned resource.
- Admin role checks.
- Rate limit behavior.
- Validation failures.
- File upload type and size constraints.
- Payment webhook signature validation.

Security review checklist before launch:

- No default secrets.
- No cleartext production traffic.
- No token/body logging in production.
- No raw payment data stored.
- All high-risk writes idempotent.
- All admin actions audited.
- Backups encrypted and restore-tested.

