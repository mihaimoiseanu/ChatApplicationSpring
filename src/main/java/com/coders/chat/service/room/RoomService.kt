package com.coders.chat.service.room

import com.coders.chat.model.room.RoomDTO
import com.coders.chat.persistence.room.Room
import com.coders.chat.service.base.CrudService
import javax.transaction.Transactional

interface RoomService:CrudService<Room,Long> {
    fun getPublicRooms(): List<RoomDTO>
    @Transactional
    fun createRoom(roomDTO: RoomDTO): RoomDTO
    fun getRoom(roomId: Long): RoomDTO
    fun getMyRooms(): List<RoomDTO>
}