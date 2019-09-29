package io.craigmiller160.videomanagerserver.crypto

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class AesEncryptHandler (private val secretKey: SecretKey): EncryptHandler {

    companion object {
        private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    }

    override fun doEncrypt(value: String): String {
        val iv = ByteArray(16)
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        return Base64.getEncoder().encodeToString(cipher.doFinal(value.toByteArray()))
    }

    override fun doDecrypt(value: String): String {
        val iv = ByteArray(16)
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        val bytes = Base64.getDecoder().decode(value)
        return String(cipher.doFinal(bytes))
    }

}
