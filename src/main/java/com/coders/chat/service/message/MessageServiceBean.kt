package com.coders.chat.service.message

import com.coders.chat.model.message.MessageDTO
import com.coders.chat.persistence.message.Message
import com.coders.chat.persistence.message.MessageRepository
import com.coders.chat.persistence.room.RoomRepository
import com.coders.chat.persistence.user.UserRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
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
        val sender = userRepository.getOne(messageDTO.senderId!!)
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
                    senderId = message.sender?.id,
                    sentAt = message.sentAt,
                    roomId = message.room?.id
            )
}