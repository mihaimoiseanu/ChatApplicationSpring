package com.coders.chat.model.friendship

import com.coders.chat.model.event.EventDTO
import com.coders.chat.model.user.UserDto

data class FriendshipDTO(
        val user: UserDto? = null,
        val status: FriendshipStatus? = null,
        val lastUserActioned: Long? = null
) : EventDTO