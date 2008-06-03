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
package org.eclipse.osee.framework.database.utility;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.db.DbConfigFileInformation;

// TODO: this class has some overlap with methods provided in org.eclipse.osee.framework.jdk.core.Lib
public class FileUtility {

   public static boolean isValidDirectory(File directory) {
      if (directory != null && directory.exists() && directory.canRead()) {
         File[] listOfFiles = directory.listFiles();
         if (listOfFiles.length != 0) {
            return true;
         }
      }
      return false;
   }

   public static void setupDirectoryForWrite(File directory) throws IOException {
      if (directory.exists() && directory.canWrite()) {
         if (!directory.isDirectory()) {
            directory.mkdirs();
         }
      } else {
         directory.mkdirs();
      }
   }

   public static List<File> getFileList(File sourceDirectory, final String extension) {
      File[] listOfFiles = sourceDirectory.listFiles(new FilenameFilter() {
         public boolean accept(File directoryName, String filename) {
            return filename.endsWith(extension) && (new File(directoryName + File.separator + filename)).canRead();
         }
      });
      return Arrays.asList(listOfFiles);
   }

   public static List<URL> getSchemaFileList(File sourceDirectory) {
      List<File> files = getFileList(sourceDirectory, DbConfigFileInformation.getSchemaFileExtension());
      List<URL> streams = new ArrayList<URL>();
      for (File file : files) {
         try {
            streams.add(file.toURI().toURL());
         } catch (MalformedURLException e) {
            e.printStackTrace();
         }
      }
      return streams;
   }

   public static List<File> getDBDataFileList(File sourceDirectory) {
      return getFileList(sourceDirectory, DbConfigFileInformation.getDbDataFileExtension());
   }

   public static List<URL> getDBDataFileListInputStream(File sourceDirectory) {
      List<File> files = getFileList(sourceDirectory, DbConfigFileInformation.getDbDataFileExtension());
      List<URL> streams = new ArrayList<URL>();
      for (File file : files) {
         try {
            streams.add(file.toURI().toURL());
         } catch (MalformedURLException e) {
            e.printStackTrace();
         }
      }
      return streams;
   }
}
