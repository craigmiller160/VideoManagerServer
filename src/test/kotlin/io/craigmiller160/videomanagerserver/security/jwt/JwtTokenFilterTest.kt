package io.craigmiller160.videomanagerserver.security.jwt

import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import junit.framework.Assert.assertEquals
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
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
class JwtTokenFilterTest {

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @InjectMocks
    private lateinit var jwtTokenFilter: JwtTokenFilter

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

    @Test
    fun test_doFilterInternal_validToken() {
        val token = "TOKEN"

        `when`(jwtTokenProvider.resolveToken(request))
                .thenReturn(token)
        `when`(jwtTokenProvider.validateToken(token))
                .thenReturn(true)
        `when`(jwtTokenProvider.getAuthentication(token))
                .thenReturn(authentication)

        jwtTokenFilter.doFilterInternal(request, response, chain)

        val authArgCaptor = ArgumentCaptor.forClass(Authentication::class.java)
        verify(securityContext, times(1)).authentication = authArgCaptor.capture()
        assertEquals(authentication, authArgCaptor.value)

        verify(chain, times(1))
                .doFilter(request, response)
    }

    @Test
    fun test_doFilterInternal_noToken() {
        jwtTokenFilter.doFilterInternal(request, response, chain)
        assertThat(securityContext, not(equalTo(SecurityContextHolder.getContext())))
        verify(chain, times(1))
                .doFilter(request, response)
    }

    @Test
    fun test_doFilterInternal_invalidToken() {
        val token = "TOKEN"

        `when`(jwtTokenProvider.resolveToken(request))
                .thenReturn(token)
        `when`(jwtTokenProvider.validateToken(token))
                .thenReturn(false)

        jwtTokenFilter.doFilterInternal(request, response, chain)

        assertThat(securityContext, not(equalTo(SecurityContextHolder.getContext())))
        verify(chain, times(1))
                .doFilter(request, response)
    }

    @Test
    fun test_doFilterInternal_exception() {
        val token = "TOKEN"

        `when`(jwtTokenProvider.resolveToken(request))
                .thenReturn(token)
        `when`(jwtTokenProvider.validateToken(token))
                .thenThrow(RuntimeException("Hello World"))

        try {
            jwtTokenFilter.doFilterInternal(request, response, chain)
        }
        catch (ex: Exception) {
            assertEquals(RuntimeException::class.java, ex.javaClass)
            assertThat(securityContext, not(equalTo(SecurityContextHolder.getContext())))
            return
        }

        throw Exception("Test should have thrown exception")
    }

}