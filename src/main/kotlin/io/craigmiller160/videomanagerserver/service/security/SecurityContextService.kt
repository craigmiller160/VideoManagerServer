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

package io.craigmiller160.videomanagerserver.service.security

import java.util.UUID
import org.keycloak.KeycloakPrincipal
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class SecurityContextService {

  fun getSecurityContext(): SecurityContext = SecurityContextHolder.getContext()

  fun getUserId(): UUID =
    (SecurityContextHolder.getContext().authentication.principal as KeycloakPrincipal<*>).name.let {
      UUID.fromString(it)
    }

  fun getUserName(): String {
    val principal = getSecurityContext().authentication.principal
    if (principal is UserDetails) {
      return principal.username
    }

    return principal.toString()
  }
}
