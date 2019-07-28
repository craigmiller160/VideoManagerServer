package io.craigmiller160.videomanagerserver.security.jwt

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtTokenFilter (
        private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    public override fun doFilterInternal(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {
        val token = jwtTokenProvider.resolveToken(req)
        token?.let {
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    val auth = jwtTokenProvider.getAuthentication(token)
                    SecurityContextHolder.getContext().authentication = auth
                    chain.doFilter(req, resp)
                    return
                }
            }
            catch (ex: Exception) {
                SecurityContextHolder.clearContext()
                throw ex
            }
        }

        SecurityContextHolder.clearContext()
        chain.doFilter(req, resp)
    }
}