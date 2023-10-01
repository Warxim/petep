/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2023 Michal Válka
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <https://www.gnu.org/licenses/>.
 */
package com.warxim.petep.gui.control.byteseditor;

import com.warxim.petep.common.Constant;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.util.BytesUtils;
import com.warxim.petep.util.GuiUtils;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.nio.charset.Charset;

/**
 * Text editor that extends {@link TextArea} in order to correctly support clipboard.
 */
@PetepAPI
public class TextEditor extends TextArea implements BytesEditorComponent {
    private Charset charset;

    public TextEditor() {
        super();

        charset = Constant.DEFAULT_CHARSET;

        setWrapText(true);
    }

    @Override
    public void setBytes(byte[] bytes, int size, Charset charset) {
        this.charset = charset;

        setTextWithUndo(GuiUtils.formatText(BytesUtils.getString(bytes, size, charset)));
    }

    @Override
    public byte[] getBytes() {
        return GuiUtils.unformatText(getText()).getBytes(charset);
    }

    @Override
    public void selectBytes(IndexRange selectionRange) {
        var start = selectionRange.getStart();
        var end = selectionRange.getEnd();

        if (end <= start) {
            return;
        }

        var textBytes = GuiUtils.unformatText(getText()).getBytes(charset);
        var beforeText = GuiUtils.formatText(new String(textBytes, 0, start, charset));
        var selectedText = GuiUtils.formatText(new String(textBytes, start, end - start, charset));

        selectRange(beforeText.length(), beforeText.length() + selectedText.length());
    }

    @Override
    public IndexRange getBytesSelection() {
        var text = getText();
        var selectedRange = getSelection();
        var start = selectedRange.getStart();
        var end = selectedRange.getEnd();

        var beforeSelectedDataBytesLength = GuiUtils.unformatText(text.substring(0, start))
                .getBytes(charset)
                .length;
        var selectedDataBytesLength = GuiUtils.unformatText(text.substring(start, end))
                .getBytes(charset)
                .length;

        return new IndexRange(
                beforeSelectedDataBytesLength,
                beforeSelectedDataBytesLength + selectedDataBytesLength
        );
    }

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
