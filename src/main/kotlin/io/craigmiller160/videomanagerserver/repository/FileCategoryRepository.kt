package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.entity.FileCategory
import io.craigmiller160.videomanagerserver.entity.id.FileCategoryId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface FileCategoryRepository : JpaRepository<FileCategory,FileCategoryId> {

    @Modifying
    @Transactional
    fun deleteAllByCategoryId(categoryId: Long): Int

    @Modifying
    @Transactional
    fun deleteAllByFileId(fileId: Long): Int

}
