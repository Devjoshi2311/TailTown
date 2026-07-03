-- ============================================================
-- bookings
-- ============================================================
CREATE TABLE bookings (
    id                   UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id              UUID        NOT NULL,
    pet_id               UUID        NOT NULL,
    vet_id               UUID        NOT NULL,
    slot_id              UUID        NOT NULL,
    service_type         VARCHAR(40) NOT NULL,
    visit_type           VARCHAR(32) NOT NULL DEFAULT 'CLINIC',
    scheduled_start      TIMESTAMPTZ NOT NULL,
    scheduled_end        TIMESTAMPTZ NOT NULL,
    status               VARCHAR(40) NOT NULL DEFAULT 'CONFIRMED',
    address_id           UUID,
    address_snapshot     TEXT,
    notes                TEXT,
    cancelled_at         TIMESTAMPTZ,
    cancelled_by         UUID,
    cancellation_reason  TEXT,
    -- audit columns
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by           UUID,
    updated_by           UUID,
    deleted_at           TIMESTAMPTZ,
    deleted_by           UUID,
    version              BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_bookings                PRIMARY KEY (id),
    CONSTRAINT fk_bookings_user           FOREIGN KEY (user_id)       REFERENCES users(id),
    CONSTRAINT fk_bookings_pet            FOREIGN KEY (pet_id)        REFERENCES pets(id),
    CONSTRAINT fk_bookings_vet            FOREIGN KEY (vet_id)        REFERENCES vets(id),
    CONSTRAINT fk_bookings_slot           FOREIGN KEY (slot_id)       REFERENCES booking_slots(id),
    CONSTRAINT fk_bookings_address        FOREIGN KEY (address_id)    REFERENCES user_addresses(id),
    CONSTRAINT fk_bookings_cancelled_by   FOREIGN KEY (cancelled_by)  REFERENCES users(id)
);

-- one booking per slot (for active statuses) per non-deleted row
CREATE UNIQUE INDEX ux_bookings_slot_active
    ON bookings (slot_id)
    WHERE deleted_at IS NULL
      AND status IN ('PENDING_PAYMENT', 'CONFIRMED', 'COMPLETED');

CREATE INDEX idx_bookings_user_time   ON bookings (user_id, scheduled_start DESC);
CREATE INDEX idx_bookings_pet_time    ON bookings (pet_id,  scheduled_start DESC);
CREATE INDEX idx_bookings_vet_time    ON bookings (vet_id,  scheduled_start DESC);
CREATE INDEX idx_bookings_status_time ON bookings (status,  scheduled_start);

-- ============================================================
-- Deferred FK constraints for V4 tables referencing bookings
-- ============================================================
ALTER TABLE prescriptions
    ADD CONSTRAINT fk_prescriptions_booking
    FOREIGN KEY (booking_id) REFERENCES bookings(id);

ALTER TABLE vaccines
    ADD CONSTRAINT fk_vaccines_booking
    FOREIGN KEY (booking_id) REFERENCES bookings(id);
