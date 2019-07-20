package io.craigmiller160.videomanagerserver.config

import io.craigmiller160.videomanagerserver.jwt.JwtTokenFilterConfigurer
import io.craigmiller160.videomanagerserver.jwt.JwtTokenProvider
import io.craigmiller160.videomanagerserver.security.AuthFailureHandler
import io.craigmiller160.videomanagerserver.security.AuthSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.HttpStatusEntryPoint

@Configuration
class SecurityConfig (
        private val jwtTokenProvider: JwtTokenProvider,
        private val authSuccessHandler: AuthSuccessHandler,
        private val authFailureHandler: AuthFailureHandler
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        // TODO add cors configuration here using spring boot
        // TODO look into access denied handler
        http?.let {
            http.csrf().disable()
                    .authorizeRequests()
                        .anyRequest().authenticated()
                    .and()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .exceptionHandling()
                        .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // TODO create custom entry point
                    .and()
                    .formLogin()
                        .loginPage("/auth/login")
                        .successHandler(authSuccessHandler)
                        .failureHandler(authFailureHandler)

            http.apply(JwtTokenFilterConfigurer(jwtTokenProvider))
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(12)
    }
}