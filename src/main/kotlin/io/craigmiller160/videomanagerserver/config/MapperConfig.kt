package io.craigmiller160.videomanagerserver.config

import io.craigmiller160.videomanagerserver.mapper.CategoryPayloadToCategoryHandler
import io.craigmiller160.videomanagerserver.mapper.VMModelMapper
import io.craigmiller160.videomanagerserver.mapper.VideoFilePayloadToVideoFileHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MapperConfig {

    @Bean
    fun modelMapper(): VMModelMapper {
        val videoFilePayloadHandler = VideoFilePayloadToVideoFileHandler()
        val categoryPayloadHandler = CategoryPayloadToCategoryHandler()
        val mapper = VMModelMapper()
        mapper.existingPropHandlers += videoFilePayloadHandler.key to videoFilePayloadHandler
        mapper.existingPropHandlers += categoryPayloadHandler.key to categoryPayloadHandler
        return mapper
    }

}
