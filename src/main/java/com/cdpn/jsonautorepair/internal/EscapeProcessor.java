package com.cdpn.jsonautorepair.internal;

public class EscapeProcessor {
    private static final char EOF = '\0';
    private final String inputString;
    private boolean inQuotes = false;
    private StringBuilder escapedJson;
    public EscapeProcessor(String inputString) {
        this.inputString = inputString;
    }
    public String process() {
        escapedJson = new StringBuilder();
        inQuotes = false;
        for (int i = 0; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            if (currentChar != '\"') {
                processForNonQuoteCharacter(currentChar);
                continue;
            }
            processQuoteCharacter(currentChar, i);
        }
        return escapedJson.toString();
    }

    private void processQuoteCharacter(char currentChar, int i) {
        if (!inQuotes) {
            inQuotes = true;
            if(!isPreviousCloseQuoteGood(i)) {
                escapedJson.append(',');
            }
            escapedJson.append(currentChar);
            return;
        }
        if(!isValidCloseQuote(i)) {
            escapedJson.append('\\');
            escapedJson.append(currentChar);
            return;
        }
        inQuotes = false;
        escapedJson.append(currentChar);
    }

    private boolean isPreviousCloseQuoteGood(int position) {
        // If the quote is the first character, there is no need to check for a good close quote
        if(position == 0) {
            return true;
        }
        // The previous close quote is good when it ends with a comma, or a curly brace,
        // or a square bracket, or a colon
        for (int i = position - 1; i >= 0; i--) {
            char currentChar = inputString.charAt(i);
            if (currentChar != ' '
                    && currentChar != '\n'
                    && currentChar != '\t') {
                return currentChar == ','
                        || currentChar == '{'
                        || currentChar == '['
                        || currentChar == ':';
            }
        }
        return false;
    }


    private void processForNonQuoteCharacter(char currentChar) {
        if (!inQuotes) {
            escapedJson.append(currentChar);
            return;
        }
        if(currentChar == '\t' || currentChar == '\n') {
            escapedJson.append(getEscapeSequence(currentChar));
            return;
        }
        escapedJson.append(currentChar);
    }

    private String getEscapeSequence(char currentChar) {
        return switch (currentChar) {
            case '\t' -> "\\t";
            case '\n' -> "\\n";
            default -> "";
        };
    }
    private boolean isValidCloseQuote(int i) {
        char nextValidChar = findNextValidChar(inputString, i + 1);
        return  nextValidChar == EOF
                || nextValidChar == ','
                || nextValidChar == '}'
                || nextValidChar == ']'
                || nextValidChar == ':';
    }

    private char findNextValidChar(String inputString, int position) {
        for (int i = position; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            if (currentChar != ' ' && currentChar != '\n' && currentChar != '\t') {
                return currentChar;
            }
        }
        return EOF;
    }

}
