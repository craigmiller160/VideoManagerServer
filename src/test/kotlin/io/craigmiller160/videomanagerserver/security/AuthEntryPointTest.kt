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

package io.craigmiller160.videomanagerserver.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.ErrorResponse
import java.io.PrintWriter
import java.io.StringWriter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException

@ExtendWith
class AuthEntryPointTest {

  private val authEntryPoint = AuthEntryPoint()
  private val objectMapper = ObjectMapper()

  @Test
  fun test_commence() {
    val message = "My Error"
    val contextPath = "/my"
    val servletPath = "/path"
    val path = "$contextPath$servletPath"
    val req = mock(HttpServletRequest::class.java)
    val resp = mock(HttpServletResponse::class.java)
    val ex = MyAuthEx(message)
    val stringWriter = StringWriter()
    val writer = PrintWriter(stringWriter)

    `when`(resp.writer).thenReturn(writer)
    `when`(req.contextPath).thenReturn(contextPath)
    `when`(req.servletPath).thenReturn(servletPath)

    authEntryPoint.commence(req, resp, ex)

    val statusCaptor = ArgumentCaptor.forClass(Int::class.java)
    verify(resp).status = statusCaptor.capture()
    assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCaptor.value)

    val headerCaptor = ArgumentCaptor.forClass(String::class.java)
    verify(resp).addHeader(headerCaptor.capture(), headerCaptor.capture())
    assertEquals("Content-Type", headerCaptor.allValues[0])
    assertEquals("application/json", headerCaptor.allValues[1])

    val response = stringWriter.toString()
    val error = objectMapper.readValue(response, ErrorResponse::class.java)
    assertThat(
      error,
      allOf(
        hasProperty("timestamp", notNullValue()),
        hasProperty("status", equalTo(HttpStatus.UNAUTHORIZED.value())),
        hasProperty("error", equalTo(HttpStatus.UNAUTHORIZED.name)),
        hasProperty("message", equalTo(message)),
        hasProperty("path", equalTo(path))))
  }

  private class MyAuthEx(msg: String) : AuthenticationException(msg)
}
