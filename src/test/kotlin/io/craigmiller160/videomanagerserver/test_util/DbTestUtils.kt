/*
 *     video-manager-server
 *     Copyright (C) 2020 Craig Miller
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
