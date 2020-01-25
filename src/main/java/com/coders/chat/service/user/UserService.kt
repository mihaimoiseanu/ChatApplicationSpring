package com.coders.chat.service.user

import com.coders.chat.persistence.user.User
import com.coders.chat.service.base.CrudService
import org.springframework.transaction.annotation.Transactional
import javax.validation.Valid

interface UserService : CrudService<User, Long> {

    @Transactional
    fun signUp(@Valid userToRegister: User): User
}