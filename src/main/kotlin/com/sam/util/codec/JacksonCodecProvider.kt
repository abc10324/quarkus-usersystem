package com.sam.util.codec

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.bson.codecs.Codec
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.bson.codecs.DocumentCodec
import org.bson.codecs.ObjectIdCodec
import org.bson.types.ObjectId

class JacksonCodecProvider: CodecProvider {

    override fun <T : Any?> get(clazz: Class<T>?, registry: CodecRegistry?): Codec<T>{
        var objMapper = ObjectMapper()

        var simpleModule = SimpleModule("customModule")
        simpleModule.addSerializer(ObjectId::class.java,ObjectIdSerializer())
        simpleModule.addDeserializer(ObjectId::class.java,ObjectIdDeserializer())

        objMapper.registerModules(listOf(simpleModule,KotlinModule()))

        return JacksonCodec(objMapper,registry!!,clazz!!)
    }

}