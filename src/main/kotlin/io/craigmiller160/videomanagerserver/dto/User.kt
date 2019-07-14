package io.craigmiller160.videomanagerserver.dto

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users")
data class User (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var userId: Long = 0,
        var userName: String = "",
        var password: String = ""
)