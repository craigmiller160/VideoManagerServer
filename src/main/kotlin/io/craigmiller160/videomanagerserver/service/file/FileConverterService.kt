package io.craigmiller160.videomanagerserver.service.file

import java.io.File
import ws.schild.jave.Encoder
import ws.schild.jave.MultimediaObject
import ws.schild.jave.encode.AudioAttributes
import ws.schild.jave.encode.EncodingAttributes
import ws.schild.jave.encode.VideoAttributes
import ws.schild.jave.encode.enums.X264_PROFILE
import ws.schild.jave.info.VideoSize

class FileConverterService {
  fun convert(source: File, target: File) {
    // TODO need to be able to set all of this based on the source
    val audio = AudioAttributes()
    audio.setCodec("aac")
    audio.setBitRate(64000)
    audio.setChannels(2)
    audio.setSamplingRate(44100)

    val video = VideoAttributes()
    video.setCodec("h264")
    video.setX264Profile(X264_PROFILE.BASELINE)
    video.setBitRate(160000)
    video.setFrameRate(15)
    video.setSize(VideoSize(400, 300))

    val attrs = EncodingAttributes()
    attrs.setInputFormat("mkv")
    attrs.setOutputFormat("mp4")
    attrs.setAudioAttributes(audio)
    attrs.setVideoAttributes(video)

    val encoder = Encoder()
    encoder.encode(MultimediaObject(source), target, attrs)
  }
}
