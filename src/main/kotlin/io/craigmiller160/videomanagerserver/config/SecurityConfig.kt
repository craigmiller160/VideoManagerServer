package io.craigmiller160.videomanagerserver.config

import io.craigmiller160.videomanagerserver.security.AuthEntryPoint
import io.craigmiller160.videomanagerserver.security.AuthFailureHandler
import io.craigmiller160.videomanagerserver.security.AuthLoginFilter
import io.craigmiller160.videomanagerserver.security.AuthSuccessHandler
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenFilter
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenProvider
import io.craigmiller160.videomanagerserver.security.service.AuthUserDetailsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.validation.annotation.Validated

@Configuration
@Validated
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig (
        private val authSuccessHandler: AuthSuccessHandler,
        private val authFailureHandler: AuthFailureHandler,
        private val authEntryPoint: AuthEntryPoint,
        private val jwtTokenProvider: JwtTokenProvider,
        private val userDetailsService: AuthUserDetailsService,
        @Value("\${video.security.password.hashRounds}")
        private val hashRounds: Int
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.let {
            auth.userDetailsService(userDetailsService)
                    .passwordEncoder(passwordEncoder())
        }
    }

    override fun configure(http: HttpSecurity?) {
        // TODO add cors configuration here using spring boot
        // TODO look into access denied handler
        http?.let {
            http.csrf().disable()
                    .authorizeRequests()
                        .antMatchers("/temp").permitAll()
                        .anyRequest().authenticated()
                    .and()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .exceptionHandling()
                        .authenticationEntryPoint(authEntryPoint)
                    .and()
//                    .formLogin()
//                        .loginProcessingUrl("/auth/login")
//                        .successHandler(authSuccessHandler)
//                        .failureHandler(authFailureHandler)
//                    .and()
//                    .addFilter(jwtTokenFilter)
                    .addFilterBefore(JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
//                    .addFilter(AuthLoginFilter(authenticationManager()))
//                    .addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
//                    .addFilterAt(AuthLoginFilter(authenticationManager()), UsernamePasswordAuthenticationFilter::class.java)
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(hashRounds)
    }
}