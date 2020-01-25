package com.coders.chat.service.principal

import com.coders.chat.persistence.user.User
import com.coders.chat.persistence.user.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class PrincipalServiceBean(
    val userRepository: UserRepository
) : PrincipalService {

    override fun getPrincipal(): User {
        val principal = SecurityContextHolder.getContext().authentication.principal
        val username: String =
                when (principal) {
                    is String -> principal
                    is User -> principal.username
                    else -> throw EntityNotFoundException("Can't retrieve username!")
                }

        return userRepository.findByEmail(username) ?: throw EntityNotFoundException("User not found")
    }

    override fun getPrincipalOrNull(): User? {
        return try {
            getPrincipal()
        } catch (e: EntityNotFoundException) {
            null
        }
    }

    override fun getUsers(): List<User> {
        return userRepository.findAll()
    }
}