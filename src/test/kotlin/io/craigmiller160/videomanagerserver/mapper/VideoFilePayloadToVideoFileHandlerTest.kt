package io.craigmiller160.videomanagerserver.mapper

import io.craigmiller160.videomanagerserver.dto.VideoFilePayload
import io.craigmiller160.videomanagerserver.entity.VideoFile
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Test
import java.time.LocalDateTime

class VideoFilePayloadToVideoFileHandlerTest {

    private val handler = VideoFilePayloadToVideoFileHandler()

    @Test
    fun test_handleExisting() {
        val source = VideoFilePayload()
        val existing = VideoFile(
                active = true,
                lastScanTimestamp = LocalDateTime.now()
        )
        val destination = VideoFile(
                fileName = "FileName"
        )
        handler.handleExisting(source, existing, destination)
        assertThat(destination, allOf(
                hasProperty("fileName", equalTo(destination.fileName)),
                hasProperty("active", equalTo(existing.active)),
                hasProperty("lastScanTimestamp", equalTo(existing.lastScanTimestamp))
        ))
    }

}