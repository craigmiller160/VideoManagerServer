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

package io.craigmiller160.videomanagerserver.repository.query

import com.nhaarman.mockito_kotlin.verify
import io.craigmiller160.videomanagerserver.entity.sort.VideoFileSortBy
import io.craigmiller160.videomanagerserver.dto.VideoSearchRequest
import io.craigmiller160.videomanagerserver.test_util.isA
import org.junit.Assert.assertEquals
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.data.domain.Sort
import javax.persistence.Query

@RunWith(MockitoJUnitRunner::class)
class SearchQueryBuilderTest {

    @InjectMocks
    private lateinit var searchQueryBuilder: SearchQueryBuilder

    @Test
    fun test_buildQueryCriteria_noCriteria() {
        val expected = """
            ORDER BY vf.displayName ASC, vf.fileName ASC
        """.trimIndent()
        val search = VideoSearchRequest()
        val query = searchQueryBuilder.buildQueryOrderBy(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_descOrder() {
        val expected = """
            ORDER BY vf.displayName DESC, vf.fileName DESC
        """.trimIndent()
        val search = VideoSearchRequest(sortDir = Sort.Direction.DESC)
        val query = searchQueryBuilder.buildQueryOrderBy(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_sortByViewCount() {
        val expected = """
            ORDER BY vf.viewCount ASC, vf.displayName ASC, vf.fileName ASC
        """.trimIndent()
        val search = VideoSearchRequest(sortBy = VideoFileSortBy.VIEW_COUNT)
        val query = searchQueryBuilder.buildQueryOrderBy(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_sortByLastViewed() {
        val expected = """
            ORDER BY vf.lastViewed ASC, vf.displayName ASC, vf.fileName ASC
        """.trimIndent()
        val search = VideoSearchRequest(sortBy = VideoFileSortBy.LAST_VIEWED)
        val query = searchQueryBuilder.buildQueryOrderBy(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_sortByLastModified() {
        val expected = """
            ORDER BY vf.lastModified ASC, vf.displayName ASC, vf.fileName ASC
        """.trimIndent()
        val search = VideoSearchRequest(sortBy = VideoFileSortBy.LAST_MODIFIED)
        val query = searchQueryBuilder.buildQueryOrderBy(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_sortByFileAdded() {
        val expected = """
            ORDER BY vf.fileAdded ASC, vf.displayName ASC, vf.fileName ASC
        """.trimIndent()
        val search = VideoSearchRequest(sortBy = VideoFileSortBy.FILE_ADDED)
        val query = searchQueryBuilder.buildQueryOrderBy(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_allCriteria() {
        val expected = """
            LEFT JOIN vf.categories ca
            LEFT JOIN vf.series se
            LEFT JOIN vf.stars st
            WHERE vf.active = true
            AND (LOWER(vf.fileName) LIKE LOWER(:searchText)
            OR LOWER(vf.displayName) LIKE LOWER(:searchText)
            OR LOWER(vf.description) LIKE LOWER(:searchText))
            AND ca.categoryId = :categoryId
            AND se.seriesId = :seriesId
            AND st.starId = :starId
        """.trimIndent()

        val search = VideoSearchRequest("Hello", 1, 1, 1)
        val query = searchQueryBuilder.buildQueryCriteria(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_onlySearchText() {
        val expected = """
            WHERE vf.active = true
            AND (LOWER(vf.fileName) LIKE LOWER(:searchText)
            OR LOWER(vf.displayName) LIKE LOWER(:searchText)
            OR LOWER(vf.description) LIKE LOWER(:searchText))
        """.trimIndent()
        val search = VideoSearchRequest("Hello")
        val query = searchQueryBuilder.buildQueryCriteria(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_onlyCategory() {
        val expected = """
            LEFT JOIN vf.categories ca
            WHERE vf.active = true
            AND ca.categoryId = :categoryId
        """.trimIndent()
        val search = VideoSearchRequest(categoryId = 1)
        val query = searchQueryBuilder.buildQueryCriteria(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_onlyStar() {
        val expected = """
            LEFT JOIN vf.stars st
            WHERE vf.active = true
            AND st.starId = :starId
        """.trimIndent()
        val search = VideoSearchRequest(starId = 1)
        val query = searchQueryBuilder.buildQueryCriteria(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_onlySeries() {
        val expected = """
            LEFT JOIN vf.series se
            WHERE vf.active = true
            AND se.seriesId = :seriesId
        """.trimIndent()
        val search = VideoSearchRequest(seriesId = 1)
        val query = searchQueryBuilder.buildQueryCriteria(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_addParamsToQuery_noParams() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearchRequest()
        searchQueryBuilder.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, Mockito.times(0))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        Assert.assertEquals(0, argumentCaptor.allValues.size)
    }

    @Test
    fun test_addParamsToQuery_allParams() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearchRequest("Hello", 1, 1, 1)
        searchQueryBuilder.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, Mockito.times(4))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        Assert.assertEquals(4, argumentCaptor.allValues.size)
        Assert.assertThat(argumentCaptor.allValues, Matchers.contains<Any>("%Hello%", 1L, 1L, 1L))
    }

    @Test
    fun test_addParamsToQuery_onlyText() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearchRequest("Hello")
        searchQueryBuilder.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, Mockito.times(1))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        Assert.assertEquals(1, argumentCaptor.allValues.size)
        Assert.assertThat(argumentCaptor.allValues, Matchers.contains<Any>("%Hello%"))
    }

    @Test
    fun test_addParamsToQuery_onlyCategory() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearchRequest(categoryId = 1)
        searchQueryBuilder.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, Mockito.times(1))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        Assert.assertEquals(1, argumentCaptor.allValues.size)
        Assert.assertThat(argumentCaptor.allValues, Matchers.contains<Any>(1L))
    }

    @Test
    fun test_addParamsToQuery_onlySeries() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearchRequest(seriesId = 1)
        searchQueryBuilder.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, Mockito.times(1))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        Assert.assertEquals(1, argumentCaptor.allValues.size)
        Assert.assertThat(argumentCaptor.allValues, Matchers.contains<Any>(1L))
    }

    @Test
    fun test_addParamsToQuery_onlyStar() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearchRequest(starId = 1)
        searchQueryBuilder.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, Mockito.times(1))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        Assert.assertEquals(1, argumentCaptor.allValues.size)
        Assert.assertThat(argumentCaptor.allValues, Matchers.contains<Any>(1L))
    }

    @Test
    fun test_buildEntitySearchQuery() {
        val expected = """
            SELECT vf FROM VideoFile vf
            WHERE vf.active = true
            ORDER BY vf.displayName ASC, vf.fileName ASC
        """.trimIndent()
        val actual = searchQueryBuilder.buildEntitySearchQuery(VideoSearchRequest())
        assertEquals(expected, actual)
    }

    @Test
    fun test_buildCountSearchQuery() {
        val expected = """
            SELECT COUNT(vf) AS video_file_count FROM VideoFile vf
            WHERE vf.active = true
        """.trimIndent()
        val actual = searchQueryBuilder.buildCountSearchQuery(VideoSearchRequest())
        assertEquals(expected, actual)
    }

}
