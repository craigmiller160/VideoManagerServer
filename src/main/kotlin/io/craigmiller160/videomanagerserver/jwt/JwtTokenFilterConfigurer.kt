package io.craigmiller160.videomanagerserver.jwt

import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class JwtTokenFilterConfigurer (
        private val jwtTokenProvider: JwtTokenProvider
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(http: HttpSecurity?) {
        http?.let {
            val jwtFilter = JwtTokenFilter(jwtTokenProvider)
            http.addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        }
    }
}