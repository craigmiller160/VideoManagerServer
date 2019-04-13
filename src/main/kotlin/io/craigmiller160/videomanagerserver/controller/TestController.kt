package io.craigmiller160.videomanagerserver.controller

import org.springframework.core.io.UrlResource
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import kotlin.math.min

@RestController
@RequestMapping("/test")
class TestController {

    @GetMapping("/video")
    fun streamVideo(@RequestHeader headers: HttpHeaders): ResponseEntity<ResourceRegion> {
        val path = "/home/craig/Videos/VideoManager/video.mp4"
        val file = File(path)
        val uri = file.toURI()
        val video = UrlResource(uri)
        val region = resourceRegion(video, headers)
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory
                        .getMediaType(video)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region)
    }

    private fun resourceRegion(video: UrlResource, headers: HttpHeaders): ResourceRegion {
        val contentLength = video.contentLength()
        val range = headers.range.firstOrNull()
        return if (range != null) {
            val start = range.getRangeStart(contentLength)
            val end = range.getRangeEnd(contentLength)
            val rangeLength = min(1 * 1024 * 1024, end - start + 1)
            ResourceRegion(video, start, rangeLength)
        }
        else {
            val rangeLength = min(1 * 1024 * 1024, contentLength)
            ResourceRegion(video, 0, rangeLength)
        }
    }

}