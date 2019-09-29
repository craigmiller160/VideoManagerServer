package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.security.COOKIE_NAME
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import javax.servlet.http.Cookie

class MockMvcHandler (private val mockMvc: MockMvc) {

    var token = ""

    private fun applyCommon(builder: MockHttpServletRequestBuilder) {
        builder.contextPath("/api")
                .secure(true)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())

        if (token.isNotBlank()) {
            builder.cookie(Cookie(COOKIE_NAME, token))
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

        json?.let {
            builder.content(json)
        }

        return mockMvc.perform(builder).andReturn().response
    }

    fun doPut(uri: String, json: String?): MockHttpServletResponse {
        val builder = put(uri)
        applyCommon(builder)

        json?.let {
            builder.content(json)
        }

        return mockMvc.perform(builder).andReturn().response
    }

    fun doDelete(uri: String): MockHttpServletResponse {
        val builder = delete(uri)
        applyCommon(builder)

        return mockMvc.perform(builder).andReturn().response
    }

}
