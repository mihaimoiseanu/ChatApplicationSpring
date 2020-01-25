package com.coders.chat.webapp.security.filters

import com.coders.chat.webapp.security.config.SecurityConfiguration
import com.coders.chat.webapp.security.user.CustomUserDetailsService
import io.jsonwebtoken.Jwts
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import javax.servlet.http.HttpServletRequest

class AuthenticationUtils {

    companion object {
        /**
         * This method reads the JWT from the Authorization header, and then uses  Jwts to validate the token.
         * If everything is in place, we set the user in the SecurityContext and allow the request to move on.
         */
        fun getAuthentication(securityConfiguration: SecurityConfiguration,
                              userDetailsService: CustomUserDetailsService,
                              request: HttpServletRequest?): UsernamePasswordAuthenticationToken? {
            val token = request?.getHeader(securityConfiguration.headerName)

            token?.let {
                if (!token.startsWith(securityConfiguration.tokenPrefix)) {
                    return null
                }
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
}