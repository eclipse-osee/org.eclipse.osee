/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.testscript.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.ws.rs.core.StreamingOutput;

public class TmoFileStreamingOutput implements StreamingOutput {

   private final File tmoFile;

   public TmoFileStreamingOutput(File tmoFile) {
      this.tmoFile = tmoFile;
   }

   @Override
   public void write(OutputStream os) {
      try {
         FileInputStream fis = new FileInputStream(tmoFile);
         ZipInputStream zis = new ZipInputStream(fis);
         // There should only be one file per zip
         ZipEntry tmoEntry = zis.getNextEntry();
         if (tmoEntry != null) {
            zis.transferTo(os);
         }
         zis.close();
         fis.close();
      } catch (IOException ex) {
         System.out.println(ex);
      }
   }

}