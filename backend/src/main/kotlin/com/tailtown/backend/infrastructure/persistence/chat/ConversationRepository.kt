package com.tailtown.backend.infrastructure.persistence.chat

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface ConversationRepository : JpaRepository<ConversationEntity, UUID> {

    fun findAllByUserIdAndDeletedAtIsNullOrderByUpdatedAtDesc(
        userId: UUID,
        pageable: Pageable
    ): Page<ConversationEntity>

    fun findByIdAndUserIdAndDeletedAtIsNull(
        id: UUID,
        userId: UUID
    ): Optional<ConversationEntity>
}
