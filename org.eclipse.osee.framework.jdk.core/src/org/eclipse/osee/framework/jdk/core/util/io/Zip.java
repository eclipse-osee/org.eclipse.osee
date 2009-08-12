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
package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Zip {

   public static void zip(String[] filenames, String outFilename) {
      // These are the files to include in the ZIP file
      // String[] filenames = new String[]{"filename1", "filename2"};

      // Create a buffer for reading the files
      byte[] buf = new byte[1024];

      try {
         // Create the ZIP file
         ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

         // Compress the files
         for (int i = 0; i < filenames.length; i++) {
            FileInputStream in = new FileInputStream(filenames[i]);

            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(filenames[i]));

            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
               out.write(buf, 0, len);
            }

            // Complete the entry
            out.closeEntry();
            in.close();
         }

         // Complete the ZIP file
         out.close();
      } catch (IOException e) {
      }

   }

   public static void unzip(File zipFile, File destinationDir, IZipEntryCompleteCallback progressBar) throws IOException {
      int BUFFER = 2048;
      BufferedOutputStream dest = null;
      BufferedInputStream is = null;
      ZipEntry entry = null;

      try {
         ZipFile zipfile = new ZipFile(zipFile.getAbsolutePath());
         progressBar.setValue(0);
         progressBar.setMinimum(0);
         progressBar.setMaximum(zipfile.size());
         System.out.println(zipfile.size());
         Enumeration<? extends ZipEntry> e = zipfile.entries();
         int size = 0;
         while (e.hasMoreElements()) {
            entry = e.nextElement();
            is = new BufferedInputStream(zipfile.getInputStream(entry));
            int count;
            byte data[] = new byte[BUFFER];
            File fileDir = new File(destinationDir.getAbsolutePath() + File.separator + entry.getName());
            if (entry.isDirectory()) {
               fileDir.mkdirs();
               progressBar.setValue(++size);
               continue;
            } else {
               fileDir.getParentFile().mkdirs();
            }

            if (!fileDir.exists() || fileDir.exists() && fileDir.canWrite()) {
               FileOutputStream fos = new FileOutputStream(fileDir.getAbsolutePath());
               dest = new BufferedOutputStream(fos, BUFFER);
               while ((count = is.read(data, 0, BUFFER)) != -1) {
                  dest.write(data, 0, count);
               }
               dest.flush();
               dest.close();
            }

            is.close();

            if (fileDir.getAbsolutePath().endsWith(".lnk")) {
               if (fileDir.canWrite()) {
                  fileDir.setReadOnly();
               }
            }
            progressBar.setValue(++size);
         }
      } catch (RuntimeException ex) {
         String information =
               "ZipFile: " + (zipFile != null ? zipFile.getAbsolutePath() : "NULL") + "\n" + "DestinationDir: " + (destinationDir != null ? destinationDir.getAbsolutePath() : "NULL") + "\n" + "Entry Processed: " + (entry != null ? entry.toString() : "NULL") + "\n";
         throw new IOException(information + ex.getMessage());
      }
   }
}