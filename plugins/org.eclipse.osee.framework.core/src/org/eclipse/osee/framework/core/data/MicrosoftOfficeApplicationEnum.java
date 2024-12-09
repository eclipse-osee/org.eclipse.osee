/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.core.data;

/**
 * Enum representing Microsoft Office applications.
 *
 * @author Jaden W. Puckett
 */
public enum MicrosoftOfficeApplicationEnum {
   EXCEL_SPREADSHEET("Excel.Sheet", ".xlsx"),
   WORD_DOCUMENT("Word.Document", ".docx"),
   POWERPOINT_SHOW("PowerPoint.Show", ".pptx"),
   VISIO_DRAWING("Visio.Drawing", ".vsdx"),
   PUBLISHER_DOCUMENT("Publisher.Document", ".pub"),
   ACCESS_DATABASE("Access.Database", ".accdb"),
   INFOPATH_FORM("InfoPath.Form", ".xml"),
   PROJECT_FILE("Project.Project", ".mpp"),
   EXCEL_CHART("Excel.Chart", ".xlsx"),
   SENTINEL("Sentinel", "");

   private final String applicationName;
   private final String fileExtension;

   MicrosoftOfficeApplicationEnum(String applicationName, String fileExtension) {
      this.applicationName = applicationName;
      this.fileExtension = fileExtension;
   }

   public String getApplicationName() {
      return applicationName;
   }

   public String getFileExtension() {
      return fileExtension;
   }

   /**
    * Retrieves an enum constant by its application name.
    *
    * @param name the application name to match.
    * @return the matching {@link MicrosoftOfficeApplicationEnum} enum constant, or {@code SENTINEL} if no match is
    * found.
    */
   public static MicrosoftOfficeApplicationEnum fromApplicationName(String name) {
      for (MicrosoftOfficeApplicationEnum app : values()) {
         if (app.applicationName.equalsIgnoreCase(name)) {
            return app;
         }
      }
      return SENTINEL;
   }
}
