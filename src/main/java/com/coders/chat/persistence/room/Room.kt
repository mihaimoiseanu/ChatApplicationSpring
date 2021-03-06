package com.coders.chat.persistence.room

import com.coders.chat.persistence.base.TimestampedEntity
import com.coders.chat.persistence.message.Message
import com.coders.chat.persistence.room.user.RoomUser
import javax.persistence.*

@Entity(name = "room")
@Inheritance(strategy = InheritanceType.JOINED)
class Room(
        var name: String? = null,

        @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        var messages: Set<Message>? = null,

        @OneToMany(mappedBy = "room")
        var roomUsers: MutableSet<RoomUser>? = null

) : TimestampedEntity()