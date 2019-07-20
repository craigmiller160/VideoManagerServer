package io.craigmiller160.videomanagerserver.security

import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthSuccessHandler : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(req: HttpServletRequest?, resp: HttpServletResponse?, auth: Authentication?) {
        // TODO need to make sure the token is being returned... need to get it from Authentication
        resp?.status = HttpStatus.OK.value()
    }
}