package io.craigmiller160.videomanagerserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File

@SpringBootApplication
class VideoManagerServerApplication

fun main(args: Array<String>) {
    runApplication<VideoManagerServerApplication>(*args)
}

