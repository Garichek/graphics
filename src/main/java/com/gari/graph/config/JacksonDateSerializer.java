package com.gari.graph.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gari.graph.utils.StringDateUtils;

import java.io.IOException;
import java.time.OffsetDateTime;

public class JacksonDateSerializer extends JsonSerializer<OffsetDateTime> {

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(StringDateUtils.parseDateTimeToString(value));
    }
}
