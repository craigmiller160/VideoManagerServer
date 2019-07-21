package io.craigmiller160.videomanagerserver.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.ErrorMessage
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import java.io.PrintWriter
import java.io.StringWriter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RunWith(MockitoJUnitRunner::class)
class AuthEntryPointTest {

    private val authEntryPoint = AuthEntryPoint()
    private val objectMapper = ObjectMapper()

    @Test
    fun test_commence() {
        val message = "My Error"
        val path = "/my/path"
        val req = mock(HttpServletRequest::class.java)
        val resp = mock(HttpServletResponse::class.java)
        val ex = MyAuthEx(message)
        val stringWriter = StringWriter()
        val writer = PrintWriter(stringWriter)

        `when`(resp.writer)
                .thenReturn(writer)
        `when`(req.pathInfo)
                .thenReturn(path)

        authEntryPoint.commence(req, resp, ex)

        val statusCaptor = ArgumentCaptor.forClass(Int::class.java)
        verify(resp).status = statusCaptor.capture()
        assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCaptor.value)

        val response = stringWriter.toString()
        val error = objectMapper.readValue(response, ErrorMessage::class.java)
        assertThat(error, allOf(
                hasProperty("timestamp", notNullValue()),
                hasProperty("status", equalTo(HttpStatus.UNAUTHORIZED.value())),
                hasProperty("error", equalTo(HttpStatus.UNAUTHORIZED.name)),
                hasProperty("message", equalTo(message)),
                hasProperty("path", equalTo(path))
        ))
    }

    private class MyAuthEx (msg: String) : AuthenticationException(msg)

}