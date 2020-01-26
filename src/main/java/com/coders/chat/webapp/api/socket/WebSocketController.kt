package com.coders.chat.webapp.api.socket

import com.coders.chat.model.message.MessageDTO
import com.coders.chat.service.message.MessageService
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller


@Controller
class WebSocketController
@Autowired constructor(
        private val messageService: MessageService) {

    private val logger: Logger =
            LoggerFactory.getLogger(WebSocketController::class.java.simpleName)


    @MessageMapping("/{room_id}/message")
    @SendTo("/chat-replay/{room_id}/message")
    fun processMessageFromClient(
            @DestinationVariable("room_id") roomId: Long,
            @Payload message: String?,
            headerAccessor: SimpMessageHeaderAccessor): MessageDTO {
        logger.debug(message)
        val messageDTO = ObjectMapper().readValue<MessageDTO>(message, MessageDTO::class.java)
        return messageService.addMessage(messageDTO, roomId)
    }
}