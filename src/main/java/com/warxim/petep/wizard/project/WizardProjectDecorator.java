/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2020 Michal Válka
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
package com.warxim.petep.wizard.project;

import com.warxim.petep.project.Project;
import lombok.Data;

import java.time.Instant;

/**
 * Project decorator (containing project, path to the project and date of project file changes).
 */
@Data
public final class WizardProjectDecorator {
    private String path;
    private Instant created;
    private Project project;

    /**
     * Project decorator constructor.
     * @param project Project to be wrapped
     * @param path Path to the project
     * @param date Instant of creation/update of the project
     */
    public WizardProjectDecorator(Project project, String path, Instant date) {
        this.project = project;
        this.path = path;
        this.created = date;
    }

    /**
     * Obtains name of the project.
     * @return Project name
     */
    public String getName() {
        return project.getName();
    }

    /**
     * Obtains description of the project.
     * @return Project description
     */
    public String getDescription() {
        return project.getDescription();
    }

    /**
     * Sets time of creation of this project (when it has been created/modified).
     * @param created Project creation/update time
     */
    public void setCreated(Instant created) {
        this.created = created;
    }

    /**
     * Sets underlying project.
     * @param project Project
     */
    public void setProject(Project project) {
        this.project = project;
    }
}
