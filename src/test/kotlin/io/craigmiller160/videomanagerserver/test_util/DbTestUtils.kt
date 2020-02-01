package io.craigmiller160.videomanagerserver.test_util

import io.craigmiller160.videomanagerserver.repository.CategoryRepository
import io.craigmiller160.videomanagerserver.repository.FileCategoryRepository
import io.craigmiller160.videomanagerserver.repository.FileSeriesRepository
import io.craigmiller160.videomanagerserver.repository.FileStarRepository
import io.craigmiller160.videomanagerserver.repository.SeriesRepository
import io.craigmiller160.videomanagerserver.repository.StarRepository
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class DbTestUtils {

    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var videoFileRepo: VideoFileRepository
    @Autowired
    private lateinit var categoryRepo: CategoryRepository
    @Autowired
    private lateinit var seriesRepo: SeriesRepository
    @Autowired
    private lateinit var starRepo: StarRepository
    @Autowired
    private lateinit var fileCategoryRepo: FileCategoryRepository
    @Autowired
    private lateinit var fileSeriesRepo: FileSeriesRepository
    @Autowired
    private lateinit var fileStarRepo: FileStarRepository

    fun cleanDb() {
        fileCategoryRepo.deleteAll()
        fileSeriesRepo.deleteAll()
        fileStarRepo.deleteAll()

        categoryRepo.deleteAll()
        starRepo.deleteAll()
        seriesRepo.deleteAll()
        videoFileRepo.deleteAll()

        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeUpdate("ALTER TABLE categories ALTER COLUMN category_id RESTART WITH 1")
                stmt.executeUpdate("ALTER TABLE series ALTER COLUMN series_id RESTART WITH 1")
                stmt.executeUpdate("ALTER TABLE stars ALTER COLUMN star_id RESTART WITH 1")
                stmt.executeUpdate("ALTER TABLE video_files ALTER COLUMN file_id RESTART WITH 1")
            }
            conn.commit()
        }
    }

}
