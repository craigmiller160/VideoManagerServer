package io.craigmiller160.videomanagerserver.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// TODO needs tests

@Component
class AuthLoginFilter : UsernamePasswordAuthenticationFilter() {

    private val objectMapper = ObjectMapper()

    override fun attemptAuthentication(req: HttpServletRequest?, resp: HttpServletResponse?): Authentication? {
        return req?.let {
            val userRequest = objectMapper.readValue(req.inputStream, User::class.java)
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(
                    userRequest.userName,
                    userRequest.password
            ))
        }
    }

    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?, authResult: Authentication?) {
        TODO("Implement the returning of the JWT here")
    }
}