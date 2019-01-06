package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.VideoFile
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

interface VideoFileRepository : PagingAndSortingRepository<VideoFile,Long> {

    @Query("MERGE INTO video_files (file_name) " +
                    "KEY (file_name) " +
                    "VALUES (?1)",
            nativeQuery = true)
    @Modifying
    fun mergeVideoFilesByName(fileName: String)


    @Query("SELECT vf.file_id, vf.file_name, vf.display_name, vf.description " +
            "FROM video_files vf " +
            "LEFT JOIN file_stars fs ON vf.file_id = fs.file_id " +
            "LEFT JOIN file_categories fc ON vf.file_id = fc.file_id " +
            "LEFT JOIN file_series fse ON vf.file_id = fse.file_id " +
            "WHERE (:searchText IS NULL OR " +
            "(vf.file_name LIKE :searchText OR vf.display_name LIKE :searchText OR vf.description LIKE :searchText)) " +
            "AND (:seriesId IS NULL OR fse.series_id = :seriesId) " +
            "AND (:starId IS NULL OR fs.star_id = :starId) " +
            "AND (:categoryId IS NULL OR fc.category_id = :categoryId)",
            nativeQuery = true)
    fun searchByValues(searchText: String?, seriesId: Long?, starId: Long?, categoryId: Long?, paging: Pageable): List<VideoFile>

}