package io.craigmiller160.videomanagerserver.mapper

import io.craigmiller160.videomanagerserver.dto.CategoryPayload
import io.craigmiller160.videomanagerserver.entity.Category

class CategoryPayloadToCategoryHandler() : ExistingPropHandler<CategoryPayload,Category> {

    override val sourceType = CategoryPayload::class.java
    override val destinationType = Category::class.java
    override val key = ExistingPropHandlerKey(sourceType, destinationType)

    override fun handleExisting(source: CategoryPayload, existing: Category, destination: Category) {
        destination.hidden = existing.hidden
    }
}