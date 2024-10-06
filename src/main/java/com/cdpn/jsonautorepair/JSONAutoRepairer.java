package com.cdpn.jsonautorepair;

import com.cdpn.jsonautorepair.internal.BasicCorrector;
import com.cdpn.jsonautorepair.internal.EscapeProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class JSONAutoRepairer {
    public String repair(String json) {
        String processedJSON = json.trim();
        if(processedJSON.isEmpty()) {
            return null;
        }
        processedJSON = cleanupMarkdownJSONCodeBlock(processedJSON);
        try {
            JsonElement jsonElement = JsonParser.parseString(processedJSON);
            return jsonElement.toString();
        } catch (JsonSyntaxException e) {
            return attemptToFix(processedJSON);
        }
    }

    private String cleanupMarkdownJSONCodeBlock(String processedJSON) {
        if(processedJSON.startsWith("```json")) {
            processedJSON = processedJSON.substring(7);
        }
        if(processedJSON.endsWith("```")) {
            processedJSON = processedJSON.substring(0, processedJSON.length() - 3);
        }
        return processedJSON;
    }

    private String attemptToFix(String processedJSON) {
        BasicCorrector basicCorrector = new BasicCorrector();
        String fixedString = basicCorrector.fix(processedJSON);
        EscapeProcessor internalQuoteEscapeProcessor = new EscapeProcessor(fixedString);
        fixedString = internalQuoteEscapeProcessor.process();
        try {
            JsonElement jsonElement = JsonParser.parseString(fixedString);
            return jsonElement.toString();
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
