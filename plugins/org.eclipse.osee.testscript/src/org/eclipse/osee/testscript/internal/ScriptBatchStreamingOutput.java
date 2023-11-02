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
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.ws.rs.core.StreamingOutput;

public class ScriptBatchStreamingOutput implements StreamingOutput {

   private final File[] tmoFiles;
   private final ScriptBatchToken batch;

   public ScriptBatchStreamingOutput(File[] tmoFiles, ScriptBatchToken batch) {
      this.tmoFiles = tmoFiles;
      this.batch = batch;
   }

   @Override
   public void write(OutputStream os) {
      ZipOutputStream zipOut = new ZipOutputStream(os);
      try {
         for (File tmo : tmoFiles) {
            FileInputStream fis = new FileInputStream(tmo);
            ZipInputStream zis = new ZipInputStream(fis);
            // There should only be one file per zip
            ZipEntry tmoEntry = zis.getNextEntry();
            if (tmoEntry != null) {
               zipOut.putNextEntry(new ZipEntry(tmoEntry.getName()));
               zis.transferTo(zipOut);
               zipOut.closeEntry();
            }
            zis.close();
            fis.close();
         }
         zipOut.putNextEntry(new ZipEntry("runId.txt"));
         zipOut.write(new String(batch.getTestEnvBatchId()).getBytes(Charset.forName("UTF-8")));
         zipOut.closeEntry();
         zipOut.close();
      } catch (IOException ex) {
         System.out.println(ex);
      }
   }

}
