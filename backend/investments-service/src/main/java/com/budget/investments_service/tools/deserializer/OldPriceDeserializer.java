package com.budget.investments_service.tools.deserializer;


import com.budget.investments_service.models.dto.DailyPriceDto;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class OldPriceDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {

            JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);
            JsonNode marketDataNode = rootNode.get("market_data");
            if (marketDataNode != null) {
                JsonNode currentPriceNode = marketDataNode.get("current_price");
                if (currentPriceNode != null) {
                    JsonNode usdNode = currentPriceNode.get("usd");
                    if (usdNode != null && usdNode.isNumber()) {
                        return usdNode.decimalValue();
                    }
                }
            }
            return null;

    }
}
