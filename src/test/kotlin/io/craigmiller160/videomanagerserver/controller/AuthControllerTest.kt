package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenProvider
import io.craigmiller160.videomanagerserver.service.security.AuthService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext


@RunWith(SpringJUnit4ClassRunner::class)
//@WebMvcTest(AuthController::class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@WebAppConfiguration
@ContextConfiguration
class AuthControllerTest {

    // TODO replicate the security for other controllers

    // TODO figure out how to unit test security with it

    @Mock
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var authController: AuthController

    private lateinit var videoManagerControllerAdvice: VideoManagerControllerAdvice

    private lateinit var jacksonUser: JacksonTester<AppUser>
    private lateinit var jacksonToken: JacksonTester<Token>

    private lateinit var mockMvc: MockMvc
    private lateinit var mockMvcHandler: MockMvcHandler

    @Autowired
    private lateinit var webAppContext: WebApplicationContext

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Before
    fun setup() {
        videoManagerControllerAdvice = VideoManagerControllerAdvice() // TODO remove this if not needed
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
//                .standaloneSetup(authController)
//                .setControllerAdvice(videoManagerControllerAdvice)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
                .build()
        mockMvcHandler = MockMvcHandler(mockMvc)

        JacksonTester.initFields(this, ObjectMapper())
        MockitoAnnotations.initMocks(this)
        ReflectionTestUtils.setField(authController, "authService", authService)
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
        val user = AppUser().apply {
            userName = "userName"
        }
        val token = jwtTokenProvider.createToken(user)
        println(token) // TODO delete this
        mockMvcHandler.token = token

        // TODO make this actually work
        val response = mockMvcHandler.doGet("/auth/roles")
        println("Status: ${response.status}")
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