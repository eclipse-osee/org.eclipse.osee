/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class JarCreator {
   private final JarOutputStream out;
   private final byte[] buffer;

   public JarCreator(File path, String title, String version) throws FileNotFoundException, IOException {
      super();
      Manifest manifest = new Manifest();
      Attributes attributes = manifest.getMainAttributes();
      attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
      attributes.putValue("Implementation-Title", title);
      attributes.putValue("Implementation-Version", version);
      out = new JarOutputStream(new FileOutputStream(path), manifest);
      buffer = new byte[20480];
   }

   private void addFile(File file, String pathInJar) throws IOException {

      out.putNextEntry(new JarEntry(pathInJar.replace('\\', '/')));

      // Read the file and write it to the jar.
      try (FileInputStream in = new FileInputStream(file)) {
         int bytesRead;
         while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
         }
      }
   }

   private void addRelativeToPosition(File path, int rootPathPos, FileFilter filenameFilter) throws IOException {
      if (path.isDirectory()) {
         File[] files = path.listFiles(filenameFilter);
         if (files != null) {
            for (int i = 0; i < files.length; i++) {
               addRelativeToPosition(files[i], rootPathPos, filenameFilter);
            }
         }
      } else {
         addFile(path, path.getAbsolutePath().substring(rootPathPos));
      }
   }

   /**
    * just an entry point into the recursive addRelativeTo so the (unchanging) rootPathPos can be computed once
    */
   private void addDirectoryContents(File directory, FileFilter filenameFilter) throws IOException {
      if (directory.isDirectory()) {
         int rootPathPos = directory.getPath().length() + 1;
         addRelativeToPosition(directory, rootPathPos, filenameFilter);
      } else {
         throw new IllegalArgumentException("Must be a directory: " + directory);
      }
   }

   public void addFileRelativeTo(File path, String relativeTo) throws IOException {
      addRelativeToPosition(path, relativeTo.length() + 1, null);
   }

   public void addDirectoryContents(File directory) throws IOException {
      addDirectoryContents(directory, (FilenameAndDirectoryFilter) null);
   }

   public void addDirectoryContents(File directory, String fileNamePattern) throws IOException {
      addDirectoryContents(directory, new FilenameAndDirectoryFilter(fileNamePattern));
   }

   public void close() throws IOException {
      out.close();
   }

   private static class FilenameAndDirectoryFilter implements FileFilter {
      private final Matcher matcher;

      public FilenameAndDirectoryFilter(String pattern) {
         this.matcher = Pattern.compile(pattern).matcher("");
      }

      @Override
      public boolean accept(File pathname) {
         if (pathname.isDirectory()) {
            return true;
         }
         matcher.reset(pathname.getName());
         return matcher.matches();
      }

   }
}
