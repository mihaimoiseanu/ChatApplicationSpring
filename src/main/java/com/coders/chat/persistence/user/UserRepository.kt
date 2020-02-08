package com.coders.chat.persistence.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String): User?

    fun findUserFriends(userId: Long): List<User>

    fun getAllUsers(userId: Long): List<User>

    fun searchUsers(userId: Long, searchTerm: String): List<User>
}