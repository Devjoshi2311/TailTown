-- ============================================================
-- vets
-- ============================================================
CREATE TABLE vets (
    id                    UUID         NOT NULL DEFAULT gen_random_uuid(),
    display_name          VARCHAR(160) NOT NULL,
    specialty             VARCHAR(120),
    bio                   TEXT,
    phone                 VARCHAR(32),
    email                 CITEXT,
    avatar_url            TEXT,
    license_number        VARCHAR(120),
    license_verified_at   TIMESTAMPTZ,
    status                VARCHAR(32)  NOT NULL DEFAULT 'PENDING_VERIFICATION',
    rating                NUMERIC(3,2) NOT NULL DEFAULT 0,
    review_count          INT          NOT NULL DEFAULT 0,
    years_experience      INT          NOT NULL DEFAULT 0,
    home_visit_available  BOOLEAN      NOT NULL DEFAULT FALSE,
    clinic_name           VARCHAR(160),
    address_line1         TEXT,
    city                  VARCHAR(100),
    state                 VARCHAR(100),
    pincode               VARCHAR(20),
    latitude              NUMERIC(9,6),
    longitude             NUMERIC(9,6),
    -- audit columns
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by            UUID,
    updated_by            UUID,
    deleted_at            TIMESTAMPTZ,
    deleted_by            UUID,
    version               BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_vets PRIMARY KEY (id)
);

-- unique license per non-deleted row (when license_number is present)
CREATE UNIQUE INDEX ux_vets_license_active
    ON vets (license_number)
    WHERE license_number IS NOT NULL AND deleted_at IS NULL;

-- unique email per non-deleted row (when email is present)
CREATE UNIQUE INDEX ux_vets_email_active
    ON vets (email)
    WHERE email IS NOT NULL AND deleted_at IS NULL;

CREATE INDEX idx_vets_status    ON vets (status);
CREATE INDEX idx_vets_specialty ON vets (specialty);
CREATE INDEX idx_vets_city      ON vets (city);
CREATE INDEX idx_vets_rating    ON vets (rating DESC, review_count DESC);
CREATE INDEX idx_vets_location  ON vets (latitude, longitude);

-- ============================================================
-- booking_slots
-- ============================================================
CREATE TABLE booking_slots (
    id                UUID         NOT NULL DEFAULT gen_random_uuid(),
    vet_id            UUID         NOT NULL,
    service_type      VARCHAR(40)  NOT NULL,
    starts_at         TIMESTAMPTZ  NOT NULL,
    ends_at           TIMESTAMPTZ  NOT NULL,
    status            VARCHAR(32)  NOT NULL DEFAULT 'AVAILABLE',
    price             NUMERIC(12,2) NOT NULL DEFAULT 0,
    hold_expires_at   TIMESTAMPTZ,
    held_by_user_id   UUID,
    -- audit columns
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by        UUID,
    updated_by        UUID,
    deleted_at        TIMESTAMPTZ,
    deleted_by        UUID,
    version           BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_booking_slots          PRIMARY KEY (id),
    CONSTRAINT fk_booking_slots_vet      FOREIGN KEY (vet_id)          REFERENCES vets(id),
    CONSTRAINT fk_booking_slots_held_by  FOREIGN KEY (held_by_user_id) REFERENCES users(id)
);

-- unique vet time window per non-deleted row
CREATE UNIQUE INDEX ux_booking_slots_vet_time_active
    ON booking_slots (vet_id, starts_at, ends_at)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_booking_slots_vet_time    ON booking_slots (vet_id, starts_at);
CREATE INDEX idx_booking_slots_status_time ON booking_slots (status, starts_at);
CREATE INDEX idx_booking_slots_hold_expiry
    ON booking_slots (hold_expires_at)
    WHERE hold_expires_at IS NOT NULL;

-- ============================================================
-- Deferred FK constraints for V4 tables referencing vets
-- ============================================================
ALTER TABLE prescriptions
    ADD CONSTRAINT fk_prescriptions_vet
    FOREIGN KEY (vet_id) REFERENCES vets(id);

ALTER TABLE vaccines
    ADD CONSTRAINT fk_vaccines_vet
    FOREIGN KEY (vet_id) REFERENCES vets(id);
