package io.craigmiller160.videomanagerserver.security

enum class JwtValidationStatus {
    VALID,
    BAD_SIGNATURE,
    EXPIRED,
    NO_TOKEN
}