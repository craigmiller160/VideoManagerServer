package io.craigmiller160.videomanagerserver.service

import io.craigmiller160.videomanagerserver.dto.Category
import java.util.Optional

interface CategoryService {

    fun getAllCategories(): List<Category>

    fun getCategory(categoryId: Long): Optional<Category>

    fun addCategory(category: Category): Category

    fun updateCategory(categoryId: Long, category: Category): Optional<Category>

    fun deleteCategory(categoryId: Long): Optional<Category>

}