package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.VideoFile
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.LocalDateTime
import javax.transaction.Transactional

interface VideoFileRepository : PagingAndSortingRepository<VideoFile,Long> {

    fun findByFileName(fileName: String): VideoFile?

    @Query("DELETE FROM VideoFile WHERE lastScanTimestamp IS NULL OR lastScanTimestamp < :scanTimestamp")
    @Modifying
    @Transactional
    fun deleteOldFiles(scanTimestamp: LocalDateTime)

    @Query("UPDATE VideoFile vf SET vf.active = false WHERE vf.lastScanTimestamp IS NULL OR vf.lastScanTimestamp <= :scanTimestamp")
    @Modifying
    fun setOldFilesInactive(scanTimestamp: LocalDateTime)



}