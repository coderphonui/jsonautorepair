package com.cdpn.jsonautorepair;


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

    private String wrapWithBracketWhenNotAJSONArray(String processedJSON) {
        if (!processedJSON.startsWith("{") && !processedJSON.startsWith("[")) {
            processedJSON = "{" + processedJSON;
        }

        if (!processedJSON.endsWith("}") && !processedJSON.endsWith("]")) {
            processedJSON = processedJSON + "}";
        }
        return processedJSON;
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
        String fixedString = processedJSON.trim();
        fixedString = wrapWithBracketWhenNotAJSONArray(fixedString);
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
