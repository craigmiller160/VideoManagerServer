package io.craigmiller160.videomanagerserver.service.videofile

import io.craigmiller160.videomanagerserver.dto.StarPayload
import io.craigmiller160.videomanagerserver.entity.Star
import io.craigmiller160.videomanagerserver.repository.FileStarRepository
import io.craigmiller160.videomanagerserver.repository.StarRepository
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class StarService (
        private val starRepo: StarRepository,
        private val fileStarRepo: FileStarRepository
) {

    private val modelMapper = ModelMapper()

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