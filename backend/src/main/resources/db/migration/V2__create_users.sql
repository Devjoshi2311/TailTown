-- ============================================================
-- users
-- ============================================================
CREATE TABLE users (
    id                   UUID        NOT NULL DEFAULT gen_random_uuid(),
    email                CITEXT      NOT NULL,
    phone                VARCHAR(32),
    password_hash        TEXT,
    name                 VARCHAR(160) NOT NULL,
    avatar_url           TEXT,
    status               VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    email_verified_at    TIMESTAMPTZ,
    phone_verified_at    TIMESTAMPTZ,
    last_login_at        TIMESTAMPTZ,
    marketing_consent    BOOLEAN      NOT NULL DEFAULT FALSE,
    terms_version        VARCHAR(32),
    privacy_version      VARCHAR(32),
    firebase_uid         VARCHAR(128) UNIQUE,
    referral_code        VARCHAR(40)  NOT NULL,
    -- audit columns
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by           UUID,
    updated_by           UUID,
    deleted_at           TIMESTAMPTZ,
    deleted_by           UUID,
    version              BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT ux_users_referral_code UNIQUE (referral_code)
);

-- unique email per non-deleted row
CREATE UNIQUE INDEX ux_users_email_active
    ON users (email)
    WHERE deleted_at IS NULL;

-- unique phone per non-deleted row
CREATE UNIQUE INDEX ux_users_phone_active
    ON users (phone)
    WHERE phone IS NOT NULL AND deleted_at IS NULL;

CREATE INDEX idx_users_status        ON users (status);
CREATE INDEX idx_users_firebase_uid  ON users (firebase_uid);
CREATE INDEX idx_users_referral_code ON users (referral_code);

-- ============================================================
-- refresh_tokens
-- ============================================================
CREATE TABLE refresh_tokens (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL,
    token       TEXT        NOT NULL,
    device_id   VARCHAR(200),
    expires_at  TIMESTAMPTZ NOT NULL,
    revoked_at  TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_refresh_tokens    PRIMARY KEY (id),
    CONSTRAINT ux_refresh_tokens_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_user  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- otp_credentials
-- ============================================================
CREATE TABLE otp_credentials (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL,
    phone       VARCHAR(32) NOT NULL,
    otp_hash    TEXT        NOT NULL,
    attempts    INT         NOT NULL DEFAULT 0,
    expires_at  TIMESTAMPTZ NOT NULL,
    verified_at TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_otp_credentials     PRIMARY KEY (id),
    CONSTRAINT fk_otp_credentials_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- user_addresses
-- ============================================================
CREATE TABLE user_addresses (
    id              UUID         NOT NULL DEFAULT gen_random_uuid(),
    user_id         UUID         NOT NULL,
    label           VARCHAR(80),
    recipient_name  VARCHAR(160),
    phone           VARCHAR(32),
    line1           TEXT         NOT NULL,
    line2           TEXT,
    landmark        TEXT,
    city            VARCHAR(100) NOT NULL,
    state           VARCHAR(100) NOT NULL,
    pincode         VARCHAR(20)  NOT NULL,
    country         VARCHAR(2)      NOT NULL DEFAULT 'IN',
    latitude        NUMERIC(9,6),
    longitude       NUMERIC(9,6),
    is_default      BOOLEAN      NOT NULL DEFAULT FALSE,
    -- audit columns
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    deleted_at      TIMESTAMPTZ,
    deleted_by      UUID,
    version         BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_user_addresses     PRIMARY KEY (id),
    CONSTRAINT fk_user_addresses_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- unique label per user per non-deleted row
CREATE UNIQUE INDEX ux_addresses_user_label_active
    ON user_addresses (user_id, label)
    WHERE deleted_at IS NULL;

-- only one default address per user per non-deleted row
CREATE UNIQUE INDEX ux_addresses_one_default
    ON user_addresses (user_id)
    WHERE is_default = TRUE AND deleted_at IS NULL;

CREATE INDEX idx_addresses_user_id ON user_addresses (user_id);
CREATE INDEX idx_addresses_pincode ON user_addresses (pincode);

-- ============================================================
-- notification_preferences
-- ============================================================
CREATE TABLE notification_preferences (
    id            UUID    NOT NULL DEFAULT gen_random_uuid(),
    user_id       UUID    NOT NULL,
    appointments  BOOLEAN NOT NULL DEFAULT TRUE,
    medications   BOOLEAN NOT NULL DEFAULT TRUE,
    orders        BOOLEAN NOT NULL DEFAULT TRUE,
    promos        BOOLEAN NOT NULL DEFAULT TRUE,
    chat          BOOLEAN NOT NULL DEFAULT TRUE,
    version       BIGINT  NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_notification_preferences      PRIMARY KEY (id),
    CONSTRAINT ux_notification_preferences_user UNIQUE (user_id),
    CONSTRAINT fk_notification_preferences_user FOREIGN KEY (user_id) REFERENCES users(id)
);
