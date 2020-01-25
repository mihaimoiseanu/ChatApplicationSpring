package com.coders.chat.webapp.security.config

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
open class GlobalMethodSecurityConfig(
        private val appContext: ApplicationContext
) : GlobalMethodSecurityConfiguration() {

    @Bean("roleHierarchy")
    open fun roleHierarchy(): RoleHierarchy {
        val roleHierarchy = RoleHierarchyImpl()
        roleHierarchy.setHierarchy(
                "ROLE_ADMIN > ROLE_PUBLISHER " +
                    "ROLE_PUBLISHER > ROLE_USER " +
                        "ROLE_USER > ROLE_ANONYMOUS"

        )
        return roleHierarchy
    }


    override fun createExpressionHandler(): MethodSecurityExpressionHandler {
        val dmseh = DefaultMethodSecurityExpressionHandler()
        dmseh.setRoleHierarchy(roleHierarchy())
        dmseh.setApplicationContext(appContext)
        return dmseh
    }
}