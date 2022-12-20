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

package io.craigmiller160.videomanagerserver

import io.craigmiller160.videomanagerserver.security.AllowAllHostnameVerifier
import java.security.KeyStore
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class VideoManagerServerApplication

private const val TRUST_STORE_TYPE = "JKS"
private const val TRUST_STORE_PATH = "truststore.jks"
private const val TRUST_STORE_PASSWORD = "changeit"

private val logger = LoggerFactory.getLogger(VideoManagerServerApplication::class.java)

fun main(args: Array<String>) {
  setupTls()
  Thread.setDefaultUncaughtExceptionHandler { _, e -> logger.error("Uncaught exception", e) }
  runApplication<VideoManagerServerApplication>(*args)
}

fun setupTls() {
  val trustManagerFactory =
    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

  val trustStore = KeyStore.getInstance(TRUST_STORE_TYPE)
  val trustStoreStream =
    VideoManagerServerApplication::class.java.classLoader.getResourceAsStream(TRUST_STORE_PATH)
  trustStore.load(trustStoreStream, TRUST_STORE_PASSWORD.toCharArray())

  trustManagerFactory.init(trustStore)

  val trustManagers = trustManagerFactory.trustManagers
  val sslContext = SSLContext.getInstance("TLS")
  sslContext.init(null, trustManagers, null)
  SSLContext.setDefault(sslContext)

  HttpsURLConnection.setDefaultHostnameVerifier(AllowAllHostnameVerifier())
}
