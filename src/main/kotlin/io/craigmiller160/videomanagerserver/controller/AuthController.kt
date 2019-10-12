package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.dto.VideoToken
import io.craigmiller160.videomanagerserver.security.COOKIE_NAME
import io.craigmiller160.videomanagerserver.security.ROLE_ADMIN
import io.craigmiller160.videomanagerserver.service.security.AuthService
import org.springframework.http.ResponseCookie
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
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/auth")
class AuthController (
        private val authService: AuthService
) {

    companion object {
        const val DEFAULT_MAX_AGE = 1_000_000L
    }

    fun createCookie(token: String, maxAge: Long) = ResponseCookie
            .from(COOKIE_NAME, token)
            .path("/")
            .secure(true)
            .httpOnly(true)
            .maxAge(maxAge)
            .sameSite("strict")
            .build()

    @GetMapping("/check")
    fun checkAuth(): ResponseEntity<AppUser> {
        val user = authService.checkAuth()
        return ResponseEntity.ok(user)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: AppUser, response: HttpServletResponse): ResponseEntity<Void> {
        val token = authService.login(request)
        val cookie = createCookie(token, DEFAULT_MAX_AGE)
        response.addHeader("Set-Cookie", cookie.toString())
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/refresh")
    fun refreshToken(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Void> {
        val token = request.cookies?.find { cookie -> cookie.name == COOKIE_NAME }?.value ?: return ResponseEntity.status(401).build()
        val newToken = authService.refreshToken(token)
        val cookie = createCookie(newToken, DEFAULT_MAX_AGE)
        response.addHeader("Set-Cookie", cookie.toString())
        return ResponseEntity.noContent().build()
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
    @PutMapping("/users/admin/{userId}")
    fun updateUserAdmin(@PathVariable("userId") userId: Long, @RequestBody user: AppUser): ResponseEntity<AppUser> {
        return okOrNoContent(authService.updateUserAdmin(userId, user))
    }

    @PutMapping("/users/self/{userId}")
    fun updateUserSelf(@PathVariable("userId") userId: Long, @RequestBody user: AppUser): ResponseEntity<AppUser> {
        TODO("Finish this")
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

    @GetMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Void> {
        val cookie = createCookie("", 0)
        response.addHeader("Set-Cookie", cookie.toString())
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/videotoken/{fileId}")
    fun getVideoToken(@PathVariable fileId: Long): ResponseEntity<VideoToken> {
        val token = authService.getVideoToken(fileId)
        return ResponseEntity.ok(token)
    }

}