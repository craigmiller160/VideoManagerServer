package io.craigmiller160.videomanagerserver.dto

enum class SortBy(val orderByClause: String) {
    NAME ("vf.displayName"),
    VIEW_COUNT ("vf.viewCount"),
    LAST_VIEWED ("vf.lastViewed"),
    LAST_MODIFIED ("vf.lastModified"),
    FILE_ADDED ("vf.fileAdded")
}