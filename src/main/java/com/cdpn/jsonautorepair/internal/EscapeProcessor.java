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
            escapedJson.append(currentChar);
            return;
        }
        if(isValidCloseQuote(i)) {
            inQuotes = false;
            escapedJson.append(currentChar);
            return;
        }
        if(findNextValidChar(i + 1) == '\"') {
            int nextValidCharPosition = getNextValidCharPosition(i + 1);
            if(isValidCloseQuote(nextValidCharPosition)) {
                escapedJson.append('\\');
                escapedJson.append(currentChar);
                return;
            }
            inQuotes = false;
            escapedJson.append(currentChar);
            escapedJson.append(',');
            return;
        }
        escapedJson.append('\\');
        escapedJson.append(currentChar);
    }

    private int getNextValidCharPosition(int position) {
        for (int i = position; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            if (currentChar != ' ' && currentChar != '\n' && currentChar != '\t') {
                return i;
            }
        }
        return -1;
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
        char nextValidChar = findNextValidChar(i + 1);
        return  nextValidChar == EOF
                || nextValidChar == ','
                || nextValidChar == '}'
                || nextValidChar == ']'
                || nextValidChar == ':';
    }

    private char findNextValidChar(int position) {
        for (int i = position; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            if (currentChar != ' ' && currentChar != '\n' && currentChar != '\t') {
                return currentChar;
            }
        }
        return EOF;
    }

}
