package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.security.ROLE_ADMIN
import io.craigmiller160.videomanagerserver.service.security.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
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

    @Secured(ROLE_ADMIN)
    @PostMapping("/users")
    fun createUser(@RequestBody user: AppUser): ResponseEntity<AppUser> {
        return ResponseEntity.ok(authService.createUser(user))
    }

    @Secured(ROLE_ADMIN)
    @PutMapping("/users/{userId}")
    fun updateUser(@PathVariable("userId") userId: Long, @RequestBody user: AppUser): ResponseEntity<AppUser> {
        return okOrNoContent(authService.updateUser(userId, user))
    }

    @Secured(ROLE_ADMIN)
    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<List<AppUser>> {
        val users = authService.getAllUsers()
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.ok(users)
    }

    @Secured(ROLE_ADMIN)
    @GetMapping("/users/{userId}")
    fun getUser(@PathVariable("userId") userId: Long): ResponseEntity<AppUser> {
        return okOrNoContent(authService.getUser(userId))
    }

    @Secured(ROLE_ADMIN)
    @GetMapping("/roles")
    fun getRoles(): ResponseEntity<List<Role>> {
        val roles = authService.getRoles()
        if (roles.isEmpty()) {
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.ok(roles)
    }

    @Secured(ROLE_ADMIN)
    @DeleteMapping("/users/{userId}")
    fun deleteUser(@PathVariable("userId") userId: Long): ResponseEntity<AppUser> {
        return okOrNoContent(authService.deleteUser(userId))
    }

}