package io.craigmiller160.videomanagerserver.entity

import javax.persistence.*

@Entity
@Table(name = "is_scanning")
class IsScanning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    var isScanning: Boolean = false
    @Version
    var version: Long = 1
}