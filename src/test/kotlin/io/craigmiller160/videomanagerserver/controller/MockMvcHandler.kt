package io.craigmiller160.videomanagerserver.controller

import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

class MockMvcHandler (private val mockMvc: MockMvc) {

    fun doGet(uri: String): MockHttpServletResponse {
        return mockMvc.perform(
                get(uri)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().response
    }

    fun doPost(uri: String, json: String): MockHttpServletResponse {
        return mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().response
    }

    fun doPut(uri: String, json: String): MockHttpServletResponse {
        return mockMvc.perform(
                put(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().response
    }

    fun doDelete(uri: String): MockHttpServletResponse {
        return mockMvc.perform(
                delete(uri)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().response
    }

}