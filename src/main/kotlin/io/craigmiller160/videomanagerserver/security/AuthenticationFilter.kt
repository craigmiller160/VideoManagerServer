package io.craigmiller160.videomanagerserver.security

import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenProvider
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenValidationStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// TODO need tests for the differing behavior for URLs

class AuthenticationFilter (
        private val jwtTokenProvider: TokenProvider
) : OncePerRequestFilter() {

    companion object {
        val VIDEO_URI = Regex("""^\/video-files\/play\/\d{1,4}""")
    }

    public override fun doFilterInternal(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        if (VIDEO_URI.matches(req.servletPath)) {
            validateVideoToken(req, resp, chain)
        }
        else {
            validateJwtToken(req, resp, chain)
        }
    }

    private fun validateVideoToken(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        TODO("Finish this")
    }

    private fun validateJwtToken(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        val token = jwtTokenProvider.resolveToken(req)
        token?.let {
            try {
                when (jwtTokenProvider.validateToken(token)) {
                    TokenValidationStatus.VALID -> {
                        val auth = jwtTokenProvider.createAuthentication(token)
                        SecurityContextHolder.getContext().authentication = auth
                        chain.doFilter(req, resp)
                    }
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

    private fun unauthenticated(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        SecurityContextHolder.clearContext()
        chain.doFilter(req, resp)
    }
}