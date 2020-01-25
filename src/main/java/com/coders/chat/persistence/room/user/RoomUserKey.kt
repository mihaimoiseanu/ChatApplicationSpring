package com.coders.chat.persistence.room.user

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class RoomUserKey(
        @Column(name = "room_id")
        val roomId:Long? = null,
        @Column(name = "user_id")
        var userId:Long? = null
):Serializable