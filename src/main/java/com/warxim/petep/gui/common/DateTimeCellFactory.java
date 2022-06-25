/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2021 Michal Válka
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
package com.warxim.petep.gui.common;

import com.warxim.petep.common.Constant;
import javafx.scene.control.TableCell;

import java.time.LocalDateTime;

/**
 * Reusable date time cell factory for displaying local date time.
 * @param <T> Type of cell item type
 */
public class DateTimeCellFactory<T> extends TableCell<T, LocalDateTime> {
    @Override
    protected void updateItem(LocalDateTime dateTime, boolean empty) {
        super.updateItem(dateTime, empty);

        setText(empty ? null : Constant.DATE_TIME_FORMATTER.format(dateTime));
    }
}
