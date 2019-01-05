package io.craigmiller160.videomanagerserver.dto

import com.fasterxml.jackson.annotation.JsonIgnore

data class FileScanStatus (
        var inProgress: Boolean = false,
        @JsonIgnore
        var alreadyRunning: Boolean = false
)