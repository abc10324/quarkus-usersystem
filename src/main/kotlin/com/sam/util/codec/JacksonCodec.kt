package com.sam.util.codec

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.Document
import org.bson.RawBsonDocument
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.configuration.CodecRegistry
import org.bson.types.ObjectId
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class JacksonCodec<T>(
        var objMapper: ObjectMapper,
        codecRegistry: CodecRegistry,
        var type: Class<T>
): Codec<T>{

    private var documentCodec: Codec<Document> = codecRegistry.get(Document::class.java)

    override fun getEncoderClass(): Class<T> = this.type

    override fun encode(writer: BsonWriter?, value: T, encoderContext: EncoderContext?) {

        documentCodec.encode(writer, parseToDocument(value),encoderContext)

    }

    private fun parseToDocument(obj: T): Document{
        var clazz = this.type

        var doc = Document()

        clazz.declaredFields.forEach {
            it.isAccessible = true

            doc.append(it.name,formatValue(it, obj))

        }

        return doc
    }

    private fun formatValue(field: Field, obj: T): Any?{
        return when(field.type){
                    LocalDateTime::class.java -> Date.from((field.get(obj) as LocalDateTime).atZone(ZoneId.systemDefault()).toInstant())
                    LocalDate::class.java -> Date.from((field.get(obj) as LocalDate).atStartOfDay(ZoneId.systemDefault()).toInstant())
                    else -> field.get(obj)
                }
    }

    override fun decode(reader: BsonReader?, decoderContext: DecoderContext?): T {
        var document = documentCodec.decode(reader, decoderContext)

        var json = document.toJson()

        return objMapper.readValue(json, type)
    }

}