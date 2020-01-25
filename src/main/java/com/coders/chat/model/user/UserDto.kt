package com.coders.chat.model.user

data class UserDto(
        var id: Long? = null,
        var email: String? = null,
        var firstName: String? = null,
        var lastName: String? = null
)
