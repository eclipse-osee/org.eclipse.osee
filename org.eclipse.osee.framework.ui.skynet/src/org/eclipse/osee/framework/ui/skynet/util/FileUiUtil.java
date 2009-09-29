/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Megumi Telles
 */
public class FileUiUtil {
   public static String FILENAME_WARNING_MESSAGE =
         "\n\nis approaching a large size which may cause the opening application to error. " + "\nSuggest moving your workspace to avoid potential errors. ";
   public final static int FILENAME_LIMIT = 215;
   public static boolean showAgain = true;

   public static boolean ensureFilenameLimit(IFile file) {
      boolean withinLimit = true;
      String absPath = file.getLocation().toFile().getAbsolutePath();
      if (absPath.length() > FILENAME_LIMIT) {
         String warningMessage = "Your filename: \n\n" + absPath + FILENAME_WARNING_MESSAGE;
         // need to warn user that their filename size is large and may cause the program (Word, Excel, PPT) to error
         if (showAgain) {
            //display warning once per session
            WordUiUtil.displayWarningMessageDialog("Filename Size Warning", warningMessage);
            showAgain = false;
         }
         //log the warning every time
         OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, warningMessage);
         withinLimit = false;
      }
      return withinLimit;
   }

}
