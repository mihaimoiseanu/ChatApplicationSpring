package com.coders.chat.service.event

import com.coders.chat.model.event.Event
import com.coders.chat.model.friendship.FriendshipDTO
import com.coders.chat.model.message.MessageDTO
import com.coders.chat.model.room.RoomDTO

interface EventService {
    fun handleFriendshipEvent(event: Event<FriendshipDTO>)
    fun handleRoomEvent(event: Event<RoomDTO>)
    fun handleMessageEvent(event: Event<MessageDTO>)
}