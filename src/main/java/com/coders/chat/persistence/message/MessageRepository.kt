package com.coders.chat.persistence.message

import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository : JpaRepository<Message, Long> {

    fun findAllByRoomId(roomId: Long): List<Message>

    fun findAllBySenderId(senderId: Long): List<Message>
}