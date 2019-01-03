package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.Series
import org.springframework.data.repository.CrudRepository

interface SeriesRepository : CrudRepository<Series,Long>