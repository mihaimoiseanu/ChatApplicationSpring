package com.coders.chat.model.event

data class Event<T : EventDTO>(
        val type: EventType? = null,
        val eventDTO: T? = null
)

interface EventDTO

enum class EventType {

    FRIENDSHIP_CREATED,
    FRIENDSHIP_UPDATED,
    FRIENDSHIP_DELETED,

    ROOM_CREATED,
    ROOM_UPDATED,
    ROOM_DELETED,

    MESSAGE_CREATED,
    MESSAGE_UPDATED,
    MESSAGE_DELETED
}