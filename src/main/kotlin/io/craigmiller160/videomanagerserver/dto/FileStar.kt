package io.craigmiller160.videomanagerserver.dto

import io.craigmiller160.videomanagerserver.entity.id.FileStarId
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@Table(name = "file_stars")
@IdClass(FileStarId::class)
data class FileStar (
        @Id
        @Column(name = "file_id")
        var fileId: Long = 0,
        @Id
        @Column(name = "star_id")
        var starId: Long = 0
)
