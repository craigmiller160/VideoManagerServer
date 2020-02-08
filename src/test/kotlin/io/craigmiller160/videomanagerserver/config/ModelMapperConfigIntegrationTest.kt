package io.craigmiller160.videomanagerserver.config

import io.craigmiller160.videomanagerserver.dto.CategoryPayload
import io.craigmiller160.videomanagerserver.dto.SeriesPayload
import io.craigmiller160.videomanagerserver.dto.StarPayload
import io.craigmiller160.videomanagerserver.dto.VideoFilePayload
import io.craigmiller160.videomanagerserver.entity.Category
import io.craigmiller160.videomanagerserver.entity.Series
import io.craigmiller160.videomanagerserver.entity.Star
import io.craigmiller160.videomanagerserver.entity.VideoFile
import io.craigmiller160.videomanagerserver.util.DEFAULT_TIMESTAMP
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.contains
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
        private const val CATEGORY_NAME = "category"
        private const val SERIES_NAME = "series"
        private const val STAR_NAME = "star"
    }

    private lateinit var category1: Category
    private lateinit var categoryPayload1: CategoryPayload
    private lateinit var series1: Series
    private lateinit var seriesPayload1: SeriesPayload
    private lateinit var star1: Star
    private lateinit var starPayload1: StarPayload
    private lateinit var categoryList: MutableSet<Category>
    private lateinit var categoryPayloadList: MutableSet<CategoryPayload>
    private lateinit var seriesList: MutableSet<Series>
    private lateinit var seriesPayloadList: MutableSet<SeriesPayload>
    private lateinit var starList: MutableSet<Star>
    private lateinit var starPayloadList: MutableSet<StarPayload>

    @Autowired
    private lateinit var modelMapperConfig: ModelMapperConfig

    @Before
    fun setup() {
        category1 = Category(categoryName = CATEGORY_NAME)
        categoryPayload1 = CategoryPayload(categoryName = CATEGORY_NAME)
        series1 = Series(seriesName = SERIES_NAME)
        seriesPayload1 = SeriesPayload(seriesName = SERIES_NAME)
        star1 = Star(starName = STAR_NAME)
        starPayload1 = StarPayload(starName = STAR_NAME)

        categoryList = mutableSetOf(category1)
        categoryPayloadList = mutableSetOf(categoryPayload1)
        seriesList = mutableSetOf(series1)
        seriesPayloadList = mutableSetOf(seriesPayload1)
        starList = mutableSetOf(star1)
        starPayloadList = mutableSetOf(starPayload1)
    }

    @Test
    fun test_convertVideoFileToVideoFilePayload() {
        // TODO if unnecessary delete this
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
                viewCount = VIEW_COUNT,
                categories = categoryList,
                series = seriesList,
                stars = starList
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
                hasProperty("viewCount", equalTo(VIEW_COUNT)),
                hasProperty("categories", contains<CategoryPayload>(
                        allOf(
                                isA(CategoryPayload::class.java),
                                hasProperty("categoryName", equalTo(CATEGORY_NAME))
                        )
                ))
        ))
    }

    @Test
    fun test_convertVideoFilePayloadToVideoFile() {
        // TODO if unnecessary delete this
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
