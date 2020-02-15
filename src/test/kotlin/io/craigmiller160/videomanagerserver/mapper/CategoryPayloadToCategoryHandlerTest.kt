package io.craigmiller160.videomanagerserver.mapper

import io.craigmiller160.videomanagerserver.dto.CategoryPayload
import io.craigmiller160.videomanagerserver.entity.Category
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Test

class CategoryPayloadToCategoryHandlerTest {

    private val handler = CategoryPayloadToCategoryHandler()

    @Test
    fun test_handleExisting() {
        val source = CategoryPayload()
        val existing = Category(
                hidden = true
        )
        val destination = Category(
                categoryName = "Category"
        )
        handler.handleExisting(source, existing, destination)
        assertThat(destination, allOf(
                hasProperty("categoryName", equalTo(destination.categoryName)),
                hasProperty("hidden", equalTo(existing.hidden))
        ))
    }

}