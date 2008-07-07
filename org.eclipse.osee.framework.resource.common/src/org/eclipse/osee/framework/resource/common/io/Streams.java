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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Roberto E. Escobar
 */
public class Streams {

   public static void inputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) throws IOException {
      byte[] buf = new byte[10000];
      int count = -1;
      while ((count = inputStream.read(buf)) != -1) {
         outputStream.write(buf, 0, count);
      }
      inputStream.close();
   }

   public static String decompressStream(InputStream inputStream, OutputStream outputStream) throws IOException {
      String zipEntryName = null;
      ZipInputStream zipInputStream = null;
      try {
         zipInputStream = new ZipInputStream(inputStream);
         ZipEntry entry = zipInputStream.getNextEntry();
         zipEntryName = entry.getName();
         // Transfer bytes from the ZIP file to the output file
         byte[] buf = new byte[1024];
         int len;
         while ((len = zipInputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
         }
      } finally {
         if (zipInputStream != null) {
            zipInputStream.close();
         }
      }
      return zipEntryName;
   }

   public static byte[] compressStream(InputStream in, String name) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream out = null;
      try {
         out = new ZipOutputStream(bos);
         // Add ZIP entry to output stream.
         out.putNextEntry(new ZipEntry(name));
         byte[] buf = new byte[1024];
         int count = -1;
         while ((count = in.read(buf)) > 0) {
            out.write(buf, 0, count);
         }
      } finally {
         if (out != null) {
            out.closeEntry();
            out.close();
         }
      }
      return bos.toByteArray();
   }
}
