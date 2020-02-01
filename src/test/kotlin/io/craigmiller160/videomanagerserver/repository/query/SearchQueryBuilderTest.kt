package io.craigmiller160.videomanagerserver.repository.query

import com.nhaarman.mockito_kotlin.verify
import io.craigmiller160.videomanagerserver.dto.SortBy
import io.craigmiller160.videomanagerserver.dto.VideoSearch
import io.craigmiller160.videomanagerserver.test_util.isA
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
            ORDER BY vf.displayName ASC
        """.trimIndent()
        val search = VideoSearch()
        val query = searchQueryBuilder.buildQueryOrderBy(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_descOrder() {
        val expected = """
            ORDER BY vf.displayName DESC
        """.trimIndent()
        val search = VideoSearch(sortDir = Sort.Direction.DESC)
        val query = searchQueryBuilder.buildQueryOrderBy(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_sortByViewCount() {
        val expected = """
            ORDER BY vf.viewCount ASC
        """.trimIndent()
        val search = VideoSearch(sortBy = SortBy.VIEW_COUNT)
        val query = searchQueryBuilder.buildQueryOrderBy(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_sortByLastViewed() {
        val expected = """
            ORDER BY vf.lastViewed ASC
        """.trimIndent()
        val search = VideoSearch(sortBy = SortBy.LAST_VIEWED)
        val query = searchQueryBuilder.buildQueryOrderBy(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_sortByLastModified() {
        val expected = """
            ORDER BY vf.lastModified ASC
        """.trimIndent()
        val search = VideoSearch(sortBy = SortBy.LAST_MODIFIED)
        val query = searchQueryBuilder.buildQueryOrderBy(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_sortByFileAdded() {
        val expected = """
            ORDER BY vf.fileAdded ASC
        """.trimIndent()
        val search = VideoSearch(sortBy = SortBy.FILE_ADDED)
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
            OR LOWER(vf.displayName) LIKE LOWER(:searchText))
            AND ca.categoryId = :categoryId
            AND se.seriesId = :seriesId
            AND st.starId = :starId
            ORDER BY vf.displayName ASC
        """.trimIndent()

        val search = VideoSearch("Hello", 1, 1, 1)
        val query = searchQueryBuilder.buildQueryCriteria(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_onlySearchText() {
        val expected = """
            WHERE vf.active = true
            AND (LOWER(vf.fileName) LIKE LOWER(:searchText)
            OR LOWER(vf.displayName) LIKE LOWER(:searchText))
            ORDER BY vf.displayName ASC
        """.trimIndent()
        val search = VideoSearch("Hello")
        val query = searchQueryBuilder.buildQueryCriteria(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_onlyCategory() {
        val expected = """
            LEFT JOIN vf.categories ca
            WHERE vf.active = true
            AND ca.categoryId = :categoryId
            ORDER BY vf.displayName ASC
        """.trimIndent()
        val search = VideoSearch(categoryId = 1)
        val query = searchQueryBuilder.buildQueryCriteria(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_onlyStar() {
        val expected = """
            LEFT JOIN vf.stars st
            WHERE vf.active = true
            AND st.starId = :starId
            ORDER BY vf.displayName ASC
        """.trimIndent()
        val search = VideoSearch(starId = 1)
        val query = searchQueryBuilder.buildQueryCriteria(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_buildQueryCriteria_onlySeries() {
        val expected = """
            LEFT JOIN vf.series se
            WHERE vf.active = true
            AND se.seriesId = :seriesId
            ORDER BY vf.displayName ASC
        """.trimIndent()
        val search = VideoSearch(seriesId = 1)
        val query = searchQueryBuilder.buildQueryCriteria(search)
        Assert.assertEquals(expected, query)
    }

    @Test
    fun test_addParamsToQuery_noParams() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearch()
        searchQueryBuilder.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, Mockito.times(0))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        Assert.assertEquals(0, argumentCaptor.allValues.size)
    }

    @Test
    fun test_addParamsToQuery_allParams() {
        val query = Mockito.spy(Query::class.java)
        val search = VideoSearch("Hello", 1, 1, 1)
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
        val search = VideoSearch("Hello")
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
        val search = VideoSearch(categoryId = 1)
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
        val search = VideoSearch(seriesId = 1)
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
        val search = VideoSearch(starId = 1)
        searchQueryBuilder.addParamsToQuery(search, query)
        val argumentCaptor = ArgumentCaptor.forClass(Any::class.java)
        verify(query, Mockito.times(1))
                .setParameter(isA(String::class.java), argumentCaptor.capture())
        Assert.assertEquals(1, argumentCaptor.allValues.size)
        Assert.assertThat(argumentCaptor.allValues, Matchers.contains<Any>(1L))
    }

}