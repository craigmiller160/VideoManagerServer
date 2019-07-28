package io.craigmiller160.videomanagerserver

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VideoManagerServerApplication

private val logger = LoggerFactory.getLogger(VideoManagerServerApplication::class.java)

fun main(args: Array<String>) {
    Thread.setDefaultUncaughtExceptionHandler { _, e -> logger.error("Uncaught exception", e) }
    runApplication<VideoManagerServerApplication>(*args)
}

