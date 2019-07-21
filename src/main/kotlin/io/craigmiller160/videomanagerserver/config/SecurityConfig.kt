package io.craigmiller160.videomanagerserver.config

import io.craigmiller160.videomanagerserver.security.AuthEntryPoint
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenFilter
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.validation.annotation.Validated

@Configuration
@Validated
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig (
        private val authEntryPoint: AuthEntryPoint,
        private val jwtTokenProvider: JwtTokenProvider,
        @Value("\${video.security.password.hashRounds}")
        private val hashRounds: Int
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        // TODO add cors configuration here using spring boot
        // TODO existing CorsFilter doesn't work
        http?.let {
            http.csrf().disable()
                    .authorizeRequests()
                        .antMatchers("/auth/**").permitAll()
                        .anyRequest().fullyAuthenticated()
                    .and()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .exceptionHandling()
                        .authenticationEntryPoint(authEntryPoint)
                    .and()
                    .addFilterBefore(JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
        }
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder(hashRounds)
    }
}