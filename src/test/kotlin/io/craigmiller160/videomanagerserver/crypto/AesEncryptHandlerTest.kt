package io.craigmiller160.videomanagerserver.crypto

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@RunWith(MockitoJUnitRunner::class)
class AesEncryptHandlerTest {

    private lateinit var secretKey: SecretKey

    private lateinit var aesEncryptHandler: AesEncryptHandler

    @Before
    fun setup() {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        this.secretKey = keyGen.generateKey()
        this.aesEncryptHandler = AesEncryptHandler(this.secretKey)
    }

    @Test
    fun test_encryptDecrypt() {
        // TODO I need a way to have the key be static
        val value = "Hello World"
        val expectedEncrypted = "9q2wt+ANTSz/YRBm4AekSg=="
        val encrypted = aesEncryptHandler.doEncrypt(value)
        assertEquals(expectedEncrypted, encrypted)
        val decrypted = aesEncryptHandler.doDecrypt(encrypted)
        assertEquals(value, decrypted)
    }

}
