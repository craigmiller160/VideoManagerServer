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
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.io.path.extension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
  private val fileExts = videoConfig.splitFileExts()
  private val converterFileExts = videoConfig.splitConverterFileExts()

  fun scanForFiles(done: (Boolean) -> Unit = {}): Job {
    val settings = settingsService.getOrCreateSettings()
    if (settings.rootDir.isEmpty()) {
      throw InvalidSettingException("No root directory is set")
    }

    val filePathRoot = ensureTrailingSlash(settings.rootDir)

    val scanTimestamp = LocalDateTime.now()

    return GlobalScope.launch(Dispatchers.IO) {
      try {
        logger.info("Starting scan of directory: $filePathRoot")
        val allPossibleFiles =
          Files.walk(Paths.get(filePathRoot))
            .filter { p -> !p.toFile().isDirectory }
            .filter { p -> !p.toFile().isHidden }
            .toList()

        val filesMap = allPossibleFiles.groupBy { getFileType(it) }

        val deferredFileConsumations =
          (filesMap[FileType.CONSUME] ?: listOf()).map { file ->
            async { consumeFile(filePathRoot, scanTimestamp, file) }
          }

        val deferredFileConversions =
          (filesMap[FileType.CONVERT] ?: listOf()).map { file -> async { convertFile(file) } }

        deferredFileConsumations.awaitAll()
        deferredFileConversions.awaitAll()

        videoFileRepo.setOldFilesInactive(scanTimestamp)
        logger.info("Scan completed successfully")
        done(true)
      } catch (ex: Exception) {
        logger.error("Error scanning for files", ex)
        done(false)
      }
    }
  }

  private suspend fun consumeFile(filePathRoot: String, scanTimestamp: LocalDateTime, file: Path) =
    withContext(Dispatchers.IO) {
      val name = file.toString().replace(Regex("^$filePathRoot"), "")
      logger.trace("Scanning file: $name")
      val lastModifiedTime = Files.getLastModifiedTime(file)
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

  private fun getFileType(file: Path): FileType {
    if (fileExts.contains(file.extension)) {
      return FileType.CONSUME
    }

    if (converterFileExts.contains(file.extension)) {
      return FileType.CONVERT
    }

    return FileType.IGNORE
  }

  private suspend fun convertFile(file: Path) =
    withContext(Dispatchers.IO) {
      // TODO finish this
    }
}

private enum class FileType {
  CONSUME,
  CONVERT,
  IGNORE
}
