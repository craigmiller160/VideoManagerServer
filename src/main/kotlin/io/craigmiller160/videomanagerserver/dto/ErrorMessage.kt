package io.craigmiller160.videomanagerserver.dto

data class ErrorMessage (
        var timestamp: String = "",
        var status: Int = 0,
        var error: String = "",
        var message: String = "",
        var path: String = ""
)