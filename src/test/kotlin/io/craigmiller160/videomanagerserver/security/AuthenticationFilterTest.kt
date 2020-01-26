package io.craigmiller160.videomanagerserver.security

import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.craigmiller160.videomanagerserver.security.tokenprovider.JwtTokenProvider
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenValidationStatus
import io.craigmiller160.videomanagerserver.security.tokenprovider.VideoTokenProvider
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RunWith(MockitoJUnitRunner::class)
class AuthenticationFilterTest {

    companion object {
        private const val VIDEO_PATH = "/api/video-files/play/1"
        private const val JWT_PATH = "/api/categories"
    }

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    private lateinit var videoTokenProvider: VideoTokenProvider

    @InjectMocks
    private lateinit var authenticationFilter: AuthenticationFilter

    @Mock
    private lateinit var request: HttpServletRequest
    @Mock
    private lateinit var response: HttpServletResponse
    @Mock
    private lateinit var chain: FilterChain
    @Mock
    private lateinit var authentication: Authentication
    @Mock
    private lateinit var securityContext: SecurityContext

    @Before
    fun setup() {
        SecurityContextHolder.setContext(securityContext)
    }

    @After
    fun after() {
        SecurityContextHolder.clearContext()
    }

    private fun setupRequest(path: String) {
        `when`(request.requestURI)
                .thenReturn(path)
        `when`(request.contextPath)
                .thenReturn("/api")
    }

    @Test
    fun test_doFilterInternal_jwt_validToken() {
        val token = "TOKEN"

        val params = HashMap<String,Any>()
        `when`(jwtTokenProvider.resolveToken(request))
                .thenReturn(token)
        `when`(jwtTokenProvider.validateToken(token, params))
                .thenReturn(TokenValidationStatus.VALID)
        `when`(jwtTokenProvider.createAuthentication(token))
                .thenReturn(authentication)
        setupRequest(JWT_PATH)

        authenticationFilter.doFilterInternal(request, response, chain)

        val authArgCaptor = ArgumentCaptor.forClass(Authentication::class.java)
        verify(securityContext, times(1)).authentication = authArgCaptor.capture()
        assertEquals(authentication, authArgCaptor.value)

        verify(chain, times(1))
                .doFilter(request, response)
    }

    @Test
    fun test_doFilterInternal_jwt_noToken() {
        setupRequest(JWT_PATH)
        authenticationFilter.doFilterInternal(request, response, chain)
        assertThat(securityContext, not(equalTo(SecurityContextHolder.getContext())))
        verify(chain, times(1))
                .doFilter(request, response)
    }

    @Test
    fun test_doFilterInternal_jwt_badSignature() {
        val token = "TOKEN"

        val params = HashMap<String,Any>()
        setupRequest(JWT_PATH)
        `when`(jwtTokenProvider.resolveToken(request))
                .thenReturn(token)
        `when`(jwtTokenProvider.validateToken(token, params))
                .thenReturn(TokenValidationStatus.BAD_SIGNATURE)

        authenticationFilter.doFilterInternal(request, response, chain)

        assertThat(securityContext, not(equalTo(SecurityContextHolder.getContext())))
        verify(chain, times(1))
                .doFilter(request, response)
    }

    @Test
    fun test_doFilterInternal_jwt_expiredToken() {
        val token = "TOKEN"

        val params = HashMap<String,Any>()
        setupRequest(JWT_PATH)
        `when`(jwtTokenProvider.resolveToken(request))
                .thenReturn(token)
        `when`(jwtTokenProvider.validateToken(token, params))
                .thenReturn(TokenValidationStatus.EXPIRED)

        authenticationFilter.doFilterInternal(request, response, chain)

        assertThat(securityContext, not(equalTo(SecurityContextHolder.getContext())))
        verify(chain, times(1))
                .doFilter(request, response)
    }

    @Test
    fun test_doFilterInternal_jwt_exception() {
        val token = "TOKEN"

        val params = HashMap<String,Any>()
        setupRequest(JWT_PATH)
        `when`(jwtTokenProvider.resolveToken(request))
                .thenReturn(token)
        `when`(jwtTokenProvider.validateToken(token, params))
                .thenThrow(RuntimeException("Hello World"))

        authenticationFilter.doFilterInternal(request, response, chain)

        assertThat(securityContext, not(equalTo(SecurityContextHolder.getContext())))
        verify(chain, times(1))
                .doFilter(request, response)
    }

    @Test
    fun test_doFilterInternal_video_valid() {
        val token = "TOKEN"

        val params = mapOf(TokenConstants.PARAM_VIDEO_ID to "1")
        `when`(videoTokenProvider.resolveToken(request))
                .thenReturn(token)
        `when`(videoTokenProvider.validateToken(token, params))
                .thenReturn(TokenValidationStatus.VALID)
        `when`(videoTokenProvider.createAuthentication(token))
                .thenReturn(authentication)
        setupRequest(VIDEO_PATH)

        authenticationFilter.doFilterInternal(request, response, chain)

        val authArgCaptor = ArgumentCaptor.forClass(Authentication::class.java)
        verify(securityContext, times(1)).authentication = authArgCaptor.capture()
        assertEquals(authentication, authArgCaptor.value)

        verify(chain, times(1))
                .doFilter(request, response)
    }

    @Test
    fun test_doFilterInternal_video_badSignature() {
        val token = "TOKEN"

        val params = mapOf(TokenConstants.PARAM_VIDEO_ID to "1")
        setupRequest(VIDEO_PATH)
        `when`(videoTokenProvider.resolveToken(request))
                .thenReturn(token)
        `when`(videoTokenProvider.validateToken(token, params))
                .thenReturn(TokenValidationStatus.BAD_SIGNATURE)

        authenticationFilter.doFilterInternal(request, response, chain)

        assertThat(securityContext, not(equalTo(SecurityContextHolder.getContext())))
        verify(chain, times(1))
                .doFilter(request, response)
    }

    @Test
    fun test_doFilterInternal_video_exception() {
        val token = "TOKEN"

        val params = mapOf(TokenConstants.PARAM_VIDEO_ID to "1")
        setupRequest(VIDEO_PATH)
        `when`(videoTokenProvider.resolveToken(request))
                .thenReturn(token)
        `when`(videoTokenProvider.validateToken(token, params))
                .thenThrow(RuntimeException("Hello World"))

        authenticationFilter.doFilterInternal(request, response, chain)

        assertThat(securityContext, not(equalTo(SecurityContextHolder.getContext())))
        verify(chain, times(1))
                .doFilter(request, response)
    }

    @Test
    fun test_doFilterInternal_video_expiredToken() {
        val token = "TOKEN"

        val params = mapOf(TokenConstants.PARAM_VIDEO_ID to "1")
        setupRequest(VIDEO_PATH)
        `when`(videoTokenProvider.resolveToken(request))
                .thenReturn(token)
        `when`(videoTokenProvider.validateToken(token, params))
                .thenReturn(TokenValidationStatus.EXPIRED)

        authenticationFilter.doFilterInternal(request, response, chain)

        assertThat(securityContext, not(equalTo(SecurityContextHolder.getContext())))
        verify(chain, times(1))
                .doFilter(request, response)
    }

    @Test
    fun test_doFilterInternal_video_resourceForbidden() {
        val token = "TOKEN"

        val params = mapOf(TokenConstants.PARAM_VIDEO_ID to "1")
        setupRequest(VIDEO_PATH)
        `when`(videoTokenProvider.resolveToken(request))
                .thenReturn(token)
        `when`(videoTokenProvider.validateToken(token, params))
                .thenReturn(TokenValidationStatus.RESOURCE_FORBIDDEN)

        authenticationFilter.doFilterInternal(request, response, chain)

        assertThat(securityContext, not(equalTo(SecurityContextHolder.getContext())))
        verify(chain, times(1))
                .doFilter(request, response)
    }

    @Test
    fun test_doFilterInternal_video_noToken() {
        setupRequest(VIDEO_PATH)
        authenticationFilter.doFilterInternal(request, response, chain)
        assertThat(securityContext, not(equalTo(SecurityContextHolder.getContext())))
        verify(chain, times(1))
                .doFilter(request, response)
    }

}
