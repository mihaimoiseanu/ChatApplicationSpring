package com.coders.chat.service.message

import com.coders.chat.model.message.MessageDTO
import com.coders.chat.model.user.UserDto
import com.coders.chat.persistence.message.Message
import com.coders.chat.persistence.message.MessageRepository
import com.coders.chat.persistence.room.RoomRepository
import com.coders.chat.persistence.user.User
import com.coders.chat.persistence.user.UserRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import java.sql.Timestamp
import javax.transaction.Transactional

@Service
open class MessageServiceBean(
        private val messageRepository: MessageRepository,
        private val roomRepository: RoomRepository,
        private val userRepository: UserRepository
) : MessageService {

    override fun getAllForRoom(roomId: Long): List<MessageDTO> {
        return messageRepository.findAllByRoomId(roomId).map { fromEntity(it) }.sortedBy { it.sentAt }
    }

    @Transactional
    override fun addMessage(messageDTO: MessageDTO, roomId: Long): MessageDTO {
        val room = roomRepository.getOne(roomId)
        val sender = userRepository.getOne(messageDTO.sender?.id!!)
        val message = save(
                Message(
                        message = messageDTO.message,
                        sender = sender,
                        room = room,
                        sentAt = messageDTO.sentAt
                )
        )
        return fromEntity(message)
    }

    override fun getEntityRepository(): JpaRepository<Message, Long> = messageRepository

    private fun fromEntity(message: Message): MessageDTO =
            MessageDTO(
                    id = message.id,
                    message = message.message,
                    sender = fromEntity(message.sender!!),
                    sentAt = message.sentAt
            )

    private fun fromEntity(user: User): UserDto {
        return UserDto(
                id = user.id,
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email
        )
    }
}