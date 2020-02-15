package io.craigmiller160.videomanagerserver.mapper

import org.modelmapper.ModelMapper

open class VMModelMapper {

    private val mapper = ModelMapper()
    val existingPropHandlers: MutableMap<ExistingPropHandlerKey<out Any,out Any>,ExistingPropHandler<out Any,out Any>> = mutableMapOf()

    fun <D : Any> map(source: Any, destType: Class<D>): D {
        return mapper.map(source, destType)
    }

    fun <S: Any, D : Any> mapFromExisting(source: S, existing: D): D {
        val destType = existing::class.java
        val destination = map(source, destType)
        val key = ExistingPropHandlerKey(source::class.java, destType)
        val handler = existingPropHandlers[key]
        handler?.let {
            (handler as ExistingPropHandler<S,D>).handleExisting(source, existing, destination)
        }
        return destination
    }

}
