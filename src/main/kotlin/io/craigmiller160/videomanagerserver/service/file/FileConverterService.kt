package io.craigmiller160.videomanagerserver.service.file

import java.io.File
import ws.schild.jave.Encoder
import ws.schild.jave.MultimediaObject
import ws.schild.jave.encode.AudioAttributes
import ws.schild.jave.encode.EncodingAttributes
import ws.schild.jave.encode.VideoAttributes
import ws.schild.jave.encode.enums.X264_PROFILE
import ws.schild.jave.info.MultimediaInfo

class FileConverterService {
  fun convert(source: File, target: File) {
    // TODO need to be able to set all of this based on the source

    val mediaSource = MultimediaObject(source)
    val info = mediaSource.info

    val (audio, video) = mediaSource.let { getAudioVideoAttributes(it.info) }

    val attrs = EncodingAttributes()
    attrs.setInputFormat("mkv")
    attrs.setOutputFormat("mp4")
    attrs.setAudioAttributes(audio)
    attrs.setVideoAttributes(video)

    val encoder = Encoder()
    encoder.encode(mediaSource, target, attrs)
  }

  private fun getAudioVideoAttributes(
    info: MultimediaInfo
  ): Pair<AudioAttributes, VideoAttributes> {
    val audio =
      AudioAttributes().apply {
        setCodec("aac")
        setBitRate(info.audio.bitRate)
        setChannels(info.audio.channels)
        setSamplingRate(info.audio.samplingRate)
      }

    val video =
      VideoAttributes().apply {
        setCodec("h264")
        setX264Profile(X264_PROFILE.BASELINE)
        setBitRate(info.video.bitRate)
        setFrameRate(info.video.frameRate.toInt())
        setSize(info.video.size)
      }

    return audio to video
  }
}
