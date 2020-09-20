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

import org.junit.Assert.assertEquals
import org.springframework.mock.web.MockHttpServletResponse

const val CONTENT_TYPE_JSON = "application/json"

fun assertOkResponse(response: MockHttpServletResponse, content: String) {
    assertEquals(200, response.status)
    assertEquals(CONTENT_TYPE_JSON, response.contentType)
    assertEquals(response.contentAsString, content)
}

fun assertNoContentResponse(response: MockHttpServletResponse) {
    assertEquals(204, response.status)
}

fun assertBadRequest(response: MockHttpServletResponse, body: String? = null) {
    assertEquals(400, response.status)
    body?.let {
        assertEquals(response.contentAsString, body)
    }
}
