package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.entity.AppUser
import io.craigmiller160.videomanagerserver.entity.Role
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
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class SettingsControllerTest : AbstractControllerTest() {

    companion object {
        private const val ROOT_DIR = "rootDir"
    }

    @MockBean
    private lateinit var settingsService: SettingsService

    @Autowired
    private lateinit var settingsController: SettingsController

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private lateinit var jacksonSettings: JacksonTester<Settings>

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
