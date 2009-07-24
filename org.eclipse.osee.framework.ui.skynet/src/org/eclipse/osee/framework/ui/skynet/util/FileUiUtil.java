/*
 * Created on Jun 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Megumi Telles
 */
public class FileUiUtil {
   public static String filenameWarningMessage =
         "\n\nis approaching a large size which may cause the opening application to error. " + "\nSuggest moving your workspace to avoid potential errors. ";

   public static boolean ensureFilenameLimit(IFile file) {
      boolean withinLimit = true;
      String absPath = file.getLocation().toFile().getAbsolutePath();
      if (absPath.length() > 215) {
         String warningMessage = "Your filename: \n\n" + absPath + filenameWarningMessage;
         // need to warn user that their filename size is large and may cause the program (Word, Excel, PPT) to error
         WordUiUtil.displayWarningMessageDialog("Filename Size Warning", warningMessage);
         OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, warningMessage);
         withinLimit = false;
      }
      return withinLimit;
   }

}
