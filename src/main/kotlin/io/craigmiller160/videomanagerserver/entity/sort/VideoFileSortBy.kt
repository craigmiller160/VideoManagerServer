package io.craigmiller160.videomanagerserver.entity.sort

enum class VideoFileSortBy(vararg val orderByClause: String) {
    NAME ("vf.displayName", "vf.fileName"),
    VIEW_COUNT ("vf.viewCount"),
    LAST_VIEWED ("vf.lastViewed"),
    LAST_MODIFIED ("vf.lastModified"),
    FILE_ADDED ("vf.fileAdded")
}
