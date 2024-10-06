package com.cdpn.jsonautorepair.internal;

public class EscapeProcessor {
    private static final char EOF = '\0';
    private static final char TAB_CHAR = '\t';
    private static final char BREAK_LINE_CHAR = '\n';
    private static final char SPACE_CHAR = ' ';

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
                handleNonQuoteCharacter(currentChar);
            }
            else {
                handleQuoteCharacter(currentChar, i);
            }
        }
        return escapedJson.toString();
    }

    private void handleQuoteCharacter(char currentChar, int i) {
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
        if(hasNextQuoteRightAfterCurrentQuoteWithoutComma(i + 1)) {
            handleQuoteNextToQuoteCase(currentChar, i);
            return;
        }
        escapedJson.append('\\');
        escapedJson.append(currentChar);
    }

    private void handleQuoteNextToQuoteCase(char currentChar, int i) {
        int nextQuotePosition = getNextValidCharPosition(i + 1);
        // If next valid quote is a good close quote, then the current quote MUST be an escaped quote
        if(isValidCloseQuote(nextQuotePosition)) {
            escapedJson.append('\\');
            escapedJson.append(currentChar);
        }
        else {
            // If the next valid quote is not a good close quote, then the current quote should be a good close quote
            // However, the current quote and the next quote is next to each other (without separation by a comma),
            // we need to add a comma in between
            inQuotes = false;
            escapedJson.append(currentChar);
            escapedJson.append(',');
        }
    }

    private boolean hasNextQuoteRightAfterCurrentQuoteWithoutComma(int position) {
        return findNextValidChar(position + 1) == '\"';
    }

    private int getNextValidCharPosition(int position) {
        for (int i = position; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            if (currentChar != SPACE_CHAR && currentChar != BREAK_LINE_CHAR && currentChar != TAB_CHAR) {
                return i;
            }
        }
        return -1;
    }


    private void handleNonQuoteCharacter(char currentChar) {
        if (!inQuotes) {
            escapedJson.append(currentChar);
            return;
        }
        if(currentChar == TAB_CHAR || currentChar == BREAK_LINE_CHAR) {
            escapedJson.append(getEscapeSequence(currentChar));
            return;
        }
        escapedJson.append(currentChar);
    }

    private String getEscapeSequence(char currentChar) {
        return switch (currentChar) {
            case TAB_CHAR -> "\\t";
            case BREAK_LINE_CHAR -> "\\n";
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
            if (currentChar != SPACE_CHAR && currentChar != BREAK_LINE_CHAR && currentChar != TAB_CHAR) {
                return currentChar;
            }
        }
        return EOF;
    }

}
