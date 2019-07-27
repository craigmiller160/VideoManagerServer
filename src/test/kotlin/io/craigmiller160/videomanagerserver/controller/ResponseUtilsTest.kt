package io.craigmiller160.videomanagerserver.controller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.Optional

class ResponseUtilsTest {

    @Test
    fun test_okOrNoContent_optional_ok() {
        val value = "Hello"
        val result = okOrNoContent(Optional.of(value))
        assertNotNull(result)
        assertEquals(200, result.statusCode.value())
        assertEquals(value, result.body)
    }

    @Test
    fun test_okOrNoContent_optional_noContent() {
        val result = okOrNoContent<Nothing>(Optional.empty())
        assertNotNull(result)
        assertEquals(204, result.statusCode.value())
    }

    @Test
    fun test_okOrNoContent_ok() {
        val value = "Hello"
        val result = okOrNoContent(value)
        assertNotNull(result)
        assertEquals(200, result.statusCode.value())
        assertEquals(value, result.body)
    }

    @Test
    fun test_okOrNoContent_noContent() {
        val result = okOrNoContent<String>(null)
        assertNotNull(result)
        assertEquals(204, result.statusCode.value())
    }

}