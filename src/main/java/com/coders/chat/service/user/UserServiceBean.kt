package com.coders.chat.service.user

import com.coders.chat.persistence.user.Role
import com.coders.chat.persistence.user.User
import com.coders.chat.persistence.user.UserRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.InvalidParameterException
import javax.annotation.PostConstruct
import javax.validation.Valid

@Service
open class UserServiceBean(
        val userRepository: UserRepository,
        val passwordEncoder: PasswordEncoder
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

    override fun getEntityRepository(): JpaRepository<User, Long> = userRepository

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
}