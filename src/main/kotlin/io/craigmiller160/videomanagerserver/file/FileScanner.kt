package io.craigmiller160.videomanagerserver.file

import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import io.craigmiller160.videomanagerserver.util.ensureTrailingSlash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class FileScanner @Autowired constructor(
        private val videoConfig: VideoConfiguration,
        private val videoFileRepo: VideoFileRepository
) {

    private val logger = LoggerFactory.getLogger(FileScanner::class.java)

    fun scanForFiles(done: (Boolean) -> Unit = {}): Job {
        val filePathRoot = ensureTrailingSlash(videoConfig.filePathRoot)
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
                            val videoFile = videoFileRepo.findByFileName(name) ?: VideoFile(fileName = name, fileAdded = LocalDateTime.now())
                            videoFile.lastModified = lastModified
                            videoFile.lastScanTimestamp = scanTimestamp
                            if (videoFile.displayName == "") videoFile.displayName = videoFile.fileName
                            videoFileRepo.save(videoFile)
                        }
                videoFileRepo.deleteOldFiles(scanTimestamp)
                logger.info("Scan completed successfully")
                done(true)
            }
            catch (ex: Exception) {
                logger.error("Error scanning for files", ex)
                done(false)
            }
        }
    }

}