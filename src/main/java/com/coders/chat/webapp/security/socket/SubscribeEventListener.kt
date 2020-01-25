package com.coders.chat.webapp.security.socket

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent

@Component
class EventInterceptor : ApplicationListener<SessionConnectedEvent> {

    private val logger = LoggerFactory.getLogger(EventInterceptor::class.java)

    override fun onApplicationEvent(p0: SessionConnectedEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(p0.message)
        println("${headerAccessor.sessionId?.toString()} connected")
    }
}

@Component
class DisconnectEventInterceptor : ApplicationListener<SessionDisconnectEvent> {

    private val logger = LoggerFactory.getLogger(EventInterceptor::class.java)

    override fun onApplicationEvent(p0: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(p0.message)
        println("${headerAccessor.sessionId?.toString()} disconnected")
    }
}

@Component
class UnsubscribeEvent : ApplicationListener<SessionUnsubscribeEvent> {

    private val logger = LoggerFactory.getLogger(UnsubscribeEvent::class.java)
    override fun onApplicationEvent(p0: SessionUnsubscribeEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(p0.message)
        println("${headerAccessor.sessionId?.toString()} unsubscribed from ${headerAccessor.subscriptionId}")
    }
}

@Component
class SubscribeEvent : ApplicationListener<SessionSubscribeEvent> {

    private val logger = LoggerFactory.getLogger(SubscribeEvent::class.java)
    override fun onApplicationEvent(p0: SessionSubscribeEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(p0.message)
        println("${headerAccessor.sessionId?.toString()} subscribed to ${headerAccessor.destination}")
    }
}
