package io.craigmiller160.videomanagerserver.jwt

import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// TODO needs tests

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

        throw ApiUnauthorizedException("User is unauthorized")
    }
}