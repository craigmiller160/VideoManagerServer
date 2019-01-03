package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.VideoFile
import org.springframework.data.repository.CrudRepository

interface VideoFileRepository : CrudRepository<VideoFile,Long>