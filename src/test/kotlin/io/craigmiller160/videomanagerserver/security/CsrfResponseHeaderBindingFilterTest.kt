package io.craigmiller160.videomanagerserver.security

import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.craigmiller160.videomanagerserver.security.CsrfResponseHeaderBindingFilter.Companion.CSRF_REQ_ATTR_NAME
import io.craigmiller160.videomanagerserver.security.CsrfResponseHeaderBindingFilter.Companion.RESPONSE_TOKEN_NAME
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.security.web.csrf.CsrfToken
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CsrfResponseHeaderBindingFilterTest {

    private val csrfResponseHeaderBindingFilter = CsrfResponseHeaderBindingFilter()

    @Test
    fun test_doFilterInternal() {
        val req = mock(HttpServletRequest::class.java)
        val resp = mock(HttpServletResponse::class.java)
        val chain = mock(FilterChain::class.java)
        val token = mock(CsrfToken::class.java)

        val tokenString = "ABCDEFG"

        `when`(token.token)
                .thenReturn(tokenString)
        `when`(req.getAttribute(CSRF_REQ_ATTR_NAME))
                .thenReturn(token)

        csrfResponseHeaderBindingFilter.doFilterInternal(req, resp, chain)

        verify(resp, times(1))
                .setHeader(RESPONSE_TOKEN_NAME, tokenString)
        verify(chain, times(1))
                .doFilter(req, resp)
    }
}