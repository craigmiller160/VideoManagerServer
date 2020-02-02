package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.dto.Category
import io.craigmiller160.videomanagerserver.repository.CategoryRepository
import io.craigmiller160.videomanagerserver.repository.FileCategoryRepository
import io.craigmiller160.videomanagerserver.service.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.Optional
import javax.transaction.Transactional

@Service
@Transactional
class CategoryServiceImpl @Autowired constructor(
        private val categoryRepo: CategoryRepository,
        private val fileCategoryRepo: FileCategoryRepository
) : CategoryService {

    override fun getAllCategories(): List<Category> {
        val sort = Sort.by(
                Sort.Order(Sort.Direction.ASC, "categoryName", Sort.NullHandling.NULLS_LAST)
        )
        return categoryRepo.findAll(sort).toList()
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
        fileCategoryRepo.deleteAllByCategoryId(categoryId)
        categoryRepo.deleteById(categoryId)
        return categoryOptional
    }
}
