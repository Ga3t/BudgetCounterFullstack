package com.budget.investments_service.tools.deserializer;


import com.budget.investments_service.models.dto.DailyPriceDto;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


@Component
public class PriceListDeserializer extends JsonDeserializer<List<DailyPriceDto>> {

    @Override
    public List<DailyPriceDto> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                                                                            throws IOException, JacksonException {
        JsonNode root = jsonParser.getCodec().readTree(jsonParser);
        JsonNode pricesNode = root.path("prices");
        List<DailyPriceDto> list = new ArrayList<>();

        for (JsonNode arr:pricesNode){
            long timestampMs = arr.get(0).asLong();
            BigDecimal price = new BigDecimal(arr.get(1).asText());

            LocalDateTime dateTime = Instant.ofEpochMilli(timestampMs)
                                            .atZone(ZoneOffset.UTC)
                                            .toLocalDateTime();

            list.add(new DailyPriceDto(price, dateTime));
        }

        return list;
    }
}
