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

import io.craigmiller160.videomanagerserver.security.VideoAuthenticationFilterConfigurer
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.validation.annotation.Validated

@Configuration
@Validated
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfig(
  private val videoAuthenticationFilterConfigurer: VideoAuthenticationFilterConfigurer
) : KeycloakWebSecurityConfigurerAdapter() {

  override fun configure(http: HttpSecurity) {
    http
      .csrf()
      .disable()
      .authorizeRequests()
      .antMatchers("/actuator/health")
      .permitAll()
      .anyRequest()
      .hasRole("access")
      .and()
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
      .and()
      .apply(videoAuthenticationFilterConfigurer)
      .and()
      .requiresChannel()
      .anyRequest()
      .requiresSecure()
  }

  override fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy =
    NullAuthenticatedSessionStrategy()
}
