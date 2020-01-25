package com.coders.chat.webapp.security.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "security.jwt")
open class SecurityConfiguration {
    lateinit var secret: String
    var expirationTime: Long = 604_800_000 //= 7 days, gets overridden by application.yml
    lateinit var tokenPrefix: String
    lateinit var basicPrefix: String
    lateinit var headerName: String
    lateinit var signupURL: String
    lateinit var loginURL: String
    lateinit var roles: String
    val secretByteArray:ByteArray by lazy { secret.toByteArray() }

}