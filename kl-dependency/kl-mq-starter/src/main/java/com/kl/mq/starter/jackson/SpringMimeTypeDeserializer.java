package com.kl.mq.starter.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.SneakyThrows;
import org.springframework.util.MimeTypeUtils;

import javax.activation.MimeType;
import java.io.IOException;

public class SpringMimeTypeDeserializer extends StdDeserializer<MimeType> {

    private static final long serialVersionUID = 1L;

    public SpringMimeTypeDeserializer() {
        super(MimeType.class);
    }

    @SneakyThrows
    @Override
    public MimeType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode tree = p.getCodec().readTree(p);
        JsonNode type = tree.get("type");
        JsonNode subtype = tree.get("subtype");
        if (null != type && null != subtype) {
            return new MimeType(type.textValue(), subtype.textValue());
        } else {
            return new MimeType(MimeTypeUtils.TEXT_PLAIN_VALUE);
        }

    }

}