package io.craigmiller160.videomanagerserver.dto

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "series")
data class Series (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var seriesId: Long = 0,
        var seriesName: String
)