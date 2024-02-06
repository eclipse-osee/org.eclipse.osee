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

package org.eclipse.osee.framework.jdk.core.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * A collection of static methods for Zip compression and decompression.
 *
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

public class Zip {

   /**
    * Construction is private to prevent instantiation of the class.
    */

   private Zip() {
   }

   public static byte[] compressByteBuffer(ByteBuffer byteBuffer, String name) {
      return Zip.compressBytes(byteBuffer.array(), name);
   }

   public static byte[] compressBytes(byte[] bytes, String name) {

      try (var byteArrayInputStream = new ByteArrayInputStream(bytes)) {

         return Zip.compressStream(byteArrayInputStream, name);

      } catch (IOException e) {
         throw OseeCoreException.wrap(e);
      }
   }

   public static void compressDirectory(File directory, String zipTarget, boolean includeSubDirectories) throws IOException, IllegalArgumentException {
      if (directory.isDirectory() != true) {
         throw new IllegalArgumentException(String.format("Error source is not a directory: [%s]", directory));
      }
      if (Strings.isValid(zipTarget) != true) {
         throw new IllegalArgumentException("Error target zip filename is invalid");
      }
      ZipOutputStream outputStream = null;
      try {
         outputStream = new ZipOutputStream(new FileOutputStream(zipTarget));
         Zip.compressDirectory(directory.getPath(), directory, outputStream, includeSubDirectories);
      } finally {
         Lib.close(outputStream);
      }
   }

   private static void compressDirectory(String basePath, File source, ZipOutputStream outputStream, boolean includeSubDirectories) throws IOException {
      File[] children = source.listFiles();
      if (children != null) {
         for (File file : children) {
            if (file.isDirectory() != true) {
               compressFile(basePath, file, outputStream);
            } else {
               if (includeSubDirectories) {
                  compressDirectory(basePath, file, outputStream, includeSubDirectories);
               }
            }
         }
      }
   }

   public static byte[] compressFile(File file) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      ZipOutputStream outputStream = null;
      try {
         outputStream = new ZipOutputStream(bos);
         compressFile(null, file, outputStream);
         outputStream.closeEntry();
      } finally {
         Lib.close(outputStream);
         Lib.close(bos);
      }
      return bos.toByteArray();
   }

   public static void compressFile(String basePath, File file, ZipOutputStream outputStream) throws IOException {
      FileInputStream inputStream = null;
      try {
         inputStream = new FileInputStream(file);
         String entryName = file.getPath();
         if (Strings.isValid(basePath) && entryName.startsWith(basePath)) {
            if (basePath.endsWith(File.separator) != true) {
               basePath = basePath + File.separator;
            }
            entryName = entryName.replace(basePath, "");
         }
         ZipEntry entry = new ZipEntry(entryName);
         outputStream.putNextEntry(entry);
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } finally {
         Lib.close(inputStream);
      }
   }

   public static void compressFiles(String basePath, Collection<File> files, String zipTarget) throws IOException {
      if (Strings.isValid(zipTarget) != true) {
         throw new IllegalArgumentException("Error target zip filename is invalid");
      }
      ZipOutputStream out = null;
      try {
         out = new ZipOutputStream(new FileOutputStream(zipTarget));
         for (File file : files) {
            if (file.isDirectory() != true) {
               Zip.compressFile(basePath, file, out);
            }
         }
      } finally {
         Lib.close(out);
      }
   }

   public static byte[] compressStream(InputStream in, String name) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream out = null;
      try {
         out = new ZipOutputStream(bos);
         // Add ZIP entry to output stream.
         out.putNextEntry(new ZipEntry(name));
         Lib.inputStreamToOutputStream(in, out);
         out.closeEntry();
      } finally {
         Lib.close(out);
      }
      return bos.toByteArray();
   }

   public static byte[] compressString(String string, String name, Charset charset) {
      return Zip.compressBytes(string.getBytes(charset), name);
   }

   public static byte[] decompressBytes(InputStream inputStream) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      ZipInputStream in = null;
      try {
         in = new ZipInputStream(inputStream);
         in.getNextEntry();
         Lib.inputStreamToOutputStream(in, out);
      } finally {
         Lib.close(in);
         Lib.close(out);
      }
      return out.toByteArray();
   }

   public static void decompressStream(InputStream inputStream, File targetDirectory) throws IOException {
      ZipInputStream zipInputStream = null;
      try {
         zipInputStream = new ZipInputStream(inputStream);
         if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
         }
         ZipEntry entry = null;
         while ((entry = zipInputStream.getNextEntry()) != null) {
            String zipEntryName = entry.getName();

            OutputStream outputStream = null;
            try {
               File target = new File(targetDirectory, zipEntryName);
               if (!entry.isDirectory()) {
                  File parent = target.getParentFile();
                  if (parent != null && !parent.exists()) {
                     parent.mkdirs();
                  }
                  outputStream = new BufferedOutputStream(new FileOutputStream(target));
                  Lib.inputStreamToOutputStream(zipInputStream, outputStream);
               }
            } finally {
               Lib.close(outputStream);
            }
         }
      } finally {
         Lib.close(zipInputStream);
      }
   }

   public static String decompressStream(InputStream inputStream, OutputStream outputStream) {
      String zipEntryName = null;
      ZipInputStream zipInputStream = null;
      try {
         zipInputStream = new ZipInputStream(inputStream);
         ZipEntry entry = zipInputStream.getNextEntry();
         zipEntryName = entry.getName();
         // Transfer bytes from the ZIP file to the output file
         Lib.inputStreamToOutputStream(zipInputStream, outputStream);
      } catch (Exception ex) {
         throw new OseeCoreException("Failed to decompress zip stream");
      } finally {
         Lib.close(zipInputStream);
      }
      return zipEntryName;
   }

   public static void decompressStream(ZipInputStream zis, byte[] streamBuffer, File unzipLocation) {
      ZipEntry zipEntry;
      try {
         zipEntry = zis.getNextEntry();

         unzipLocation.mkdirs();
         while (zipEntry != null) {
            File uploadedDirectory = Zip.newFile(unzipLocation, zipEntry);
            if (zipEntry.isDirectory()) {
               if (!uploadedDirectory.isDirectory() && !uploadedDirectory.mkdirs()) {
                  zis.close();
                  throw new OseeCoreException("Failed to create directory " + uploadedDirectory);
               }
            } else {
               // fix for Windows-created archives
               File parent = uploadedDirectory.getParentFile();
               if (!parent.isDirectory() && !parent.mkdirs()) {
                  zis.close();
                  throw new OseeCoreException("Failed to create directory " + parent);
               }
               // write file content
               try (FileOutputStream fos = new FileOutputStream(uploadedDirectory);) {
                  int len;
                  while ((len = zis.read(streamBuffer)) > 0) {
                     fos.write(streamBuffer, 0, len);
                  }
               } catch (Exception ex) {
                  throw new OseeCoreException(ex);
               }
            }
            zipEntry = zis.getNextEntry();
         }
         zis.closeEntry();
         zis.close();
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   private static File newFile(File destinationDir, ZipEntry zipEntry) {
      File destFile = new File(destinationDir, zipEntry.getName());
      String destDirPath;
      try {
         destDirPath = destinationDir.getCanonicalPath();
         String destFilePath = destFile.getCanonicalPath();

         if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new OseeCoreException("Entry is outside of the target dir: " + zipEntry.getName());
         }
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
      return destFile;
   }

}
