/*
 *     video-manager-server
 *     Copyright (C) 2020 Craig Miller
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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