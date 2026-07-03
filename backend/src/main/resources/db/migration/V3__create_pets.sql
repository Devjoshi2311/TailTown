-- ============================================================
-- pets
-- ============================================================
CREATE TABLE pets (
    id              UUID         NOT NULL DEFAULT gen_random_uuid(),
    user_id         UUID         NOT NULL,
    name            VARCHAR(120) NOT NULL,
    species         VARCHAR(40)  NOT NULL,
    breed           VARCHAR(120),
    gender          VARCHAR(24),
    date_of_birth   DATE,
    weight_kg       NUMERIC(6,2),
    avatar_url      TEXT,
    microchip_id    VARCHAR(80),
    neutered        BOOLEAN,
    allergies       TEXT,
    medical_notes   TEXT,
    -- audit columns
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    deleted_at      TIMESTAMPTZ,
    deleted_by      UUID,
    version         BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_pets      PRIMARY KEY (id),
    CONSTRAINT fk_pets_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- unique microchip per non-deleted row (when microchip_id is present)
CREATE UNIQUE INDEX ux_pets_microchip_active
    ON pets (microchip_id)
    WHERE microchip_id IS NOT NULL AND deleted_at IS NULL;

CREATE INDEX idx_pets_user_active ON pets (user_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_pets_species     ON pets (species);
