package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.dto.AppUser
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

    // TODO refactor all of this to test the API
    // TODO figure out how to unit test security with it

    @Test
    fun test_login() {
        val request = AppUser().apply {
            userName = "userName"
            password = "password"
        }
        val token = Token("ABCDEFG")
        `when`(authService.login(request))
                .thenReturn(token)

        val result = authController.login(request)
        assertEquals(token, result.body)
    }

    @Test
    fun test_getRoles() {
        TODO("Finish this")
    }

    @Test
    fun test_getRoles_noRoles() {
        TODO("Finish this")
    }

    @Test
    fun test_createUser() {
        TODO("Finish this")
    }

    @Test
    fun test_getAllUsers() {
        TODO("Finish this")
    }

    @Test
    fun test_getAllUsers_noUsers() {
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

    @Test
    fun test_updateUser() {
        TODO("Finish this")
    }

    @Test
    fun test_updateUser_notFound() {
        TODO("Finish this")
    }

}