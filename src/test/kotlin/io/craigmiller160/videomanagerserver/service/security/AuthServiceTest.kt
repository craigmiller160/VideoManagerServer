package io.craigmiller160.videomanagerserver.service.security

import com.nhaarman.mockito_kotlin.times
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import io.craigmiller160.videomanagerserver.repository.AppUserRepository
import io.craigmiller160.videomanagerserver.repository.RoleRepository
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenProvider
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.lang.IllegalArgumentException

@RunWith(MockitoJUnitRunner::class)
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
        assertEquals(Token(TOKEN), result)
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
    fun test_updateUser() {
        TODO("Finish this")
    }

    @Test
    fun test_updateUser_withPassword() {
        TODO("Finish this")
    }

    @Test
    fun test_updateUser_notFound() {
        TODO("Finish this")
    }

    @Test
    fun test_getAllUsers() {
        TODO("Finish this")
    }

    @Test
    fun test_getUser() {
        TODO("Finish this")
    }

    @Test
    fun test_getUser_notFound() {
        TODO("Finish this")
    }

}