package io.craigmiller160.videomanagerserver.dto

import io.craigmiller160.videomanagerserver.entity.id.FileSeriesId
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@Table(name = "file_series")
@IdClass(FileSeriesId::class)
data class FileSeries (
        @Id
        @Column(name = "file_id")
        var fileId: Long = 0,
        @Id
        @Column(name = "series_id")
        var seriesId: Long = 0
)
