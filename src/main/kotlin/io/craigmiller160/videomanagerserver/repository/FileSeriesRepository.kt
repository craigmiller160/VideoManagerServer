package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.FileSeries
import io.craigmiller160.videomanagerserver.dto.id.FileSeriesId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileSeriesRepository : JpaRepository<FileSeries,FileSeriesId>
