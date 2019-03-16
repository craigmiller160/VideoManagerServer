package io.craigmiller160.videomanagerserver.util

import org.junit.Assert.assertEquals
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

}