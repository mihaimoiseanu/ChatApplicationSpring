package com.coders.chat.persistence.room

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RoomRepository : JpaRepository<Room, Long> {

    @Query("select r from room r join r.roomUsers ru where ru.user.id = :userId")
    fun findAllByUserId(userId:Long):List<Room>
}