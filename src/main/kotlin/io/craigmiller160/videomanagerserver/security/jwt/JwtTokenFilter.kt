package io.craigmiller160.videomanagerserver.security.jwt

import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// TODO needs tests

// TODO refactor this based on Auth.0 example that uses the AuthenticationManager

@Component
class JwtTokenFilter (
        private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        val token = jwtTokenProvider.resolveToken(req)
        token?.let {
            try {
                if (!jwtTokenProvider.validateToken(token)) {
                    throw ApiUnauthorizedException("User is unauthorized")
                }
                val auth = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = auth
                chain.doFilter(req, resp)
                return
            }
            catch (ex: Exception) {
                SecurityContextHolder.clearContext()
                throw ex
            }
        }

        throw ApiUnauthorizedException("User is unauthorized") // TODO instead of this, try continuing the filter chain without authentication
    }
}