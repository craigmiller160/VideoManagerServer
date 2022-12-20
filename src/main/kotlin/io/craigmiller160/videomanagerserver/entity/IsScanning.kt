package io.craigmiller160.videomanagerserver.entity

import javax.persistence.*

@Entity
@Table(name = "is_scanning")
class IsScanning(
    @Id
    var id: Long = 0,
    var isScanning: Boolean = false,
    var lastScanSuccess: Boolean = true,
    @Version
    var version: Long = 1
)