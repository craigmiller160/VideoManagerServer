package io.craigmiller160.videomanagerserver.mapper

interface ExistingPropHandler<S,D> {

    fun handleExisting(existing: S, destination: D)

    val sourceType: Class<S>
    val destType: Class<D>

}