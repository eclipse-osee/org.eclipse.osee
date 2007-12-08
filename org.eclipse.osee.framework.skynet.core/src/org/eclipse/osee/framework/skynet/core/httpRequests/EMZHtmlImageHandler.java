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
package org.eclipse.osee.framework.skynet.core.httpRequests;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * @author Roberto E. Escobar
 */
public class EMZHtmlImageHandler implements IHtmlImageHandler {

   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(EMZHtmlImageHandler.class);
   private static final byte[] EMZ_HEADER = new byte[] {31, -117, 8, 0, 0, 0, 0, 0, 2, 11};

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.util.IHtmlImageSupport#convert(java.io.InputStream,
    *      java.io.OutputStream)
    */
   public void convert(InputStream is, OutputStream os) throws IOException {
      byte[] data = new byte[2048];
      GZIPInputStream zin = new GZIPInputStream(new BufferedInputStream(is));
      int numberOfBytes = 0;
      while ((numberOfBytes = zin.read(data, 0, data.length)) != -1) {
         os.write(data, 0, numberOfBytes);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.util.IHtmlImageSupport#isValid(java.io.InputStream)
    */
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
                  toReturn &= (header[index] == EMZ_HEADER[index]);
               }
            }
         } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception during isValid check. ", ex);
         } finally {
            try {
               is.reset();
            } catch (IOException ex) {
               logger.log(Level.WARNING, "Exception during isValid check. ", ex);
            }
         }
      }
      return toReturn;
   }
}
