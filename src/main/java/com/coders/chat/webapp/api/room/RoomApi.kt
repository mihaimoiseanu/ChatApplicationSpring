package com.coders.chat.webapp.api.room

import com.coders.chat.model.message.MessageDTO
import com.coders.chat.model.room.RoomDTO
import com.coders.chat.service.message.MessageService
import com.coders.chat.service.room.RoomService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["api/rooms"])
class RoomApi(private val roomService: RoomService,
              private val messageService: MessageService) {

    @GetMapping
    fun getMyRooms(): List<RoomDTO> = roomService.getMyRooms()

    @GetMapping(value = ["{id}"])
    fun getRoom(@PathVariable(value = "id") roomId: Long): RoomDTO {
        return roomService.getRoom(roomId)
    }

    @GetMapping(value = ["{id}/messages"])
    fun getMessages(@PathVariable("id") roomId: Long): List<MessageDTO> {
        return messageService.getAllForRoom(roomId)
    }
}