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

package io.craigmiller160.videomanagerserver.file

import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.entity.VideoFile
import io.craigmiller160.videomanagerserver.exception.InvalidSettingException
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import io.craigmiller160.videomanagerserver.service.settings.SettingsService
import io.craigmiller160.videomanagerserver.util.ensureTrailingSlash
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FileScanner
@Autowired
constructor(
  private val videoConfig: VideoConfiguration,
  private val videoFileRepo: VideoFileRepository,
  private val settingsService: SettingsService
) {

  private val logger = LoggerFactory.getLogger(FileScanner::class.java)

  fun scanForFiles(done: (Boolean) -> Unit = {}): Job {
    val settings = settingsService.getOrCreateSettings()
    if (settings.rootDir.isEmpty()) {
      throw InvalidSettingException("No root directory is set")
    }

    val filePathRoot = ensureTrailingSlash(settings.rootDir)
    val fileExts = videoConfig.splitFileExts()
    val scanTimestamp = LocalDateTime.now()

    return GlobalScope.launch(Dispatchers.IO) {
      try {
        logger.info("Starting scan of directory: $filePathRoot")
        Files.walk(Paths.get(filePathRoot))
          .filter { p -> !p.toFile().isDirectory }
          .filter { p -> !p.toFile().isHidden }
          .filter { p -> fileExts.contains(p.toFile().extension) }
          .forEach { p ->
            val name = p.toString().replace(Regex("^$filePathRoot"), "")
            logger.trace("Scanning file: $name")
            val lastModifiedTime = Files.getLastModifiedTime(p)
            val lastModified = LocalDateTime.ofInstant(lastModifiedTime.toInstant(), ZoneOffset.UTC)
            val videoFile =
              videoFileRepo.findByFileName(name)
                ?: VideoFile(fileName = name, fileAdded = LocalDateTime.now())
            videoFile.lastModified = lastModified
            videoFile.lastScanTimestamp = scanTimestamp
            videoFile.active = true
            if (videoFile.fileAdded == null) {
              videoFile.fileAdded = lastModified
            }
            if (videoFile.displayName == "") videoFile.displayName = videoFile.fileName
            videoFileRepo.save(videoFile)
          }
        videoFileRepo.setOldFilesInactive(scanTimestamp)
        logger.info("Scan completed successfully")
        done(true)
      } catch (ex: Exception) {
        logger.error("Error scanning for files", ex)
        done(false)
      }
    }
  }
}
