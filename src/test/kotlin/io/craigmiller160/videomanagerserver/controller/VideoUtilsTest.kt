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

package io.craigmiller160.videomanagerserver.controller

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
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

    `when`(video.contentLength()).thenReturn(contentLength)
    `when`(headers.range).thenReturn(listOf(range))

    val result = resourceRegion(video, headers)
    assertThat(
      result,
      allOf(
        hasProperty("count", equalTo(20L)),
        hasProperty("position", equalTo(0L)),
        hasProperty("resource", equalTo(video))))
  }

  @Test
  fun test_resourceRegion_noRange() {
    val contentLength = 20L
    val video = mock(UrlResource::class.java)
    val headers = mock(HttpHeaders::class.java)

    `when`(video.contentLength()).thenReturn(contentLength)

    val result = resourceRegion(video, headers)
    assertThat(
      result,
      allOf(
        hasProperty("count", equalTo(20L)),
        hasProperty("position", equalTo(0L)),
        hasProperty("resource", equalTo(video))))
  }
}
