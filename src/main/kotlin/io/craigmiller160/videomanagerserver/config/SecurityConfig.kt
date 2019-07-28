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
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@Validated
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true
)
class SecurityConfig (
        private val authEntryPoint: AuthEntryPoint,
        private val jwtTokenProvider: JwtTokenProvider,
        @Value("\${video.security.password.hashRounds}")
        private val hashRounds: Int,
        @Value("\${cors.origins}")
        private val corsOrigins: String
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http?.let {
            http.csrf().disable()
                    .cors()
                        .configurationSource(corsConfigurationSource())
                    .and()
                    .authorizeRequests()
                        .antMatchers("/auth/login").permitAll()
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

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("https://spring.io") // TODO configure this better
        config.allowedMethods = listOf("GET", "OPTIONS", "POST")
        config.allowedHeaders = listOf("Authorization")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}