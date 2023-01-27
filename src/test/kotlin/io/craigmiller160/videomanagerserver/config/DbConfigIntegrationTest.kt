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

package io.craigmiller160.videomanagerserver.config

import io.craigmiller160.videomanagerserver.test_util.AuthenticationConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class DbConfigIntegrationTest {

  companion object {
    private const val URL = "jdbc:h2:mem:testdb"
    private const val USERNAME = "user"
    private const val PASSWORD = "password"
    private const val DRIVER_CLASS_NAME = "org.h2.Driver"
    private const val DB = "test"
  }

  @Autowired private lateinit var dbConfig: DbConfig
  @MockBean private lateinit var authConfig: AuthenticationConfig

  @Test
  fun testUrl() {
    assertEquals(URL, dbConfig.url)
  }

  @Test
  fun testUsername() {
    assertEquals(USERNAME, dbConfig.username)
  }

  @Test
  fun testPassword() {
    assertEquals(PASSWORD, dbConfig.password)
  }

  @Test
  fun testDriverClassName() {
    assertEquals(DRIVER_CLASS_NAME, dbConfig.driverClassName)
  }

  @Test
  fun testDb() {
    assertEquals(DB, dbConfig.db)
  }

  @Test
  fun testDocker() {
    assertTrue(dbConfig.docker)
  }
}
