package io.craigmiller160.videomanagerserver.dto

data class VideoSearch (
        var searchText: String? = null,
        var seriesId: Long? = null,
        var starId: Long? = null,
        var categoryId: Long? = null
) {

    fun hasCriteria() = this.seriesId != null || this.starId != null || this.categoryId != null || this.searchText != null

    fun isJoinedSearch()  =
            this.seriesId != null || this.starId != null || this.categoryId != null
}