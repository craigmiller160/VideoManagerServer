package io.craigmiller160.videomanagerserver.security

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthEntryPoint : AuthenticationEntryPoint {

    override fun commence(req: HttpServletRequest?, resp: HttpServletResponse?, ex: AuthenticationException?) {
        resp?.status = HttpStatus.UNAUTHORIZED.value()
    }
}