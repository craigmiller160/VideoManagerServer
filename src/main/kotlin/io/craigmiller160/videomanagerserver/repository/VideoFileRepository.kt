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
        // TODO delete all these constants
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

        private const val SEARCH_SELECT_FROM = """
            SELECT vf
            FROM VideoFile vf
        """

        private const val SEARCH_TEXT_COND = """
            WHERE (:searchText IS NULL OR vf.fileName LIKE :searchText OR vf.displayName LIKE :searchText)
        """

        private const val LEFT_JOIN_ENTITIES = """
            LEFT JOIN vf.stars st
            LEFT JOIN vf.categories c
            LEFT JOIN vf.series se
        """

        private const val JOINED_CONDITIONS = """
            AND (:seriesId IS NULL OR se.seriesId = :seriesId)
            AND (:starId IS NULL OR st.starId = :starId)
            AND (:categoryId IS NULL OR c.categoryId = :categoryId)
        """
    }

    fun findByFileName(fileName: String): VideoFile?

    // TODO delete this old one
    @Query("SELECT vf FROM VideoFile vf")
    fun searchByValues(searchText: String?, seriesId: Long?, starId: Long?, categoryId: Long?, paging: Pageable): List<VideoFile>

    @Query("""
        SELECT vf
        FROM VideoFile vf
        WHERE (:searchText IS NULL OR vf.fileName LIKE :searchText OR vf.displayName LIKE :searchText)
    """)
    fun searchByText(searchText: String?, pageable: Pageable): List<VideoFile>

    // This method will produce duplicate records if all 3 of the Long parameters are null
    @Query("""
        SELECT vf
        FROM VideoFile vf
        LEFT JOIN vf.stars st
        LEFT JOIN vf.categories c
        LEFT JOIN vf.series se
        WHERE (:searchText IS NULL OR vf.fileName LIKE :searchText OR vf.displayName LIKE :searchText)
        AND (:seriesId IS NULL OR se.seriesId = :seriesId)
        AND (:starId IS NULL OR st.starId = :starId)
        AND (:categoryId IS NULL OR c.categoryId = :categoryId)
    """)
    fun searchByTextAndEntities(searchText: String?, seriesId: Long?, starId: Long?, categoryId: Long?, paging: Pageable): List<VideoFile>


    // TODO delete this old one
    @Query("SELECT COUNT(vf) $SEARCH_CONDITIONS")
    fun countByValues(searchText: String?, seriesId: Long?, starId: Long?, categoryId: Long?): Long

    @Query("DELETE FROM VideoFile WHERE lastScanTimestamp IS NULL OR lastScanTimestamp < :scanTimestamp")
    @Modifying
    @Transactional
    fun deleteOldFiles(scanTimestamp: LocalDateTime)

    @Query("""
        SELECT vf
        FROM VideoFile vf
        WHERE vf.series.seriesId = 1
    """)
    fun test(): List<VideoFile> // TODO delete this

}