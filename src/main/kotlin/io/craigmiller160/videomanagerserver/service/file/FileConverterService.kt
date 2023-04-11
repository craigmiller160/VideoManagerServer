package io.craigmiller160.videomanagerserver.service.file

import java.io.File
import java.nio.file.Paths
import ws.schild.jave.Encoder
import ws.schild.jave.MultimediaObject
import ws.schild.jave.encode.AudioAttributes
import ws.schild.jave.encode.EncodingAttributes
import ws.schild.jave.encode.VideoAttributes
import ws.schild.jave.encode.enums.X264_PROFILE
import ws.schild.jave.info.MultimediaInfo

// TODO delete this
fun main() {
  val service = FileConverterService()
  val source = Paths.get(System.getProperty("user.home"), "Downloads", "Temp", "movie.mkv").toFile()
  val target = Paths.get(System.getProperty("user.home"), "Downloads", "Temp", "movie.mp4").toFile()
  service.convert(source, target)
}

class FileConverterService {
  fun convert(source: File, target: File) {
    // TODO need to be able to set all of this based on the source

    val mediaSource = MultimediaObject(source)
    val info = mediaSource.info

    val (audio, video) = mediaSource.let { getAudioVideoAttributes(it.info) }

    val attrs =
      EncodingAttributes().apply {
        //      setInputFormat("matroska,webm")
        setOutputFormat("mp4")
        setAudioAttributes(audio)
        setVideoAttributes(video)
      }

    val encoder = Encoder()
    encoder.encode(mediaSource, target, attrs)
  }

  private fun getAudioVideoAttributes(
    info: MultimediaInfo
  ): Pair<AudioAttributes, VideoAttributes> {
    val audio =
      AudioAttributes().apply {
        val codec = getAudioCodec(info)
        setCodec(codec.codecString)
        if (info.audio.bitRate > 0) {
          setBitRate(info.audio.bitRate)
        }
        setChannels(info.audio.channels)
        setSamplingRate(info.audio.samplingRate)
      }

    val video =
      VideoAttributes().apply {
        val codec = getVideoCodec(info)
        setCodec(codec.codecString)
        if (VideoCodec.H264 == codec) {
          setX264Profile(X264_PROFILE.BASELINE)
        }
        if (info.video.bitRate > 0) {
          setBitRate(info.video.bitRate)
        }
        setFrameRate(info.video.frameRate.toInt())
        setSize(info.video.size)
      }

    return audio to video
  }

  private fun getAudioCodec(info: MultimediaInfo): AudioCodec =
    AudioCodec.values().find { codec -> codec.regex.matches(info.audio.decoder) }
      ?: throw UnsupportedOperationException("Unsupported audio codec: ${info.audio.decoder}")

  private fun getVideoCodec(info: MultimediaInfo): VideoCodec =
    VideoCodec.values().find { codec -> codec.regex.matches(info.video.decoder) }
      ?: throw UnsupportedOperationException("Unsupported video codec: ${info.video.decoder}")
}

enum class AudioCodec(val codecString: String, val regex: Regex) {
  AAC("aac", Regex("^.*aac.*$"))
}

enum class VideoCodec(val codecString: String, val regex: Regex) {
  H265("hevc", Regex("^.*hevc.*$")),
  H264("h264", Regex("^$"))
}
