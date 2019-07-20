package io.craigmiller160.videomanagerserver.security

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthFailureHandler : AuthenticationFailureHandler {

    override fun onAuthenticationFailure(req: HttpServletRequest?, resp: HttpServletResponse?, auth: AuthenticationException?) {
        resp?.status = HttpStatus.UNAUTHORIZED.value()
    }
}