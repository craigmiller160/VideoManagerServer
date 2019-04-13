package io.craigmiller160.videomanagerserver.dto

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VideoSearchTest {

    @Test
    fun test_isJoinedSearch_allNull() {
        val videoSearch = VideoSearch("SearchText")
        assertFalse(videoSearch.isJoinedSearch())
    }

    @Test
    fun test_isJoinedSearch_allValues() {
        val videoSearch = VideoSearch(null, 1, 1, 1)
        assertTrue(videoSearch.isJoinedSearch())
    }

    @Test
    fun test_isJoinedSearch_seriesId() {
        val videoSearch = VideoSearch(seriesId = 1)
        assertTrue(videoSearch.isJoinedSearch())
    }

    @Test
    fun test_isJoinedSearch_starId() {
        val videoSearch = VideoSearch(starId = 1)
        assertTrue(videoSearch.isJoinedSearch())
    }

    @Test
    fun test_isJoinedSearch_categoryId() {
        val videoSearch = VideoSearch(categoryId = 1)
        assertTrue(videoSearch.isJoinedSearch())
    }

}