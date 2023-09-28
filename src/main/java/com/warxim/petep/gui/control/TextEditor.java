package com.warxim.petep.gui.control;

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.util.GuiUtils;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * Text editor that extends {@link TextArea} in order to correctly support clipboard.
 */
@PetepAPI
public class TextEditor extends TextArea {
    @Override
    public void copy() {
        // Unformat text before copying it to clipboard
        var selectedText = getSelectedText();
        var unformattedText = GuiUtils.unformatText(selectedText);
        if (unformattedText.length() == 0) {
            return;
        }

        // Store unformatted text to clipboard
        var content = new ClipboardContent();
        content.putString(unformattedText);
        Clipboard.getSystemClipboard().setContent(content);
    }

    @Override
    public void paste() {
        // Get clipboard content
        var clipboard = Clipboard.getSystemClipboard();
        if (!clipboard.hasString()) {
            return;
        }

        var clipboardText = clipboard.getString();
        if (clipboardText == null) {
            return;
        }

        // Format text before displaying it in text editor
        var formattedText = GuiUtils.formatText(clipboardText);
        replaceSelection(formattedText);
    }
}
