package io.craigmiller160.videomanagerserver.mapper

import org.modelmapper.ModelMapper

class VMModelMapper {

    private val mapper = ModelMapper()
    val existingPropHandlers: Map<ExistingPropHandlerKey<*,*>,ExistingPropHandler<*,*>> = mutableMapOf()

    fun <D : Any> map(source: Any, destType: Class<D>): D {
        return mapper.map(source, destType)
    }

    fun <D : Any> mapFromExisting(source: Any, existing: D): D {
        val destType = existing::class.java
        val result = map(source, destType)
        TODO("Figure out how to properly map existing values onto result")
    }

}