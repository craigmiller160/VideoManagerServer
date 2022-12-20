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

import kotlin.test.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

@RunWith(MockitoJUnitRunner::class)
class SecurityContextServiceTest {

  private lateinit var securityContext: SecurityContext
  private lateinit var authentication: Authentication
  private lateinit var securityContextService: SecurityContextService

  @Before
  fun setup() {
    securityContext = mock(SecurityContext::class.java)
    authentication = mock(Authentication::class.java)
    `when`(securityContext.authentication).thenReturn(authentication)

    SecurityContextHolder.setContext(securityContext)

    securityContextService = SecurityContextService()
  }

  @After
  fun cleanup() {
    SecurityContextHolder.clearContext()
  }

  @Test
  fun test_getSecurityContext() {
    val result = securityContextService.getSecurityContext()
    assertEquals(securityContext, result)
  }

  @Test
  fun test_getUserName_userDetails() {
    val userName = "userName"
    val userDetails = mock(UserDetails::class.java)
    `when`(userDetails.username).thenReturn(userName)
    `when`(authentication.principal).thenReturn(userDetails)

    val result = securityContextService.getUserName()
    assertEquals(userName, result)
  }

  @Test
  fun test_getUserName_string() {
    val userName = "userName"
    `when`(authentication.principal).thenReturn(userName)

    val result = securityContextService.getUserName()
    assertEquals(userName, result)
  }
}
