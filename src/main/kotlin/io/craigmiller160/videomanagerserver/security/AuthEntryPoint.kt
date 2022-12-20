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
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class AuthEntryPoint : AuthenticationEntryPoint {

  private val objectMapper = ObjectMapper()
  private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  override fun commence(
    req: HttpServletRequest?,
    resp: HttpServletResponse?,
    ex: AuthenticationException?
  ) {
    val status =
      resp?.let {
        if (resp.status >= 400) HttpStatus.valueOf(resp.status) else HttpStatus.UNAUTHORIZED
      }
        ?: HttpStatus.UNAUTHORIZED
    resp?.status = status.value()

    resp?.addHeader("Content-Type", "application/json")
    val error =
      ErrorResponse().apply {
        timestamp = formatter.format(ZonedDateTime.now())
        this.status = status.value()
        error = status.name
        message = ex?.message ?: ""
        path = "${req?.contextPath ?: ""}${req?.servletPath ?: ""}"
      }
    val payload = objectMapper.writeValueAsString(error)
    resp?.writer?.use { it.write(payload) }
  }
}
