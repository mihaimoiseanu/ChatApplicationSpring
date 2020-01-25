package com.coders.chat.persistence.room.user

import org.springframework.data.jpa.repository.JpaRepository

interface RoomUserRepository : JpaRepository<RoomUser, RoomUserKey> {
}