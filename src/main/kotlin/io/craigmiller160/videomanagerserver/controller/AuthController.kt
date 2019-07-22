package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.service.security.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    // TODO need to ensure that the following are restricted to admin access

    @PostMapping("/user")
    fun createUser(@RequestBody user: AppUser): ResponseEntity<AppUser> {
        TODO("Finish this")
    }

    @PutMapping("/user/{userId}")
    fun updateUser(@PathVariable("userId") userId: Long, @RequestBody user: AppUser): ResponseEntity<AppUser> {
        TODO("Finish this")
    }

    @GetMapping("/user")
    fun getAllUsers(): ResponseEntity<List<AppUser>> {
        TODO("Finish this")
    }

    @GetMapping("/user/{userId}")
    fun getUser(@PathVariable("userId") userId: Long): ResponseEntity<AppUser> {
        TODO("Finish this")
    }

    @GetMapping("/roles")
    fun getRoles(): ResponseEntity<List<Role>> {
        TODO("Finish this")
    }

}