package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.dto.User
import io.craigmiller160.videomanagerserver.service.security.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// TODO needs tests

@RestController
@RequestMapping("/auth")
class AuthController (
        private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(request: User): ResponseEntity<Token> {
        val token = authService.login(request)
        return ResponseEntity.ok(token)
    }

}