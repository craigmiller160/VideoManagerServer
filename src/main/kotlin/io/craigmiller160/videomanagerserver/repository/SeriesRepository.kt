package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.Series
import org.springframework.data.repository.PagingAndSortingRepository

interface SeriesRepository : PagingAndSortingRepository<Series,Long>