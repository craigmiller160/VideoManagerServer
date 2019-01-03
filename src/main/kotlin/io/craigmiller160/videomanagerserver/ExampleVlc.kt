package io.craigmiller160.videomanagerserver

import java.io.File

fun main(args: Array<String>) {
    val video = File("/home/craig/BackupDrive/Backup/New folder/New folder 1/PublicAgent - Linda aka Linda Sweet.mp4")
    val procBuilder = ProcessBuilder("vlc", video.absolutePath)
    procBuilder.start()
}