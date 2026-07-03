-- ============================================================
-- orders: razorpay gateway linkage
-- ============================================================
ALTER TABLE orders
    ADD COLUMN razorpay_order_id   VARCHAR(64),
    ADD COLUMN razorpay_payment_id VARCHAR(64);

CREATE UNIQUE INDEX ux_orders_razorpay_order_id
    ON orders (razorpay_order_id)
    WHERE razorpay_order_id IS NOT NULL;

CREATE INDEX idx_orders_payment_status_created
    ON orders (payment_status, created_at);

-- ============================================================
-- payment_webhook_events: idempotency ledger for Razorpay webhooks
-- ============================================================
CREATE TABLE payment_webhook_events (
    id                 UUID          NOT NULL DEFAULT gen_random_uuid(),
    razorpay_event_id  VARCHAR(64)   NOT NULL,
    event_type         VARCHAR(64)   NOT NULL,
    payload            JSONB,
    processed_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_payment_webhook_events              PRIMARY KEY (id),
    CONSTRAINT ux_payment_webhook_events_event_id      UNIQUE (razorpay_event_id)
);
