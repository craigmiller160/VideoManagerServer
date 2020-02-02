package io.craigmiller160.videomanagerserver.dto

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

// TODO refactor

@Entity
@Table(name = "categories")
data class Category (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var categoryId: Long = 0,
        var categoryName: String = "",
        @Column(columnDefinition = "boolean default false")
        var hidden: Boolean = false
)