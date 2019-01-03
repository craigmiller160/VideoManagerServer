package io.craigmiller160.videomanagerserver.dto

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "video_files")
data class VideoFile(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var fileId: Long = 0,
        var fileName: String = "",
        var displayName: String = ""
)