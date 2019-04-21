package io.craigmiller160.videomanagerserver.dto

import org.springframework.data.domain.Sort

data class VideoSearch (
        var searchText: String? = null,
        var seriesId: Long? = null,
        var starId: Long? = null,
        var categoryId: Long? = null,
        var sortBy: SortBy = SortBy.NAME,
        var sortDir: Sort.Direction = Sort.Direction.ASC
) {

    fun hasCriteria() =
            this.seriesId != null || this.starId != null || this.categoryId != null || this.searchText != null
}