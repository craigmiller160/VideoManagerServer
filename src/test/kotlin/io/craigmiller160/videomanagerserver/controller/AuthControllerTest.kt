package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.service.security.AuthService
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.boot.test.json.JacksonTester
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@RunWith(MockitoJUnitRunner::class)
class AuthControllerTest {

    // TODO figure out how to unit test security with it

    @Mock
    private lateinit var authService: AuthService

    @InjectMocks
    private lateinit var authController: AuthController

    private lateinit var videoManagerControllerAdvice: VideoManagerControllerAdvice

    private lateinit var jacksonUser: JacksonTester<AppUser>
    private lateinit var jacksonToken: JacksonTester<Token>

    private lateinit var mockMvc: MockMvc
    private lateinit var mockMvcHandler: MockMvcHandler

    @Before
    fun setup() {
        videoManagerControllerAdvice = VideoManagerControllerAdvice()
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(videoManagerControllerAdvice)
                .build()
        mockMvcHandler = MockMvcHandler(mockMvc)

        JacksonTester.initFields(this, ObjectMapper())
    }

    @Test
    fun test_login() {
        val request = AppUser().apply {
            userName = "userName"
            password = "password"
        }
        val token = Token("ABCDEFG")
        `when`(authService.login(request))
                .thenReturn(token)

        val response = mockMvcHandler.doPost("/auth/login", jacksonUser.write(request).json)
        assertOkResponse(response, jacksonToken.write(token).json)
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