package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.FileStar
import io.craigmiller160.videomanagerserver.dto.id.FileStarId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileStarRepository : JpaRepository<FileStar,FileStarId> {

    fun deleteAllByStarId(starId: Long): Int

    fun deleteAllByFileId(fileId: Long): Int

}
