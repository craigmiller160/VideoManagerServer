package io.craigmiller160.videomanagerserver.security.tokenprovider

import java.util.UUID

data class VideoToken(val userId: UUID, val videoId: Long, val filePath: String) {
  companion object {}
}

fun VideoToken.Companion.fromMap(params: Map<String, Any>): VideoToken =
  VideoToken(
    userId = UUID.fromString(params[TokenConstants.PARAM_USER_ID]!! as String),
    videoId = params[TokenConstants.PARAM_VIDEO_ID]!! as Long,
    filePath = params[TokenConstants.PARAM_FILE_PATH]!! as String)

fun VideoToken.toMap(): Map<String, Any> =
  mapOf(
    TokenConstants.PARAM_USER_ID to userId.toString(),
    TokenConstants.PARAM_VIDEO_ID to videoId,
    TokenConstants.PARAM_FILE_PATH to filePath)
