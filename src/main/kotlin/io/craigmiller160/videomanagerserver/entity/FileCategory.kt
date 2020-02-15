package io.craigmiller160.videomanagerserver.entity

import io.craigmiller160.videomanagerserver.entity.id.FileCategoryId
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@Table(name = "file_categories")
@IdClass(FileCategoryId::class)
data class FileCategory (
        @Id
        @Column(name = "file_id")
        var fileId: Long = 0,
        @Id
        @Column(name = "category_id")
        var categoryId: Long = 0
)
