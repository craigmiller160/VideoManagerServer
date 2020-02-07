package io.craigmiller160.videomanagerserver.dto

import io.craigmiller160.videomanagerserver.entity.Role

data class AppUserRequest (
        var userId: Long = 0,
        var userName: String = "",
        var firstName: String = "",
        var lastName: String = "",
        var password: String = "",
        var roles: List<Role> = listOf() // TODO refactor the Role into a DTO
)