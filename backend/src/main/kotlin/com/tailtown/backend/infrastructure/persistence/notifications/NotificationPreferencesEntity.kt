package com.tailtown.backend.infrastructure.persistence.notifications

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.util.UUID

@Entity
@Table(name = "notification_preferences")
class NotificationPreferencesEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false, unique = true, columnDefinition = "uuid")
    var userId: UUID,

    @Column(name = "appointments", nullable = false)
    var appointments: Boolean = true,

    @Column(name = "medications", nullable = false)
    var medications: Boolean = true,

    @Column(name = "orders", nullable = false)
    var orders: Boolean = true,

    @Column(name = "promos", nullable = false)
    var promos: Boolean = true,

    @Column(name = "chat", nullable = false)
    var chat: Boolean = true,

    @Version
    @Column(name = "version")
    var version: Long = 0
)
