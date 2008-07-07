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

   /**
    * Determine if file is a compressed file
    * 
    * @param file to check
    * @return <b>true</b> if the files is a compressed file
    */
   public static boolean isCompressed(File file) {
      boolean toReturn = false;
      String ext = getExtension(file.getAbsolutePath());
      if (ext.equals("zip")) {
         toReturn = true;
      }
      return toReturn;
   }

   /**
    * Determine is OS is windows
    * 
    * @return <b>true</b> if OS is windows
    */
   private static boolean isWindows() {
      return System.getProperty("os.name").indexOf("indows") != -1;
   }

   /**
    * Get file extension from the file path
    * 
    * @param filepath
    * @return file extension
    */
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

   /**
    * Remove the file extension from the file path
    * 
    * @param filepath
    * @return modified file path
    */
   public static String removeExtension(String filepath) {
      String ext = getExtension(filepath);
      if (ext != null && ext.length() > 0) {
         filepath = filepath.substring(0, filepath.length() - ext.length());
      }
      return filepath;
   }

   /**
    * Deletes all files from directory
    * 
    * @param directory
    */
   public static void emptyDirectory(File directory) {
      File[] children = directory.listFiles();
      if (children != null) {
         for (File child : children) {
            if (child.isDirectory()) {
               emptyDirectory(child);
            } else { // else is a file
               child.delete();
            }
         }
      }
   }
}
