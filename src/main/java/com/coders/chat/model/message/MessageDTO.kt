package com.coders.chat.model.message

import com.coders.chat.model.user.UserDto

data class MessageDTO(
    val id:Long? = null,
    val message:String? = null,
    val sender:UserDto? = null,
    val sentAt:Long? = null
)