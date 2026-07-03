# Admin Panel Architecture

## Purpose

The admin panel is the operational control center for TailTown. It should support customer support, vet operations, catalog management, order fulfillment, subscription management, notifications, promotions, finance workflows, and audit review.

The current backend does not include admin APIs, admin roles, or an admin UI. This document defines the production target.

## Recommended Architecture

Use a separate web frontend with a secured admin API namespace:

- Admin frontend: React, Next.js, or another internal web stack.
- Backend routes: `/api/admin/v1/**`.
- Authentication: SSO/MFA for internal users.
- Authorization: role-based and action-based checks.
- Audit: mandatory for all sensitive reads and writes.

Admin APIs should not reuse customer-facing endpoints directly. They can call shared domain services internally, but must apply stricter authorization, audit, and workflow rules.

## Admin Roles

- Super Admin: full access, role assignment, dangerous operations.
- Support Agent: customer lookup, support conversations, limited order/booking assistance.
- Vet Operations: vet onboarding, vet verification, availability, booking exceptions.
- Catalog Manager: products, categories, brands, inventory metadata, promotions.
- Order Operations: order status, shipment, delivery exceptions.
- Finance Manager: payments, refunds, invoices, settlements.
- Marketing Manager: campaigns, notifications, referral programs.
- Read-only Analyst: dashboards and exports only.

## Core Modules

### Dashboard

Required widgets:

- Daily active users.
- New registrations.
- Booking volume and completion rate.
- Order GMV and conversion.
- Active subscriptions.
- Open support conversations.
- Payment failure count.
- Low-stock products.
- Notification campaign performance.
- System health summary.

### Customer Management

Capabilities:

- Search by email, phone, name, user id.
- View profile, devices, addresses, pets, bookings, orders, subscriptions.
- Block/unblock account.
- Trigger password reset or logout all sessions.
- View notification preferences.
- View audit trail.

Sensitive access:

- Medical records require explicit reason.
- Chat transcripts require ticket context.
- Account deletion/export must follow privacy workflow.

### Pet and Health Records

Capabilities:

- View pet profiles.
- View vaccination status.
- View prescriptions and medical records.
- Assist with uploaded document issues.
- Link medical record to booking or vet visit.

Restrictions:

- Support cannot modify medical facts unless a privileged clinical/support role exists.
- Every medical record read should be auditable.

### Vet Operations

Capabilities:

- Create and edit vet profile.
- Verify licenses and certifications.
- Manage clinic locations.
- Manage services and price ranges.
- Manage availability rules and time off.
- Activate/deactivate provider.
- Review booking performance and ratings.

Production workflows:

- Vet onboarding checklist.
- Document verification.
- Service area approval.
- Suspension and reinstatement flow.

### Booking Operations

Capabilities:

- Search bookings by user, pet, vet, date, status.
- View status history.
- Reschedule booking.
- Cancel booking with reason.
- Reassign provider if operationally necessary.
- Trigger customer/vet notification.
- Initiate refund where applicable.

Required safeguards:

- Status transitions must be validated.
- Refund actions require finance permission or approval.
- Customer-impacting actions generate notifications.

### Catalog Management

Capabilities:

- Product CRUD.
- Variant/SKU management.
- Category and brand management.
- Image management.
- Price and MRP update.
- Bestseller and active flags.
- Product availability status.

Required safeguards:

- Price changes are audited.
- Product deletion should be deactivation, not hard delete.
- Inventory adjustments require reason.

### Inventory

Capabilities:

- View stock by SKU.
- Adjust stock.
- Import stock updates.
- View inventory movement ledger.
- Low-stock alerts.

Production rules:

- Inventory is ledger-based.
- Manual adjustments require reason and actor.
- Checkout must reserve inventory or atomically decrement it after payment confirmation.

### Orders and Fulfillment

Capabilities:

- Search orders.
- View order details, payment state, shipment state.
- Update fulfillment status.
- Add shipment tracking.
- Cancel order.
- Process return/refund workflows.

Required integrations:

- Payment provider dashboard links.
- Shipping/delivery provider status.
- Invoice generation.

### Subscription Delivery

Capabilities:

- View active subscriptions.
- Pause/resume/cancel with reason.
- Skip next cycle.
- Change next delivery date.
- View generated orders.
- View payment failures.

Required safeguards:

- Customer-visible subscription changes generate notifications.
- Subscription billing and delivery events are auditable.

### Notifications and Campaigns

Capabilities:

- Manage notification templates.
- Create campaigns.
- Segment users.
- Preview messages.
- Schedule delivery.
- View send, delivery, open, and failure metrics.

Required safeguards:

- Campaign approval workflow for large sends.
- Respect notification preferences.
- Dedupe campaign sends.
- Rate limit sends.

### Promotions and Referral

Capabilities:

- Manage promotion content.
- Create coupon/promotion rules.
- Configure referral rewards.
- View referral fraud signals.
- View reward ledger.

Required safeguards:

- Promotion changes must be audited.
- Reward grants must be ledger-based and reversible only via compensating transaction.

### Chat and Support Inbox

Capabilities:

- Queue view for open conversations.
- Assignment to support agents.
- Conversation history.
- Internal notes.
- Escalation to vet operations or finance.
- SLA timers.

Production requirements:

- Message search.
- Attachment scanning.
- Abuse reporting.
- Agent performance metrics.

## Audit Log

Admin audit is mandatory.

Every admin action should record:

- Actor id and role.
- Action.
- Resource type and id.
- Reason.
- Request id.
- Source IP.
- Timestamp.
- Before and after values for mutations where feasible.

Audit logs must be immutable to normal admin users.

## Admin API Standards

- Namespace: `/api/admin/v1`.
- All endpoints require admin authentication.
- All endpoints require explicit permission checks.
- All write endpoints require audit reason for sensitive domains.
- Pagination required for lists.
- Exports must be asynchronous and access-controlled.
- Bulk operations require preview, confirmation, and audit.

## Minimum Viable Admin Launch

Required before production:

- Admin authentication with MFA.
- User search and profile view.
- Vet profile management.
- Booking management.
- Product/catalog management.
- Order status management.
- Support conversation view.
- Audit log.
- Role management limited to Super Admin.

