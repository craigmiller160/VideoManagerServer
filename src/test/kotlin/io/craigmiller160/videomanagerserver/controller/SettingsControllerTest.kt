package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.dto.SETTINGS_ID
import io.craigmiller160.videomanagerserver.dto.Settings
import io.craigmiller160.videomanagerserver.security.ROLE_ADMIN
import io.craigmiller160.videomanagerserver.security.tokenprovider.JwtTokenProvider
import io.craigmiller160.videomanagerserver.service.settings.SettingsService
import io.craigmiller160.videomanagerserver.test_util.responseBody
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class SettingsControllerTest {

    companion object {
        private const val ROOT_DIR = "rootDir"
    }

    private lateinit var mockMvc: MockMvc
    private lateinit var mockMvcHandler: MockMvcHandler

    @MockBean
    private lateinit var settingsService: SettingsService

    @Autowired
    private lateinit var settingsController: SettingsController

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var webAppContext: WebApplicationContext

    private lateinit var jacksonSettings: JacksonTester<Settings>

    @Before
    fun setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
                .build()
        mockMvcHandler = MockMvcHandler(mockMvc)

        JacksonTester.initFields(this, objectMapper)
    }

    @Test
    fun test_getSettings() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_ADMIN))
        )
        val settings = Settings(
                settingsId = SETTINGS_ID,
                rootDir = ROOT_DIR
        )
        `when`(settingsService.getOrCreateSettings())
                .thenReturn(settings)

        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val response = mockMvcHandler.doGet("/api/settings")
        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonSettings.write(settings).json))
        ))
    }

    @Test
    fun test_getSettings_unauthorized() {
        val response = mockMvcHandler.doGet("/api/settings")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_getSettings_missingRole() {
        val user = AppUser(
                userName = "userName"
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val response = mockMvcHandler.doGet("/api/settings")
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun test_updateSettings() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_ADMIN))
        )
        val settings = Settings(
                settingsId = SETTINGS_ID,
                rootDir = ROOT_DIR
        )
        `when`(settingsService.updateSettings(settings))
                .thenReturn(settings)

        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val response = mockMvcHandler.doPut("/api/settings", jacksonSettings.write(settings).json)
        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonSettings.write(settings).json))
        ))
    }

    @Test
    fun test_updateSettings_unauthorized() {
        val settings = Settings(
                settingsId = SETTINGS_ID,
                rootDir = ROOT_DIR
        )
        val response = mockMvcHandler.doPut("/api/settings", jacksonSettings.write(settings).json)
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_updateSettings_missingRole() {
        val user = AppUser(
                userName = "userName"
        )
        val settings = Settings(
                settingsId = SETTINGS_ID,
                rootDir = ROOT_DIR
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val response = mockMvcHandler.doPut("/api/settings", jacksonSettings.write(settings).json)
        assertThat(response, hasProperty("status", equalTo(403)))
    }

}