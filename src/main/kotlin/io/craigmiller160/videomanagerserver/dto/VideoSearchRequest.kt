package io.craigmiller160.videomanagerserver.dto

import io.craigmiller160.videomanagerserver.entity.sort.VideoFileSortBy
import org.springframework.data.domain.Sort

data class VideoSearchRequest (
        var searchText: String? = null,
        var seriesId: Long? = null,
        var starId: Long? = null,
        var categoryId: Long? = null,
        var sortBy: VideoFileSortBy = VideoFileSortBy.NAME,
        var sortDir: Sort.Direction = Sort.Direction.ASC,
        var page: Int = 0
) {

    fun hasCriteria() =
            this.seriesId != null || this.starId != null || this.categoryId != null || this.searchText != null
}
