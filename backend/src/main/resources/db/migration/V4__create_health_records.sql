-- ============================================================
-- weight_records
-- ============================================================
CREATE TABLE weight_records (
    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
    pet_id      UUID        NOT NULL,
    user_id     UUID        NOT NULL,
    weight_kg   NUMERIC(6,2) NOT NULL,
    recorded_on DATE        NOT NULL,
    source      VARCHAR(40) NOT NULL DEFAULT 'USER',
    notes       TEXT,
    -- audit columns
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    deleted_at  TIMESTAMPTZ,
    deleted_by  UUID,
    version     BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_weight_records      PRIMARY KEY (id),
    CONSTRAINT fk_weight_records_pet  FOREIGN KEY (pet_id)  REFERENCES pets(id),
    CONSTRAINT fk_weight_records_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- one weight entry per pet per day per non-deleted row
CREATE UNIQUE INDEX ux_weight_records_pet_date_active
    ON weight_records (pet_id, recorded_on)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_weight_records_pet_date  ON weight_records (pet_id, recorded_on);
CREATE INDEX idx_weight_records_user_date ON weight_records (user_id, recorded_on DESC);

-- ============================================================
-- prescriptions
-- NOTE: vet_id FK to vets and booking_id FK to bookings are
--       added in V5 and V6 respectively after those tables exist.
-- ============================================================
CREATE TABLE prescriptions (
    id                  UUID         NOT NULL DEFAULT gen_random_uuid(),
    pet_id              UUID         NOT NULL,
    user_id             UUID         NOT NULL,
    vet_id              UUID,
    booking_id          UUID,
    medication_name     VARCHAR(180) NOT NULL,
    dosage              VARCHAR(120) NOT NULL,
    frequency           VARCHAR(120) NOT NULL,
    instructions        TEXT,
    start_date          DATE         NOT NULL,
    end_date            DATE,
    status              VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    prescribed_by_name  VARCHAR(160),
    document_url        TEXT,
    -- audit columns
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by          UUID,
    updated_by          UUID,
    deleted_at          TIMESTAMPTZ,
    deleted_by          UUID,
    version             BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_prescriptions      PRIMARY KEY (id),
    CONSTRAINT fk_prescriptions_pet  FOREIGN KEY (pet_id)  REFERENCES pets(id),
    CONSTRAINT fk_prescriptions_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_prescriptions_pet_status  ON prescriptions (pet_id, status);
CREATE INDEX idx_prescriptions_user_start  ON prescriptions (user_id, start_date DESC);

-- ============================================================
-- prescription_doses
-- ============================================================
CREATE TABLE prescription_doses (
    id               UUID        NOT NULL DEFAULT gen_random_uuid(),
    prescription_id  UUID        NOT NULL,
    user_id          UUID        NOT NULL,
    taken_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    note             TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_prescription_doses             PRIMARY KEY (id),
    CONSTRAINT fk_prescription_doses_rx          FOREIGN KEY (prescription_id) REFERENCES prescriptions(id),
    CONSTRAINT fk_prescription_doses_user        FOREIGN KEY (user_id)         REFERENCES users(id)
);

CREATE INDEX idx_prescription_doses_prescription
    ON prescription_doses (prescription_id, taken_at DESC);

-- ============================================================
-- vaccines
-- NOTE: vet_id FK to vets and booking_id FK to bookings are
--       added in V5 and V6 respectively after those tables exist.
-- ============================================================
CREATE TABLE vaccines (
    id                  UUID         NOT NULL DEFAULT gen_random_uuid(),
    pet_id              UUID         NOT NULL,
    user_id             UUID         NOT NULL,
    vet_id              UUID,
    booking_id          UUID,
    vaccine_name        VARCHAR(180) NOT NULL,
    dose_label          VARCHAR(80),
    due_date            DATE,
    administered_date   DATE,
    status              VARCHAR(32)  NOT NULL DEFAULT 'DUE',
    provider_name       VARCHAR(160),
    certificate_url     TEXT,
    notes               TEXT,
    -- audit columns
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by          UUID,
    updated_by          UUID,
    deleted_at          TIMESTAMPTZ,
    deleted_by          UUID,
    version             BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_vaccines      PRIMARY KEY (id),
    CONSTRAINT fk_vaccines_pet  FOREIGN KEY (pet_id)  REFERENCES pets(id),
    CONSTRAINT fk_vaccines_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- unique vaccine per pet per due_date per non-deleted row (when due_date is present)
CREATE UNIQUE INDEX ux_vaccines_pet_name_due_active
    ON vaccines (pet_id, vaccine_name, due_date)
    WHERE due_date IS NOT NULL AND deleted_at IS NULL;

CREATE INDEX idx_vaccines_pet_status ON vaccines (pet_id, status);
CREATE INDEX idx_vaccines_user_due   ON vaccines (user_id, due_date);
CREATE INDEX idx_vaccines_status_due ON vaccines (status, due_date);
