package com.coders.chat.service

import com.coders.chat.persistence.PersistenceModule
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan
@Import(PersistenceModule::class)
open class ServiceModule