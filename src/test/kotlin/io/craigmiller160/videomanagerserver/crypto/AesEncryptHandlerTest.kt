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
