package com.coders.chat.webapp.security.jwt

import com.coders.chat.persistence.user.User
import com.coders.chat.persistence.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
open class JwtService(
        private var userRepository: UserRepository
) {

    @Transactional
    open fun createUserJwtHash(user: User): User {
        user.jwtHash = UUID.randomUUID().toString()

        return userRepository.save(user)
    }
}