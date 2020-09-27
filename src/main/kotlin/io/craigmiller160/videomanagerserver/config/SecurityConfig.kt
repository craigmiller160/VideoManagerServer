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

package io.craigmiller160.videomanagerserver.config

import io.craigmiller160.oauth2.security.JwtValidationFilterConfigurer
import io.craigmiller160.videomanagerserver.security.AuthEntryPoint
import io.craigmiller160.videomanagerserver.security.VideoAuthenticationFilterConfigurer
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
        private val videoAuthenticationFilterConfigurer: VideoAuthenticationFilterConfigurer,
        @Value("\${video.security.password.hashRounds}")
        private val hashRounds: Int,
        @Value("\${cors.origins}")
        private val corsOrigins: String,
        private val jwtValidationFilterConfigurer: JwtValidationFilterConfigurer
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http?.let {
            http
                    .csrf().disable()
                    .cors()
                        .configurationSource(corsConfigurationSource())
                    .and()
                    .authorizeRequests()
                        .antMatchers("/actuator/health").permitAll()
                        .antMatchers(*jwtValidationFilterConfigurer.getInsecurePathPatterns()).permitAll()
                        .anyRequest().fullyAuthenticated()
                    .and()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .exceptionHandling()
                        .authenticationEntryPoint(authEntryPoint)
                    .and()
                    .apply(jwtValidationFilterConfigurer)
                    .and()
                    .apply(videoAuthenticationFilterConfigurer)
                    .and()
                    .requiresChannel().anyRequest().requiresSecure()
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