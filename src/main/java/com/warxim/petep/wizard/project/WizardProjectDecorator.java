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

import java.util.Date;
import com.warxim.petep.project.Project;

/** Project decorator (containing project, path to the project and date of project file changes). */
public final class WizardProjectDecorator {
  private String path;
  private Date date;
  private Project project;

  /** Project decorator constructor. */
  public WizardProjectDecorator(Project project, String path, Date date) {
    this.project = project;
    this.path = path;
    this.date = date;
  }

  public String getPath() {
    return path;
  }

  public Date getDate() {
    return date;
  }

  public String getName() {
    return project.getName();
  }

  public String getDescription() {
    return project.getDescription();
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
