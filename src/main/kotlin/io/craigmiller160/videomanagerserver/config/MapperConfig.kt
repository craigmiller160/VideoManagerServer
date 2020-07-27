package io.craigmiller160.videomanagerserver.config

import io.craigmiller160.modelmapper.EnhancedModelMapper
import io.craigmiller160.videomanagerserver.mapper.CategoryPayloadToCategoryHandler
import io.craigmiller160.videomanagerserver.mapper.VideoFilePayloadToVideoFileHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MapperConfig {

    @Bean
    fun modelMapper(): EnhancedModelMapper {
        val videoFilePayloadHandler = VideoFilePayloadToVideoFileHandler()
        val categoryPayloadHandler = CategoryPayloadToCategoryHandler()
        val mapper = EnhancedModelMapper()
        mapper.existingPropHandlers += videoFilePayloadHandler.key to videoFilePayloadHandler
        mapper.existingPropHandlers += categoryPayloadHandler.key to categoryPayloadHandler
        return mapper
    }

}
