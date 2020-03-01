package com.coders.chat.webapp.api.user

import com.coders.chat.model.exceptions.base.ApplicationException
import com.coders.chat.model.friendship.FriendshipDTO
import com.coders.chat.model.friendship.FriendshipStatus
import com.coders.chat.model.user.UserDto
import com.coders.chat.persistence.user.User
import com.coders.chat.service.principal.PrincipalService
import com.coders.chat.service.user.UserService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping(value = ["api/users"])
class UserApi(
        private val userService: UserService,
        private val principalService: PrincipalService
) {

    @PostMapping(value = ["/signup"])
    fun signup(@Valid @RequestBody user: User): User {
        return userService.signUp(user)
    }

    @GetMapping(value = ["/principal"])
    fun getPrincipal() = principalService.getPrincipal()

    @GetMapping
    fun getUsers(@RequestParam(value = "search_term", required = false) searchTerm: String?) = userService.getUsers(searchTerm)

    @GetMapping(value = ["/friends"])
    fun getFriends(): List<UserDto> {
        val principal = principalService.getPrincipal()
        return userService.getUserFriends(principal.id!!)
    }

    @GetMapping(value = ["/friendships"])
    fun getFriendships(@RequestParam(value = "status", required = false) friendshipStatus: FriendshipStatus?): List<FriendshipDTO> {
        val principal = principalService.getPrincipal()
        return userService.getFriendships(principal.id!!, friendshipStatus)
    }

    @GetMapping(value = ["/friendships/{other_user_id}"])
    fun getFriendship(@PathVariable(value = "other_user_id") otherUserId: Long): FriendshipDTO {
        val principalId = principalService.getPrincipal().id!!
        return userService.getFriendship(otherUserId, principalId)
    }

    @PostMapping(value = ["/friendships"])
    fun requestFriendship(@RequestBody friendshipDTO: FriendshipDTO): FriendshipDTO {
        val principal = principalService.getPrincipal().id!!
        return userService.requestFriendship(friendshipDTO, principal)
    }

    @PutMapping(value = ["/friendships"])
    fun updateFriendship(@RequestBody friendshipDTO: FriendshipDTO): FriendshipDTO {
        val principal = principalService.getPrincipal().id!!
        if (principal == friendshipDTO.user?.id) {
            throw ApplicationException.conflictException("Can't be friend with yourself")
        }
        return when (friendshipDTO.status) {
            FriendshipStatus.PENDING -> userService.handlePendingStatus(friendshipDTO, principal)
            FriendshipStatus.ACCEPTED -> userService.handleAcceptedStatus(friendshipDTO, principal)
            FriendshipStatus.BLOCKED -> userService.handleBlockedStatus(friendshipDTO, principal)
            else -> {
                friendshipDTO
            }
        }
    }

    @DeleteMapping(value = ["/friendships"])
    fun deleteFriendship(@RequestParam(value = "other_user_id") otherUserId: Long) {
        val principal = principalService.getPrincipal().id!!
        userService.deleteFriendship(otherUserId, principal)
    }

    @GetMapping(value = ["/{id}/friends"])
    fun getUserFriends(@PathVariable("id") userId: Long): List<UserDto> = userService.getUserFriends(userId)

}