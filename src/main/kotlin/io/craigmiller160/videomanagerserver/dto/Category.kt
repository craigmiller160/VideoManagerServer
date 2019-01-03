package io.craigmiller160.videomanagerserver.dto

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "categories")
data class Category (
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var categoryId: Long = 0,
        var categoryName: String = "",
        var hidden: Boolean = false
)