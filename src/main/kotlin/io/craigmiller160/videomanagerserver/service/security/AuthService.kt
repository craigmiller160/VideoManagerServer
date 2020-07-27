package io.craigmiller160.videomanagerserver.service.security

import io.craigmiller160.modelmapper.EnhancedModelMapper
import io.craigmiller160.videomanagerserver.dto.AppUserRequest
import io.craigmiller160.videomanagerserver.dto.AppUserResponse
import io.craigmiller160.videomanagerserver.dto.LoginRequest
import io.craigmiller160.videomanagerserver.dto.RolePayload
import io.craigmiller160.videomanagerserver.dto.VideoTokenResponse
import io.craigmiller160.videomanagerserver.entity.AppUser
import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import io.craigmiller160.videomanagerserver.exception.NoUserException
import io.craigmiller160.videomanagerserver.repository.AppUserRepository
import io.craigmiller160.videomanagerserver.repository.RoleRepository
import io.craigmiller160.videomanagerserver.security.ROLE_ADMIN
import io.craigmiller160.videomanagerserver.security.tokenprovider.JwtTokenProvider
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenValidationStatus
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthService (
        private val appUserRepository: AppUserRepository,
        private val roleRepository: RoleRepository,
        private val passwordEncoder: BCryptPasswordEncoder,
        private val jwtTokenProvider: JwtTokenProvider,
        private val videoTokenProvider: VideoTokenProvider,
        private val securityContextService: SecurityContextService,
        private val modelMapper: EnhancedModelMapper
) {

    fun checkAuth(): AppUserResponse {
        val userName = securityContextService.getUserName()
        val user = appUserRepository.findByUserName(userName) ?: throw ApiUnauthorizedException("Invalid user name")
        return modelMapper.map(user, AppUserResponse::class.java)
    }

    fun login(request: LoginRequest): String {
        val user = appUserRepository.findByUserName(request.userName) ?: throw ApiUnauthorizedException("Invalid login")
        if (passwordEncoder.matches(request.password, user.password)) {
            user.lastAuthenticated = LocalDateTime.now()
            appUserRepository.save(user)
            return jwtTokenProvider.createToken(user)
        }
        throw ApiUnauthorizedException("Invalid login")
    }

    fun getRoles(): List<RolePayload> {
        return roleRepository.findAll()
                .map { role -> modelMapper.map(role, RolePayload::class.java) }
    }

    fun createUser(userRequest: AppUserRequest): AppUserResponse {
        require(!(userRequest.userName.isEmpty() || userRequest.password.isEmpty())) {
            "User is missing required fields"
        }

        require(!(userRequest.roles.isNotEmpty() && !rolesHaveIds(userRequest.roles))) {
            "User roles are not configured properly"
        }

        val user = modelMapper.map(userRequest, AppUser::class.java)
        user.password = passwordEncoder.encode(userRequest.password)
        val savedUser = appUserRepository.save(user)
        return modelMapper.map(savedUser, AppUserResponse::class.java)
    }

    fun updateUserAdmin(userId: Long, userRequest: AppUserRequest): AppUserResponse? {
        require(!(userRequest.roles.isNotEmpty() && !rolesHaveIds(userRequest.roles))) {
            "User roles are not configured properly"
        }

        val existing = appUserRepository.findById(userId).orElse(null)
        return existing?.let {
            val user =  modelMapper.map(userRequest, AppUser::class.java)
            user.userId = userId
            user.userName = existing.userName
            user.lastAuthenticated = existing.lastAuthenticated
            user.password =
                    if (userRequest.password.isEmpty()) {
                        existing.password
                    } else {
                        passwordEncoder.encode(userRequest.password)
                    }
            val updatedUser = appUserRepository.save(user)
            modelMapper.map(updatedUser, AppUserResponse::class.java)
        }
    }

    fun updateUserSelf(userRequest: AppUserRequest): AppUserResponse? {
        require(!(userRequest.roles.isNotEmpty() && !rolesHaveIds(userRequest.roles))) {
            "User roles are not configured properly"
        }

        val userName = securityContextService.getUserName()
        val existing = appUserRepository.findByUserName(userName)

        return existing?.let {
            val user = modelMapper.map(userRequest, AppUser::class.java)
            user.userId = existing.userId
            user.userName = userName
            user.lastAuthenticated = existing.lastAuthenticated
            if (existing.roles.find { role -> role.name == ROLE_ADMIN } == null) {
                user.roles = existing.roles
            }
            user.password =
                    if (userRequest.password.isEmpty()) {
                        existing.password
                    } else {
                        passwordEncoder.encode(userRequest.password)
                    }
            val updatedUser = appUserRepository.save(user)
            modelMapper.map(updatedUser, AppUserResponse::class.java)
        }
    }

    fun getAllUsers(): List<AppUserResponse> {
        return appUserRepository.findAll()
                .map { user -> modelMapper.map(user, AppUserResponse::class.java) }
                .toList()
    }

    fun getUser(userId: Long): AppUserResponse? {
        val user = appUserRepository.findById(userId).orElse(null)
        return user?.let {
            modelMapper.map(user, AppUserResponse::class.java)
        }
    }

    fun deleteUser(userId: Long): AppUserResponse? {
        val user = appUserRepository.findById(userId).orElse(null)
        appUserRepository.deleteById(userId)
        return user?.let {
            modelMapper.map(user, AppUserResponse::class.java)
        }
    }

    fun refreshToken(token: String): String {
        val validStatus = jwtTokenProvider.validateToken(token)
        if (TokenValidationStatus.BAD_SIGNATURE == validStatus) {
            throw ApiUnauthorizedException("Invalid token")
        }

        val claims = jwtTokenProvider.getClaims(token)
        val user = appUserRepository.findByUserName(claims[TokenConstants.CLAIM_SUBJECT] as String)
                ?: throw ApiUnauthorizedException("No user exists for token")
        if (!jwtTokenProvider.isRefreshAllowed(user)) {
            throw ApiUnauthorizedException("Token refresh not allowed")
        }

        val newToken = jwtTokenProvider.createToken(user)
        user.lastAuthenticated = LocalDateTime.now()
        appUserRepository.save(user)
        return newToken
    }

    fun revokeAccess(userId: Long): AppUserResponse {
        val existingUser = appUserRepository.findById(userId)
                .orElseThrow { NoUserException("Cannot find user") }
        existingUser.lastAuthenticated = null
        val updatedUser = appUserRepository.save(existingUser)
        return modelMapper.map(updatedUser, AppUserResponse::class.java)
    }

    fun getVideoToken(videoId: Long): VideoTokenResponse {
        val userName = securityContextService.getUserName()
        val user = AppUser(userName = userName)
        val params = mapOf(TokenConstants.PARAM_VIDEO_ID to videoId)
        val token = videoTokenProvider.createToken(user, params)
        return VideoTokenResponse(token)
    }

    fun rolesHaveIds(roles: List<RolePayload>) =
            roles.none { role -> role.roleId == 0L }

}
