package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.VideoFile
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.LocalDateTime
import javax.transaction.Transactional

interface VideoFileRepository : PagingAndSortingRepository<VideoFile,Long> {

    companion object {
        private const val SEARCH_CONDITIONS =
                "FROM VideoFile vf " +
                "LEFT JOIN vf.stars st " +
                "LEFT JOIN vf.categories c " +
                "LEFT JOIN vf.series se " +
                "WHERE (:searchText IS NULL OR (" +
                "vf.fileName LIKE :searchText OR vf.displayName LIKE :searchText OR vf.description LIKE :searchText)) " +
                "AND (:seriesId IS NULL OR se.seriesId = :seriesId) " +
                "AND (:starId IS NULL OR st.starId = :starId) " +
                "AND (:categoryId IS NULL OR c.categoryId = :categoryId)"
    }

    fun findByFileName(fileName: String): VideoFile?

    @Query("SELECT vf $SEARCH_CONDITIONS")
    fun searchByValues(searchText: String?, seriesId: Long?, starId: Long?, categoryId: Long?, paging: Pageable): List<VideoFile>


    @Query("SELECT COUNT(vf) $SEARCH_CONDITIONS")
    fun countByValues(searchText: String?, seriesId: Long?, starId: Long?, categoryId: Long?): Long

    @Query("DELETE FROM VideoFile WHERE lastScanTimestamp IS NULL OR lastScanTimestamp < :scanTimestamp")
    @Modifying
    @Transactional
    fun deleteOldFiles(scanTimestamp: LocalDateTime)

}