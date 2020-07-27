package io.craigmiller160.videomanagerserver.mapper

import io.craigmiller160.modelmapper.ExistingPropHandler
import io.craigmiller160.modelmapper.ExistingPropHandlerKey
import io.craigmiller160.videomanagerserver.dto.VideoFilePayload
import io.craigmiller160.videomanagerserver.entity.VideoFile

class VideoFilePayloadToVideoFileHandler() : ExistingPropHandler<VideoFilePayload, VideoFile> {

    override val sourceType = VideoFilePayload::class.java
    override val destinationType = VideoFile::class.java
    override val key = ExistingPropHandlerKey(sourceType, destinationType)

    override fun handleExisting(source: VideoFilePayload, existing: VideoFile, destination: VideoFile) {
        destination.active = existing.active
        destination.lastScanTimestamp = existing.lastScanTimestamp
    }
}
