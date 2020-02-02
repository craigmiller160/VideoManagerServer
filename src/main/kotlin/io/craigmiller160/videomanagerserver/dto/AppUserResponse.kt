package io.craigmiller160.videomanagerserver.dto

import java.time.LocalDateTime

data class AppUserResponse (
        var userId: Long = 0,
        var userName: String = "",
        var firstName: String = "",
        var lastName: String = "",
        var lastAuthenticated: LocalDateTime? = null,
        var roles: List<Role> = listOf() // TODO need to refactor the Role to a DTO here
)
