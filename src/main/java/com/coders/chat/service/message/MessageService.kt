package com.coders.chat.service.message

import com.coders.chat.model.message.MessageDTO
import com.coders.chat.persistence.message.Message
import com.coders.chat.service.base.CrudService
import javax.transaction.Transactional

interface MessageService : CrudService<Message, Long> {

    fun getAllForRoom(roomId:Long):List<MessageDTO>

    @Transactional
    fun addMessage(messageDTO: MessageDTO, roomId: Long) : MessageDTO
}