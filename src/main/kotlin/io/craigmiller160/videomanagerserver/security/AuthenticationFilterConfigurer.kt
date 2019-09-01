package io.craigmiller160.videomanagerserver.security

import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.stereotype.Component

@Component
class AuthenticationFilterConfigurer (
        private val jwtTokenProvider: JwtTokenProvider
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(http: HttpSecurity?) {
        http?.addFilterBefore(AuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
                ?.addFilterAfter(CsrfResponseHeaderBindingFilter(), CsrfFilter::class.java)
    }
}