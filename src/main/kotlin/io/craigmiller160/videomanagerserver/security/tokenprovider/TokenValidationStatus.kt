package io.craigmiller160.videomanagerserver.security.tokenprovider

enum class TokenValidationStatus {
    VALID,
    BAD_SIGNATURE,
    EXPIRED,
    NO_TOKEN,
    RESOURCE_FORBIDDEN
}
