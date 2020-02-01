package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.FileCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileCategoryRepository : JpaRepository<FileCategory,Void>
