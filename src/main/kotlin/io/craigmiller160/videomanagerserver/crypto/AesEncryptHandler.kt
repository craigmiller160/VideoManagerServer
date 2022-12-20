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

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class AesEncryptHandler(private val secretKey: SecretKey, private val urlEncode: Boolean) :
  EncryptHandler {

  companion object {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
  }

  override fun doEncrypt(value: String): String {
    val iv = ByteArray(16)
    val ivSpec = IvParameterSpec(iv)

    val cipher = Cipher.getInstance(ALGORITHM)
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
    val encoder =
      if (urlEncode) Base64.getUrlEncoder() else Base64.getEncoder() // TODO add unit tests for this
    return encoder.encodeToString(cipher.doFinal(value.toByteArray()))
  }

  override fun doDecrypt(value: String): String {
    val iv = ByteArray(16)
    val ivSpec = IvParameterSpec(iv)

    val cipher = Cipher.getInstance(ALGORITHM)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
    val decoder =
      if (urlEncode) Base64.getUrlDecoder() else Base64.getDecoder() // TODO add unit tests for this
    val bytes = decoder.decode(value)
    return String(cipher.doFinal(bytes))
  }
}
