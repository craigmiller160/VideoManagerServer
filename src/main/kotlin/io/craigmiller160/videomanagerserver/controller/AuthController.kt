package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.service.security.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController (
        private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: AppUser): ResponseEntity<Token> {
        val token = authService.login(request)
        return ResponseEntity.ok(token)
    }

}