package io.craigmiller160.videomanagerserver.dto

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "video_files")
data class VideoFile(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var fileId: Long = 0,
        @Column(unique = true)
        var fileName: String = "",
        var displayName: String = "",
        var description: String = "",
        var lastModified: LocalDateTime = LocalDateTime.MIN,
        @Column(columnDefinition = "int default 0")
        var viewCount: Int = 0,

        @ManyToMany (fetch = FetchType.EAGER)
        @JoinTable(name = "file_categories",
                joinColumns = [JoinColumn(name = "file_id")],
                inverseJoinColumns = [JoinColumn(name = "category_id")])
        var categories: Set<Category> = HashSet(),

        @ManyToMany (fetch = FetchType.EAGER)
        @JoinTable(name = "file_series",
                joinColumns = [JoinColumn(name = "file_id")],
                inverseJoinColumns = [JoinColumn(name = "series_id")])
        var series: Set<Series> = HashSet(),

        @ManyToMany (fetch = FetchType.EAGER)
        @JoinTable(name = "file_stars",
                joinColumns = [JoinColumn(name = "file_id")],
                inverseJoinColumns = [JoinColumn(name = "star_id")])
        var stars: Set<Star> = HashSet()
)