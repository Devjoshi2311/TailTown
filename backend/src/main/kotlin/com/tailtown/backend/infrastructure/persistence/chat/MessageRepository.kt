package com.tailtown.backend.infrastructure.persistence.chat

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MessageRepository : JpaRepository<MessageEntity, UUID> {

    fun findAllByConversationIdAndDeletedAtIsNullOrderBySentAtDesc(
        conversationId: UUID,
        pageable: Pageable
    ): Page<MessageEntity>
}
