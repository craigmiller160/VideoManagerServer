package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.Category
import io.craigmiller160.videomanagerserver.service.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categories")
class CategoryController @Autowired constructor(
        private val categoryService: CategoryService
) {

    @GetMapping
    fun getAllCategories(): ResponseEntity<List<Category>> {
        val categories = categoryService.getAllCategories()
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.ok(categories)
    }

    @GetMapping("/{categoryId}")
    fun getCategory(@PathVariable categoryId: Long): ResponseEntity<Category> {
        return okOrNoContent(categoryService.getCategory(categoryId))
    }

    @PostMapping()
    fun addCategory(@RequestBody category: Category): ResponseEntity<Category> {
        return ResponseEntity.ok(categoryService.addCategory(category))
    }

    @PutMapping("/{categoryId}")
    fun updateCategory(@PathVariable categoryId: Long, @RequestBody category: Category): ResponseEntity<Category> {
        return okOrNoContent(categoryService.updateCategory(categoryId, category))
    }

    @DeleteMapping("/{categoryId}")
    fun deleteCategory(@PathVariable categoryId: Long): ResponseEntity<Category> {
        return okOrNoContent(categoryService.deleteCategory(categoryId))
    }

}