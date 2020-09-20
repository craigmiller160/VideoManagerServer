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
