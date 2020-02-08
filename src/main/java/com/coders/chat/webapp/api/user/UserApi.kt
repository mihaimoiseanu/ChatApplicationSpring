package com.coders.chat.webapp.api.user

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

    @GetMapping(value = ["/friendships/{id}"])
    fun getFriendship(@PathVariable("id") friendshipId: Long): FriendshipDTO {
        return userService.getFriendship(friendshipId)
    }

    @PostMapping(value = ["/friendships"])
    fun requestFriendship(@RequestBody friendshipDTO: FriendshipDTO) =
            userService.requestFriendship(friendshipDTO)

    @PutMapping(value = ["/friendships"])
    fun updateFriendship(@RequestBody friendshipDTO: FriendshipDTO): FriendshipDTO {
        return when (friendshipDTO.status) {
            FriendshipStatus.PENDING -> userService.handlePendingStatus(friendshipDTO)
            FriendshipStatus.ACCEPTED -> userService.handleAcceptedStatus(friendshipDTO)
            FriendshipStatus.BLOCKED -> userService.handleBlockedStatus(friendshipDTO)
        }
    }

    @DeleteMapping(value = ["/friendships/{id}"])
    fun deleteFriendship(@PathVariable(name = "id") friendshipId: Long) {
        userService.deleteFriendship(friendshipId)
    }

    @GetMapping(value = ["/{id}/friends"])
    fun getUserFriends(@PathVariable("id") userId: Long): List<UserDto> = userService.getUserFriends(userId)

}