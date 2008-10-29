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
package org.eclipse.osee.framework.resource.provider.common.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Roberto E. Escobar
 */
public class Streams {

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

}
