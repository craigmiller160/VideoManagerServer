package io.craigmiller160.videomanagerserver.dto

enum class SortBy(val orderByClause: String) {
    NAME ("vf.displayName, vf.fileName"),
    VIEW_COUNT ("vf.view_count"),
    LAST_VIEWED ("vf.last_viewed"),
    LAST_MODIFIED ("vf.last_modified")
}