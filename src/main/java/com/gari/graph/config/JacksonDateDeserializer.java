package com.gari.graph.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.gari.graph.utils.StringDateUtils;

import java.io.IOException;
import java.time.OffsetDateTime;

public class JacksonDateDeserializer extends JsonDeserializer<OffsetDateTime> {

    @Override
    public OffsetDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();

        if (currentToken.equals(JsonToken.VALUE_STRING)) {
            OffsetDateTime value = StringDateUtils.parseDateTimeString(jp.getText());
            if (value != null) {
                return value;
            }
        } else if (currentToken.equals(JsonToken.VALUE_NULL)) {
            return getNullValue();
        }

        throw new IOException(String.format("Only valid date values supported. Values was %s", jp.getText()));
    }

    @Override
    public OffsetDateTime getNullValue() {
        return null;
    }

}
