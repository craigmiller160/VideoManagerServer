package io.craigmiller160.videomanagerserver.security

import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenProvider
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenValidationStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationFilter (
        private val jwtTokenProvider: TokenProvider,
        private val videoTokenProvider: TokenProvider
) : OncePerRequestFilter() {

    companion object {
        val VIDEO_URI = Regex("""^\/video-files\/play\/\d{1,10}""")
    }

    public override fun doFilterInternal(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        if (VIDEO_URI.matches(req.servletPath)) {
            validateToken(req, resp, chain, videoTokenProvider)
        }
        else {
            validateToken(req, resp, chain, jwtTokenProvider)
        }
    }

    private fun validateToken(req: HttpServletRequest, resp: HttpServletResponse,
                              chain: FilterChain, tokenProvider: TokenProvider) {
        val token = tokenProvider.resolveToken(req)
        token?.let {
            try {
                when (tokenProvider.validateToken(token)) {
                    TokenValidationStatus.VALID -> {
                        val auth = tokenProvider.createAuthentication(token)
                        SecurityContextHolder.getContext().authentication = auth
                        chain.doFilter(req, resp)
                    }
                    else -> unauthenticated(req, resp, chain)
                }
            }
            catch (ex: Exception) {
                logger.error("Error handling token", ex)
                unauthenticated(req, resp, chain)
            }
        } ?: unauthenticated(req, resp, chain)
    }

    private fun unauthenticated(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        logger.error("Attempted unauthenticated access") // TODO make this better and more robust
        SecurityContextHolder.clearContext()
        chain.doFilter(req, resp)
    }
}
