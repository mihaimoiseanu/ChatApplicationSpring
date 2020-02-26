package com.coders.chat.service.event

import com.coders.chat.model.event.Event
import com.coders.chat.model.event.EventType
import com.coders.chat.model.friendship.FriendshipDTO
import com.coders.chat.model.friendship.FriendshipStatus
import com.coders.chat.model.message.MessageDTO
import com.coders.chat.model.room.RoomDTO
import com.coders.chat.service.message.MessageService
import com.coders.chat.service.principal.PrincipalService
import com.coders.chat.service.room.RoomService
import com.coders.chat.service.user.UserService
import com.coders.chat.service.user.UserServiceBean
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class EventServiceBean(
        private val userService: UserService,
        private val roomService: RoomService,
        private val messageService: MessageService,
        private val principalService: PrincipalService,
        private val simp: SimpMessagingTemplate
) : EventService {

    override fun handleFriendshipEvent(event: Event<FriendshipDTO>) {
        val friendshipDTO = event.eventDTO
        var friendshipUpdated: FriendshipDTO = friendshipDTO!!
        when (event.type) {
            EventType.FRIENDSHIP_CREATED -> {
                friendshipUpdated = userService.requestFriendship(friendshipDTO)
            }
            EventType.FRIENDSHIP_UPDATED -> {
                friendshipUpdated = when (friendshipDTO.status) {
                    FriendshipStatus.PENDING -> userService.handlePendingStatus(friendshipDTO)
                    FriendshipStatus.ACCEPTED -> userService.handleAcceptedStatus(friendshipDTO)
                    FriendshipStatus.BLOCKED -> userService.handleBlockedStatus(friendshipDTO)
                    FriendshipStatus.NONE -> TODO("Maybe delete it?")
                }
            }
            EventType.FRIENDSHIP_DELETED -> {
                userService.deleteFriendship(friendshipDTO.user.id!!)
            }
            else -> { /* do nothing */
            }
        }
        val principal = principalService.getPrincipal()
        simp.convertAndSend("/events-replay/${principal.id}", Event(event.type, friendshipUpdated))
        val otherUserId = friendshipDTO.user.id
        simp.convertAndSend("/events-replay/$otherUserId",
                Event(
                        event.type,
                        FriendshipDTO(
                                user = UserServiceBean.fromEntity(principal),
                                status = friendshipUpdated.status,
                                lastUserActioned = friendshipUpdated.lastUserActioned
                        )
                )
        )
    }

    override fun handleRoomEvent(event: Event<RoomDTO>) {
        val roomDTO = event.eventDTO
        when (event.type) {
            EventType.ROOM_CREATED -> {
                roomService.createRoom(roomDTO?.users?.map { it.id!! }!!)
            }
            else -> {
                /* do nothing */
            }
        }
    }

    override fun handleMessageEvent(event: Event<MessageDTO>) {
        val messageDTO = event.eventDTO
        when (event.type) {
            EventType.MESSAGE_CREATED -> {
                messageService.addMessage(messageDTO!!)
            }
            else -> {
                /* do nothing */
            }
        }
    }
}