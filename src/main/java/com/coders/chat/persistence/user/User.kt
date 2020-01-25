package com.coders.chat.persistence.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.coders.chat.persistence.base.TimestampedEntity
import com.coders.chat.persistence.message.Message
import com.coders.chat.persistence.room.Room
import com.coders.chat.persistence.room.user.RoomUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
class User(
        @Column(unique = true)
        @field:Email
        @field:NotEmpty
        var email: String? = null,

        @field:JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        var pass: String? = null,

        @field:NotEmpty
        var firstName: String? = null,

        @field:NotEmpty
        var lastName: String? = null,

        @ElementCollection(targetClass = Role::class, fetch = FetchType.EAGER)
        @CollectionTable(name = "USER_ROLES", joinColumns = [JoinColumn(name = "USER_ID")])
        @Column(name = "ROLE", nullable = false)
        @Enumerated(EnumType.STRING)
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        var roles: MutableSet<Role>? = null,

        @field:JsonIgnore
        var jwtHash: String? = null,

        @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        @field:JsonIgnore
        var message: Set<Message>? = null,

        @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        @field:JsonIgnore
        var createdRooms:Set<Room>? = null,

        @OneToMany(mappedBy = "user")
        @field:JsonIgnore
        var roomUser:Set<RoomUser>?=null

) : TimestampedEntity(), UserDetails {

    @JsonIgnore
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()
        roles?.mapTo(authorities) { SimpleGrantedAuthority(it.name) }
        return authorities
    }

    @JsonIgnore
    override fun isEnabled(): Boolean = true

    @JsonIgnore
    override fun getUsername(): String = this.email!!

    @JsonIgnore
    override fun getPassword(): String = this.pass!!

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean = true

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean = true

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean = true

    @PrePersist
    override fun prePersist() {
        super.prePersist()
        if (jwtHash == null) {
            jwtHash = UUID.randomUUID().toString()
        }
    }
}