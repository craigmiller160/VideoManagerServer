package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.dto.FileScanStatus
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.dto.VideoSearch
import io.craigmiller160.videomanagerserver.dto.VideoSearchResults
import io.craigmiller160.videomanagerserver.dto.createScanAlreadyRunningStatus
import io.craigmiller160.videomanagerserver.dto.createScanErrorStatus
import io.craigmiller160.videomanagerserver.dto.createScanNotRunningStatus
import io.craigmiller160.videomanagerserver.dto.createScanRunningStatus
import io.craigmiller160.videomanagerserver.exception.InvalidSettingException
import io.craigmiller160.videomanagerserver.file.FileScanner
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import io.craigmiller160.videomanagerserver.service.VideoFileService
import io.craigmiller160.videomanagerserver.service.settings.SettingsService
import io.craigmiller160.videomanagerserver.util.ensureTrailingSlash
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.UrlResource
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDateTime
import java.util.Optional
import java.util.concurrent.atomic.AtomicBoolean
import javax.persistence.EntityManager
import javax.persistence.Query
import javax.transaction.Transactional

@Service
@Transactional
class VideoFileServiceImpl @Autowired constructor(
        private val videoFileRepo: VideoFileRepository,
        private val videoConfig: VideoConfiguration,
        private val fileScanner: FileScanner,
        private val entityManager: EntityManager,
        private val settingsService: SettingsService
): VideoFileService {

    private val fileScanRunning = AtomicBoolean(false)
    private val lastScanSuccess = AtomicBoolean(true)

    private fun getVideoFileSort(sortDirection: Sort.Direction): Sort {
        return Sort.by(
                Sort.Order(sortDirection, "displayName", Sort.NullHandling.NULLS_FIRST),
                Sort.Order(sortDirection, "fileName", Sort.NullHandling.NULLS_FIRST)
        )
    }

    override fun getAllVideoFiles(page: Int, sortDirection: String): List<VideoFile> {
        val sort = getVideoFileSort(Sort.Direction.valueOf(sortDirection))
        val pageable = PageRequest.of(page, videoConfig.apiPageSize, sort)
        return videoFileRepo.findAll(pageable).toList()
    }

    override fun getVideoFile(fileId: Long): Optional<VideoFile> {
        return videoFileRepo.findById(fileId)
    }

    override fun addVideoFile(videoFile: VideoFile): VideoFile {
        return videoFileRepo.save(videoFile)
    }

    override fun updateVideoFile(fileId: Long, videoFile: VideoFile): Optional<VideoFile> {
        videoFile.fileId = fileId
        return videoFileRepo.findById(fileId)
                .map { videoFileRepo.save(videoFile) }
    }

    override fun deleteVideoFile(fileId: Long): Optional<VideoFile> {
        val videoFileOptional = videoFileRepo.findById(fileId)
        videoFileRepo.deleteById(fileId)
        return videoFileOptional
    }

    override fun startVideoFileScan(): FileScanStatus {
        if (fileScanRunning.get()) {
            return createScanAlreadyRunningStatus()
        }
        fileScanRunning.set(true)
        lastScanSuccess.set(true)
        try {
            fileScanner.scanForFiles { result ->
                fileScanRunning.set(false)
                lastScanSuccess.set(result)
            }
        }
        catch (ex: Exception) {
            fileScanRunning.set(false)
            lastScanSuccess.set(false)
            throw ex
        }

        return createScanRunningStatus()
    }

    override fun isVideoFileScanRunning(): FileScanStatus {
        val scanRunning = fileScanRunning.get()
        val lastScanSuccess = lastScanSuccess.get()
        if (scanRunning) {
            return createScanRunningStatus()
        }

        if (lastScanSuccess) {
            return createScanNotRunningStatus()
        }

        return createScanErrorStatus()
    }

    override fun playVideo(fileId: Long): UrlResource {
        val settings = settingsService.getOrCreateSettings()
        if (settings.rootDir.isEmpty()) {
            throw InvalidSettingException("No root directory is set")
        }

        val dbVideoFile = videoFileRepo.findById(fileId)
                .orElseThrow { Exception("Could not find video file in DB by ID: $fileId") }
        val fullPath = "${ensureTrailingSlash(settings.rootDir)}${dbVideoFile.fileName}"
        return UrlResource(File(fullPath).toURI())
    }

    override fun recordNewVideoPlay(fileId: Long) {
        val dbVideoFile = videoFileRepo.findById(fileId)
                .orElseThrow { Exception("Could not find video file in DB by ID: $fileId") }
        dbVideoFile.viewCount++
        dbVideoFile.lastViewed = LocalDateTime.now()
        videoFileRepo.save(dbVideoFile)
    }

    internal fun buildQueryCriteria(search: VideoSearch, useOrderBy: Boolean): String {
        val queryBuilder = StringBuilder()
        search.categoryId?.let {
            queryBuilder.appendln("LEFT JOIN vf.categories ca")
        }
        search.seriesId?.let {
            queryBuilder.appendln("LEFT JOIN vf.series se")
        }
        search.starId?.let {
            queryBuilder.appendln("LEFT JOIN vf.stars st")
        }

        if (search.hasCriteria()) {
            queryBuilder.append("WHERE ")
        }

        var needsAnd = false
        search.searchText?.let {
            queryBuilder.appendln("(LOWER(vf.fileName) LIKE LOWER(:searchText)")
                    .appendln("OR LOWER(vf.displayName) LIKE LOWER(:searchText))")
            needsAnd = true
        }
        search.categoryId?.let {
            if (needsAnd) {
                queryBuilder.append("AND ")
            }
            queryBuilder.appendln("ca.categoryId = :categoryId")
            needsAnd = true
        }
        search.seriesId?.let {
            if (needsAnd) {
                queryBuilder.append("AND ")
            }
            queryBuilder.appendln("se.seriesId = :seriesId")
            needsAnd = true
        }
        search.starId?.let {
            if (needsAnd) {
                queryBuilder.append("AND ")
            }
            queryBuilder.appendln("st.starId = :starId")
        }

        if (useOrderBy) {
            queryBuilder.appendln("ORDER BY ${search.sortBy.orderByClause} ${search.sortDir.toString()}")
        }

        return queryBuilder.toString()
    }

    internal fun addParamsToQuery(search: VideoSearch, query: Query) {
        search.searchText?.let {
            query.setParameter("searchText", "%$it%")
        }
        search.categoryId?.let {
            query.setParameter("categoryId", it)
        }
        search.seriesId?.let {
            query.setParameter("seriesId", it)
        }
        search.starId?.let {
            query.setParameter("starId", it)
        }
    }


    override fun searchForVideos(search: VideoSearch): VideoSearchResults {
        val page = search.page
        val pageSize = videoConfig.apiPageSize

        val searchQueryString = StringBuilder()
                .appendln("SELECT vf FROM VideoFile vf")
                .appendln(buildQueryCriteria(search, true))
                .toString()
        val countQueryString = StringBuilder()
                .appendln("SELECT COUNT(vf) AS video_file_count FROM VideoFile vf")
                .appendln(buildQueryCriteria(search, false))
                .toString()

        val searchQuery = entityManager.createQuery(searchQueryString)
        val countQuery = entityManager.createQuery(countQueryString)
        addParamsToQuery(search, searchQuery)
        addParamsToQuery(search, countQuery)

        val videoList = searchQuery
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .resultList as List<VideoFile>
        val totalCount = countQuery.singleResult as Long

        return VideoSearchResults().apply {
            totalFiles = totalCount
            filesPerPage = pageSize
            currentPage = page
            this.videoList = videoList
        }
    }
}