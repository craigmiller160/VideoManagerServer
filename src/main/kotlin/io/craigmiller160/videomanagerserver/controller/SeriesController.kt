package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.entity.Series
import io.craigmiller160.videomanagerserver.security.ROLE_EDIT
import io.craigmiller160.videomanagerserver.service.SeriesService
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
class SeriesController @Autowired constructor(
        private val seriesService: SeriesService
) {

    @GetMapping
    fun getAllSeries(): ResponseEntity<List<Series>> {
        val series = seriesService.getAllSeries()
        if (series.isEmpty()) {
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.ok(series)
    }

    @GetMapping("/{seriesId}")
    fun getSeries(@PathVariable seriesId: Long): ResponseEntity<Series> {
        return okOrNoContent(seriesService.getSeries(seriesId))
    }

    @Secured(ROLE_EDIT)
    @PostMapping
    fun addSeries(@RequestBody series: Series): ResponseEntity<Series> {
        return ResponseEntity.ok(seriesService.addSeries(series))
    }

    @Secured(ROLE_EDIT)
    @PutMapping("/{seriesId}")
    fun updateSeries(@PathVariable seriesId: Long, @RequestBody series: Series): ResponseEntity<Series> {
        return okOrNoContent(seriesService.updateSeries(seriesId, series))
    }

    @Secured(ROLE_EDIT)
    @DeleteMapping("/{seriesId}")
    fun deleteSeries(@PathVariable seriesId: Long): ResponseEntity<Series> {
        return okOrNoContent(seriesService.deleteSeries(seriesId))
    }

}
