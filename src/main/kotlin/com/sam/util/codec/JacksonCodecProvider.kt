package com.sam.util.codec

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.bson.codecs.Codec
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.bson.Document
import org.bson.codecs.DocumentCodec
import org.bson.codecs.ObjectIdCodec
import org.bson.types.ObjectId
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class JacksonCodecProvider: CodecProvider {

    override fun <T : Any?> get(clazz: Class<T>?, registry: CodecRegistry?): Codec<T>{

        var objMapper = ObjectMapper()

        var customModule = SimpleModule("customModule")

        customModule.addSerializer(ObjectId::class.java,ObjectIdSerializer())
        customModule.addDeserializer(ObjectId::class.java,ObjectIdDeserializer())
        customModule.addDeserializer(LocalDateTime::class.java,LocalDateTimeDeserializer())
        customModule.addDeserializer(LocalDate::class.java,LocalDateDeserializer())
        customModule.addDeserializer(Date::class.java,DateDeserializer())

        objMapper.registerModules(listOf(customModule,KotlinModule()))

        return JacksonCodec(objMapper,registry!!,clazz!!)
    }

}