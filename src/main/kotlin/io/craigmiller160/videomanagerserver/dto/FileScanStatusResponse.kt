package io.craigmiller160.videomanagerserver.dto

import com.fasterxml.jackson.annotation.JsonIgnore

const val SCAN_STATUS_RUNNING = "Video file scan is running"
const val SCAN_STATUS_NOT_RUNNING = "Video file scan is not running"
const val SCAN_STATUS_ALREADY_RUNNING = "Video file scan is already running and cannot be started again until it is complete"
const val SCAN_STATUS_ERROR = "Video file scan ended with an error and did not complete"

fun createScanRunningStatus(): FileScanStatusResponse = FileScanStatusResponse(true, SCAN_STATUS_RUNNING)
fun createScanNotRunningStatus(): FileScanStatusResponse = FileScanStatusResponse(false, SCAN_STATUS_NOT_RUNNING)
fun createScanAlreadyRunningStatus(): FileScanStatusResponse = FileScanStatusResponse(true, SCAN_STATUS_ALREADY_RUNNING, true)
fun createScanErrorStatus(): FileScanStatusResponse = FileScanStatusResponse(false, SCAN_STATUS_ERROR, false, true)

data class FileScanStatusResponse (
        var inProgress: Boolean = false,
        var message: String = "",
        @JsonIgnore
        var alreadyRunning: Boolean = false,
        var scanError: Boolean = false
)