package com.coders.chat.webapp.security.config

import com.coders.chat.webapp.security.filters.JWTAuthenticationFilter
import com.coders.chat.webapp.security.filters.JWTAuthorizationFilter
import com.coders.chat.webapp.security.user.CustomUserDetailsService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension

@Configuration
@EnableWebSecurity
open class WebSecurityConfig(private val userDetailsService: CustomUserDetailsService,
                             private val securityConfiguration: SecurityConfiguration,
                             @Qualifier("roleHierarchy") private val roleHierarchy: RoleHierarchy,
                             private val appContext: ApplicationContext
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, securityConfiguration.signupURL).permitAll()
                .antMatchers(HttpMethod.GET, "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/auth", "/swagger-ui.html", "/webjars/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(JWTAuthenticationFilter(authenticationManager(), securityConfiguration))
                .addFilter(JWTAuthorizationFilter(authenticationManager(), securityConfiguration, userDetailsService))

        http.cors().and().csrf().disable()

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
    }

    @Bean
    open fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    open fun securityEvaluationContextExtension(): SecurityEvaluationContextExtension = SecurityEvaluationContextExtension()
}