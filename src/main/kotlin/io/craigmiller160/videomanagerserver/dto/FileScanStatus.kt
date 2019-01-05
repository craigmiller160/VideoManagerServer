package io.craigmiller160.videomanagerserver.dto

import com.fasterxml.jackson.annotation.JsonIgnore

const val SCAN_STATUS_RUNNING = "Video file scan is running"
const val SCAN_STATUS_NOT_RUNNING = "Video file scan is not running"
const val SCAN_STATUS_ALREADY_RUNNING = "Video file scan is already running and cannot be started again until it is complete"

data class FileScanStatus (
        var inProgress: Boolean = false,
        var message: String = "",
        @JsonIgnore
        var alreadyRunning: Boolean = false
)