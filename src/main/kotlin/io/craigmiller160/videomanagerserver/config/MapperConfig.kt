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
