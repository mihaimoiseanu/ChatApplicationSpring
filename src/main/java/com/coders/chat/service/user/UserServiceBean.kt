package com.coders.chat.service.user

import com.coders.chat.model.event.Event
import com.coders.chat.model.event.EventType
import com.coders.chat.model.exceptions.base.ApplicationException
import com.coders.chat.model.friendship.FriendshipDTO
import com.coders.chat.model.friendship.FriendshipStatus
import com.coders.chat.model.user.UserDto
import com.coders.chat.persistence.user.Role
import com.coders.chat.persistence.user.User
import com.coders.chat.persistence.user.UserRepository
import com.coders.chat.persistence.user.friendship.Friendship
import com.coders.chat.persistence.user.friendship.FriendshipKey
import com.coders.chat.persistence.user.friendship.FriendshipRepository
import com.coders.chat.service.principal.PrincipalService
import com.coders.chat.service.room.RoomService
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.InvalidParameterException
import javax.annotation.PostConstruct
import javax.transaction.Transactional
import javax.validation.Valid

@Service
open class UserServiceBean(
        private val userRepository: UserRepository,
        private val principalService: PrincipalService,
        private val friendshipRepository: FriendshipRepository,
        private val roomService: RoomService,
        private val passwordEncoder: PasswordEncoder,
        private val simp: SimpMessagingTemplate
) : UserService {

    @Transactional
    override fun signUp(@Valid userToRegister: User): User {
        val encodedPassword = passwordEncoder.encode(
                userToRegister.pass ?: throw InvalidParameterException("Password can't be null!")
        )
        userToRegister.pass = encodedPassword
        userToRegister.roles = userToRegister.roles ?: mutableSetOf(Role.ROLE_USER)
        return userRepository.save(userToRegister)
    }

    override fun getUsers(searchTerm: String?): List<UserDto> {
        val principal = principalService.getPrincipal()
        if (searchTerm != null) {
            val search = "%$searchTerm%"
            return userRepository.searchUsers(principal.id!!, search).filter { it.id != principal.id }.map { fromEntity(it) }
        }
        return userRepository.getAllUsers(principal.id!!).filter { it.id != principal.id }.map { fromEntity(it) }
    }

    override fun getUserFriends(userId: Long): List<UserDto> {
        return userRepository.findUserFriends(userId).map { fromEntity(it) }
    }

    @Transactional
    override fun requestFriendship(friendshipDTO: FriendshipDTO): FriendshipDTO {
        val principal = principalService.getPrincipal()
        val friend = read(friendshipDTO.user.id!!)
        if (principal == friend) {
            throw ApplicationException.conflictException("Can't be friend with yourself")
        }
        val key = generateFriendshipKey(friend?.id!!)
        friendshipRepository.findByIdOrNull(key)?.let {
            return fromEntity(it, principal.id!!)
        }
        val friendship = friendshipRepository.save(Friendship(
                userOne = principal,
                userTwo = friend,
                status = FriendshipStatus.PENDING,
                actionUserId = principal.id
        ))
        val event = Event(EventType.FRIENDSHIP_CREATED, FriendshipDTO(fromEntity(principal), FriendshipStatus.PENDING, principal.id))
        simp.convertAndSend("/events-replay/${friend.id}", event)
        return fromEntity(friendship, principal.id!!)
    }

    override fun getFriendship(userId: Long): FriendshipDTO {
        val key = generateFriendshipKey(userId)
        val friendship = friendshipRepository.findByIdOrNull(key)
        val otherUser = userRepository.getOne(userId)
        return fromEntity(friendship, otherUser)
    }

    override fun getFriendships(userId: Long, status: FriendshipStatus?): List<FriendshipDTO> {
        status?.let {
            if (FriendshipStatus.BLOCKED == status) {
                //if we want to query for the blocked people we don't want to show the ones that blocked us
                return friendshipRepository.getBlockedFriendships(userId).map { fr -> fromEntity(fr, userId) }
            }
            return friendshipRepository.getFriendships(userId, it).map { fr -> fromEntity(fr, userId) }
        }
        return friendshipRepository.getFriendships(userId).map { fromEntity(it, userId) }
    }

    @Transactional
    override fun handlePendingStatus(friendshipDTO: FriendshipDTO): FriendshipDTO {
        throw ApplicationException.conflictException("You can't update an existing invite to pending ")
    }

    @Transactional
    override fun handleAcceptedStatus(friendshipDTO: FriendshipDTO): FriendshipDTO {
        val principal = principalService.getPrincipal()
        val key = generateFriendshipKey(friendshipDTO.user.id!!)
        val friendship = friendshipRepository.getOne(key)

        if (FriendshipStatus.BLOCKED == friendship.status && friendship.actionUserId != principal.id) {
            throw ApplicationException.conflictException("You can't unblock yourself")
        }

        if (FriendshipStatus.PENDING == friendship.status && friendship.actionUserId == principal.id) {
            throw ApplicationException.conflictException("You can't accept this invitation")
        }
        friendship.actionUserId = principal.id
        friendship.status = friendshipDTO.status
        val updatedFriendship = friendshipRepository.save(friendship)
        roomService.createRoom(listOf(updatedFriendship.userOne?.id!!, updatedFriendship.userTwo?.id!!))
        val event = Event(EventType.FRIENDSHIP_UPDATED, FriendshipDTO(fromEntity(principal), FriendshipStatus.PENDING, principal.id))
        simp.convertAndSend("/events-replay/${friendshipDTO.user.id}", event)
        return fromEntity(updatedFriendship, principal.id!!)
    }

    @Transactional
    override fun handleBlockedStatus(friendshipDTO: FriendshipDTO): FriendshipDTO {
        val principal = principalService.getPrincipal()
        val key = generateFriendshipKey(friendshipDTO.user.id!!)
        val friendship = friendshipRepository.getOne(key)

        if (FriendshipStatus.PENDING == friendship.status && friendship.actionUserId == principal.id) {
            throw ApplicationException.conflictException("You are not friend with this user, so why block?!")
        }
        friendship.actionUserId = principal.id
        friendship.status = friendshipDTO.status
        val updatedFriendship = friendshipRepository.save(friendship)
        val event = Event(EventType.FRIENDSHIP_UPDATED, FriendshipDTO(fromEntity(principal), FriendshipStatus.PENDING, principal.id))
        simp.convertAndSend("/events-replay/${friendshipDTO.user.id}", event)
        return fromEntity(updatedFriendship, principal.id!!)
    }

    @Transactional
    override fun deleteFriendship(userId: Long) {
        val key = generateFriendshipKey(userId)
        val principal = principalService.getPrincipal()
        friendshipRepository.deleteById(key)
        val event = Event(EventType.FRIENDSHIP_UPDATED, FriendshipDTO(fromEntity(principal), FriendshipStatus.PENDING, principal.id))
        simp.convertAndSend("/events-replay/${userId}", event)
    }

    override fun getEntityRepository(): JpaRepository<User, Long> = userRepository

    private fun generateFriendshipKey(otherUserId: Long): FriendshipKey {
        val principal = principalService.getPrincipal()
        val firstKey = if (principal.id!! < otherUserId) principal.id else otherUserId
        val secondKey = if (principal.id!! > otherUserId) principal.id else otherUserId
        return FriendshipKey(firstKey, secondKey)
    }

    @PostConstruct
    @Transactional
    open fun createAdmin() {
        val user = User(
                email = "admin@admin.com",
                pass = "Pass4me!",
                firstName = "Admin",
                lastName = "Super",
                roles = mutableSetOf(Role.ROLE_ADMIN)
        )
        user.email
                ?.let { userRepository.findByEmail(it) }
                ?: signUp(user)
        println("admin created")
    }

    private fun fromEntity(friendship: Friendship, loggedUserId: Long) = FriendshipDTO(
            user = fromEntity(
                    if (loggedUserId == friendship.userOne?.id) friendship.userTwo!! else friendship.userOne!!
            ),
            status = friendship.status!!,
            lastUserActioned = friendship.actionUserId
    )

    private fun fromEntity(friendship: Friendship?, otherUser: User) = FriendshipDTO(
            user = fromEntity(otherUser),
            status = friendship?.status ?: FriendshipStatus.NONE,
            lastUserActioned = friendship?.actionUserId
    )

    companion object {
        fun fromEntity(user: User): UserDto {
            return UserDto(
                    id = user.id,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email
            )
        }
    }

}