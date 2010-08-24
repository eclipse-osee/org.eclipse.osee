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
package org.eclipse.osee.framework.core.datastore.schema.operations;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: this class has some overlap with methods provided in org.eclipse.osee.framework.jdk.core.Lib
public final class FileUtility {

   public static String SCHEMA_EXTENSION = ".SCHEMA.xml";
   public static String DB_DATA_EXTENSION = ".DATA.xml";

   private FileUtility() {
      // Utility class
   }

   public static boolean isValidDirectory(File directory) {
      if (directory != null && directory.exists() && directory.canRead()) {
         File[] listOfFiles = directory.listFiles();
         if (listOfFiles.length != 0) {
            return true;
         }
      }
      return false;
   }

   public static void setupDirectoryForWrite(File directory) {
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
         @Override
         public boolean accept(File directoryName, String filename) {
            return filename.endsWith(extension) && new File(directoryName + File.separator + filename).canRead();
         }
      });
      return Arrays.asList(listOfFiles);
   }

   public static List<URL> getSchemaFileList(File sourceDirectory) {
      List<File> files = getFileList(sourceDirectory, SCHEMA_EXTENSION);
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
      return getFileList(sourceDirectory, DB_DATA_EXTENSION);
   }

   public static List<URL> getDBDataFileListInputStream(File sourceDirectory) {
      List<File> files = getFileList(sourceDirectory, DB_DATA_EXTENSION);
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
