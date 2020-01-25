package com.coders.chat.webapp.security.filters

import com.coders.chat.webapp.security.config.SecurityConfiguration
import com.coders.chat.webapp.security.user.CustomUserDetailsService
import io.jsonwebtoken.Jwts
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(authenticationManager: AuthenticationManager?,
                             private val securityConfiguration: SecurityConfiguration,
                             private val userDetailsService: CustomUserDetailsService
) : BasicAuthenticationFilter(authenticationManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = request.getHeader(securityConfiguration.headerName)

        if (header == null || !header.startsWith(securityConfiguration.tokenPrefix)) {
            chain.doFilter(request, response)
            return
        }
        var authentication = getAuthentication(request)
        SecurityContextHolder.getContext().authentication = authentication;
        chain.doFilter(request, response)

    }

    /**
     * This method reads the JWT from the Authorization header, and then uses  Jwts to validate the token.
     * If everything is in place, we set the user in the SecurityContext and allow the request to move on.
     */
    fun getAuthentication(request: HttpServletRequest?): UsernamePasswordAuthenticationToken? {
        val token = request?.getHeader(securityConfiguration.headerName)
        token?.let {
            val claims = Jwts.parser()
                    .setSigningKey(securityConfiguration.secretByteArray)
                    .parseClaimsJws(token.replace(securityConfiguration.tokenPrefix, ""))
                    .body
            val userEmail = claims.subject
            //get up to date user from db
            val userDetails = userDetailsService.loadUserByUsername(userEmail)
            val jwtHash = claims["jwtHash"]

            if (userDetails.jwtHash != jwtHash) {
                return null
            }

            if (userEmail != null) {
                return UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities)
            }
        }
        return null
    }
}