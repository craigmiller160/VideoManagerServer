package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.security.COOKIE_NAME
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
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import kotlin.math.exp

@RestController
@RequestMapping("/auth")
class AuthController (
        private val authService: AuthService
) {

    fun createCookie(token: String): Cookie {
        return Cookie(COOKIE_NAME, token).apply {
            path = "/"
            secure = true
            isHttpOnly = true
            maxAge = 1_000_000
            domain = "https://localhost:8443" // TODO probably need to change that for prod
            // TODO test out same-site, see if it works
            // Todo test out domain, see if it works
        }
    }

    @GetMapping("/check")
    fun checkAuth(): ResponseEntity<Void> {
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/login")
    fun login(@RequestBody request: AppUser, response: HttpServletResponse): ResponseEntity<Void> {
        val token = authService.login(request)
        val cookie = createCookie(token)
        response.addCookie(cookie)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody token: Token): ResponseEntity<Token> {
        val token = authService.refreshToken(token)
        return ResponseEntity.ok(token)
    }

    @Secured(ROLE_ADMIN)
    @PostMapping("/users/revoke")
    fun revokeAccess(@RequestBody user: AppUser): ResponseEntity<AppUser> {
        val result = authService.revokeAccess(user)
        return ResponseEntity.ok(result)
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