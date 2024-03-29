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

import io.craigmiller160.videomanagerserver.dto.SeriesPayload
import io.craigmiller160.videomanagerserver.security.ROLE_EDIT
import io.craigmiller160.videomanagerserver.service.videofile.SeriesService
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
@RequestMapping("/series")
class SeriesController @Autowired constructor(private val seriesService: SeriesService) {

  @GetMapping
  fun getAllSeries(): ResponseEntity<List<SeriesPayload>> {
    val series = seriesService.getAllSeries()
    if (series.isEmpty()) {
      return ResponseEntity.noContent().build()
    }
    return ResponseEntity.ok(series)
  }

  @GetMapping("/{seriesId}")
  fun getSeries(@PathVariable seriesId: Long): ResponseEntity<SeriesPayload> {
    return okOrNoContent(seriesService.getSeries(seriesId))
  }

  @Secured(ROLE_EDIT)
  @PostMapping
  fun addSeries(@RequestBody series: SeriesPayload): ResponseEntity<SeriesPayload> {
    return ResponseEntity.ok(seriesService.addSeries(series))
  }

  @Secured(ROLE_EDIT)
  @PutMapping("/{seriesId}")
  fun updateSeries(
    @PathVariable seriesId: Long,
    @RequestBody series: SeriesPayload
  ): ResponseEntity<SeriesPayload> {
    return okOrNoContent(seriesService.updateSeries(seriesId, series))
  }

  @Secured(ROLE_EDIT)
  @DeleteMapping("/{seriesId}")
  fun deleteSeries(@PathVariable seriesId: Long): ResponseEntity<SeriesPayload> {
    return okOrNoContent(seriesService.deleteSeries(seriesId))
  }
}
