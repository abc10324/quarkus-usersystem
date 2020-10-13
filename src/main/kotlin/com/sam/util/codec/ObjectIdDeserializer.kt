package com.sam.util.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.bson.types.ObjectId

class ObjectIdDeserializer: JsonDeserializer<ObjectId>() {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): ObjectId {
        var result: ObjectId
        var oid = p?.readValueAsTree<JsonNode>()

        result = when{
                    ObjectId.isValid(oid?.asText()) ->  ObjectId(oid?.asText())
                    ObjectId.isValid(oid?.get("\$oid")?.asText()) -> ObjectId(oid?.get("\$oid")?.asText())
                    else ->  ObjectId()
                }

        return result
    }

}