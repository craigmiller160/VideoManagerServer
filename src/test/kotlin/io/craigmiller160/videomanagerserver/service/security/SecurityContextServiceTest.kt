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

package io.craigmiller160.videomanagerserver.service.security

import com.nhaarman.mockito_kotlin.whenever
import java.util.UUID
import kotlin.test.assertEquals
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.keycloak.KeycloakPrincipal
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

@ExtendWith(MockitoExtension::class)
class SecurityContextServiceTest {

  @Mock private lateinit var securityContext: SecurityContext
  @Mock private lateinit var authentication: Authentication
  @Mock private lateinit var principal: KeycloakPrincipal<*>
  private lateinit var securityContextService: SecurityContextService

  @BeforeEach
  fun setup() {
    SecurityContextHolder.setContext(securityContext)
    whenever(securityContext.authentication).thenReturn(authentication)

    securityContextService = SecurityContextService()
  }

  @AfterEach
  fun cleanup() {
    SecurityContextHolder.clearContext()
  }

  @Test
  fun test_getUserId() {
    val userId = UUID.randomUUID()
    whenever(authentication.principal).thenReturn(principal)
    whenever(principal.name).thenReturn(userId.toString())

    val result = securityContextService.getUserId()
    assertEquals(userId, result)
  }

  @Test
  fun test_getSecurityContext() {
    val result = securityContextService.getSecurityContext()
    assertEquals(securityContext, result)
  }
}
