package io.craigmiller160.videomanagerserver.config

import io.craigmiller160.videomanagerserver.dto.VideoFilePayload
import io.craigmiller160.videomanagerserver.entity.VideoFile
import io.craigmiller160.videomanagerserver.util.DEFAULT_TIMESTAMP
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.isA
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@SpringBootTest
class ModelMapperConfigIntegrationTest {

    companion object {
        private const val FILE_ID = 1L
        private const val FILE_NAME = "fileName"
        private const val DISPLAY_NAME = "displayName"
        private const val DESCRIPTION = "description"
        private val NOW_TIMESTAMP = LocalDateTime.now()
        private const val ACTIVE = true
        private const val VIEW_COUNT = 10
    }

    @Autowired
    private lateinit var modelMapperConfig: ModelMapperConfig

    @Test
    fun test_convertVideoFileToVideoFilePayload() {
        // TODO add child entities
        val videoFile = VideoFile(
                fileId = FILE_ID,
                fileName = FILE_NAME,
                displayName = DISPLAY_NAME,
                description = DESCRIPTION,
                lastModified = NOW_TIMESTAMP,
                fileAdded = NOW_TIMESTAMP,
                lastViewed = NOW_TIMESTAMP,
                active = ACTIVE,
                lastScanTimestamp = NOW_TIMESTAMP,
                viewCount = VIEW_COUNT
        )
        val modelMapper = modelMapperConfig.modelMapper()
        val result = modelMapper.map(videoFile, VideoFilePayload::class.java)
        assertThat(result, allOf(
                isA(VideoFilePayload::class.java),
                hasProperty("fileId", equalTo(FILE_ID)),
                hasProperty("fileName", equalTo(FILE_NAME)),
                hasProperty("displayName", equalTo(DISPLAY_NAME)),
                hasProperty("description", equalTo(DESCRIPTION)),
                hasProperty("lastModified", equalTo(NOW_TIMESTAMP)),
                hasProperty("fileAdded", equalTo(NOW_TIMESTAMP)),
                hasProperty("lastViewed", equalTo(NOW_TIMESTAMP)),
                not(hasProperty("active")),
                not(hasProperty("lastScanTimestamp")),
                hasProperty("viewCount", equalTo(VIEW_COUNT))
        ))
    }

    @Test
    fun test_convertVideoFilePayloadToVideoFile() {
        // TODO add child entities
        val videoFile = VideoFilePayload(
                fileId = FILE_ID,
                fileName = FILE_NAME,
                displayName = DISPLAY_NAME,
                description = DESCRIPTION,
                lastModified = NOW_TIMESTAMP,
                fileAdded = NOW_TIMESTAMP,
                lastViewed = NOW_TIMESTAMP,
                viewCount = VIEW_COUNT
        )
        val modelMapper = modelMapperConfig.modelMapper()
        val result = modelMapper.map(videoFile, VideoFile::class.java)
        assertThat(result, allOf(
                isA(VideoFile::class.java),
                hasProperty("fileId", equalTo(FILE_ID)),
                hasProperty("fileName", equalTo(FILE_NAME)),
                hasProperty("displayName", equalTo(DISPLAY_NAME)),
                hasProperty("description", equalTo(DESCRIPTION)),
                hasProperty("lastModified", equalTo(NOW_TIMESTAMP)),
                hasProperty("fileAdded", equalTo(NOW_TIMESTAMP)),
                hasProperty("lastViewed", equalTo(NOW_TIMESTAMP)),
                hasProperty("active", equalTo(false)),
                hasProperty("lastScanTimestamp", equalTo(DEFAULT_TIMESTAMP)),
                hasProperty("viewCount", equalTo(VIEW_COUNT))
        ))
    }

}
