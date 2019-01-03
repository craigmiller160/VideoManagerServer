package io.craigmiller160.videomanagerserver.dto

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "stars")
data class Star (
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var starId: Long = 0,
        var starName: String = ""
)