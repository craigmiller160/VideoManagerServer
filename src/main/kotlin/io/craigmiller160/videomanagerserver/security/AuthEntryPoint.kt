package io.craigmiller160.videomanagerserver.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthEntryPoint : AuthenticationEntryPoint {

    private val objectMapper = ObjectMapper()
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    override fun commence(req: HttpServletRequest?, resp: HttpServletResponse?, ex: AuthenticationException?) {
        val status = resp?.let {
            if (resp.status >= 400) HttpStatus.valueOf(resp.status) else HttpStatus.UNAUTHORIZED
        } ?: HttpStatus.UNAUTHORIZED
        resp?.status = status.value()

        resp?.addHeader("Content-Type", "application/json")
        val error = ErrorResponse().apply {
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
