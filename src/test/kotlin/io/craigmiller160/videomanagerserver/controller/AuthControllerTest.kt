package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.dto.User
import io.craigmiller160.videomanagerserver.service.security.AuthService
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AuthControllerTest {

    @Mock
    private lateinit var authService: AuthService

    @InjectMocks
    private lateinit var authController: AuthController


    @Test
    fun test_login() {
        val request = User().apply {
            userName = "userName"
            password = "password"
        }
        val token = Token("ABCDEFG")
        `when`(authService.login(request))
                .thenReturn(token)

        val result = authController.login(request)
        assertEquals(token, result.body)
    }

}