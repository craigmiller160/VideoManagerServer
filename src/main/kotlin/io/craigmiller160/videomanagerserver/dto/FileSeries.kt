package io.craigmiller160.videomanagerserver.dto

import io.craigmiller160.videomanagerserver.dto.id.FileSeriesId
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@Table(name = "file_series")
@IdClass(FileSeriesId::class)
data class FileSeries (
        @Id
        var fileId: Long = 0,
        @Id
        var seriesId: Long = 0
)
