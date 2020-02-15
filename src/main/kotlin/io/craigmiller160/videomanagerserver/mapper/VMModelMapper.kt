package io.craigmiller160.videomanagerserver.mapper

import org.modelmapper.ModelMapper

class VMModelMapper {

    private val mapper = ModelMapper()
    val existingPropHandlers: MutableMap<ExistingPropHandlerKey<out Any,out Any>,ExistingPropHandler<out Any,out Any>> = mutableMapOf()

    // TODO externalize this
    init {
        val handler = VideoFilePayloadToVideoFileHandler()
        existingPropHandlers += handler.key to handler
    }

    fun <D : Any> map(source: Any, destType: Class<D>): D {
        return mapper.map(source, destType)
    }

    fun <S: Any, D : Any> mapFromExisting(source: S, existing: D): D {
        val destType = existing::class.java
        val destination = map(source, destType)
        val key = ExistingPropHandlerKey<S,D>(source::class.java, destType) // TODO remove explicit type arguments once this works
        val handler = existingPropHandlers[key] // TODO make sure this can handle null values
        handler?.let {
            (handler as ExistingPropHandler<S,D>).handleExisting(source, existing, destination)
        }
        return destination
    }

}