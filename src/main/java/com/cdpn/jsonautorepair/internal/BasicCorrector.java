package com.cdpn.jsonautorepair.internal;

public class BasicCorrector {
    public String fix(String originalJSON) {
        String processedJSON = originalJSON.trim();
        processedJSON = wrapWithBracketWhenNotAJSONArray(processedJSON);
        return processedJSON;
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
}
