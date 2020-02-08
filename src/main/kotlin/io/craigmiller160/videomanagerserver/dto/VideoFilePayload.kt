package io.craigmiller160.videomanagerserver.dto

import io.craigmiller160.videomanagerserver.util.DEFAULT_TIMESTAMP
import java.time.LocalDateTime

// TODO this does not have all the fields from VideoFile
// TODO lastScanTimestamp and active are not here
data class VideoFilePayload (
        var fileId: Long = 0,
        var fileName: String = "",
        var displayName: String = "",
        var description: String = "",
        var lastModified: LocalDateTime = DEFAULT_TIMESTAMP,
        var fileAdded: LocalDateTime? = null,
        var lastViewed: LocalDateTime? = null,
        var viewCount: Int = 0,
        var categories: MutableSet<CategoryPayload> = HashSet(),
        var series: MutableSet<SeriesPayload> = HashSet(),
        var stars: MutableSet<StarPayload> = HashSet()
)
