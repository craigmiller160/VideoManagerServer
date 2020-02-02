package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.entity.FileSeries
import io.craigmiller160.videomanagerserver.entity.id.FileSeriesId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface FileSeriesRepository : JpaRepository<FileSeries,FileSeriesId> {

    @Modifying
    @Transactional
    fun deleteAllBySeriesId(seriesId: Long): Int

    @Modifying
    @Transactional
    fun deleteAllByFileId(fileId: Long): Int

}
