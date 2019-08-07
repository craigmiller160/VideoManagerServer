package io.craigmiller160.videomanagerserver.service.security

import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import io.craigmiller160.videomanagerserver.repository.AppUserRepository
import io.craigmiller160.videomanagerserver.repository.RoleRepository
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenProvider
import io.craigmiller160.videomanagerserver.security.jwt.JwtValidationStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthService (
        private val appUserRepository: AppUserRepository,
        private val roleRepository: RoleRepository,
        private val passwordEncoder: BCryptPasswordEncoder,
        private val jwtTokenProvider: JwtTokenProvider
) {

    fun login(request: AppUser): Token {
        val user = appUserRepository.findByUserName(request.userName)
        if (user != null && passwordEncoder.matches(request.password, user.password)) {
            val token = jwtTokenProvider.createToken(user)
            return Token(token)
        }
        throw ApiUnauthorizedException("Invalid login")
    }

    fun getRoles(): List<Role> {
        return roleRepository.findAll().toList()
    }

    fun createUser(user: AppUser): AppUser {
        if (user.userName.isEmpty() || user.password.isEmpty() || user.roles.isEmpty() || !rolesHaveIds(user.roles)) {
            throw IllegalArgumentException("User is missing required fields")
        }
        user.password = passwordEncoder.encode(user.password)
        val savedUser = appUserRepository.save(user)
        return removePassword(savedUser)
    }

    fun updateUser(userId: Long, user: AppUser): AppUser? {
        val existing = appUserRepository.findById(userId).orElse(null)
        return existing?.let {
            user.userId = userId
            if (user.password.isEmpty()) {
                user.password = existing.password
            } else {
                user.password = passwordEncoder.encode(user.password)
            }
            val savedUser = appUserRepository.save(user)
            removePassword(savedUser)
        }
    }

    fun getAllUsers(): List<AppUser> {
        return appUserRepository.findAll()
                .map { user -> removePassword(user) }
                .toList()
    }

    fun getUser(userId: Long): AppUser? {
        val user = appUserRepository.findById(userId).orElse(null)
        return user?.let {
            removePassword(user)
            user
        }
    }

    fun deleteUser(userId: Long): AppUser? {
        val user = appUserRepository.findById(userId).orElse(null)
        appUserRepository.deleteById(userId)
        return user
    }

    fun refreshToken(token: Token): Token {
        val validStatus = jwtTokenProvider.validateToken(token.token)
        if (JwtValidationStatus.BAD_SIGNATURE == validStatus) {
            throw ApiUnauthorizedException("Invalid token")
        }

        val claims = jwtTokenProvider.getClaims(token.token)
        val user = appUserRepository.findByUserName(claims.subject) ?: throw ApiUnauthorizedException("No user exists for token")
        if (!jwtTokenProvider.isRefreshAllowed(user)) {
            throw ApiUnauthorizedException("Token not allowed")
        }

        val newToken = jwtTokenProvider.createToken(user)
        return Token(newToken)
    }

    fun rolesHaveIds(roles: List<Role>) =
            roles.none { role -> role.roleId == 0L }

    private fun removePassword(user: AppUser): AppUser {
        user.password = ""
        return user
    }

}