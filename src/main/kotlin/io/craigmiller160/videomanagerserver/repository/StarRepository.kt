package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.entity.Star
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface StarRepository : PagingAndSortingRepository<Star,Long>
