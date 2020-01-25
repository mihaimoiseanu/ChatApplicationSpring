package com.coders.chat.webapp.security.filters

import com.coders.chat.persistence.user.User
import com.coders.chat.webapp.security.config.SecurityConfiguration
import com.coders.chat.webapp.security.jwt.JwtUtils
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JWTAuthenticationFilter(authManager: AuthenticationManager,
                              private val securityConfiguration: SecurityConfiguration
) : BasicAuthenticationFilter(authManager) {

    /**
     * Parse the user's credentials and issue them to the AuthenticationManager.
     */
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = request.getHeader(securityConfiguration.headerName)
        when {
            header == null -> chain.doFilter(request, response)
            header.startsWith(securityConfiguration.basicPrefix) -> super.doFilterInternal(request, response, chain)
            else -> chain.doFilter(request, response)
        }
    }

    /**
     *  Method called when a user successfully logs in. Used to generate a JWT for this user.
     */
    override fun onSuccessfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, authResult: Authentication?) {
        val user = authResult?.principal as? User
                ?: throw RuntimeException("Can't extract principal from authentication")

        val claims = JwtUtils.createClaims(securityConfiguration, user)
        val token = JwtUtils.createToken(claims, securityConfiguration)
        response!!.addHeader(securityConfiguration.headerName, securityConfiguration.tokenPrefix + token)
    }
}