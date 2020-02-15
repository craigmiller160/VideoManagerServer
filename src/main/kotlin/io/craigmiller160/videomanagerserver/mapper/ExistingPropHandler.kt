package io.craigmiller160.videomanagerserver.mapper

interface ExistingPropHandler<S,D> {

    val sourceType: Class<S>
    val destinationType: Class<D>
    val key: ExistingPropHandlerKey<S,D>

    fun handleExisting(source: S, existing: D, destination: D)

}