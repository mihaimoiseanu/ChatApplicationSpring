package com.coders.chat.persistence.base

import java.time.LocalDateTime
import javax.persistence.MappedSuperclass
import javax.persistence.PrePersist
import javax.persistence.PreUpdate

/**
 * Base class for an timestamped entity that extends {@see BaseEntity}
 */
@MappedSuperclass
open class TimestampedEntity : BaseEntity() {
    var created: LocalDateTime? = null
    var updated: LocalDateTime? = null

    @PrePersist
    open fun prePersist() {
        created = LocalDateTime.now()
    }

    @PreUpdate
    open fun preUpdate() {
        updated = LocalDateTime.now()
    }
}