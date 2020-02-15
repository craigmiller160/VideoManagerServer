package io.craigmiller160.videomanagerserver.service.security

import com.nhaarman.mockito_kotlin.times
import io.craigmiller160.videomanagerserver.config.MapperConfig
import io.craigmiller160.videomanagerserver.dto.AppUserRequest
import io.craigmiller160.videomanagerserver.dto.AppUserResponse
import io.craigmiller160.videomanagerserver.dto.LoginRequest
import io.craigmiller160.videomanagerserver.dto.RolePayload
import io.craigmiller160.videomanagerserver.entity.AppUser
import io.craigmiller160.videomanagerserver.entity.Role
import io.craigmiller160.videomanagerserver.dto.VideoTokenResponse
import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import io.craigmiller160.videomanagerserver.exception.NoUserException
import io.craigmiller160.videomanagerserver.repository.AppUserRepository
import io.craigmiller160.videomanagerserver.repository.RoleRepository
import io.craigmiller160.videomanagerserver.security.ROLE_ADMIN
import io.craigmiller160.videomanagerserver.security.ROLE_EDIT
import io.craigmiller160.videomanagerserver.security.tokenprovider.JwtTokenProvider
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenValidationStatus
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertFailsWith


@RunWith(MockitoJUnitRunner.Silent::class)
class AuthServiceTest {

    companion object {
        private const val USER_NAME = "craig"
        private const val PASSWORD = "password"
        private const val ENCODED_PASSWORD = "encoded_password"
        private const val TOKEN = "token"
        private const val ROLE = "role"
    }

    @Mock
    private lateinit var appUserRepository: AppUserRepository

    @Mock
    private lateinit var roleRepository: RoleRepository

    @Mock
    private lateinit var passwordEncoder: BCryptPasswordEncoder

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Spy
    private var modelMapper = MapperConfig().modelMapper()

    @Mock
    private lateinit var videoTokenProvider: VideoTokenProvider

    @Mock
    private lateinit var securityContextService: SecurityContextService

    @InjectMocks
    private lateinit var authService: AuthService

    private fun mockLogin() {
        val user = AppUser().apply {
            userName = USER_NAME
            password = ENCODED_PASSWORD
        }
        `when`(appUserRepository.findByUserName(USER_NAME))
                .thenReturn(user)
        `when`(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD))
                .thenReturn(true)
        `when`(jwtTokenProvider.createToken(user))
                .thenReturn(TOKEN)
    }

    @Test
    fun test_login() {
        mockLogin()
        val request = LoginRequest(
                userName = USER_NAME,
                password = PASSWORD
        )

        val result = authService.login(request)
        assertEquals(TOKEN, result)

        val captor = ArgumentCaptor.forClass(AppUser::class.java)

        verify(appUserRepository, times(1))
                .save(captor.capture())

        assertThat(captor.value, hasProperty("lastAuthenticated", notNullValue()))
    }

    @Test(expected = ApiUnauthorizedException::class)
    fun test_login_cantFindUser() {
        mockLogin()
        val request = LoginRequest(
                userName = "Bob",
                password = PASSWORD
        )
        authService.login(request)
    }

    @Test(expected = ApiUnauthorizedException::class)
    fun test_login_wrongPassword() {
        mockLogin()
        val request = LoginRequest(
                userName = USER_NAME,
                password = "FooBar"
        )
        authService.login(request)
    }

    @Test
    fun test_getRoles() {
        val roles = listOf(Role(1, "Role1"), Role(2, "Role2"))
        val rolePayloads = listOf(RolePayload(1, "Role1"), RolePayload(2, "Role2"))
        `when`(roleRepository.findAll())
                .thenReturn(roles)

        val result = authService.getRoles()
        assertEquals(rolePayloads, result)
    }

    @Test
    fun test_createUser() {
        val roles = listOf(Role(1, ROLE))
        val rolePayloads = listOf(RolePayload(1, ROLE))
        val userRequest = AppUserRequest(
            userName = USER_NAME,
            password = PASSWORD,
            roles = rolePayloads
        )
        val user = AppUser(
                userName = USER_NAME,
                password = ENCODED_PASSWORD,
                roles = roles
        )
        val userWithId = user.copy(userId = 1L)
        val userResponse = AppUserResponse(
                userName = USER_NAME,
                roles = rolePayloads,
                userId = 1L
        )

        `when`(appUserRepository.save(user))
                .thenReturn(userWithId)
        `when`(passwordEncoder.encode(PASSWORD))
                .thenReturn(ENCODED_PASSWORD)

        val userCaptor = ArgumentCaptor.forClass(AppUser::class.java)

        val result = authService.createUser(userRequest)

        verify(appUserRepository, times(1))
                .save(userCaptor.capture())

        assertThat(userCaptor.value, hasProperty("password", equalTo(ENCODED_PASSWORD)))
        assertEquals(userResponse, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_createUser_missingPass() {
        val request = AppUserRequest(
                userName = "Bob"
        )
        authService.createUser(request)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_createUser_badRoles() {
        val request = AppUserRequest(
                userName = "Bob",
                password = "pass",
                roles = listOf(RolePayload(name = "Foo"))
        )
        authService.createUser(request)
    }

    @Test
    fun test_rolesHaveIds() {
        val roles = listOf(RolePayload(1, ROLE))
        val result = authService.rolesHaveIds(roles)
        assertTrue(result)
    }

    @Test
    fun test_rolesHaveIds_noIds() {
        val roles = listOf(RolePayload(name = ROLE))
        val result = authService.rolesHaveIds(roles)
        assertFalse(result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_updateUserAdmin_badRoles() {
        val userId = 1L
        val request = AppUserRequest(
                userName = USER_NAME,
                roles = listOf(RolePayload(name = ROLE))
        )
        authService.updateUserAdmin(userId, request)
    }

    @Test
    fun test_updateUserAdmin() {
        val lastAuth = LocalDateTime.now()
        val userId = 1L
        val roles = listOf(Role(roleId = 1, name = ROLE))
        val rolePayloads = listOf(RolePayload(roleId = 1, name = ROLE))
        val request = AppUserRequest(
            userName = USER_NAME,
            roles = rolePayloads
        )
        val existing = AppUser(
                userId = userId,
                userName = USER_NAME,
                roles = listOf(),
                password = PASSWORD,
                lastAuthenticated = lastAuth
        )
        val updatedUser = existing.copy(roles = roles)
        val response = AppUserResponse(
                userId = userId,
                userName = USER_NAME,
                roles = rolePayloads,
                lastAuthenticated = lastAuth
        )

        `when`(appUserRepository.save(updatedUser))
                .thenReturn(updatedUser)
        `when`(appUserRepository.findById(userId))
                .thenReturn(Optional.of(existing))

        val result = authService.updateUserAdmin(userId, request)
        assertEquals(response, result)
    }

    @Test
    fun test_updateUserAdmin_noUpdateUsername() {
        val userId = 1L
        val roles = listOf(Role(roleId = 1, name = ROLE))
        val rolePayloads = listOf(RolePayload(roleId = 1, name = ROLE))
        val lastAuthenticated = LocalDateTime.now()
        val request = AppUserRequest(
            userName = "userName2",
            roles = rolePayloads
        )
        val existing = AppUser(
                userId = userId,
                userName = USER_NAME,
                roles = listOf(),
                password = PASSWORD,
                lastAuthenticated = lastAuthenticated
        )
        val response = existing.copy(
                roles = roles
        )
        val expected = AppUserResponse(
                userId = userId,
                userName = USER_NAME,
                roles = rolePayloads,
                lastAuthenticated = lastAuthenticated
        )

        `when`(appUserRepository.save(response))
                .thenReturn(response)
        `when`(appUserRepository.findById(userId))
                .thenReturn(Optional.of(existing))

        val result = authService.updateUserAdmin(userId, request)
        assertEquals(expected, result)
    }

    @Test
    fun test_updateUserAdmin_withPassword() {
        val userId = 1L
        val roles = listOf(Role(roleId = 1, name = ROLE))
        val rolePayloads = listOf(RolePayload(roleId = 1, name = ROLE))
        val lastAuthenticated = LocalDateTime.now()
        val request = AppUserRequest(
            userName = USER_NAME,
            roles = rolePayloads,
            password = PASSWORD
        )
        val existing = AppUser(
                userId = userId,
                userName = USER_NAME,
                roles = listOf(),
                password = "${PASSWORD}2"
        )
        val toSave = existing.copy(
                password = ENCODED_PASSWORD,
                roles = roles
        )
        val expected = AppUserResponse(
                userId = userId,
                userName = USER_NAME,
                roles = rolePayloads
        )

        `when`(appUserRepository.save(toSave))
                .thenReturn(toSave)
        `when`(appUserRepository.findById(userId))
                .thenReturn(Optional.of(existing))
        `when`(passwordEncoder.encode(PASSWORD))
                .thenReturn(ENCODED_PASSWORD)

        val result = authService.updateUserAdmin(userId, request)
        assertEquals(expected, result)
    }

    @Test
    fun test_updateUserAdmin_notFound() {
        val userId = 1L
        val user = AppUserRequest(
            userName = USER_NAME,
            roles = listOf(RolePayload(roleId = 1, name = ROLE))
        )

        val result = authService.updateUserAdmin(userId, user)
        assertNull(result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_updateUserSelf_badRoles() {
        val request = AppUserRequest(
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller",
                roles = listOf(RolePayload(name = "role"))
        )
        authService.updateUserSelf(request)
    }

    @Test
    fun test_updateUserSelf() {
        val request = AppUserRequest(
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller"
        )

        val existing = AppUser(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Bob",
                lastName = "Saget",
                password = PASSWORD
        )

        val toSave = existing.copy(
                firstName = "Craig",
                lastName = "Miller"
        )

        val expected = AppUserResponse(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller"
        )

        `when`(securityContextService.getUserName())
                .thenReturn(USER_NAME)
        `when`(appUserRepository.findByUserName(USER_NAME))
                .thenReturn(existing)
        `when`(appUserRepository.save(toSave))
                .thenReturn(toSave)

        val result = authService.updateUserSelf(request)
        assertEquals(expected, result)
    }

    @Test
    fun test_updateUserSelf_skipsRoles() {
        val request = AppUserRequest(
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller",
                roles = listOf(RolePayload(1, ROLE_ADMIN))
        )

        val existing = AppUser(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Bob",
                lastName = "Saget",
                password = PASSWORD,
                roles = listOf()
        )

        val toSave = AppUser(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller",
                password = PASSWORD,
                roles = listOf()
        )

        val expected = AppUserResponse(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller",
                roles = listOf()
        )

        `when`(securityContextService.getUserName())
                .thenReturn(USER_NAME)
        `when`(appUserRepository.findByUserName(USER_NAME))
                .thenReturn(existing)
        `when`(appUserRepository.save(toSave))
                .thenReturn(toSave)

        val result = authService.updateUserSelf(request)
        assertEquals(expected, result)
    }

    @Test
    fun test_updateUserSelf_adminCanUpdateRoles() {
        val rolePayloads = listOf(RolePayload(1, ROLE_ADMIN), RolePayload(2, ROLE_EDIT))
        val roles = listOf(Role(1, ROLE_ADMIN), Role(2, ROLE_EDIT))
        val request = AppUserRequest(
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller",
                roles = rolePayloads
        )

        val existing = AppUser(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Bob",
                lastName = "Saget",
                password = PASSWORD,
                roles = listOf(Role(1, ROLE_ADMIN))
        )

        val toSave = AppUser(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller",
                password = PASSWORD,
                roles = roles
        )

        val expected = AppUserResponse(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller",
                roles = rolePayloads
        )

        `when`(securityContextService.getUserName())
                .thenReturn(USER_NAME)
        `when`(appUserRepository.findByUserName(USER_NAME))
                .thenReturn(existing)
        `when`(appUserRepository.save(toSave))
                .thenReturn(toSave)

        val result = authService.updateUserSelf(request)
        assertEquals(expected, result)
    }

    @Test
    fun test_updateUserSelf_notFound() {
        val request = AppUserRequest(
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller"
        )

        `when`(securityContextService.getUserName())
                .thenReturn(USER_NAME)

        val result = authService.updateUserSelf(request)
        assertNull(result)
    }

    @Test
    fun test_updateUserSelf_withPassword() {
        val newPassword = "newPassword"
        val request = AppUserRequest(
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller",
                password = newPassword
        )

        val existing = AppUser(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Bob",
                lastName = "Saget",
                password = PASSWORD
        )

        val toSave = AppUser(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller",
                password = newPassword
        )

        val expected = AppUserResponse(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller"
        )

        `when`(securityContextService.getUserName())
                .thenReturn(USER_NAME)
        `when`(appUserRepository.findByUserName(USER_NAME))
                .thenReturn(existing)
        `when`(appUserRepository.save(toSave))
                .thenReturn(toSave)
        `when`(passwordEncoder.encode(newPassword))
                .thenReturn(newPassword)

        val result = authService.updateUserSelf(request)
        assertEquals(expected, result)
    }

    @Test
    fun test_updateUserSelf_noUpdateUsername() {
        val request = AppUserRequest(
                userName = "userName2",
                firstName = "Craig",
                lastName = "Miller"
        )

        val existing = AppUser(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Bob",
                lastName = "Saget",
                password = PASSWORD
        )

        val toSave = AppUser(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller",
                password = PASSWORD
        )

        val expected = AppUserResponse(
                userId = 1L,
                userName = USER_NAME,
                firstName = "Craig",
                lastName = "Miller"
        )

        `when`(securityContextService.getUserName())
                .thenReturn(USER_NAME)
        `when`(appUserRepository.findByUserName(USER_NAME))
                .thenReturn(existing)
        `when`(appUserRepository.save(toSave))
                .thenReturn(toSave)

        val result = authService.updateUserSelf(request)
        assertEquals(expected, result)
    }

    @Test
    fun test_getAllUsers() {
        val user = AppUser().apply {
            userName = USER_NAME
            password = PASSWORD
            roles = listOf(Role(name = ROLE))
        }
        val expected = user.copy(password = "")
        val userList = listOf(user)
        val expectedList = userList.map { u ->
            AppUserResponse(
                    userName = u.userName,
                    roles = listOf(RolePayload(name = ROLE))
            )
        }

        `when`(appUserRepository.findAll())
                .thenReturn(userList)

        val result = authService.getAllUsers()
        assertEquals(expectedList, result)
    }

    @Test
    fun test_getUser() {
        val user = AppUser().apply {
            userName = USER_NAME
            password = PASSWORD
            roles = listOf(Role(name = ROLE))
        }
        val expected = AppUserResponse(
                userName = USER_NAME,
                roles = listOf(RolePayload(name = ROLE))
        )
        `when`(appUserRepository.findById(1L))
                .thenReturn(Optional.of(user))

        val result = authService.getUser(1L)
        assertEquals(expected, result)
    }

    @Test
    fun test_getUser_notFound() {
        val result = authService.getUser(1L)
        assertNull(result)
    }

    @Test
    fun test_deleteUser() {
        val userId = 1L
        val user = AppUser(userId, USER_NAME)
        `when`(appUserRepository.findById(userId))
                .thenReturn(Optional.of(user))

        val expected = AppUserResponse(
                userId = userId,
                userName = USER_NAME
        )

        val result = authService.deleteUser(userId)
        assertEquals(expected, result)

        val userIdCaptor = ArgumentCaptor.forClass(Long::class.java)
        verify(appUserRepository, times(1))
                .deleteById(userIdCaptor.capture())
        assertEquals(userId, userIdCaptor.value)
    }

    @Test
    fun test_deleteUser_notFound() {
        val userId = 1L

        val result = authService.deleteUser(userId)
        assertNull(result)

        val userIdCaptor = ArgumentCaptor.forClass(Long::class.java)
        verify(appUserRepository, times(1))
                .deleteById(userIdCaptor.capture())
        assertEquals(userId, userIdCaptor.value)
    }

    @Test
    fun test_refreshToken() {
        val token1 = "token1"
        val token2 = "token2"
        val userName = "userName"

        val user = AppUser(userName = userName)
        val claims = mapOf(TokenConstants.CLAIM_SUBJECT to userName)

        `when`(jwtTokenProvider.validateToken(token1))
                .thenReturn(TokenValidationStatus.EXPIRED)
        `when`(jwtTokenProvider.isRefreshAllowed(user))
                .thenReturn(true)
        `when`(jwtTokenProvider.getClaims(token1))
                .thenReturn(claims)
        `when`(appUserRepository.findByUserName(userName))
                .thenReturn(user)
        `when`(jwtTokenProvider.createToken(user))
                .thenReturn(token2)

        val result = authService.refreshToken(token1)
        assertEquals(token2, result)

        verify(appUserRepository, times(1))
                .save(ArgumentMatchers.isA(AppUser::class.java))
    }

    @Test
    fun test_refreshToken_invalidSignature() {
        val token1 = "token1"

        `when`(jwtTokenProvider.validateToken(token1))
                .thenReturn(TokenValidationStatus.BAD_SIGNATURE)

        val ex = assertFailsWith<ApiUnauthorizedException> {
            authService.refreshToken(token1)
        }
        assertThat(ex, hasProperty("message", containsString("Invalid token")))
    }

    @Test
    fun test_refreshToken_cannotRefresh() {
        val token1 = "token1"
        val userName = "userName"

        val user = AppUser(userName = userName)
        val claims = mapOf(TokenConstants.CLAIM_SUBJECT to userName)

        `when`(jwtTokenProvider.validateToken(token1))
                .thenReturn(TokenValidationStatus.EXPIRED)
        `when`(jwtTokenProvider.isRefreshAllowed(user))
                .thenReturn(false)
        `when`(jwtTokenProvider.getClaims(token1))
                .thenReturn(claims)
        `when`(appUserRepository.findByUserName(userName))
                .thenReturn(user)

        val ex = assertFailsWith<ApiUnauthorizedException> {
            authService.refreshToken(token1)
        }
        assertThat(ex, hasProperty("message", containsString("Token refresh not allowed")))
    }

    @Test
    fun test_refreshToken_noUser() {
        val token1 = "token1"
        val userName = "userName"

        val claims = mapOf(TokenConstants.CLAIM_SUBJECT to userName)

        `when`(jwtTokenProvider.validateToken(token1))
                .thenReturn(TokenValidationStatus.EXPIRED)
        `when`(jwtTokenProvider.getClaims(token1))
                .thenReturn(claims)

        val ex = assertFailsWith<ApiUnauthorizedException> {
            authService.refreshToken(token1)
        }
        assertThat(ex, hasProperty("message", containsString("No user exists for token")))
    }

    @Test
    fun test_revokeAccess() {
        val user = AppUser().apply {
            lastAuthenticated = LocalDateTime.now()
            userName = "userName"
            password = "password"
            userId = 1L
        }
        `when`(appUserRepository.findById(1L))
                .thenReturn(Optional.of(user))
        `when`(appUserRepository.save(ArgumentMatchers.isA(AppUser::class.java)))
                .thenReturn(user)

        val expected = AppUserResponse(
                userName = "userName",
                userId = 1L
        )

        val result = authService.revokeAccess(1L)
        assertEquals(expected, result)

        val userCaptor = ArgumentCaptor.forClass(AppUser::class.java)

        verify(appUserRepository, times(1))
                .save(userCaptor.capture())

        assertThat(userCaptor.value, hasProperty("lastAuthenticated", nullValue()))
    }

    @Test
    fun test_revokeAccess_noUser() {
        val user = AppUser().apply {
            lastAuthenticated = LocalDateTime.now()
            userName = "userName"
            password = "password"
            userId = 1L
        }
        `when`(appUserRepository.findById(user.userId))
                .thenReturn(Optional.empty())

        val ex = assertFailsWith<NoUserException> {
            authService.revokeAccess(user.userId)
        }

        assertThat(ex, hasProperty("message", containsString("Cannot find user")))
    }

    @Test
    fun test_getVideoToken() {
        val userName = "userName"
        val user = AppUser(userName = userName)
        val videoId = 10L
        val token = "ABCDEFG"

        `when`(securityContextService.getUserName())
                .thenReturn(userName)
        `when`(videoTokenProvider.createToken(user, mapOf(TokenConstants.PARAM_VIDEO_ID to videoId)))
                .thenReturn(token)

        val result = authService.getVideoToken(videoId)
        assertEquals(VideoTokenResponse(token), result)
    }

    @Test
    fun test_checkAuth() {
        val userName = "userName"
        val user = AppUser(userName = userName, password = "ABCDEFG")

        `when`(securityContextService.getUserName())
                .thenReturn(userName)
        `when`(appUserRepository.findByUserName(userName))
                .thenReturn(user)

        val expected = AppUserResponse(
                userName = userName
        )

        val result = authService.checkAuth()
        assertEquals(expected, result)
    }

    @Test(expected = ApiUnauthorizedException::class)
    fun test_checkAuth_invalidUserName() {
        val userName = "userName"
        `when`(securityContextService.getUserName())
                .thenReturn(userName)

        authService.checkAuth()
    }

}
