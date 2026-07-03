-- ============================================================
-- categories
-- ============================================================
CREATE TABLE categories (
    id           UUID         NOT NULL DEFAULT gen_random_uuid(),
    parent_id    UUID,
    name         VARCHAR(120) NOT NULL,
    slug         VARCHAR(140) NOT NULL,
    description  TEXT,
    sort_order   INT          NOT NULL DEFAULT 0,
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    image_url    TEXT,
    -- audit columns
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by   UUID,
    updated_by   UUID,
    deleted_at   TIMESTAMPTZ,
    deleted_by   UUID,
    version      BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_categories        PRIMARY KEY (id),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
);

-- unique slug per non-deleted row
CREATE UNIQUE INDEX ux_categories_slug_active
    ON categories (slug)
    WHERE deleted_at IS NULL;

-- unique name within a parent per non-deleted row
CREATE UNIQUE INDEX ux_categories_parent_name_active
    ON categories (parent_id, name)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_categories_parent      ON categories (parent_id);
CREATE INDEX idx_categories_active_sort ON categories (is_active, sort_order);

-- ============================================================
-- products
-- ============================================================
CREATE TABLE products (
    id                     UUID         NOT NULL DEFAULT gen_random_uuid(),
    category_id            UUID,
    sku                    VARCHAR(80)  NOT NULL,
    name                   VARCHAR(180) NOT NULL,
    slug                   VARCHAR(200) NOT NULL,
    brand                  VARCHAR(120),
    subtitle               VARCHAR(180),
    description            TEXT,
    price                  NUMERIC(12,2) NOT NULL,
    mrp                    NUMERIC(12,2) NOT NULL,
    currency               VARCHAR(3)      NOT NULL DEFAULT 'INR',
    stock_qty              INT          NOT NULL DEFAULT 0,
    is_active              BOOLEAN      NOT NULL DEFAULT TRUE,
    is_bestseller          BOOLEAN      NOT NULL DEFAULT FALSE,
    rating                 NUMERIC(3,2) NOT NULL DEFAULT 0,
    review_count           INT          NOT NULL DEFAULT 0,
    image_url              TEXT,
    subscription_eligible  BOOLEAN      NOT NULL DEFAULT FALSE,
    -- audit columns
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by             UUID,
    updated_by             UUID,
    deleted_at             TIMESTAMPTZ,
    deleted_by             UUID,
    version                BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_products           PRIMARY KEY (id),
    CONSTRAINT fk_products_category  FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- unique sku per non-deleted row
CREATE UNIQUE INDEX ux_products_sku_active
    ON products (sku)
    WHERE deleted_at IS NULL;

-- unique slug per non-deleted row
CREATE UNIQUE INDEX ux_products_slug_active
    ON products (slug)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_products_category_active ON products (category_id, is_active);
CREATE INDEX idx_products_brand           ON products (brand);
CREATE INDEX idx_products_price           ON products (price);
CREATE INDEX idx_products_stock           ON products (stock_qty);
CREATE INDEX idx_products_search
    ON products
    USING GIN (to_tsvector('english', name || ' ' || COALESCE(brand, '') || ' ' || COALESCE(subtitle, '')));
