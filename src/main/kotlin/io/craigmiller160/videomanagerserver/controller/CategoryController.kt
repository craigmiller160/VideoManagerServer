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

package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.CategoryPayload
import io.craigmiller160.videomanagerserver.security.ROLE_EDIT
import io.craigmiller160.videomanagerserver.service.videofile.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
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
    fun getAllCategories(): ResponseEntity<List<CategoryPayload>> {
        val categories = categoryService.getAllCategories()
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.ok(categories)
    }

    @GetMapping("/{categoryId}")
    fun getCategory(@PathVariable categoryId: Long): ResponseEntity<CategoryPayload> {
        return okOrNoContent(categoryService.getCategory(categoryId))
    }

    @Secured(ROLE_EDIT)
    @PostMapping
    fun addCategory(@RequestBody category: CategoryPayload): ResponseEntity<CategoryPayload> {
        return ResponseEntity.ok(categoryService.addCategory(category))
    }

    @Secured(ROLE_EDIT)
    @PutMapping("/{categoryId}")
    fun updateCategory(@PathVariable categoryId: Long, @RequestBody category: CategoryPayload): ResponseEntity<CategoryPayload> {
        return okOrNoContent(categoryService.updateCategory(categoryId, category))
    }

    @Secured(ROLE_EDIT)
    @DeleteMapping("/{categoryId}")
    fun deleteCategory(@PathVariable categoryId: Long): ResponseEntity<CategoryPayload> {
        return okOrNoContent(categoryService.deleteCategory(categoryId))
    }

}
