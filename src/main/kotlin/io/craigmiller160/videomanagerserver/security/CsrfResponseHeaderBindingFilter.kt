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

import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Deprecated(message = "Using Tomcat CSRF filter now")
class CsrfResponseHeaderBindingFilter : OncePerRequestFilter() {

    companion object {
        const val CSRF_REQ_ATTR_NAME = "_csrf"
        const val RESPONSE_TOKEN_NAME = "X-CSRF-TOKEN"
    }

    public override fun doFilterInternal(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        val token = req.getAttribute(CSRF_REQ_ATTR_NAME) as CsrfToken?
        token?.let {
            resp.setHeader(RESPONSE_TOKEN_NAME, token.token)
        }
        chain.doFilter(req, resp)
    }

}