package com.warxim.petep.gui.control.byteseditor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HexEditorFormatterTest {
    private static final char CARET = '|';
    private static final char ANCHOR = '^';

    @DataProvider(name = "normalizeHexTextCharacters")
    public Object[][] normalizeHexTextCharacters() {
        return new Object[][]{
                {"", ""},
                {"01 23 45", "01 23 45"},
                {"ab cd ef 01 23 45 67 89 AB CD EF", "AB CD EF 01 23 45 67 89 AB CD EF"},
                {"01\t23\n45", "01 23 45"},
                {"01 %@$vál2gk3 45 !;67", "01 23 45 67"},
        };
    }

    @Test(dataProvider = "normalizeHexTextCharacters")
    public void normalizeHexTextCharactersTest(
            String input,
            String expectedOutput
    ) {
        var formattedText = HexEditorFormatter.normalizeHexTextCharacters(input);
        assertThat(formattedText).isEqualTo(expectedOutput);
    }

    @DataProvider(name = "formatTextAndRecalculateCaretPosition")
    public Object[][] formatTextAndRecalculateCaretPosition() {
        return new Object[][]{
                // No formatting change
                {"^|", "^|"},
                {"^ \n\t |", "^|"},
                {"^|00", "^|00"},
                {"^00|", "^00|"},
                {"\n\t ^ 00 | \n\t", "^00|"},
                {"^|00 00", "^|00 00"},
                {"00^| 00", "00^| 00"},
                {"00 ^|00", "00 ^|00"},
                {"00 0^|0", "00 0^|0"},
                {"00 00^|", "00 00^|"},
                {"^00 00|", "^00 00|"},
                {"0^0 0|0", "0^0 0|0"},

                // Format remove spaces
                {"^|00 ", "^|00"},
                {"^|00   00 ", "^|00 00"},
                {"^|  00   00   ", "^|00 00"},
                {"^  |00   00   ", "^|00 00"},
                {"^  00   00  | ", "^00 00|"},
                {"  0^0   0|0   ", "0^0 0|0"},
                {"  0^0   11  0|0   ", "0^0 11 0|0"},
                {"  00   ^11  0|0   ", "00 ^11 0|0"},
                {"^   0   1 2   3 4 5    6 7 8 9  |", "^01 23 45 67 89|"},

                // Format merge single HEX neighbours
                {"^|0 0", "^|00"},
                {"^0 0|", "^00|"},
                {"^0| 0", "^0|0"},
                {"00 1^1 2 2| 33", "00 1^1 22| 33"},
                {"00 ^11 2 2 3 3 4|4 5 5", "00 ^11 22 33 4|4 55"},
                {"01 23 45 6^| 9", "01 23 45 6^|9"},
                {"01 23 45 6 ^|9", "01 23 45 6^|9"},

                // Complex
                {" 0  0 1^1 2    2 3  3 4|4 5   66  7   7       ", "00 1^1 22 33 4|4 5 66 77"},
                {" \t  0  0 11 2  | \n 2 3  3 44 5\t\t   66^  7\n\n   7       9 ", "00 11 2|2 33 44 5 66^ 77 9"},
                {" \t  0  0 11 ^2   \n 2 3  3 44 5\t\t   66  7\n\n |  7       ", "00 11 ^22 33 44 5 66 7|7"},
        };
    }

    @Test(dataProvider = "formatTextAndRecalculateCaretPosition")
    public void formatTextAndRecalculateCaretPositionTest(
            String inputDescriptor,
            String expectedResultDescriptor
    ) {
        var input = toTextFormatResult(inputDescriptor);
        var formattingResult = HexEditorFormatter.formatTextAndRecalculateCaretPosition(
                input.getText(),
                input.getAnchorPosition(),
                input.getCaretPosition()
        );

        var expected = toTextFormatResult(expectedResultDescriptor);
        assertThat(formattingResult).isEqualTo(expected);
    }

    @DataProvider(name = "padSingleHexCharactersAndRecalculateCaretPosition")
    public Object[][] padSingleHexCharactersAndRecalculateCaretPosition() {
        return new Object[][]{
                // No formatting change
                {"^|", "^|"},
                {"^ \n\t |", "^|"},
                {"^|11", "^|11"},
                {"^|11 22", "^|11 22"},
                {"^|11 22 33", "^|11 22 33"},
                {"1|1 22 3^3", "1|1 22 3^3"},

                // Format
                {"^|1", "^|01"},
                {"^11 2|", "^11 02|"},
                {"^1 22|", "^01 22|"},
                {"^1| 22", "^01| 22"},
                {"^1 |22", "^01 |22"},

                // Complex
                {"11   22 3  44 |5 66 7^ 88 9 AA", "11 22 03 44 |05 66 07^ 88 09 AA"},
                {"11   22 3  44 |5 66 7^ 88 9 AA", "11 22 03 44 |05 66 07^ 88 09 AA"},
                {"1 22 ^33 44 5| 66 7 88 9 AA B", "01 22 ^33 44 05| 66 07 88 09 AA 0B"},
                {"1 22 33 44 ^5| 66 7 88 9 AA B", "01 22 33 44 ^05| 66 07 88 09 AA 0B"},
                {"\n1\n\n\n 22 ^3 \t  44 \t\t5| 66 7       88 9 \tAA\n\n", "01 22 ^03 44 05| 66 07 88 09 AA"},
        };
    }

    @Test(dataProvider = "padSingleHexCharactersAndRecalculateCaretPosition")
    public void padSingleHexCharactersAndRecalculateCaretPositionTest(
            String inputDescriptor,
            String expectedResultDescriptor
    ) {
        var input = toTextFormatResult(inputDescriptor);
        var formattingResult = HexEditorFormatter.formatTextAndRecalculateCaretPositionWithPadding(
                input.getText(),
                input.getAnchorPosition(),
                input.getCaretPosition()
        );

        var expected = toTextFormatResult(expectedResultDescriptor);
        assertThat(formattingResult).isEqualTo(expected);
    }

    private HexEditorFormatter.TextFormatResult toTextFormatResult(String inputTextDescriptor) {
        var caret = inputTextDescriptor.indexOf(CARET);
        var anchor = inputTextDescriptor.indexOf(ANCHOR);
        var textBuilder = new StringBuilder(inputTextDescriptor);

        if (caret < anchor) {
            textBuilder.deleteCharAt(caret);
            --anchor;
            textBuilder.deleteCharAt(anchor);
        } else {
            textBuilder.deleteCharAt(anchor);
            --caret;
            textBuilder.deleteCharAt(caret);
        }

        return new HexEditorFormatter.TextFormatResult(
                textBuilder.toString(),
                anchor,
                caret
        );
    }

}