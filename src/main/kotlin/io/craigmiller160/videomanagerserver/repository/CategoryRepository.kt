package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.Category
import org.springframework.data.repository.PagingAndSortingRepository

interface CategoryRepository : PagingAndSortingRepository<Category,Long>