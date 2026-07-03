-- ============================================================
-- referrals
-- ============================================================
CREATE TABLE referrals (
    id                     UUID          NOT NULL DEFAULT gen_random_uuid(),
    referrer_user_id       UUID          NOT NULL,
    referred_user_id       UUID,
    referral_code          VARCHAR(40)   NOT NULL,
    status                 VARCHAR(40)   NOT NULL DEFAULT 'PENDING',
    referrer_reward_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    referred_reward_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    currency               VARCHAR(3)       NOT NULL DEFAULT 'INR',
    qualified_at           TIMESTAMPTZ,
    rewarded_at            TIMESTAMPTZ,
    fraud_reason           TEXT,
    -- audit columns
    created_at             TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by             UUID,
    updated_by             UUID,
    deleted_at             TIMESTAMPTZ,
    deleted_by             UUID,
    version                BIGINT        NOT NULL DEFAULT 0,

    CONSTRAINT pk_referrals                 PRIMARY KEY (id),
    CONSTRAINT ux_referrals_code            UNIQUE (referral_code),
    CONSTRAINT fk_referrals_referrer        FOREIGN KEY (referrer_user_id) REFERENCES users(id),
    CONSTRAINT fk_referrals_referred        FOREIGN KEY (referred_user_id) REFERENCES users(id)
);

-- each user can be referred only once per non-deleted row
CREATE UNIQUE INDEX ux_referrals_referred_active
    ON referrals (referred_user_id)
    WHERE referred_user_id IS NOT NULL AND deleted_at IS NULL;

CREATE INDEX idx_referrals_referrer    ON referrals (referrer_user_id);
CREATE INDEX idx_referrals_code        ON referrals (referral_code);
CREATE INDEX idx_referrals_status      ON referrals (status);
CREATE INDEX idx_referrals_qualified_at ON referrals (qualified_at);

-- ============================================================
-- audit_log
-- ============================================================
CREATE TABLE audit_log (
    id           UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id      UUID,
    entity_type  VARCHAR(80) NOT NULL,
    entity_id    UUID,
    action       VARCHAR(40) NOT NULL,
    before_json  JSONB,
    after_json   JSONB,
    ip_address   VARCHAR(64),
    request_id   VARCHAR(160),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_audit_log PRIMARY KEY (id)
);

CREATE INDEX idx_audit_log_user   ON audit_log (user_id, created_at DESC);
CREATE INDEX idx_audit_log_entity ON audit_log (entity_type, entity_id, created_at DESC);
