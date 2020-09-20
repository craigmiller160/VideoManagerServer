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
import io.craigmiller160.videomanagerserver.security.CsrfResponseHeaderBindingFilter.Companion.CSRF_REQ_ATTR_NAME
import io.craigmiller160.videomanagerserver.security.CsrfResponseHeaderBindingFilter.Companion.RESPONSE_TOKEN_NAME
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.security.web.csrf.CsrfToken
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Deprecated("The class this is testing is deprecated")
class CsrfResponseHeaderBindingFilterTest {

    private val csrfResponseHeaderBindingFilter = CsrfResponseHeaderBindingFilter()

    @Test
    @Ignore
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
