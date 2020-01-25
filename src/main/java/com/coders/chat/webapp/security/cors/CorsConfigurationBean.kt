package com.coders.chat.webapp.security.cors

import com.coders.chat.webapp.security.config.SecurityConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Component
class CorsConfigurationBean(private val securityConfiguration: SecurityConfiguration) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.applyPermitDefaultValues()
        corsConfiguration.addAllowedMethod("*")
        corsConfiguration.addExposedHeader(securityConfiguration.headerName)
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }
}