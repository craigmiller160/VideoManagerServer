package io.craigmiller160.videomanagerserver.security

import io.craigmiller160.videomanagerserver.security.tokenprovider.JwtTokenProvider
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenProvider
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenValidationStatus
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationFilter (
        private val jwtTokenProvider: JwtTokenProvider,
        private val videoTokenProvider: VideoTokenProvider
) : OncePerRequestFilter() {

    companion object {
        val VIDEO_URI = Regex("""^\/video-files\/play\/\d{1,10}$""")
    }

    private fun getPathUri(req: HttpServletRequest): String {
        return req.requestURI.replace(req.contextPath ?: "", "")
    }

    public override fun doFilterInternal(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        val pathUri = getPathUri(req)
        if (VIDEO_URI.matches(pathUri)) {
            val fileId = pathUri.split("/")[3]
            val params = mapOf(TokenConstants.PARAM_VIDEO_ID to fileId)
            validateToken(req, resp, chain, videoTokenProvider, params)
        }
        else {
            validateToken(req, resp, chain, jwtTokenProvider)
        }
    }

    private fun validateToken(req: HttpServletRequest, resp: HttpServletResponse,
                              chain: FilterChain, tokenProvider: TokenProvider,
                              params: Map<String,Any> = HashMap()) {
        val token = tokenProvider.resolveToken(req)
        println("ValidateToken: $token") // TODO delete this
        token?.let {
            println("Preparing to Validate Token") // TODO delete this
            try {
                val status = tokenProvider.validateToken(token, params)
                logger.debug("Token Validation Status: $status")
                when (status) {
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
