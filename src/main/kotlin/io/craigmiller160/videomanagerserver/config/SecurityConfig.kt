package io.craigmiller160.videomanagerserver.config

import io.craigmiller160.videomanagerserver.security.AuthEntryPoint
import io.craigmiller160.videomanagerserver.security.AuthenticationFilterConfigurer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@Validated
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true
)
class SecurityConfig (
        private val authEntryPoint: AuthEntryPoint,
        private val authenticationFilterConfigurer: AuthenticationFilterConfigurer,
        @Value("\${video.security.password.hashRounds}")
        private val hashRounds: Int,
        @Value("\${cors.origins}")
        private val corsOrigins: String
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http?.let {
            http
                    .cors()
                        .configurationSource(corsConfigurationSource())
                    .and()
                    .authorizeRequests()
                        .antMatchers("/auth/login", "/auth/logout", "/auth/refresh").permitAll()
                        .anyRequest().fullyAuthenticated()
                    .and()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .exceptionHandling()
                        .authenticationEntryPoint(authEntryPoint)
                    .and()
                    .apply(authenticationFilterConfigurer)
                    .and()
                    .requiresChannel().anyRequest().requiresSecure()
                    .and()
        }
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder(hashRounds)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOrigins = corsOrigins.split(",").map { it.trim() }.toList()
        config.allowedMethods = listOf(HttpMethod.GET.name, HttpMethod.DELETE.name, HttpMethod.PUT.name, HttpMethod.POST.name, HttpMethod.OPTIONS.name)
        config.allowedHeaders = listOf(HttpHeaders.AUTHORIZATION)
        config.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}