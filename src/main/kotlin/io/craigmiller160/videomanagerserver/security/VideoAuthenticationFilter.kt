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
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenValidationStatus
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import java.net.URLDecoder
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class VideoAuthenticationFilter(private val videoTokenProvider: VideoTokenProvider) :
  OncePerRequestFilter() {

  companion object {
    val VIDEO_URI = Regex("""^\/video-files\/play\/\d{1,10}$""")
  }

  private fun getPathUri(req: HttpServletRequest): String {
    return req.requestURI.replace(req.contextPath ?: "", "")
  }

  public override fun doFilterInternal(
    req: HttpServletRequest,
    resp: HttpServletResponse,
    chain: FilterChain
  ) {
    val pathUri = getPathUri(req)
    if (VIDEO_URI.matches(pathUri)) {
      val fileId = pathUri.split("/")[3]
      validateToken(req, fileId)
    }

    chain.doFilter(req, resp)
  }

  private fun validateToken(req: HttpServletRequest, videoId: String) {
    val token = videoTokenProvider.resolveToken(req)
    token?.let {
      val decodedToken = URLDecoder.decode(token, Charsets.UTF_8)
      val status =
        videoTokenProvider.validateToken(
          decodedToken, mapOf(TokenConstants.PARAM_VIDEO_ID to videoId))
      logger.debug("Token Validation Status: $status")
      if (TokenValidationStatus.VALID == status) {
        val auth = videoTokenProvider.createAuthentication(decodedToken)
        SecurityContextHolder.getContext().authentication = auth
      }
    }
  }
}
