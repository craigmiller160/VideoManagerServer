package io.craigmiller160.videomanagerserver.mapper

data class ExistingPropHandlerKey<S,D>(
        val srcType: Class<out S>,
        val destType: Class<out D>
)