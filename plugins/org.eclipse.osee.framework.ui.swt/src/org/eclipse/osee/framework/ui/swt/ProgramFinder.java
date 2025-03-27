/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.framework.ui.swt;

import org.eclipse.osee.framework.core.data.MicrosoftOfficeApplicationEnum;
import org.eclipse.swt.program.Program;

/**
 * Provides a temporary workaround for a Windows 10 or Eclipse bug where the new Windows 10 Photo viewer application is
 * seemingly not included in a program list retrieved from the org.eclipse.swt.program findProgram() function. Since, by
 * default, this new Photo viewing application is the default viewer in Windows 10, no application will be associated
 * with all media types when otherwise utilizing this function. This class considers all applicable media types based on
 * the given parameter extension, then associates the legacy Windows Photo Viewer application, which is still included
 * with Windows 10 installations. See submitted bug at this link: https://bugs.eclipse.org/bugs/show_bug.cgi?id=534441
 *
 * @author Dominic A. Guss
 */
public class ProgramFinder {

   private enum MediaExtensions {
      png,
      bmp,
      tif,
      tiff,
      jpg,
      jpeg,
      gif;
   }

   private static boolean isMediaFile(String extension) {
      try {
         MediaExtensions.valueOf(extension);
         return true;
      } catch (IllegalArgumentException e) {
         return false;
      }
   }

   public static Program findProgram(String extension, MicrosoftOfficeApplicationEnum msoApplication) {
      if (extension.equals("xml") && !msoApplication.equals(MicrosoftOfficeApplicationEnum.SENTINEL)) {
         switch (msoApplication) {
            case WORD_DOCUMENT:
            case EXCEL_SPREADSHEET:
            case POWERPOINT_SHOW:
            case VISIO_DRAWING:
            case PUBLISHER_DOCUMENT:
            case ACCESS_DATABASE:
            case INFOPATH_FORM:
            case PROJECT_FILE:
            case EXCEL_CHART:
               return findProgram(msoApplication.getFileExtension());
            default:
               break;
         }
      }
      return findProgram(extension);
   }

   public static Program findProgram(String extension) {
      Program program = Program.findProgram(extension);
      if (program == null && isMediaFile(extension.toLowerCase())) {
         Program[] programs = Program.getPrograms();
         for (Program prog : programs) {
            if (prog.getName().equals("PhotoViewer.FileAssoc.Tiff")) {
               program = prog;
               break;
            }
         }
      }
      return program;
   }
}
