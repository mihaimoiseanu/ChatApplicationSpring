package com.coders.chat.persistence.room

import com.coders.chat.persistence.base.TimestampedEntity
import com.coders.chat.persistence.message.Message
import com.coders.chat.persistence.room.user.RoomUser
import com.coders.chat.persistence.user.User
import javax.persistence.*

@Entity(name = "room")
@Inheritance(strategy = InheritanceType.JOINED)
class Room(
        var name: String? = null,

        @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        var messages: Set<Message>? = null,

        @OneToMany(mappedBy = "room")
        var roomUsers: Set<RoomUser>? = null,

        @Column(name = "is_private")
        var isPrivate: Boolean? = null,

        @ManyToOne
        @JoinColumn(name = "fk_created_by")
        var createdBy: User? = null

) : TimestampedEntity()