/*
 * Created on May 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.resource.common.io;

import java.io.File;

/**
 * @author Roberto E. Escobar
 */
public class Files {

   /**
    * Delete the current file and all empty parents. The method will stop deleting empty parents once it reaches the
    * stopAt parent.
    * 
    * @param stopAt path of the parent file to stop deleting at
    * @param file to delete
    * @return status <b>true</b> if successful
    */
   public static boolean deleteFileAndEmptyParents(String stopAt, File file) {
      boolean result = true;
      if (file != null) {
         if (file.isDirectory() != false) {
            if (file.list().length == 0) {
               result &= file.delete();
            }
         } else {
            result &= file.delete();
         }
      }
      File parent = file.getParentFile();
      if (parent != null && parent.getAbsolutePath().equals(stopAt) != true) {
         result &= deleteFileAndEmptyParents(stopAt, parent);
      }
      return result;
   }

   public static boolean isCompressed(File file) {
      boolean toReturn = false;
      String ext = getExtension(file.getAbsolutePath());
      if (ext.equals("zip")) {
         toReturn = true;
      }
      return toReturn;
   }

   private static boolean isWindows() {
      return System.getProperty("os.name").indexOf("indows") != -1;
   }

   public static String getExtension(String filepath) {
      filepath = filepath.trim();
      String separatorRegEx = File.separator;
      if (isWindows() != false) {
         separatorRegEx = "\\\\";
      }
      String[] pathsArray = filepath.split(separatorRegEx);

      String fileName = pathsArray[0];
      if (pathsArray.length > 0) {
         fileName = pathsArray[pathsArray.length - 1];
      }

      int index = fileName.lastIndexOf('.');
      if (index >= 0 && index + 1 < fileName.length()) {
         return fileName.substring(index + 1);
      } else {
         return "";
      }
   }

   public static String removeExtension(String path) {
      String ext = getExtension(path);
      if (ext != null && ext.length() > 0) {
         path = path.substring(0, path.length() - ext.length());
      }
      return path;
   }
}
