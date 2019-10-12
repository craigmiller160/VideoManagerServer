package io.craigmiller160.videomanagerserver.service.security

import com.nhaarman.mockito_kotlin.times
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.dto.VideoToken
import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import io.craigmiller160.videomanagerserver.exception.NoUserException
import io.craigmiller160.videomanagerserver.repository.AppUserRepository
import io.craigmiller160.videomanagerserver.repository.RoleRepository
import io.craigmiller160.videomanagerserver.security.tokenprovider.JwtTokenProvider
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenValidationStatus
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.isEmptyString
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
        val request = AppUser().apply {
            userName = USER_NAME
            password = PASSWORD
        }

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
        val request = AppUser().apply {
            userName = "Bob"
            password = PASSWORD
        }
        authService.login(request)
    }

    @Test(expected = ApiUnauthorizedException::class)
    fun test_login_wrongPassword() {
        mockLogin()
        val request = AppUser().apply {
            userName = USER_NAME
            password = "FooBar"
        }
        authService.login(request)
    }

    @Test
    fun test_getRoles() {
        val roles = listOf(Role(1, "Role1"), Role(2, "Role2"))
        `when`(roleRepository.findAll())
                .thenReturn(roles)

        val result = authService.getRoles()
        assertEquals(roles, result)
    }

    @Test
    fun test_createUser() {
        val user = AppUser().apply {
            userName = USER_NAME
            password = PASSWORD
            roles = listOf(Role(1, ROLE))
        }
        val userWithId = user.copy(userId = 1L)

        `when`(appUserRepository.save(user))
                .thenReturn(userWithId)
        `when`(passwordEncoder.encode(PASSWORD))
                .thenReturn(ENCODED_PASSWORD)

        val userCaptor = ArgumentCaptor.forClass(AppUser::class.java)

        val result = authService.createUser(user)

        verify(appUserRepository, times(1))
                .save(userCaptor.capture())

        assertThat(userCaptor.value, hasProperty("password", equalTo(ENCODED_PASSWORD)))
        assertEquals(userWithId, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_createUser_invalid() {
        val user = AppUser().apply {
            userName = "Bob"
        }
        authService.createUser(user)
    }

    @Test
    fun test_rolesHaveIds() {
        val roles = listOf(Role(1, ROLE))
        val result = authService.rolesHaveIds(roles)
        assertTrue(result)
    }

    @Test
    fun test_rolesHaveIds_noIds() {
        val roles = listOf(Role(name = ROLE))
        val result = authService.rolesHaveIds(roles)
        assertFalse(result)
    }

    @Test
    fun test_updateUserAdmin() {
        val userId = 1L
        val request = AppUser().apply {
            userName = USER_NAME
            roles = listOf(Role(name = ROLE))
        }
        val response = request.copy(
                userId = userId,
                password = PASSWORD
        )
        val existing = request.copy(
                userId = userId,
                roles = listOf(),
                password = PASSWORD
        )
        val expected = response.copy(password = "")

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
        val request = AppUser().apply {
            userName = USER_NAME
            roles = listOf(Role(name = ROLE))
            password = PASSWORD
        }
        val response = request.copy(
                userId = userId
        )
        val existing = request.copy(
                userId = userId,
                roles = listOf(),
                password = "${PASSWORD}2"
        )
        val toSave = response.copy(
                password = ENCODED_PASSWORD
        )
        val expected = toSave.copy(password = "")

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
        val user = AppUser().apply {
            userName = USER_NAME
            roles = listOf(Role(name = ROLE))
        }
        val expected = user.copy(userId = userId)

        `when`(appUserRepository.save(expected))
                .thenReturn(expected)

        val result = authService.updateUserAdmin(userId, user)
        assertNull(result)
    }

    @Test
    fun test_updateUserSelf() {
        TODO("Finish this")
    }

    @Test
    fun test_updateUserSelf_skipsRoles() {
        TODO("Finish this")
    }

    @Test
    fun test_updateUserSelf_notFound() {
        TODO("Finish this")
    }

    @Test
    fun test_updateUserSelf_withPassword() {
        TODO("Finish this")
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
        val expectedList = listOf(expected)

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
        val expected = user.copy(password = "")
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

        val result = authService.deleteUser(userId)
        assertEquals(user, result)

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
        `when`(appUserRepository.findById(user.userId))
                .thenReturn(Optional.of(user))
        `when`(appUserRepository.save(ArgumentMatchers.isA(AppUser::class.java)))
                .thenReturn(user)
        val result = authService.revokeAccess(user)
        assertThat(result, hasProperty("password", isEmptyString()))

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
            authService.revokeAccess(user)
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
        assertEquals(VideoToken(token), result)
    }

    @Test
    fun test_checkAuth() {
        val userName = "userName"
        val user = AppUser(userName = userName, password = "ABCDEFG")

        `when`(securityContextService.getUserName())
                .thenReturn(userName)
        `when`(appUserRepository.findByUserName(userName))
                .thenReturn(user)

        val result = authService.checkAuth()
        assertThat(result, allOf(
                hasProperty("userName", equalTo(userName)),
                hasProperty("password", equalTo(""))
        ))
    }

    @Test(expected = ApiUnauthorizedException::class)
    fun test_checkAuth_invalidUserName() {
        val userName = "userName"
        `when`(securityContextService.getUserName())
                .thenReturn(userName)

        authService.checkAuth()
    }

}