-- ============================================================
-- notifications
-- ============================================================
CREATE TABLE notifications (
    id               UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id          UUID        NOT NULL,
    type             VARCHAR(40) NOT NULL,
    title            VARCHAR(180) NOT NULL,
    body             TEXT        NOT NULL,
    deep_link        TEXT,
    priority         VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    is_read          BOOLEAN     NOT NULL DEFAULT FALSE,
    read_at          TIMESTAMPTZ,
    sent_at          TIMESTAMPTZ,
    delivery_status  VARCHAR(40) NOT NULL DEFAULT 'CREATED',
    dedupe_key       VARCHAR(160),
    -- audit columns
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by       UUID,
    updated_by       UUID,
    deleted_at       TIMESTAMPTZ,
    deleted_by       UUID,
    version          BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_notifications      PRIMARY KEY (id),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- unique dedupe key per user per non-deleted row
CREATE UNIQUE INDEX ux_notifications_user_dedupe
    ON notifications (user_id, dedupe_key)
    WHERE dedupe_key IS NOT NULL AND deleted_at IS NULL;

CREATE INDEX idx_notifications_user_created   ON notifications (user_id, created_at DESC);
CREATE INDEX idx_notifications_user_unread    ON notifications (user_id, is_read, created_at DESC);
CREATE INDEX idx_notifications_delivery_status ON notifications (delivery_status);

-- ============================================================
-- push_tokens
-- ============================================================
CREATE TABLE push_tokens (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL,
    device_id   VARCHAR(200) NOT NULL,
    token       TEXT        NOT NULL,
    platform    VARCHAR(20) NOT NULL DEFAULT 'ANDROID',
    is_active   BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_push_tokens        PRIMARY KEY (id),
    CONSTRAINT ux_push_token_value   UNIQUE (token),
    CONSTRAINT fk_push_tokens_user   FOREIGN KEY (user_id) REFERENCES users(id)
);

-- one token entry per device per user
CREATE UNIQUE INDEX ux_push_tokens_device
    ON push_tokens (user_id, device_id);

CREATE INDEX idx_push_tokens_user ON push_tokens (user_id);
