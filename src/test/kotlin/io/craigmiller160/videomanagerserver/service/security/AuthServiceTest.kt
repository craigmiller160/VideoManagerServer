package io.craigmiller160.videomanagerserver.service.security

import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import io.craigmiller160.videomanagerserver.repository.AppUserRepository
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@RunWith(MockitoJUnitRunner::class)
class AuthServiceTest {

    companion object {
        private const val USER_NAME = "craig"
        private const val PASSWORD = "password"
        private const val ENCODED_PASSWORD = "encoded_password"
        private const val TOKEN = "token"
    }

    @Mock
    private lateinit var appUserRepository: AppUserRepository

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

}