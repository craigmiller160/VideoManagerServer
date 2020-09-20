/*
 *     video-manager-server
 *     Copyright (C) 2020 Craig Miller
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.SettingsPayload
import io.craigmiller160.videomanagerserver.entity.AppUser
import io.craigmiller160.videomanagerserver.entity.Role
import io.craigmiller160.videomanagerserver.security.ROLE_ADMIN
import io.craigmiller160.videomanagerserver.service.settings.SettingsService
import io.craigmiller160.videomanagerserver.test_util.responseBody
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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

    private lateinit var jacksonSettings: JacksonTester<SettingsPayload>

    @Test
    fun test_getSettings() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_ADMIN))
        )
        val settings = SettingsPayload(
                rootDir = ROOT_DIR
        )
        `when`(settingsService.getOrCreateSettings())
                .thenReturn(settings)

        mockMvcHandler.token = token
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
        mockMvcHandler.token = token
        val response = mockMvcHandler.doGet("/api/settings")
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun test_updateSettings() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_ADMIN))
        )
        val settings = SettingsPayload(
                rootDir = ROOT_DIR
        )
        `when`(settingsService.updateSettings(settings))
                .thenReturn(settings)

        mockMvcHandler.token = token
        val response = mockMvcHandler.doPut("/api/settings", jacksonSettings.write(settings).json)
        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonSettings.write(settings).json))
        ))
    }

    @Test
    fun test_updateSettings_unauthorized() {
        val settings = SettingsPayload(
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
        val settings = SettingsPayload(
                rootDir = ROOT_DIR
        )
        mockMvcHandler.token = token
        val response = mockMvcHandler.doPut("/api/settings", jacksonSettings.write(settings).json)
        assertThat(response, hasProperty("status", equalTo(403)))
    }

}
