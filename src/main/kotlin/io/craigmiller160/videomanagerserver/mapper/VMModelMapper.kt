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

package io.craigmiller160.videomanagerserver.mapper

import org.modelmapper.ModelMapper

open class VMModelMapper {

  private val mapper = ModelMapper()
  val existingPropHandlers:
    MutableMap<ExistingPropHandlerKey<out Any, out Any>, ExistingPropHandler<out Any, out Any>> =
    mutableMapOf()

  fun <D : Any> map(source: Any, destType: Class<D>): D {
    return mapper.map(source, destType)
  }

  fun <S : Any, D : Any> mapFromExisting(source: S, existing: D): D {
    val destType = existing::class.java
    val destination = map(source, destType)
    val key = ExistingPropHandlerKey(source::class.java, destType)
    val handler = existingPropHandlers[key]
    handler?.let {
      (handler as ExistingPropHandler<S, D>).handleExisting(source, existing, destination)
    }
    return destination
  }
}
