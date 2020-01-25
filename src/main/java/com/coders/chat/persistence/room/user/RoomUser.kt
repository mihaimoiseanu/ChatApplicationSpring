package com.coders.chat.persistence.room.user

import com.coders.chat.persistence.room.Room
import com.coders.chat.persistence.user.User
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "room_user")
@Inheritance(strategy = InheritanceType.JOINED)
class RoomUser(
        @EmbeddedId
        var id: RoomUserKey? = null,

        @ManyToOne
        @MapsId("room_id")
        @JoinColumn(name = "room_id")
        var room: Room? = null,

        @ManyToOne
        @MapsId("user_id")
        @JoinColumn(name = "user_id")
        var user: User? = null,

        var added: LocalDateTime? = null) {

    @PrePersist
    fun prePersist() {
        id = RoomUserKey(room?.id, user?.id)
        added = LocalDateTime.now()
    }
}