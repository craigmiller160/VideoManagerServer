package io.craigmiller160.videomanagerserver.security

import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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