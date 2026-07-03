package com.tailtown.backend.infrastructure.persistence.shop

import com.tailtown.backend.common.AuditableEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.UUID

@Entity
@Table(name = "categories")
@EntityListeners(AuditingEntityListener::class)
class CategoryEntity(

    @Column(name = "parent_id", columnDefinition = "uuid")
    var parentId: UUID? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "slug", nullable = false, unique = true)
    var slug: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "image_url")
    var imageUrl: String? = null

) : AuditableEntity()
