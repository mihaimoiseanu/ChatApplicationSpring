package com.coders.chat.webapp.api.socket

import com.coders.chat.model.event.Event
import com.coders.chat.model.event.EventType
import com.coders.chat.model.exceptions.base.ApplicationException
import com.coders.chat.model.friendship.FriendshipDTO
import com.coders.chat.model.message.MessageDTO
import com.coders.chat.model.room.RoomDTO
import com.coders.chat.persistence.user.User
import com.coders.chat.service.event.EventService
import com.coders.chat.service.principal.PrincipalService
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller


@Controller
class WebSocketController
@Autowired constructor(
        private val eventService: EventService,
        private val principalService: PrincipalService) {

    private val logger: Logger =
            LoggerFactory.getLogger(WebSocketController::class.java.simpleName)


    @MessageMapping("/{user_id}")
    fun processEvents(
            @DestinationVariable("user_id") senderId: Long,
            @Payload message: String?,
            headerAccessor: SimpMessageHeaderAccessor) {
        logger.debug(message)
        val principalId = ((headerAccessor.messageHeaders["simpUser"] as UsernamePasswordAuthenticationToken).principal as User).id
        if (senderId != principalId) {
            throw ApplicationException.conflictException("You can't send events for other user except yourself")
        }
        when (EventType.valueOf(ObjectMapper().readTree(message).get("type").asText())) {
            EventType.FRIENDSHIP_CREATED,
            EventType.FRIENDSHIP_UPDATED,
            EventType.FRIENDSHIP_DELETED -> {
                val event = ObjectMapper().readValue(message, object : TypeReference<Event<FriendshipDTO>>() {})
                eventService.handleFriendshipEvent(event)
            }
            EventType.ROOM_CREATED,
            EventType.ROOM_UPDATED,
            EventType.ROOM_DELETED -> {
                val event = ObjectMapper().readValue(message, object : TypeReference<Event<RoomDTO>>() {})
                eventService.handleRoomEvent(event)
            }
            EventType.MESSAGE_CREATED,
            EventType.MESSAGE_UPDATED,
            EventType.MESSAGE_DELETED -> {
                val event = ObjectMapper().readValue(message, object : TypeReference<Event<MessageDTO>>() {})
                eventService.handleMessageEvent(event)
            }
        }
    }
}