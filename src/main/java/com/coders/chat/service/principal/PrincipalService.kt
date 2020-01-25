package com.coders.chat.service.principal

import com.coders.chat.persistence.user.User

interface PrincipalService {

    fun getPrincipal(): User

    fun getPrincipalOrNull(): User?

    fun getUsers(): List<User>
}