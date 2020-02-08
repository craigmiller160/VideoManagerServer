package io.craigmiller160.videomanagerserver.config

import io.craigmiller160.videomanagerserver.dto.VideoFilePayload
import io.craigmiller160.videomanagerserver.entity.VideoFile
import org.modelmapper.AbstractConverter
import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ModelMapperConfig {

    @Bean
    fun modelMapper(): ModelMapper {
        val mapper = ModelMapper()
        mapper.addConverter(videoFileToVideoFilePayloadConverter())
        mapper.addConverter(videoFilePayloadToVideoFileConverter())
        return mapper
    }

    private fun videoFileToVideoFilePayloadConverter() =
            object : AbstractConverter<VideoFile,VideoFilePayload>() {

                private val modelMapper = ModelMapper()

                override fun convert(videoFile: VideoFile): VideoFilePayload {
                    return modelMapper.map(videoFile, VideoFilePayload::class.java)
                }
            }

    private fun videoFilePayloadToVideoFileConverter() =
            object : AbstractConverter<VideoFilePayload,VideoFile>() {

                private val modelMapper = ModelMapper()

                override fun convert(videoFilePayload: VideoFilePayload): VideoFile {
                    return modelMapper.map(videoFilePayload, VideoFile::class.java)
                }
            }

}
