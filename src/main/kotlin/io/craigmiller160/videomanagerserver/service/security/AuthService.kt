package io.craigmiller160.videomanagerserver.service.security

import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenProvider
import io.craigmiller160.videomanagerserver.repository.AppUserRepository
import io.craigmiller160.videomanagerserver.repository.RoleRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

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
        if (user.userName == "" || user.password == "" || user.roles.isEmpty() || !rolesHaveIds(user.roles)) {
            throw IllegalArgumentException("User is missing required fields")
        }
        user.password = passwordEncoder.encode(user.password)
        val savedUser = appUserRepository.save(user)
        removePassword(savedUser)
        return savedUser
    }

    fun updateUser(userId: Long, user: AppUser): AppUser {
        TODO("Finish this")
    }

    fun getAllUsers(): List<AppUser> {
        TODO("Finish this")
    }

    fun getUser(userId: Long): AppUser {
        TODO("Finish this")
    }

    fun rolesHaveIds(roles: List<Role>) =
            roles.none { role -> role.roleId == 0L }

    private fun removePassword(user: AppUser) {
        user.password = ""
    }

}