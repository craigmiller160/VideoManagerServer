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

import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenValidationStatus
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

@ExtendWith(MockitoExtension::class)
class VideoAuthenticationFilterTest {

  companion object {
    private const val VIDEO_PATH = "/api/video-files/play/1"
    private const val JWT_PATH = "/api/categories"
    private const val COOKIE_NAME = "cookie"
  }

  @Mock private lateinit var videoTokenProvider: VideoTokenProvider

  private lateinit var videoAuthenticationFilter: VideoAuthenticationFilter

  @Mock private lateinit var request: HttpServletRequest
  @Mock private lateinit var response: HttpServletResponse
  @Mock private lateinit var chain: FilterChain
  @Mock private lateinit var authentication: Authentication
  @Mock private lateinit var securityContext: SecurityContext

  @BeforeEach
  fun setup() {
    SecurityContextHolder.setContext(securityContext)
    videoAuthenticationFilter = VideoAuthenticationFilter(videoTokenProvider)
  }

  @AfterEach
  fun after() {
    SecurityContextHolder.clearContext()
  }

  private fun setupRequest(path: String) {
    `when`(request.requestURI).thenReturn(path)
    `when`(request.contextPath).thenReturn("/api")
  }

  @Test
  fun test_doFilterInternal_video_valid() {
    val token = "TOKEN"

    val params = mapOf(TokenConstants.PARAM_VIDEO_ID to "1")
    `when`(videoTokenProvider.resolveToken(request)).thenReturn(token)
    `when`(videoTokenProvider.validateToken(token, params)).thenReturn(TokenValidationStatus.VALID)
    `when`(videoTokenProvider.createAuthentication(token)).thenReturn(authentication)
    setupRequest(VIDEO_PATH)

    videoAuthenticationFilter.doFilterInternal(request, response, chain)

    val authArgCaptor = ArgumentCaptor.forClass(Authentication::class.java)
    verify(securityContext, times(1)).authentication = authArgCaptor.capture()
    assertEquals(authentication, authArgCaptor.value)

    verify(chain, times(1)).doFilter(request, response)
  }

  @Test
  fun test_doFilterInternal_video_badSignature() {
    val token = "TOKEN"

    val params = mapOf(TokenConstants.PARAM_VIDEO_ID to "1")
    setupRequest(VIDEO_PATH)
    `when`(videoTokenProvider.resolveToken(request)).thenReturn(token)
    `when`(videoTokenProvider.validateToken(token, params))
      .thenReturn(TokenValidationStatus.BAD_SIGNATURE)

    videoAuthenticationFilter.doFilterInternal(request, response, chain)

    verify(securityContext, times(0)).authentication
    verify(chain, times(1)).doFilter(request, response)
  }

  @Test
  fun test_doFilterInternal_video_exception() {
    val token = "TOKEN"

    val params = mapOf(TokenConstants.PARAM_VIDEO_ID to "1")
    setupRequest(VIDEO_PATH)
    `when`(videoTokenProvider.resolveToken(request)).thenReturn(token)
    `when`(videoTokenProvider.validateToken(token, params))
      .thenThrow(RuntimeException("Hello World"))

    assertThrows<RuntimeException> {
      videoAuthenticationFilter.doFilterInternal(request, response, chain)
    }

    verify(securityContext, times(0)).authentication
    verify(chain, times(0)).doFilter(request, response)
  }

  @Test
  fun test_doFilterInternal_video_expiredToken() {
    val token = "TOKEN"

    val params = mapOf(TokenConstants.PARAM_VIDEO_ID to "1")
    setupRequest(VIDEO_PATH)
    `when`(videoTokenProvider.resolveToken(request)).thenReturn(token)
    `when`(videoTokenProvider.validateToken(token, params))
      .thenReturn(TokenValidationStatus.EXPIRED)

    videoAuthenticationFilter.doFilterInternal(request, response, chain)

    verify(securityContext, times(0)).authentication
    verify(chain, times(1)).doFilter(request, response)
  }

  @Test
  fun test_doFilterInternal_video_noToken() {
    setupRequest(VIDEO_PATH)
    videoAuthenticationFilter.doFilterInternal(request, response, chain)
    verify(securityContext, times(0)).authentication
    verify(chain, times(1)).doFilter(request, response)
  }
}
