package io.craigmiller160.videomanagerserver.service.security

import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import io.craigmiller160.videomanagerserver.exception.NoUserException
import io.craigmiller160.videomanagerserver.repository.AppUserRepository
import io.craigmiller160.videomanagerserver.repository.RoleRepository
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenClaims
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenProvider
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenValidationStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthService (
        private val appUserRepository: AppUserRepository,
        private val roleRepository: RoleRepository,
        private val passwordEncoder: BCryptPasswordEncoder,
        private val jwtTokenProvider: TokenProvider
) {

    fun login(request: AppUser): String {
        val user = appUserRepository.findByUserName(request.userName)
        if (user != null && passwordEncoder.matches(request.password, user.password)) {
            user.lastAuthenticated = LocalDateTime.now()
            appUserRepository.save(user)
            return jwtTokenProvider.createToken(user)
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

    fun refreshToken(token: String): String {
        val validStatus = jwtTokenProvider.validateToken(token)
        if (TokenValidationStatus.BAD_SIGNATURE == validStatus) {
            throw ApiUnauthorizedException("Invalid token")
        }

        val claims = jwtTokenProvider.getClaims(token)
        val user = appUserRepository.findByUserName(claims[TokenClaims.CLAIM_SUBJECT] as String)
                ?: throw ApiUnauthorizedException("No user exists for token")
        if (!jwtTokenProvider.isRefreshAllowed(user)) {
            throw ApiUnauthorizedException("Token refresh not allowed")
        }

        val newToken = jwtTokenProvider.createToken(user)
        user.lastAuthenticated = LocalDateTime.now()
        appUserRepository.save(user)
        return newToken
    }

    fun revokeAccess(user: AppUser): AppUser {
        val existingUser = appUserRepository.findById(user.userId)
                .orElseThrow { NoUserException("Cannot find user") }
        existingUser.lastAuthenticated = null
        return removePassword(appUserRepository.save(existingUser))
    }

    fun rolesHaveIds(roles: List<Role>) =
            roles.none { role -> role.roleId == 0L }

    private fun removePassword(user: AppUser): AppUser {
        user.password = ""
        return user
    }

}