package com.coders.chat.model.friendship

import com.coders.chat.model.event.EventDTO
import com.coders.chat.model.user.UserDto

data class FriendshipDTO(
        val user: UserDto,
        val status: FriendshipStatus = FriendshipStatus.NONE,
        val lastUserActioned: Long?
) : EventDTO