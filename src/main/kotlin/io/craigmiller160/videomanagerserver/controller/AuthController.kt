package io.craigmiller160.videomanagerserver.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController {

    @PostMapping("/login")
    fun login() {
        TODO("Finish this")
    }

    @GetMapping("/logout")
    fun logout() {
        TODO("Finish this")
    }

}