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

package org.eclipse.osee.framework.ui.skynet.render.imageDetection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class EMZHtmlImageHandler {
   private static final byte[] EMZ_HEADER = new byte[] {31, -117, 8, 0, 0, 0, 0, 0, 2, 11};

   public void convert(InputStream is, OutputStream os) throws IOException {
      byte[] data = new byte[2048];
      GZIPInputStream zin = new GZIPInputStream(new BufferedInputStream(is));
      int numberOfBytes = 0;
      while ((numberOfBytes = zin.read(data, 0, data.length)) != -1) {
         os.write(data, 0, numberOfBytes);
      }
   }

   public boolean isValid(InputStream is) {
      boolean toReturn = false;
      if (is != null) {
         is.mark(EMZ_HEADER.length);
         byte[] header = new byte[EMZ_HEADER.length];
         try {
            int numberOfBytes = is.read(header);
            if (numberOfBytes == EMZ_HEADER.length) {
               toReturn = true;
               for (int index = 0; index < EMZ_HEADER.length; index++) {
                  toReturn &= header[index] == EMZ_HEADER[index];
               }
            }
         } catch (Exception ex) {
            OseeLog.log(this.getClass(), Level.WARNING, "Exception during isValid check. ", ex);
         } finally {
            try {
               is.reset();
            } catch (IOException ex) {
               OseeLog.log(this.getClass(), Level.WARNING, "Exception during isValid check. ", ex);
            }
         }
      }
      return toReturn;
   }
}
