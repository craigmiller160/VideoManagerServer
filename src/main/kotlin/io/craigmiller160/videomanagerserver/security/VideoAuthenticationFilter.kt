/*
 *     video-manager-server
 *     Copyright (C) 2020 Craig Miller
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.craigmiller160.videomanagerserver.security

import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenProvider
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenValidationStatus
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class VideoAuthenticationFilter (
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

        chain.doFilter(req, resp)
    }

    private fun validateToken(req: HttpServletRequest, resp: HttpServletResponse,
                              chain: FilterChain, tokenProvider: TokenProvider,
                              params: Map<String,Any> = HashMap()) {
        val token = tokenProvider.resolveToken(req)
        token?.let {
            try {
                val decodedToken = URLDecoder.decode(token, Charsets.UTF_8)
                println("Decoded: $decodedToken")
                val status = tokenProvider.validateToken(decodedToken, params)
                logger.debug("Token Validation Status: $status")
                when (status) {
                    TokenValidationStatus.VALID -> {
                        val auth = tokenProvider.createAuthentication(decodedToken)
                        SecurityContextHolder.getContext().authentication = auth
                    }
                    else -> unauthenticated(req, resp, chain, status)
                }
            }
            catch (ex: Exception) {
                logger.error("Error handling token", ex)
                unauthenticated(req, resp, chain, TokenValidationStatus.VALIDATION_ERROR)
            }
        } ?: unauthenticated(req, resp, chain, TokenValidationStatus.NO_TOKEN)
    }

    private fun unauthenticated(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain, status: TokenValidationStatus) {
        val request = "${req.method} ${getPathUri(req)}"
        logger.error("Attempted unauthenticated access. Request: $request Status: $status")
        SecurityContextHolder.clearContext()
    }
}
