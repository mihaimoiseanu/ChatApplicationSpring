package com.coders.chat.webapp.security.jwt

import com.coders.chat.persistence.user.User
import com.coders.chat.webapp.security.config.SecurityConfiguration
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*

object JwtUtils {

    fun getClaims(jwtToken: String, secret: String): Claims {
        return Jwts.parser()
                .setSigningKey(secret.toByteArray())
                .parseClaimsJws(jwtToken)
                .body
    }

    fun createClaims(securityConfiguration: SecurityConfiguration, user: User): Claims {
        val claims = Jwts.claims().setSubject(user.username)
        claims.expiration = Date(System.currentTimeMillis() + securityConfiguration.expirationTime);
        claims[securityConfiguration.roles] = user.authorities.joinToString { it.authority }
        claims["jwtHash"] = user.jwtHash
        return claims
    }

    fun createToken(claims: Claims, securityConfiguration: SecurityConfiguration): String =
            Jwts.builder()
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS512, securityConfiguration.secretByteArray)
                    .compact()

}

