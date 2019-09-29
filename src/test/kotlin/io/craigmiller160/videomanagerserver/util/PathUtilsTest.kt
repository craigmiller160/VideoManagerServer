package io.craigmiller160.videomanagerserver.util

import org.hamcrest.Matchers.aMapWithSize
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test

class PathUtilsTest {

    @Test
    fun test_ensureTrailingSlash_hasSlash() {
        val value = "hello/"
        val result = ensureTrailingSlash(value)
        assertEquals(value, result)
    }

    @Test
    fun test_ensureTrailingSlash_noSlash() {
        val value = "hello"
        val result = ensureTrailingSlash(value)
        assertEquals("$value/", result)
    }

    @Test
    fun test_parseQueryString() {
        val queryString = "one=abc&two=def"
        val result = parseQueryString(queryString)
        assertThat(result, allOf<Map<String,String>>(
                hasEntry("one", "abc"),
                hasEntry("two", "def")
        ))
    }

    @Test
    fun test_parseQueryString_empty() {
        val queryString = ""
        val result = parseQueryString(queryString)
        assertThat(result, aMapWithSize(0))
    }

}
