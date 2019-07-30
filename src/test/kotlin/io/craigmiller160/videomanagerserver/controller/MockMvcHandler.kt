package io.craigmiller160.videomanagerserver.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

class MockMvcHandler (private val mockMvc: MockMvc) {

    var token = ""

    private fun applyAuth(builder: MockHttpServletRequestBuilder) {
        if (token.isNotBlank()) {
            builder.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        }
    }

    fun doGet(uri: String): MockHttpServletResponse {
        val builder = get(uri)
                .accept(MediaType.APPLICATION_JSON)
        applyAuth(builder)

        return mockMvc.perform(builder).andReturn().response
    }

    fun doPost(uri: String, json: String? = null): MockHttpServletResponse {
        val builder = post(uri).accept(MediaType.APPLICATION_JSON)
        applyAuth(builder)
        json?.let {
            builder.contentType(MediaType.APPLICATION_JSON)
            builder.content(json)
        }

        return mockMvc.perform(builder).andReturn().response
    }

    fun doPut(uri: String, json: String): MockHttpServletResponse {
        val builder = put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
        applyAuth(builder)

        return mockMvc.perform(builder).andReturn().response
    }

    fun doDelete(uri: String): MockHttpServletResponse {
        val builder = delete(uri)
                .accept(MediaType.APPLICATION_JSON)
        applyAuth(builder)

        return mockMvc.perform(builder).andReturn().response
    }

}