package com.cdpn.jsonautorepair.internal;

public class EscapeProcessor {
    private static final char EOF = '\0';
    private static final char TAB_CHAR = '\t';
    private static final char COMMA = ',';
    private static final char CLOSED_BRACKET = '}';
    private static final char BREAK_LINE_CHAR = '\n';
    private static final char SPACE_CHAR = ' ';
    private static final char DOUBLE_QUOTE_CHAR = '\"';
    private static final char ESCAPE_CHAR = '\\';

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
            if (currentChar != DOUBLE_QUOTE_CHAR) {
                handleNonQuoteCharacter(currentChar, i);
            }
            else {
                handleQuoteCharacter(currentChar, i);
            }
        }
        return escapedJson.toString();
    }

    private void handleQuoteCharacter(char currentChar, int position) {
        if (!inQuotes) {
            inQuotes = true;
            escapedJson.append(currentChar);
            return;
        }
        if(isValidCloseQuoteAtPosition(position)) {
            inQuotes = false;
            escapedJson.append(currentChar);
            return;
        }
        if(hasNextQuoteRightAfterCurrentQuoteWithoutComma(position + 1)) {
            handleQuoteNextToQuoteCase(currentChar, position);
            return;
        }
        escapedJson.append(ESCAPE_CHAR);
        escapedJson.append(currentChar);
    }

    private void handleQuoteNextToQuoteCase(char currentChar, int i) {
        int nextQuotePosition = getNextNonSpaceCharPosition(i + 1);
        // If next valid quote is a good close quote, then the current quote MUST be an escaped quote
        if(isValidCloseQuoteAtPosition(nextQuotePosition)) {
            escapedJson.append(ESCAPE_CHAR);
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
        return findNextNonSpaceChar(position + 1) == DOUBLE_QUOTE_CHAR;
    }

    private void handleNonQuoteCharacter(char currentChar, int position) {
        if (!inQuotes) {
            escapedJson.append(currentChar);
            return;
        }
        if(currentChar == TAB_CHAR || currentChar == BREAK_LINE_CHAR) {
            escapedJson.append(getEscapeStringFromChar(currentChar));
            return;
        }
        if (currentChar == COMMA) {
            handleCommaToFixMissingClosedQuote(currentChar, position);
            return;
        }
        if (currentChar == CLOSED_BRACKET) {
            handleClosedBracket(currentChar, position);
            return;
        }
        escapedJson.append(currentChar);
    }

    private void handleClosedBracket(char currentChar, int position) {
        int previousNonSpaceCharPosition = getPreviousNonSpaceCharPosition(position - 1);
        if (previousNonSpaceCharPosition != -1) {
           escapedJson = new StringBuilder(escapedJson.substring(0, previousNonSpaceCharPosition + 1 ));
        }
        escapedJson.append(DOUBLE_QUOTE_CHAR);
        escapedJson.append(currentChar);
        inQuotes = false;
    }

    private void handleCommaToFixMissingClosedQuote(char currentChar, int position) {
        char nextNonSpaceChar = findNextNonSpaceChar(position + 1);
        int nextNonSpaceCharPosition = getNextNonSpaceCharPosition(position + 1);
        if (nextNonSpaceChar == DOUBLE_QUOTE_CHAR ) {
            // We MUST ignore adding close quote if the next quote is a good close quote
            if(isValidCloseQuoteAtPosition(nextNonSpaceCharPosition)) {
                escapedJson.append(currentChar);
                return;
            }
            escapedJson.append(DOUBLE_QUOTE_CHAR);
            inQuotes = false;
        }
        if (nextNonSpaceChar == CLOSED_BRACKET ) {
            escapedJson.append(DOUBLE_QUOTE_CHAR);
            inQuotes = false;
            return;
        }
        escapedJson.append(currentChar);
    }

    private String getEscapeStringFromChar(char currentChar) {
        return switch (currentChar) {
            case TAB_CHAR -> "\\t";
            case BREAK_LINE_CHAR -> "\\n";
            default -> "";
        };
    }
    private boolean isValidCloseQuoteAtPosition(int position) {
        char nextValidChar = findNextNonSpaceChar(position + 1);
        return  nextValidChar == EOF
                || nextValidChar == ','
                || nextValidChar == '}'
                || nextValidChar == ']'
                || nextValidChar == ':';
    }

    private char findNextNonSpaceChar(int position) {
        for (int i = position; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            if (currentChar != SPACE_CHAR && currentChar != BREAK_LINE_CHAR && currentChar != TAB_CHAR) {
                return currentChar;
            }
        }
        return EOF;
    }


    private int getNextNonSpaceCharPosition(int position) {
        for (int i = position; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            if (currentChar != SPACE_CHAR && currentChar != BREAK_LINE_CHAR && currentChar != TAB_CHAR) {
                return i;
            }
        }
        return -1;
    }

    private int getPreviousNonSpaceCharPosition(int position) {
        for (int i = position; i >= 0; i--) {
            char currentChar = inputString.charAt(i);
            if (currentChar != SPACE_CHAR && currentChar != BREAK_LINE_CHAR && currentChar != TAB_CHAR) {
                return i;
            }
        }
        return -1;
    }

}
