package com.coders.chat.persistence.user.friendship

import com.coders.chat.model.friendship.FriendshipStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FriendshipRepository : JpaRepository<Friendship, FriendshipKey> {

    @Query("select f from friendship  f where f.status = 1 and (f.userOne.id = :userId or f.userTwo.id = :userId) ")
    fun getFriendships(@Param("userId") userId: Long): List<Friendship>

    @Query("select f from friendship  f where f.status = :status and (f.userOne.id = :userId or f.userTwo.id = :userId) ")
    fun getFriendships(@Param("userId") userId: Long, @Param("status") status: FriendshipStatus): List<Friendship>

    @Query("select f from friendship f where f.status = 2 and (f.userOne.id = :userId or f.userTwo.id = :userId) and f.actionUserId <> :userId")
    fun getBlockedFriendships(userId: Long): List<Friendship>

}