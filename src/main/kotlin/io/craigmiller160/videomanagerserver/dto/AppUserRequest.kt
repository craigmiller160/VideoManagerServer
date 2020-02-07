package io.craigmiller160.videomanagerserver.dto

data class AppUserRequest (
        var userId: Long = 0,
        var userName: String = "",
        var firstName: String = "",
        var lastName: String = "",
        var password: String = "",
        var roles: List<RolePayload> = listOf()
)