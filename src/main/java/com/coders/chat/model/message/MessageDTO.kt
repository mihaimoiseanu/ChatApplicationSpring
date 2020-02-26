package com.coders.chat.model.message

import com.coders.chat.model.event.EventDTO

data class MessageDTO(
        val id: Long? = null,
        val message: String? = null,
        val senderId: Long? = null,
        val roomId: Long? = null,
        val sentAt: Long? = null
) : EventDTO