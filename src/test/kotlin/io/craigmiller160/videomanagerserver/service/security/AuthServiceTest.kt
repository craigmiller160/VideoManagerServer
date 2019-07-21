package io.craigmiller160.videomanagerserver.service.security

import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.dto.User
import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenProvider
import io.craigmiller160.videomanagerserver.repository.UserRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.crypto.password.PasswordEncoder

@RunWith(MockitoJUnitRunner::class)
class AuthServiceTest {

    companion object {
        private const val USER_NAME = "craig"
        private const val PASSWORD = "password"
        private const val ENCODED_PASSWORD = "encoded_password"
        private const val TOKEN = "token"
    }

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @InjectMocks
    private lateinit var authService: AuthService

    @Test
    fun test_login() {
        TODO("Finish this")
//        val user = User().apply {
//            userName = USER_NAME
//            password = PASSWORD
//        }
//        `when`(passwordEncoder.encode(PASSWORD))
//                .thenReturn(ENCODED_PASSWORD)
//        `when`(userRepository.login(USER_NAME, ENCODED_PASSWORD))
//                .thenReturn(user)
//        `when`(jwtTokenProvider.createToken(user))
//                .thenReturn(TOKEN)
//
//        val result = authService.login(user)
//        assertEquals(Token(TOKEN), result)
    }

    @Test(expected = ApiUnauthorizedException::class)
    fun test_login_invalid() {
        TODO("Finish this")
//        val user = User().apply {
//            userName = "Hello"
//            password = "World"
//        }
//        `when`(passwordEncoder.encode("World"))
//                .thenReturn("Wrong_Encoded")
//        `when`(userRepository.login(USER_NAME, ENCODED_PASSWORD))
//                .thenReturn(user)
//        `when`(jwtTokenProvider.createToken(user))
//                .thenReturn(TOKEN)
//
//        authService.login(user)
    }

}