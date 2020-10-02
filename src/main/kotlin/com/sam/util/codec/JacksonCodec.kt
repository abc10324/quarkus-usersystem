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

class JacksonCodec<T>(
        var objMapper: ObjectMapper,
        codecRegistry: CodecRegistry,
        var type: Class<T>
): Codec<T>{

    private var rawBsonDocumentCodec: Codec<RawBsonDocument> = codecRegistry.get(RawBsonDocument::class.java)

    override fun getEncoderClass(): Class<T> = this.type

    override fun encode(writer: BsonWriter?, value: T, encoderContext: EncoderContext?) {

        var json = objMapper.writeValueAsString(value)

        rawBsonDocumentCodec.encode(writer, RawBsonDocument.parse(json), encoderContext)

    }

    override fun decode(reader: BsonReader?, decoderContext: DecoderContext?): T {
        var document = rawBsonDocumentCodec.decode(reader, decoderContext)

        var json = document.toJson()

        return objMapper.readValue(json, type)
    }

}