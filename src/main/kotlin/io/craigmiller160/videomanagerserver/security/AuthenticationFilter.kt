package io.craigmiller160.videomanagerserver.security

import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenProvider
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenValidationStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationFilter (
        private val jwtTokenProvider: TokenProvider
) : OncePerRequestFilter() {

    public override fun doFilterInternal(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        val token = jwtTokenProvider.resolveToken(req)
        token?.let {
            try {
                when (jwtTokenProvider.validateToken(token)) {
                    TokenValidationStatus.VALID -> validToken(token, req, resp, chain)
                    TokenValidationStatus.EXPIRED,
                    TokenValidationStatus.BAD_SIGNATURE,
                    TokenValidationStatus.NO_TOKEN -> unauthenticated(req, resp, chain)
                }
            }
            catch (ex: Exception) {
                logger.error("Error handling token", ex)
                unauthenticated(req, resp, chain)
            }
        } ?: unauthenticated(req, resp, chain)
    }

    private fun validToken(token: String, req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        val auth = jwtTokenProvider.createAuthentication(token)
        SecurityContextHolder.getContext().authentication = auth
        chain.doFilter(req, resp)
    }

    private fun unauthenticated(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        SecurityContextHolder.clearContext()
        chain.doFilter(req, resp)
    }
}