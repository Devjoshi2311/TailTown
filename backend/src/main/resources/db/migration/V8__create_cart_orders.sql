-- ============================================================
-- cart
-- ============================================================
CREATE TABLE cart (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL,
    status      VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    currency    VARCHAR(3)     NOT NULL DEFAULT 'INR',
    expires_at  TIMESTAMPTZ,
    -- audit columns
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    deleted_at  TIMESTAMPTZ,
    deleted_by  UUID,
    version     BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_cart      PRIMARY KEY (id),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- one active cart per user per non-deleted row
CREATE UNIQUE INDEX ux_cart_user_active
    ON cart (user_id)
    WHERE status = 'ACTIVE' AND deleted_at IS NULL;

CREATE INDEX idx_cart_user       ON cart (user_id);
CREATE INDEX idx_cart_status     ON cart (status);
CREATE INDEX idx_cart_expires_at ON cart (expires_at);

-- ============================================================
-- cart_items
-- ============================================================
CREATE TABLE cart_items (
    id              UUID          NOT NULL DEFAULT gen_random_uuid(),
    cart_id         UUID          NOT NULL,
    product_id      UUID          NOT NULL,
    quantity        INT           NOT NULL,
    price_snapshot  NUMERIC(12,2),
    currency        VARCHAR(3)       NOT NULL DEFAULT 'INR',
    -- audit columns
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    deleted_at      TIMESTAMPTZ,
    deleted_by      UUID,
    version         BIGINT        NOT NULL DEFAULT 0,

    CONSTRAINT pk_cart_items         PRIMARY KEY (id),
    CONSTRAINT fk_cart_items_cart    FOREIGN KEY (cart_id)    REFERENCES cart(id),
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- one line per product per cart per non-deleted row
CREATE UNIQUE INDEX ux_cart_items_cart_product_active
    ON cart_items (cart_id, product_id)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_cart_items_cart    ON cart_items (cart_id);
CREATE INDEX idx_cart_items_product ON cart_items (product_id);

-- ============================================================
-- orders
-- ============================================================
CREATE TABLE orders (
    id                         UUID          NOT NULL DEFAULT gen_random_uuid(),
    order_number               VARCHAR(40)   NOT NULL,
    user_id                    UUID          NOT NULL,
    cart_id                    UUID,
    address_id                 UUID,
    delivery_address_snapshot  TEXT          NOT NULL,
    status                     VARCHAR(40)   NOT NULL DEFAULT 'PENDING_PAYMENT',
    subtotal                   NUMERIC(12,2) NOT NULL,
    discount_total             NUMERIC(12,2) NOT NULL DEFAULT 0,
    delivery_fee               NUMERIC(12,2) NOT NULL DEFAULT 0,
    tax_total                  NUMERIC(12,2) NOT NULL DEFAULT 0,
    grand_total                NUMERIC(12,2) NOT NULL,
    currency                   VARCHAR(3)       NOT NULL DEFAULT 'INR',
    payment_status             VARCHAR(40)   NOT NULL DEFAULT 'PENDING',
    placed_at                  TIMESTAMPTZ,
    cancelled_at               TIMESTAMPTZ,
    cancelled_by               UUID,
    cancellation_reason        TEXT,
    -- audit columns
    created_at                 TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at                 TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by                 UUID,
    updated_by                 UUID,
    deleted_at                 TIMESTAMPTZ,
    deleted_by                 UUID,
    version                    BIGINT        NOT NULL DEFAULT 0,

    CONSTRAINT pk_orders               PRIMARY KEY (id),
    CONSTRAINT ux_orders_order_number  UNIQUE (order_number),
    CONSTRAINT fk_orders_user          FOREIGN KEY (user_id)      REFERENCES users(id),
    CONSTRAINT fk_orders_cart          FOREIGN KEY (cart_id)      REFERENCES cart(id),
    CONSTRAINT fk_orders_address       FOREIGN KEY (address_id)   REFERENCES user_addresses(id),
    CONSTRAINT fk_orders_cancelled_by  FOREIGN KEY (cancelled_by) REFERENCES users(id)
);

CREATE INDEX idx_orders_user_created    ON orders (user_id, created_at DESC);
CREATE INDEX idx_orders_status_created  ON orders (status, created_at DESC);
CREATE INDEX idx_orders_payment_status  ON orders (payment_status);
CREATE INDEX idx_orders_placed_at       ON orders (placed_at DESC);

-- ============================================================
-- order_items
-- ============================================================
CREATE TABLE order_items (
    id               UUID          NOT NULL DEFAULT gen_random_uuid(),
    order_id         UUID          NOT NULL,
    product_id       UUID,
    sku              VARCHAR(80)   NOT NULL,
    product_name     VARCHAR(180)  NOT NULL,
    product_image_url TEXT,
    quantity         INT           NOT NULL,
    unit_price       NUMERIC(12,2) NOT NULL,
    line_discount    NUMERIC(12,2) NOT NULL DEFAULT 0,
    line_tax         NUMERIC(12,2) NOT NULL DEFAULT 0,
    line_total       NUMERIC(12,2) NOT NULL,
    currency         VARCHAR(3)       NOT NULL DEFAULT 'INR',
    -- audit columns
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by       UUID,
    updated_by       UUID,
    deleted_at       TIMESTAMPTZ,
    deleted_by       UUID,
    version          BIGINT        NOT NULL DEFAULT 0,

    CONSTRAINT pk_order_items         PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order   FOREIGN KEY (order_id)   REFERENCES orders(id),
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_order_items_order   ON order_items (order_id);
CREATE INDEX idx_order_items_product ON order_items (product_id);
