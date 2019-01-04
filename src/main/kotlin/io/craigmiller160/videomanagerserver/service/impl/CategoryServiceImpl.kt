package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.dto.Category
import io.craigmiller160.videomanagerserver.repository.CategoryRepository
import io.craigmiller160.videomanagerserver.service.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class CategoryServiceImpl @Autowired constructor(
        private val categoryRepo: CategoryRepository
) : CategoryService {

    override fun getAllCategories(): Set<Category> {
        return categoryRepo.findAll().toSet()
    }

    override fun getCategory(categoryId: Long): Optional<Category> {
        return categoryRepo.findById(categoryId)
    }

    override fun addCategory(category: Category): Category {
        return categoryRepo.save(category)
    }

    override fun updateCategory(categoryId: Long, category: Category): Optional<Category> {
        category.categoryId = categoryId
        return categoryRepo.findById(categoryId)
                .map { categoryRepo.save(category) }
    }

    override fun deleteCategory(categoryId: Long): Optional<Category> {
        val categoryOptional = categoryRepo.findById(categoryId)
        categoryRepo.deleteById(categoryId)
        return categoryOptional
    }
}