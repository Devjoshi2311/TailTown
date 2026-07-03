-- ============================================================
-- subscriptions
-- ============================================================
CREATE TABLE subscriptions (
    id                   UUID          NOT NULL DEFAULT gen_random_uuid(),
    user_id              UUID          NOT NULL,
    product_id           UUID          NOT NULL,
    address_id           UUID,
    status               VARCHAR(40)   NOT NULL DEFAULT 'ACTIVE',
    quantity             INT           NOT NULL DEFAULT 1,
    cadence              VARCHAR(40)   NOT NULL,
    price_per_cycle      NUMERIC(12,2) NOT NULL,
    currency             VARCHAR(3)       NOT NULL DEFAULT 'INR',
    next_billing_date    DATE,
    next_delivery_date   DATE          NOT NULL,
    paused_until         DATE,
    cancelled_at         TIMESTAMPTZ,
    cancellation_reason  TEXT,
    -- audit columns
    created_at           TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by           UUID,
    updated_by           UUID,
    deleted_at           TIMESTAMPTZ,
    deleted_by           UUID,
    version              BIGINT        NOT NULL DEFAULT 0,

    CONSTRAINT pk_subscriptions          PRIMARY KEY (id),
    CONSTRAINT fk_subscriptions_user     FOREIGN KEY (user_id)    REFERENCES users(id),
    CONSTRAINT fk_subscriptions_product  FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_subscriptions_address  FOREIGN KEY (address_id) REFERENCES user_addresses(id)
);

-- one active/paused/failed subscription per user per product per non-deleted row
CREATE UNIQUE INDEX ux_subscriptions_user_product_active
    ON subscriptions (user_id, product_id)
    WHERE status IN ('ACTIVE', 'PAUSED', 'PAYMENT_FAILED') AND deleted_at IS NULL;

CREATE INDEX idx_subscriptions_user_status   ON subscriptions (user_id, status);
CREATE INDEX idx_subscriptions_next_billing  ON subscriptions (next_billing_date);
CREATE INDEX idx_subscriptions_next_delivery ON subscriptions (next_delivery_date);
CREATE INDEX idx_subscriptions_status        ON subscriptions (status);
