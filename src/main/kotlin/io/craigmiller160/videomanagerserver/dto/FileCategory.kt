package io.craigmiller160.videomanagerserver.dto

import io.craigmiller160.videomanagerserver.dto.id.FileCategoryId
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@Table(name = "file_categories")
@IdClass(FileCategoryId::class)
data class FileCategory (
        @Id
        var fileId: Long = 0,
        @Id
        var categoryId: Long = 0
)
