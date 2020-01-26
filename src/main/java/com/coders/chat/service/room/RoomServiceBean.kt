package com.coders.chat.service.room

import com.coders.chat.model.exceptions.base.ApplicationException
import com.coders.chat.model.room.RoomDTO
import com.coders.chat.model.user.UserDto
import com.coders.chat.persistence.room.Room
import com.coders.chat.persistence.room.RoomRepository
import com.coders.chat.persistence.room.user.RoomUser
import com.coders.chat.persistence.room.user.RoomUserRepository
import com.coders.chat.persistence.user.User
import com.coders.chat.service.principal.PrincipalService
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class RoomServiceBean(
        private val roomRepository: RoomRepository,
        private val roomUserRepository: RoomUserRepository,
        private val principalService: PrincipalService
) : RoomService {

    override fun getPublicRooms(): List<RoomDTO> {
        return roomRepository.findAllPublicRooms().map { fromEntity(it) }
    }

    override fun getMyRooms(): List<RoomDTO> {
        val user = principalService.getPrincipal()
        return roomRepository.findAllByUserId(user.id!!).map {
            fromEntity(it)
        }
    }

    @Transactional
    override fun createRoom(roomDTO: RoomDTO): RoomDTO {
        val currentUser = principalService.getPrincipal()
        val savedRoom = save(
                Room(
                        name = roomDTO.name,
                        isPrivate = roomDTO.isPrivate,
                        createdBy = currentUser
                )
        )
        roomUserRepository.save(
                RoomUser(
                        room = savedRoom,
                        user = currentUser
                )
        )
        return fromEntity(savedRoom)
    }

    override fun getRoom(roomId: Long): RoomDTO {
        val currentUser = principalService.getPrincipal()
        val room = read(roomId)
        val isInRoom = room?.roomUsers?.map { it.user }?.contains(currentUser) ?: false
        if (currentUser != room?.createdBy || !isInRoom) {
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
        val users = room.roomUsers?.map { roomUser -> roomUser.user }?.map { user -> user?.id ?: -1 }
        return RoomDTO(
                id = room.id,
                name = room.name,
                isPrivate = room.isPrivate,
                users = users
        )
    }
}