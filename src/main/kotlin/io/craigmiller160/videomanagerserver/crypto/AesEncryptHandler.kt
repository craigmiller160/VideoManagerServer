package io.craigmiller160.videomanagerserver.crypto

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class AesEncryptHandler (private val secretKey: SecretKey, private val urlEncode: Boolean): EncryptHandler {

    companion object {
        private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    }

    override fun doEncrypt(value: String): String {
        val iv = ByteArray(16)
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encoder = if (urlEncode) Base64.getUrlEncoder() else Base64.getEncoder() // TODO add unit tests for this
        return encoder.encodeToString(cipher.doFinal(value.toByteArray()))
    }

    override fun doDecrypt(value: String): String {
        val iv = ByteArray(16)
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        val decoder = if (urlEncode) Base64.getUrlDecoder() else Base64.getDecoder()
        val bytes = decoder.decode(value)
        return String(cipher.doFinal(bytes))
    }

}
