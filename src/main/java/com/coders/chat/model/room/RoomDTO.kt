package com.coders.chat.model.room

import com.coders.chat.model.user.UserDto

data class RoomDTO(
        val id: Long? = null,
        val name: String? = null,
        val isPrivate: Boolean? = false,
        val users: List<UserDto>? = null
)