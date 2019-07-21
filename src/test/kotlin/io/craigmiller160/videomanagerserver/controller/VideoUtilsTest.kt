package io.craigmiller160.videomanagerserver.controller

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRange

@RunWith(MockitoJUnitRunner::class)
class VideoUtilsTest {

    @Test
    fun test_resourceRegion() {
        val contentLength = 20L
        val video = mock(UrlResource::class.java)
        val headers = mock(HttpHeaders::class.java)
        val range = mock(HttpRange::class.java)

        `when`(video.contentLength())
                .thenReturn(contentLength)
        `when`(headers.range)
                .thenReturn(listOf(range))
        `when`(range.getRangeStart(contentLength))
                .thenReturn(1)
        `when`(range.getRangeEnd(contentLength))
                .thenReturn(2)

        val result = resourceRegion(video, headers)
        assertThat(result, allOf(
                hasProperty("count", equalTo(2L)),
                hasProperty("position", equalTo(1L)),
                hasProperty("resource", equalTo(video))
        ))
    }

    @Test
    fun test_resourceRegion_noRange() {
        val contentLength = 20L
        val video = mock(UrlResource::class.java)
        val headers = mock(HttpHeaders::class.java)

        `when`(video.contentLength())
                .thenReturn(contentLength)

        val result = resourceRegion(video, headers)
        assertThat(result, allOf(
                hasProperty("count", equalTo(20L)),
                hasProperty("position", equalTo(0L)),
                hasProperty("resource", equalTo(video))
        ))
    }

}