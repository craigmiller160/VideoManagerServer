package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.entity.FileStar
import io.craigmiller160.videomanagerserver.entity.id.FileStarId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface FileStarRepository : JpaRepository<FileStar,FileStarId> {

    @Modifying
    @Transactional
    fun deleteAllByStarId(starId: Long): Int

    @Modifying
    @Transactional
    fun deleteAllByFileId(fileId: Long): Int

}
