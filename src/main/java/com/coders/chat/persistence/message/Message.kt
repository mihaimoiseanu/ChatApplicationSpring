package com.coders.chat.persistence.message

import com.coders.chat.persistence.base.BaseEntity
import com.coders.chat.persistence.room.Room
import com.coders.chat.persistence.user.User
import javax.persistence.*
import javax.validation.constraints.NotEmpty

@Entity(name = "message")
@Inheritance(strategy = InheritanceType.JOINED)
class Message(
        @field:NotEmpty
        var message: String? = null,

        @Column(name = "sent_at")
        var sentAt:Long? = null,

        @ManyToOne
        @JoinColumn(name = "fk_room")
        var room: Room? = null,

        @ManyToOne
        @JoinColumn(name = "fk_user")
        var sender: User? = null
) : BaseEntity()