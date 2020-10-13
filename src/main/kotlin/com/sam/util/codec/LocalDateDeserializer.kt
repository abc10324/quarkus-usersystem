package com.sam.util.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.sam.util.Const.Companion.DATE_PATTERN
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_INSTANT

class LocalDateDeserializer: JsonDeserializer<LocalDate?>() {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDate? {

        var dateNode = p?.readValueAsTree<JsonNode>()

        if (dateNode == null || dateNode.isNull){
            return null
        }

        var temporalAccessor = if(dateNode.isTextual)
                                 DateTimeFormatter.ofPattern(DATE_PATTERN)
                                                  .parse(dateNode.asText())
                              else
                                 ISO_INSTANT.withZone(ZoneId.systemDefault())
                                            .parse(dateNode.get("\$date").asText())

        return LocalDate.from(temporalAccessor)
    }

}