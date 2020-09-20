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

package io.craigmiller160.videomanagerserver.security.tokenprovider

import org.springframework.security.core.Authentication
import javax.servlet.http.HttpServletRequest

interface TokenProvider {

    fun createToken(userName: String, params: Map<String,Any> = HashMap()): String

    fun resolveToken(req: HttpServletRequest): String?

    fun validateToken(token: String, params: Map<String,Any> = HashMap()): TokenValidationStatus

    fun createAuthentication(token: String): Authentication

    fun getClaims(token: String): Map<String,Any>

}
