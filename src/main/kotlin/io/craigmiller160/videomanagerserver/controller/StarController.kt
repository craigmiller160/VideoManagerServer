package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.entity.Star
import io.craigmiller160.videomanagerserver.security.ROLE_EDIT
import io.craigmiller160.videomanagerserver.service.StarService
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
class StarController @Autowired constructor(
        private val starService: StarService
) {

    @GetMapping
    fun getAllStars(): ResponseEntity<List<Star>> {
        val stars = starService.getAllStars()
        if (stars.isEmpty()) {
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.ok(stars)
    }

    @GetMapping("/{starId}")
    fun getStar(@PathVariable starId: Long): ResponseEntity<Star> {
        return okOrNoContent(starService.getStar(starId))
    }

    @Secured(ROLE_EDIT)
    @PostMapping
    fun addStar(@RequestBody star: Star): ResponseEntity<Star> {
        return ResponseEntity.ok(starService.addStar(star))
    }

    @Secured(ROLE_EDIT)
    @PutMapping("/{starId}")
    fun updateStar(@PathVariable starId: Long, @RequestBody star: Star): ResponseEntity<Star> {
        return okOrNoContent(starService.updateStar(starId, star))
    }

    @Secured(ROLE_EDIT)
    @DeleteMapping("/{starId}")
    fun deleteStar(@PathVariable starId: Long): ResponseEntity<Star> {
        return okOrNoContent(starService.deleteStar(starId))
    }

}
