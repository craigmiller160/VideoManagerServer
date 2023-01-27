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

import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

class MockMvcHandler(private val mockMvc: MockMvc) {

  var token = ""

  private fun applyCommon(builder: MockHttpServletRequestBuilder) {
    builder
      .contextPath("/api")
      .secure(true)
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .with(csrf())

    if (token.isNotBlank()) {
      builder.header("Authorization", "Bearer $token")
    }
  }

  fun doGet(uri: String): MockHttpServletResponse {
    val builder = get(uri)
    applyCommon(builder)

    return mockMvc.perform(builder).andReturn().response
  }

  fun doPost(uri: String, json: String? = null): MockHttpServletResponse {
    val builder = post(uri)
    applyCommon(builder)

    json?.let { builder.content(json) }

    return mockMvc.perform(builder).andReturn().response
  }

  fun doPut(uri: String, json: String?): MockHttpServletResponse {
    val builder = put(uri)
    applyCommon(builder)

    json?.let { builder.content(json) }

    return mockMvc.perform(builder).andReturn().response
  }

  fun doDelete(uri: String): MockHttpServletResponse {
    val builder = delete(uri)
    applyCommon(builder)

    return mockMvc.perform(builder).andReturn().response
  }
}
