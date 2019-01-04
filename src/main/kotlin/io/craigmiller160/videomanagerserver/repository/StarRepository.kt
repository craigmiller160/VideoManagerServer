package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.Star
import org.springframework.data.repository.PagingAndSortingRepository

interface StarRepository : PagingAndSortingRepository<Star,Long>