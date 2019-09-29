package io.craigmiller160.videomanagerserver.crypto

interface EncryptHandler {

    fun doEncrypt(value: String): String

    fun doDecrypt(value: String): String

}
