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

package io.craigmiller160.videomanagerserver.service.videofile

import io.craigmiller160.videomanagerserver.dto.StarPayload
import io.craigmiller160.videomanagerserver.entity.Star
import io.craigmiller160.videomanagerserver.mapper.VMModelMapper
import io.craigmiller160.videomanagerserver.repository.FileStarRepository
import io.craigmiller160.videomanagerserver.repository.StarRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class StarService (
        private val starRepo: StarRepository,
        private val fileStarRepo: FileStarRepository,
        private val modelMapper: VMModelMapper
) {

    fun getAllStars(): List<StarPayload> {
        val sort = Sort.by(
                Sort.Order(Sort.Direction.ASC, "starName", Sort.NullHandling.NULLS_LAST)
        )
        return starRepo.findAll(sort)
                .map { star -> modelMapper.map(star, StarPayload::class.java) }
    }

    fun getStar(starId: Long): StarPayload? {
        return starRepo.findById(starId)
                .map { star -> modelMapper.map(star, StarPayload::class.java) }
                .orElse(null)
    }

    fun addStar(payload: StarPayload): StarPayload {
        val star = modelMapper.map(payload, Star::class.java)
        val savedStar = starRepo.save(star)
        return modelMapper.map(savedStar, StarPayload::class.java)
    }

    fun updateStar(starId: Long, payload: StarPayload): StarPayload? {
        return starRepo.findById(starId)
                .map { _ ->
                    val star = modelMapper.map(payload, Star::class.java)
                    star.starId = starId
                    val updatedStar = starRepo.save(star)
                    modelMapper.map(updatedStar, StarPayload::class.java)
                }
                .orElse(null)
    }

    fun deleteStar(starId: Long): StarPayload? {
        val starOptional = starRepo.findById(starId)
        fileStarRepo.deleteAllByStarId(starId)
        starRepo.deleteById(starId)
        return starOptional
                .map { star -> modelMapper.map(star, StarPayload::class.java) }
                .orElse(null)
    }

}
