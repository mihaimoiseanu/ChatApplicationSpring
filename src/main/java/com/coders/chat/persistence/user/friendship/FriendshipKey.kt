package com.coders.chat.persistence.user.friendship

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class FriendshipKey(
        @Column(name = "user_one_id")
        val userOneId: Long? = null,
        @Column(name = "user_two_id")
        val userTwoId: Long? = null
) : Serializable