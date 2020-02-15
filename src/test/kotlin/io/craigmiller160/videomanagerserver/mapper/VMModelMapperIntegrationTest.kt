package io.craigmiller160.videomanagerserver.mapper

import io.craigmiller160.videomanagerserver.dto.CategoryPayload
import io.craigmiller160.videomanagerserver.dto.VideoFilePayload
import io.craigmiller160.videomanagerserver.entity.Category
import io.craigmiller160.videomanagerserver.entity.VideoFile
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.hasProperty
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class VMModelMapperIntegrationTest {

    private lateinit var mapper: VMModelMapper

    @Before
    fun setup() {
        val vfHandler = VideoFilePayloadToVideoFileHandler()
        val catHandler = CategoryPayloadToCategoryHandler()

        mapper = VMModelMapper()
        mapper.existingPropHandlers += vfHandler.key to vfHandler
        mapper.existingPropHandlers += catHandler.key to catHandler
    }

    @Test
    fun test_map() {
        val first = Test1("Hello")
        val result = mapper.map(first, Test2::class.java)
        assertThat(result, allOf(
                isA(Test2::class.java),
                hasProperty("firstName", equalTo(first.firstName))
        ))
    }

    @Test
    fun test_mapExisting_videoFilePayloadVideoFile() {
        val payload = VideoFilePayload(
                fileName = "FileName",
                categories = mutableSetOf(CategoryPayload(categoryName = "Category"))
        )
        val existing = VideoFile(
                fileName = "FileName2",
                categories = mutableSetOf(Category(categoryName = "Category2")),
                active = true,
                lastScanTimestamp = LocalDateTime.now()
        )
        val result = mapper.mapFromExisting(payload, existing)
        assertThat(result, allOf(
                isA(VideoFile::class.java),
                hasProperty("fileName", equalTo(payload.fileName)),
                hasProperty("categories", contains<Category>(
                        hasProperty("categoryName", equalTo(payload.categories.first().categoryName))
                )),
                hasProperty("active", equalTo(existing.active)),
                hasProperty("lastScanTimestamp", equalTo(existing.lastScanTimestamp))
        ))
    }

    @Test
    fun test_mapExisting_categoryPayloadCategory() {
        val payload = CategoryPayload(
                categoryName = "Category"
        )
        val existing = Category(
                categoryName = "Category2",
                hidden = true
        )
        val result = mapper.mapFromExisting(payload, existing)
        assertThat(result, allOf(
                hasProperty("categoryName", equalTo(payload.categoryName)),
                hasProperty("hidden", equalTo(existing.hidden))
        ))
    }

    @Test
    fun test_mapExisting_noHandler() {
        val first = Test1("Hello")
        val second = Test2("World")
        val result = mapper.mapFromExisting(first, second)
        assertThat(result, hasProperty("firstName", equalTo(first.firstName)))
    }

    class Test1 (
        var firstName: String = ""
    )

    class Test2 (
        var firstName: String = ""
    )

}