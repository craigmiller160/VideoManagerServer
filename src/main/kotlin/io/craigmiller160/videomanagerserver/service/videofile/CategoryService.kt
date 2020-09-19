/*
 *     video-manager-server
 *     Copyright (C) 2020 Craig Miller
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.craigmiller160.videomanagerserver.service.videofile

import io.craigmiller160.videomanagerserver.dto.CategoryPayload
import io.craigmiller160.videomanagerserver.entity.Category
import io.craigmiller160.videomanagerserver.mapper.VMModelMapper
import io.craigmiller160.videomanagerserver.repository.CategoryRepository
import io.craigmiller160.videomanagerserver.repository.FileCategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class CategoryService @Autowired constructor(
        private val categoryRepo: CategoryRepository,
        private val fileCategoryRepo: FileCategoryRepository,
        private val modelMapper: VMModelMapper
) {

    fun getAllCategories(): List<CategoryPayload> {
        val sort = Sort.by(
                Sort.Order(Sort.Direction.ASC, "categoryName", Sort.NullHandling.NULLS_LAST)
        )
        return categoryRepo.findAll(sort)
                .map { category -> modelMapper.map(category, CategoryPayload::class.java) }
    }

    fun getCategory(categoryId: Long): CategoryPayload? {
        return categoryRepo.findById(categoryId)
                .map { category -> modelMapper.map(category, CategoryPayload::class.java) }
                .orElse(null)
    }

    fun addCategory(payload: CategoryPayload): CategoryPayload {
        val category = modelMapper.map(payload, Category::class.java)
        val savedCategory =  categoryRepo.save(category)
        return modelMapper.map(savedCategory, CategoryPayload::class.java)
    }

    fun updateCategory(categoryId: Long, payload: CategoryPayload): CategoryPayload? {
        return categoryRepo.findById(categoryId)
                .map { existingCategory ->
                    val category = modelMapper.mapFromExisting(payload, existingCategory)
                    category.categoryId = categoryId
                    val updatedCategory = categoryRepo.save(category)
                    modelMapper.map(updatedCategory, CategoryPayload::class.java)
                }
                .orElse(null)
    }

    fun deleteCategory(categoryId: Long): CategoryPayload? {
        val categoryOptional = categoryRepo.findById(categoryId)
        fileCategoryRepo.deleteAllByCategoryId(categoryId)
        categoryRepo.deleteById(categoryId)
        return categoryOptional
                .map { category -> modelMapper.map(category, CategoryPayload::class.java) }
                .orElse(null)
    }
}
