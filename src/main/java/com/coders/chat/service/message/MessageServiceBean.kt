package com.coders.chat.service.message

import com.coders.chat.model.event.Event
import com.coders.chat.model.event.EventType
import com.coders.chat.model.message.MessageDTO
import com.coders.chat.persistence.message.Message
import com.coders.chat.persistence.message.MessageRepository
import com.coders.chat.persistence.room.RoomRepository
import com.coders.chat.persistence.user.UserRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class MessageServiceBean(
        private val messageRepository: MessageRepository,
        private val roomRepository: RoomRepository,
        private val userRepository: UserRepository,
        private val simpMessagingTemplate: SimpMessagingTemplate
) : MessageService {

    override fun getAllForRoom(roomId: Long): List<MessageDTO> {
        return messageRepository.findAllByRoomId(roomId).map { fromEntity(it) }.sortedBy { it.sentAt }
    }

    @Transactional
    override fun addMessage(messageDTO: MessageDTO): MessageDTO {
        val room = roomRepository.getOne(messageDTO.roomId!!)
        val sender = userRepository.getOne(messageDTO.senderId!!)
        val message = save(
                Message(
                        message = messageDTO.message,
                        sender = sender,
                        room = room,
                        sentAt = messageDTO.sentAt
                )
        )
        val response = fromEntity(message)
        room.roomUsers?.map { it.user }?.forEach {
            simpMessagingTemplate.convertAndSend(
                    "/events-replay/${it!!.id}",
                    Event(EventType.MESSAGE_CREATED,
                            response
                    )
            )
        }
        return response
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