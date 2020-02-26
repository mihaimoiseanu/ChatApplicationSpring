package com.coders.chat.service.room

import com.coders.chat.model.event.Event
import com.coders.chat.model.event.EventType
import com.coders.chat.model.exceptions.base.ApplicationException
import com.coders.chat.model.room.RoomDTO
import com.coders.chat.model.user.UserDto
import com.coders.chat.persistence.message.MessageRepository
import com.coders.chat.persistence.room.Room
import com.coders.chat.persistence.room.RoomRepository
import com.coders.chat.persistence.room.user.RoomUser
import com.coders.chat.persistence.room.user.RoomUserRepository
import com.coders.chat.persistence.user.User
import com.coders.chat.persistence.user.UserRepository
import com.coders.chat.service.principal.PrincipalService
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class RoomServiceBean(
        private val roomRepository: RoomRepository,
        private val roomUserRepository: RoomUserRepository,
        private val principalService: PrincipalService,
        private val userRepository: UserRepository,
        private val messageRepository: MessageRepository,
        private val simpMessagingTemplate: SimpMessagingTemplate
) : RoomService {

    override fun getMyRooms(): List<RoomDTO> {
        val user = principalService.getPrincipal()
        return roomRepository.findAllByUserId(user.id!!).map {
            fromEntity(it)
        }
    }

    @Transactional
    override fun createRoom(userIds: List<Long>): RoomDTO {
        val savedRoom = save(Room())
        userIds.forEach {
            val user = userRepository.getOne(it)
            roomUserRepository.save(
                    RoomUser(
                            room = savedRoom,
                            user = user
                    )
            )
        }
        val response = fromEntity(savedRoom)
        response.users?.forEach {
            simpMessagingTemplate.convertAndSend(
                    "/events-replay/${it.id}",
                    Event(EventType.ROOM_CREATED,
                            response)
            )
        }
        return fromEntity(savedRoom)
    }

    override fun getRoom(roomId: Long): RoomDTO {
        val currentUser = principalService.getPrincipal()
        val room = read(roomId) ?: throw ApplicationException.notFoundException("room not found")
        val isInRoom = room.roomUsers?.map { it.user }?.contains(currentUser) ?: false
        if (!isInRoom) {
            throw ApplicationException.conflictException("Can't get a room that you are not in")
        }
        return fromEntity(room)
    }

    override fun getEntityRepository(): JpaRepository<Room, Long> {
        return roomRepository
    }

    private fun fromEntity(user: User): UserDto {
        return UserDto(
                id = user.id,
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email
        )
    }

    private fun fromEntity(room: Room): RoomDTO {
        val users = room.roomUsers?.map { roomUser -> roomUser.user }?.map { user -> fromEntity(user!!) } ?: emptyList()
        val lastMessage = messageRepository.findFirstByRoomIdOrderBySentAtDesc(room.id!!)
        return RoomDTO(
                id = room.id,
                name = room.name,
                users = users,
                lastMessageId = lastMessage?.id
        )
    }
}