package io.craigmiller160.videomanagerserver.dto

import io.craigmiller160.videomanagerserver.util.DEFAULT_TIMESTAMP
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "users")
data class AppUser (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var userId: Long = 0,
        @Column(unique = true)
        var userName: String = "",
        var password: String = "",
        var lastAuthenticated: LocalDateTime = DEFAULT_TIMESTAMP,

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "user_roles",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "role_id")])
        var roles: List<Role> = listOf()
)