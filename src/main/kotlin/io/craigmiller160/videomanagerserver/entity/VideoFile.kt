package io.craigmiller160.videomanagerserver.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.craigmiller160.videomanagerserver.entity.Category
import io.craigmiller160.videomanagerserver.entity.Series
import io.craigmiller160.videomanagerserver.entity.Star
import io.craigmiller160.videomanagerserver.util.DEFAULT_TIMESTAMP
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

// TODO refactor

@Entity
@Table(name = "video_files")
data class VideoFile(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var fileId: Long = 0,

        @Column(unique = true)
        var fileName: String = "",

        var displayName: String = "",
        @Column(columnDefinition="TEXT")
        var description: String = "",
        var lastModified: LocalDateTime = DEFAULT_TIMESTAMP,
        var fileAdded: LocalDateTime? = null,
        var lastViewed: LocalDateTime? = null,
        var active: Boolean = false,

        @JsonIgnore
        var lastScanTimestamp: LocalDateTime = DEFAULT_TIMESTAMP,
        @Column(columnDefinition = "int default 0")
        var viewCount: Int = 0,

        @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
        @JoinTable(name = "file_categories",
                joinColumns = [JoinColumn(name = "file_id")],
                inverseJoinColumns = [JoinColumn(name = "category_id")])
        var categories: MutableSet<Category> = HashSet(),

        @ManyToMany (fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
        @JoinTable(name = "file_series",
                joinColumns = [JoinColumn(name = "file_id")],
                inverseJoinColumns = [JoinColumn(name = "series_id")])
        var series: MutableSet<Series> = HashSet(),

        @ManyToMany (fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
        @JoinTable(name = "file_stars",
                joinColumns = [JoinColumn(name = "file_id")],
                inverseJoinColumns = [JoinColumn(name = "star_id")])
        var stars: MutableSet<Star> = HashSet()
)
