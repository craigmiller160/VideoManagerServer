package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.VideoFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import javax.transaction.Transactional

@Repository
interface VideoFileRepository : PagingAndSortingRepository<VideoFile,Long>, JpaRepository<VideoFile,Long> {

    fun findByFileName(fileName: String): VideoFile?

    @Query("DELETE FROM VideoFile WHERE lastScanTimestamp IS NULL OR lastScanTimestamp < :scanTimestamp")
    @Modifying
    @Transactional
    fun deleteOldFiles(scanTimestamp: LocalDateTime): Int

    @Query("UPDATE VideoFile vf SET vf.active = false WHERE vf.lastScanTimestamp IS NULL OR vf.lastScanTimestamp < :scanTimestamp")
    @Modifying
    @Transactional
    fun setOldFilesInactive(scanTimestamp: LocalDateTime): Int



}
