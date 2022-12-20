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

package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.StarPayload
import io.craigmiller160.videomanagerserver.security.ROLE_EDIT
import io.craigmiller160.videomanagerserver.service.videofile.StarService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stars")
class StarController @Autowired constructor(private val starService: StarService) {

  @GetMapping
  fun getAllStars(): ResponseEntity<List<StarPayload>> {
    val stars = starService.getAllStars()
    if (stars.isEmpty()) {
      return ResponseEntity.noContent().build()
    }
    return ResponseEntity.ok(stars)
  }

  @GetMapping("/{starId}")
  fun getStar(@PathVariable starId: Long): ResponseEntity<StarPayload> {
    return okOrNoContent(starService.getStar(starId))
  }

  @Secured(ROLE_EDIT)
  @PostMapping
  fun addStar(@RequestBody star: StarPayload): ResponseEntity<StarPayload> {
    return ResponseEntity.ok(starService.addStar(star))
  }

  @Secured(ROLE_EDIT)
  @PutMapping("/{starId}")
  fun updateStar(
    @PathVariable starId: Long,
    @RequestBody star: StarPayload
  ): ResponseEntity<StarPayload> {
    return okOrNoContent(starService.updateStar(starId, star))
  }

  @Secured(ROLE_EDIT)
  @DeleteMapping("/{starId}")
  fun deleteStar(@PathVariable starId: Long): ResponseEntity<StarPayload> {
    return okOrNoContent(starService.deleteStar(starId))
  }
}
