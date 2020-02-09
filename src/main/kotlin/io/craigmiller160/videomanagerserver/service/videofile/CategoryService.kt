package io.craigmiller160.videomanagerserver.service.videofile

import io.craigmiller160.videomanagerserver.dto.CategoryPayload
import io.craigmiller160.videomanagerserver.entity.Category
import io.craigmiller160.videomanagerserver.repository.CategoryRepository
import io.craigmiller160.videomanagerserver.repository.FileCategoryRepository
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class CategoryService @Autowired constructor(
        private val categoryRepo: CategoryRepository,
        private val fileCategoryRepo: FileCategoryRepository
) {

    private val modelMapper = ModelMapper()

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
                    val category = modelMapper.map(payload, Category::class.java)
                    category.categoryId = categoryId
                    category.hidden = existingCategory.hidden // TODO I don't like this brittle approach
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
