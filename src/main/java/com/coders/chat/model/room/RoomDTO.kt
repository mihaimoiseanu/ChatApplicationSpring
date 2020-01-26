package com.coders.chat.model.room

data class RoomDTO(
        val id: Long? = null,
        val name: String? = null,
        val isPrivate: Boolean? = false,
        val users: List<Long>? = null
)