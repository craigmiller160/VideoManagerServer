package io.craigmiller160.videomanagerserver.dto

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "video_files")
data class VideoFile(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var fileId: Long = 0,
        var fileName: String = "",
        var displayName: String = "",

        @ManyToMany
        @JoinTable(name = "file_categories",
                joinColumns = [JoinColumn(name = "file_id")],
                inverseJoinColumns = [JoinColumn(name = "category_id")])
        var categories: Set<Category> = HashSet(),

        @ManyToMany
        @JoinTable(name = "file_series",
                joinColumns = [JoinColumn(name = "file_id")],
                inverseJoinColumns = [JoinColumn(name = "series_id")])
        var series: Set<Series> = HashSet(),

        @ManyToMany
        @JoinTable(name = "file_stars",
                joinColumns = [JoinColumn(name = "file_id")],
                inverseJoinColumns = [JoinColumn(name = "star_id")])
        var stars: Set<Series> = HashSet()
)