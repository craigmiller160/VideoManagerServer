package io.craigmiller160.videomanagerserver.dto

data class CategoryPayload (
        var categoryId: Long = 0,
        var categoryName: String = "",
        var hidden: Boolean = false
)