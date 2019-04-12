package io.craigmiller160.videomanagerserver.controller

import org.springframework.core.io.UrlResource
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.HttpHeaders
import kotlin.math.min

fun resourceRegion(video: UrlResource, headers: HttpHeaders): ResourceRegion {
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