package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.User
import io.craigmiller160.videomanagerserver.service.security.AuthService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

//@RestController
//@RequestMapping("/auth")
class AuthController (
        private val authService: AuthService
) {

//    @PostMapping("/login")
    fun login(request: User) {
        authService.login(request)
    }

//    @GetMapping("/logout")
    fun logout() {
        TODO("Finish this")
    }

}