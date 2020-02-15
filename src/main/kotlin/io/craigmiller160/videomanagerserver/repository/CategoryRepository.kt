package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.entity.Category
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : PagingAndSortingRepository<Category,Long>