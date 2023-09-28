package com.warxim.petep.gui.control.byteseditor;

import com.warxim.petep.extension.PetepAPI;
import lombok.Value;

/**
 * Internal formatter for {@link HexEditor}
 */
@PetepAPI
class HexEditorFormatter {
    @Value
    static class TextFormatResult {
        String text;
        int anchorPosition;
        int caretPosition;
    }

    /**
     * Formats text and recalculates new position of caret/anchor after the format is applied
     */
    static TextFormatResult formatTextAndRecalculateCaretPosition(
            String text,
            int anchorPosition,
            int caretPosition
    ) {
        return formatHexText(
                text,
                anchorPosition,
                caretPosition,
                HexEditorFormatter::handleSpaceCharFormatStepWithMerging,
                HexEditorFormatter::handleHexCharFormatStep
        );
    }

    /**
     * Formats text and recalculates new position of caret/anchor after the format is applied.
     * <p>Additionally pads single characters with 0.</p>
     * <p>For example:</p>
     * <ul>
     *     <li>00 1 2 03 4 00 will be converted to 00 01 02 03 04 00</li>
     *     <li>1 will be converted to 01</li>
     *     <li>11 22 33 will be converted 11 22 33</li>
     * </ul>
     */
    static TextFormatResult formatTextAndRecalculateCaretPositionWithPadding(
            String text,
            int anchorPosition,
            int caretPosition
    ) {
        return formatHexText(
                text,
                anchorPosition,
                caretPosition,
                HexEditorFormatter::handleSpaceCharFormatStepWithMerging,
                HexEditorFormatter::handleHexCharFormatStepWithPadding
        );
    }

    /**
     * Merges two consecutive single HEX characters into a pair
     */
    private static FormatStepState handleSpaceCharFormatStepWithMerging(FormatStepState state, StringBuilder builder, char[] chars, int charIndex) {
        // If next char is a space, skip
        if (isNextCharSpace(chars, charIndex)) {
            return state;
        }

        // Correct format with two HEX characters
        if (state == FormatStepState.SECOND_CHAR) {
            builder.append(' ');
            return FormatStepState.SPACE;
        }

        // Single HEX character detected
        // Do not add space if the next char is alone - merge into a pair
        if (state == FormatStepState.FIRST_CHAR && !isNextCharAlone(chars, charIndex)) {
            builder.append(' ');
            return FormatStepState.SPACE;
        }

        return state;
    }

    /**
     * Splits HEX triplets into pair and a single HEX
     */
    private static FormatStepState handleHexCharFormatStepWithPadding(FormatStepState state, StringBuilder builder, char[] chars, int charIndex) {
        // If the character is alone (at the end or the next char is space), pad it with 0
        if (state == FormatStepState.SPACE && (isLastChar(chars, charIndex) || isNextCharSpace(chars, charIndex))) {
            builder.append('0');
            builder.append(chars[charIndex]);
            return FormatStepState.SECOND_CHAR;
        }

        return handleHexCharFormatStep(state, builder, chars, charIndex);
    }

    /**
     * Splits HEX triplets into pair and a single HEX
     */
    private static FormatStepState handleHexCharFormatStep(FormatStepState state, StringBuilder builder, char[] chars, int charIndex) {
        // First char after space, correct HEX string format
        if (state == FormatStepState.SPACE) {
            builder.append(chars[charIndex]);
            return FormatStepState.FIRST_CHAR;
        }

        // Second character after the first, correct HEX string format
        if (state == FormatStepState.FIRST_CHAR) {
            builder.append(chars[charIndex]);
            return FormatStepState.SECOND_CHAR;
        }

        // Third HEX char after second char detected, incorrect HEX string format
        // Add space before it (e.g. ABC => AB C) to split it
        if (state == FormatStepState.SECOND_CHAR) {
            builder.append(' ');
            builder.append(chars[charIndex]);
            return FormatStepState.FIRST_CHAR;
        }

        return state;
    }

    /**
     * Formats HEX text and recalculates anchor/caret using given formatting steps
     */
    private static TextFormatResult formatHexText(
            String text,
            int anchorPosition,
            int caretPosition,
            FormatStep spaceCharFormatStep,
            FormatStep hexCharFormatStep
    ) {
        // Add 0 before single hex characters and recalculate caret position
        var chars = text.toCharArray();
        var builder = new StringBuilder(chars.length);
        var newCaretPosition = 0;
        var newAnchorPosition = 0;
        var state = FormatStepState.SPACE;

        // Go through all characters and process required steps for each one of them
        for (int i = 0; i < chars.length; ++i) {
            if (isSpace(chars[i])) {
                state = spaceCharFormatStep.format(state, builder, chars, i);
            } else {
                state = hexCharFormatStep.format(state, builder, chars, i);
            }

            if (anchorPosition == i + 1) {
                newAnchorPosition = builder.length();
            }

            if (caretPosition == i + 1) {
                newCaretPosition = builder.length();
            }
        }

        // Trim the builder if the last char is a space
        var lastCharIndex = builder.length() - 1;
        if (lastCharIndex > 0 && isSpace(builder.charAt(lastCharIndex))) {
            builder.setLength(lastCharIndex);
        }

        // If there is no change, do nothing
        return new TextFormatResult(
                builder.toString(),
                Math.min(newAnchorPosition, builder.length()),
                Math.min(newCaretPosition, builder.length())
        );
    }

    /**
     * Checks if the char is considered to be a space character
     */
    private static boolean isSpace(char c) {
        return c == ' ' || c == '\t' || c == '\n';
    }

    /**
     * Checks if given character is the last character
     */
    private static boolean isLastChar(char[] chars, int charIndex) {
        return charIndex + 1 == chars.length;
    }

    /**
     * Checks if the next char is a space
     */
    private static boolean isNextCharSpace(char[] chars, int charIndex) {
        var nextCharIndex = charIndex + 1;
        return nextCharIndex < chars.length
                && isSpace(chars[nextCharIndex]);
    }

    /**
     * Checks if the next char is alone (can be in the middle or at the end of string)
     */
    private static boolean isNextCharAlone(char[] chars, int charIndex) {
        var charAfterNextCharIndex = charIndex + 2;

        // Next char is alone at the end
        if (charAfterNextCharIndex == chars.length) {
            return true;
        }

        // Next char is alone in the middle
        if (charAfterNextCharIndex < chars.length && isSpace(chars[charAfterNextCharIndex])) {
            return true;
        }

        return false;
    }

    /**
     * Normalizes HEX text characters:
     * <ul>
     *     <li>Removes all other characters than 0-9, A-F and spaces</li>
     *     <li>Converts a-f to A-F</li>
     *     <li>Normalizes spaces from tabs and new lines to space character</li>
     * </ul>
     */
    static String normalizeHexTextCharacters(String input) {
        var chars = input.toCharArray();
        var builder = new StringBuilder(chars.length);
        for (char currentChar : chars) {
            if ((currentChar >= 'A' && currentChar <= 'F')
                    || (currentChar >= '0' && currentChar <= '9')) {
                builder.append(currentChar);
            } else if (isSpace(currentChar)) {
                builder.append(' ');
            } else if (currentChar >= 'a' && currentChar <= 'f') {
                builder.append((char) (currentChar - 'a' + 'A'));
            }
        }
        return builder.toString();
    }

    /**
     * Represents current state of formatting step (signalized type of previously processed character)
     */
    private enum FormatStepState {
        FIRST_CHAR,
        SECOND_CHAR,
        SPACE
    }

    /**
     * Function to handle format step (for space or for HEX char)
     */
    @FunctionalInterface
    private interface FormatStep {
        FormatStepState format(FormatStepState state, StringBuilder builder, char[] chars, int charIndex);
    }
}
