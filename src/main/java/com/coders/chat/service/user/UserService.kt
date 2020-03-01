package com.coders.chat.service.user

import com.coders.chat.model.friendship.FriendshipDTO
import com.coders.chat.model.friendship.FriendshipStatus
import com.coders.chat.model.user.UserDto
import com.coders.chat.persistence.user.User
import com.coders.chat.service.base.CrudService
import javax.transaction.Transactional
import javax.validation.Valid

interface UserService : CrudService<User, Long> {

    @Transactional
    fun signUp(@Valid userToRegister: User): User

    fun getUserFriends(userId: Long): List<UserDto>

    fun getFriendship(userId: Long, principalId: Long): FriendshipDTO

    fun getFriendships(userId: Long, status: FriendshipStatus?): List<FriendshipDTO>

    @Transactional
    fun requestFriendship(friendshipDTO: FriendshipDTO, principalId: Long): FriendshipDTO

    @Transactional
    fun handlePendingStatus(friendshipDTO: FriendshipDTO, principalId: Long): FriendshipDTO

    @Transactional
    fun handleAcceptedStatus(friendshipDTO: FriendshipDTO, principalId: Long): FriendshipDTO

    @Transactional
    fun handleBlockedStatus(friendshipDTO: FriendshipDTO, principalId: Long): FriendshipDTO

    @Transactional
    fun deleteFriendship(userId: Long, principalId: Long)

    fun getUsers(searchTerm: String?): List<UserDto>

}