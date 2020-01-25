package com.coders.chat.webapp.api.user

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
    fun getUsers() = principalService.getUsers()


}