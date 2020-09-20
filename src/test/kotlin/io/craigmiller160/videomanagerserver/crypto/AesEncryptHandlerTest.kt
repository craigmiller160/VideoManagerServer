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

package io.craigmiller160.videomanagerserver.crypto

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.Base64
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@RunWith(MockitoJUnitRunner::class)
class AesEncryptHandlerTest {

    companion object {
        private const val KEY = "XaTw9UVgImYHxi/jXwrq3hMWHsWsnkNC6iWszHzut/U="
    }

    private lateinit var secretKey: SecretKey

    private lateinit var aesEncryptHandler: AesEncryptHandler

    @Before
    fun setup() {
        val keyBytes = Base64.getDecoder().decode(KEY)
        this.secretKey = SecretKeySpec(keyBytes, 0, keyBytes.size, "AES")
        this.aesEncryptHandler = AesEncryptHandler(this.secretKey, false)
    }

    @Test
    fun test_encryptDecrypt() {
        val value = "Hello World"
        val expectedEncrypted = "+4mpYOphqcYmtb+yMU8Ypw=="
        val encrypted = aesEncryptHandler.doEncrypt(value)
        assertEquals(expectedEncrypted, encrypted)
        val decrypted = aesEncryptHandler.doDecrypt(encrypted)
        assertEquals(value, decrypted)
    }

}
