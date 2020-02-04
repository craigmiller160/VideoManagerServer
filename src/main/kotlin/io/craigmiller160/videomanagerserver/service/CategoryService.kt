package io.craigmiller160.videomanagerserver.service

import io.craigmiller160.videomanagerserver.dto.CategoryPayload
import io.craigmiller160.videomanagerserver.entity.Category
import java.util.Optional

interface CategoryService {

    // TODO delete this interface while im doing the refactor

    fun getAllCategories(): List<CategoryPayload>

    fun getCategory(categoryId: Long): CategoryPayload?

    fun addCategory(category: CategoryPayload): CategoryPayload

    fun updateCategory(categoryId: Long, category: CategoryPayload): CategoryPayload?

    fun deleteCategory(categoryId: Long): CategoryPayload?

}