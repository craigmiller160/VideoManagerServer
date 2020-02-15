package io.craigmiller160.videomanagerserver.mapper

data class ExistingPropHandlerKey<S,D>(
        val srcType: Class<S>,
        val destType: Class<D>
)