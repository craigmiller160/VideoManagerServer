package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.Series
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface SeriesRepository : PagingAndSortingRepository<Series,Long>
