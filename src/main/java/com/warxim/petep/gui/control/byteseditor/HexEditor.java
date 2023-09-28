package com.warxim.petep.gui.control.byteseditor;

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.util.BytesUtils;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;

import java.nio.charset.Charset;

/**
 * HEX editor that extends {@link TextArea} in order to support smart byte array editing.
 */
@PetepAPI
public class HexEditor extends TextArea implements BytesEditorComponent {
    public HexEditor() {
        super();

        setWrapText(true);
        setTextFormatter(new TextFormatter<>(this::formatTextChange));
        focusedProperty().addListener(this::onTextFocusChange);
    }

    @Override
    public void setBytes(byte[] bytes, int size, Charset charset) {
        setTextWithUndo(BytesUtils.bytesToHexString(bytes, size));
    }

    @Override
    public byte[] getBytes() {
        formatTextAndRecalculateCaretPositionWithPadding(getText());
        return BytesUtils.hexStringToBytes(getText());
    }

    @Override
    public void selectBytes(IndexRange selectionRange) {
        var start = selectionRange.getStart() * 3;
        var end = selectionRange.getEnd() * 3 - 1;

        if (end <= start) {
            return;
        }

        selectRange(start, end);
    }

    @Override
    public IndexRange getBytesSelection() {
        var hexData = getText();
        var selectedRange = getSelection();

        var start = selectedRange.getStart();
        var end = selectedRange.getEnd();
        if (end <= start) {
            return new IndexRange(0, 0);
        }

        start = 3 * ((start + 1) / 3);
        end = 3 * ((end - 1) / 3) + 2;
        if (end <= start) {
            return new IndexRange(0, 0);
        }

        var beforeSelectedData = BytesUtils.hexStringToBytes(hexData.substring(0, start));
        var selectedData = BytesUtils.hexStringToBytes(hexData.substring(start, end));

        return new IndexRange(beforeSelectedData.length, beforeSelectedData.length + selectedData.length);
    }

    /**
     * Format text change (allowed characters and delete behaviour)
     */
    private TextFormatter.Change formatTextChange(TextFormatter.Change change) {
        if (!change.isContentChange()) {
            return change;
        }

        // Restrict characters
        if (change.getText() != null) {
            change.setText(HexEditorFormatter.normalizeHexTextCharacters(change.getText()));
        }

        // Let addition get processed right away
        if (change.isAdded()) {
            return formatChange(change);
        }

        // Smart backspace and delete
        if (change.isDeleted()) {
            return formatTextDeleteChange(change);
        }

        return formatChange(change);
    }

    /**
     * Formats the text and finds minimal change required to achieve the target state
     * (makes UNDO/REDO work with minimal effort)
     */
    private TextFormatter.Change formatChange(TextFormatter.Change change) {
        if (!change.isContentChange()) {
            return change;
        }

        var textToFormat = change.getControlNewText();
        var formattingResult = HexEditorFormatter.formatTextAndRecalculateCaretPosition(
                textToFormat,
                change.getAnchor(),
                change.getCaretPosition()
        );
        var formattedText = formattingResult.getText();
        if (formattedText.equals(textToFormat)) {
            return change;
        }

        // Find common start
        var originalText = change.getControlText();
        int startIndex = 0;
        for (; startIndex < formattedText.length(); ++startIndex) {
            if (startIndex >= originalText.length()
                    || formattedText.charAt(startIndex) != originalText.charAt(startIndex)) {
                break;
            }
        }

        // Find common end
        int originalTextEndIndex = originalText.length() - 1;
        int formattedTextEndIndex = formattedText.length() - 1;
        while (originalTextEndIndex >= 0 && originalTextEndIndex > startIndex
                && formattedTextEndIndex >= 0  && formattedTextEndIndex > startIndex) {
            if (formattedText.charAt(formattedTextEndIndex) != originalText.charAt(originalTextEndIndex)) {
                break;
            }
            --originalTextEndIndex;
            --formattedTextEndIndex;
        }

        // Find out, what is the minimal change we have to do
        if (formattedText.length() < originalText.length()) {
            // Either deleting or replacing with shorter data
            if (startIndex <= formattedTextEndIndex) {
                change.setText(formattedText.substring(startIndex, formattedTextEndIndex + 1));
            }
            change.setRange(startIndex, originalTextEndIndex + 1);
        } else {
            // Appending or replacing with longer data
            change.setText(formattedText.substring(startIndex, formattedTextEndIndex + 1));
            change.setRange(startIndex, originalTextEndIndex + 1);
        }

        change.setAnchor(formattingResult.getAnchorPosition());
        change.setCaretPosition(formattingResult.getCaretPosition());
        return change;
    }

    /**
     * Process smart delete behaviour (delete/backspace)
     */
    private TextFormatter.Change formatTextDeleteChange(TextFormatter.Change change) {
        // If more than one character is deleted, just proceed
        var rangeChange = change.getRangeEnd() - change.getRangeStart();
        if (rangeChange != 1) {
            return formatChange(change);
        }

        // If text is defined, it is not just deletion
        if (change.getText().length() != 0) {
            return formatChange(change);
        }

        // If the deleted character is not space, just proceed
        var spaceCharIndex = change.getRangeStart();
        var controlText = change.getControlText();
        var changedChar = controlText.charAt(spaceCharIndex);
        if (' ' != changedChar) {
            return formatChange(change);
        }

        // Detect backspace or delete button based on changed caret position
        var caretPositionChange = change.getCaretPosition() - change.getControlCaretPosition();
        if (caretPositionChange != -1 && caretPositionChange != 0) {
            return formatChange(change);
        }

        // If there is just a single character on the left, use the delete to squash the hex together
        var charBeforeSpaceIsAlone = spaceCharIndex - 2 < 0
                || ' ' == controlText.charAt(spaceCharIndex - 2);
        if (charBeforeSpaceIsAlone) {
            return formatChange(change);
        }

        // If there is just a single character on the right, use the delete to squash the hex together
        var charAfterSpaceIsAloneBeforeStringEnd = spaceCharIndex + 2 == controlText.length();
        var charAfterSpaceIsAloneInTheMiddle = spaceCharIndex + 2 < controlText.length()
                && ' ' == controlText.charAt(spaceCharIndex + 2);
        var charAfterSpaceIsAlone = charAfterSpaceIsAloneBeforeStringEnd || charAfterSpaceIsAloneInTheMiddle;
        if (charAfterSpaceIsAlone) {
            var charOnTheLeftBeforeSpace = controlText.charAt(spaceCharIndex - 1);
            var charOnTheRightAfterSpace = controlText.charAt(spaceCharIndex + 1);
            change.setText(" " + charOnTheLeftBeforeSpace + charOnTheRightAfterSpace);
            change.setRange(spaceCharIndex - 1, spaceCharIndex + 2);
            change.setCaretPosition(change.getCaretPosition() + 1);
            change.setAnchor(change.getCaretPosition());
            return formatChange(change);
        }

        // Backspace on space character only moves caret to the left
        if (caretPositionChange == -1) {
            change.setRange(0, 0);
            return change;
        }

        // Delete on space character only moves caret to the right
        change.setRange(0, 0);
        change.setCaretPosition(change.getCaretPosition() + 1);
        change.setAnchor(change.getCaretPosition());
        return change;
    }

    /**
     * Formats the text input when focus changes.
     */
    private void onTextFocusChange(ObservableValue<? extends Boolean> observable, boolean oldValue, boolean newValue) {
        Platform.runLater(() -> formatTextAndRecalculateCaretPositionWithPadding(getText()));
    }

    private void formatTextAndRecalculateCaretPositionWithPadding(String textToFormat) {
        var formattingResult = HexEditorFormatter.formatTextAndRecalculateCaretPositionWithPadding(
                textToFormat,
                getAnchor(),
                getCaretPosition()
        );
        if (formattingResult.getText().equals(textToFormat)) {
            return;
        }

        setTextWithUndo(formattingResult.getText());
        positionCaret(formattingResult.getAnchorPosition());
        selectPositionCaret(formattingResult.getCaretPosition());
    }

    private void setTextWithUndo(String newText) {
        if (shouldCreateUndo()) {
            replaceText(0, getLength(), newText);
        } else {
            setText(newText);
        }
    }

    private boolean shouldCreateUndo() {
        return isEditable() && (isUndoable() || isRedoable() || getLength() > 0);
    }
}
