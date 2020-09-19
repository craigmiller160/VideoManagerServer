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

package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.entity.VideoFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import javax.transaction.Transactional

@Repository
interface VideoFileRepository : PagingAndSortingRepository<VideoFile,Long>, JpaRepository<VideoFile,Long> {

    fun findByFileName(fileName: String): VideoFile?

    @Query("DELETE FROM VideoFile WHERE lastScanTimestamp IS NULL OR lastScanTimestamp < :scanTimestamp")
    @Modifying
    @Transactional
    fun deleteOldFiles(scanTimestamp: LocalDateTime): Int

    @Query("UPDATE VideoFile vf SET vf.active = false WHERE vf.lastScanTimestamp IS NULL OR vf.lastScanTimestamp < :scanTimestamp")
    @Modifying
    @Transactional
    fun setOldFilesInactive(scanTimestamp: LocalDateTime): Int



}
