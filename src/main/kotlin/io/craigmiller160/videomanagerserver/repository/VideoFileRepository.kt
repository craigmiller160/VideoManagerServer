package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.VideoFile
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

}