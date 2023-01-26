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

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.test_util.DefaultUsers
import io.craigmiller160.videomanagerserver.test_util.VideoManagerIntegrationTest
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.json.JacksonTester
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@VideoManagerIntegrationTest
abstract class AbstractControllerTest {

  @Autowired private lateinit var webAppContext: WebApplicationContext

  @Autowired protected lateinit var objectMapper: ObjectMapper
  @Autowired protected lateinit var defaultUsers: DefaultUsers

  protected lateinit var mockMvcHandler: MockMvcHandler

  protected lateinit var token: String
  protected lateinit var editToken: String
  protected lateinit var scanToken: String
  protected lateinit var adminToken: String

  @BeforeEach
  open fun setup() {
    token = defaultUsers.noRolesUser.token
    editToken = defaultUsers.editOnlyUser.token
    scanToken = defaultUsers.scanOnlyUser.token
    adminToken = defaultUsers.adminOnlyUser.token
    mockMvcHandler = buildMockMvcHandler()
    JacksonTester.initFields(this, objectMapper)
  }

  protected fun buildMockMvcHandler(): MockMvcHandler {
    val mockMvc =
      MockMvcBuilders.webAppContextSetup(webAppContext)
        .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
        .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
        .build()
    return MockMvcHandler(mockMvc)
  }
}
