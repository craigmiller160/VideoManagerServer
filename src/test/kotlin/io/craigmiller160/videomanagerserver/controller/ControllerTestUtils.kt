package io.craigmiller160.videomanagerserver.controller

import org.junit.Assert.assertEquals
import org.springframework.mock.web.MockHttpServletResponse

const val CONTENT_TYPE_JSON = "application/json;charset=UTF-8"

fun assertOkResponse(response: MockHttpServletResponse, content: String) {
    assertEquals(200, response.status)
    assertEquals(CONTENT_TYPE_JSON, response.contentType)
    assertEquals(response.contentAsString, content)
}

fun assertNoContentResponse(response: MockHttpServletResponse) {
    assertEquals(204, response.status)
}

fun assertBadRequest(response: MockHttpServletResponse) {
    assertEquals(400, response.status)
}