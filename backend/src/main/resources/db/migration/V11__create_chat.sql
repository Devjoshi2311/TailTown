-- ============================================================
-- conversations
-- ============================================================
CREATE TABLE conversations (
    id                    UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id               UUID        NOT NULL,
    vet_id                UUID,
    booking_id            UUID,
    order_id              UUID,
    type                  VARCHAR(40) NOT NULL DEFAULT 'SUPPORT',
    status                VARCHAR(40) NOT NULL DEFAULT 'OPEN',
    subject               VARCHAR(180),
    last_message_preview  TEXT,
    last_message_at       TIMESTAMPTZ,
    unread_count_user     INT         NOT NULL DEFAULT 0,
    unread_count_admin    INT         NOT NULL DEFAULT 0,
    -- audit columns
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by            UUID,
    updated_by            UUID,
    deleted_at            TIMESTAMPTZ,
    deleted_by            UUID,
    version               BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_conversations            PRIMARY KEY (id),
    CONSTRAINT fk_conversations_user       FOREIGN KEY (user_id)    REFERENCES users(id),
    CONSTRAINT fk_conversations_vet        FOREIGN KEY (vet_id)     REFERENCES vets(id),
    CONSTRAINT fk_conversations_booking    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_conversations_order      FOREIGN KEY (order_id)   REFERENCES orders(id)
);

-- one conversation per booking per non-deleted row
CREATE UNIQUE INDEX ux_conversations_booking_active
    ON conversations (booking_id)
    WHERE booking_id IS NOT NULL AND deleted_at IS NULL;

CREATE INDEX idx_conversations_user_updated   ON conversations (user_id, updated_at DESC);
CREATE INDEX idx_conversations_status_updated ON conversations (status, updated_at DESC);
CREATE INDEX idx_conversations_vet_updated    ON conversations (vet_id, updated_at DESC);
CREATE INDEX idx_conversations_order          ON conversations (order_id);

-- ============================================================
-- messages
-- ============================================================
CREATE TABLE messages (
    id               UUID        NOT NULL DEFAULT gen_random_uuid(),
    conversation_id  UUID        NOT NULL,
    sender_user_id   UUID,
    sender_vet_id    UUID,
    sender_type      VARCHAR(32) NOT NULL,
    message_type     VARCHAR(32) NOT NULL DEFAULT 'TEXT',
    body             TEXT,
    attachment_url   TEXT,
    sent_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    read_at          TIMESTAMPTZ,
    -- audit columns
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by       UUID,
    updated_by       UUID,
    deleted_at       TIMESTAMPTZ,
    deleted_by       UUID,
    version          BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_messages                  PRIMARY KEY (id),
    CONSTRAINT fk_messages_conversation     FOREIGN KEY (conversation_id) REFERENCES conversations(id),
    CONSTRAINT fk_messages_sender_user      FOREIGN KEY (sender_user_id)  REFERENCES users(id),
    CONSTRAINT fk_messages_sender_vet       FOREIGN KEY (sender_vet_id)   REFERENCES vets(id)
);

CREATE INDEX idx_messages_conversation_sent ON messages (conversation_id, sent_at);
CREATE INDEX idx_messages_sender_user       ON messages (sender_user_id);
CREATE INDEX idx_messages_sender_vet        ON messages (sender_vet_id);
