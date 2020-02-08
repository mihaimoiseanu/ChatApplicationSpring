package com.coders.chat.persistence.user.friendship

import com.coders.chat.model.friendship.FriendshipStatus
import com.coders.chat.persistence.user.User
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "friendship")
@Inheritance(strategy = InheritanceType.JOINED)
class Friendship(
        @EmbeddedId
        var id: FriendshipKey? = null,

        @ManyToOne
        @MapsId("user_one_id")
        @JoinColumn(name = "user_one_id")
        var userOne: User? = null,

        @ManyToOne
        @MapsId("user_two_id")
        @JoinColumn(name = "user_two_id")
        var userTwo: User? = null,

        @Enumerated(EnumType.ORDINAL)
        var status: FriendshipStatus? = null,

        @Column(name = "action_user_id")
        var actionUserId: Long? = null,

        @Column(name = "last_action_time")
        var lastActionTime: LocalDateTime? = null
) {
    @PrePersist
    fun prePersist() {
        val firstId = minOf(userOne!!.id!!, userTwo!!.id!!)
        val secondID = maxOf(userOne!!.id!!, userTwo!!.id!!)
        id = FriendshipKey(firstId, secondID)
        lastActionTime = LocalDateTime.now()
    }
}