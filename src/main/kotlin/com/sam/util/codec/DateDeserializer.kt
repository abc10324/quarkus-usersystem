package com.sam.util.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.sam.util.Const
import com.sam.util.Const.Companion.DATETIME_PATTERN
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_INSTANT
import java.util.*

class DateDeserializer: JsonDeserializer<Date?>() {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Date? {

        var dateNode = p?.readValueAsTree<JsonNode>()

        if (dateNode == null || dateNode.isNull){
            return null
        }

        var temporalAccessor = if(dateNode.isTextual)
                                 DateTimeFormatter.ofPattern(DATETIME_PATTERN)
                                                  .parse(dateNode.asText())
                              else
                                 ISO_INSTANT.withZone(ZoneId.systemDefault())
                                            .parse(dateNode.get("\$date").asText())

        return Date.from(LocalDateTime.from(temporalAccessor).atZone(ZoneId.systemDefault()).toInstant())
    }

}