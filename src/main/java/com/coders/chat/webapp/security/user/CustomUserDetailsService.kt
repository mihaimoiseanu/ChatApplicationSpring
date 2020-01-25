package com.coders.chat.webapp.security.user

import com.coders.chat.persistence.user.User
import com.coders.chat.persistence.user.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(email: String): User {
        return userRepository.findByEmail(email) ?: throw UsernameNotFoundException(email)
    }
}