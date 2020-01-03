package io.craigmiller160.videomanagerserver.dto

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "settings")
data class Settings (
        @Id
        var settingsId: Long = 0,
        var rootDir: String = ""
)