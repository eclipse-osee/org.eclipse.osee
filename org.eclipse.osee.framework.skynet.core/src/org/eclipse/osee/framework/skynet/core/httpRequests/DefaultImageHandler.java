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

/**
 * @author Roberto E. Escobar
 */
public class DefaultImageHandler implements IHtmlImageHandler {

   private static final DefaultImageHandler instance = new DefaultImageHandler();

   public static DefaultImageHandler getInstance() {
      return instance;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.util.IHtmlImageSupport#convert(java.io.InputStream,
    *      java.io.OutputStream)
    */
   public void convert(InputStream is, OutputStream os) throws IOException {
      BufferedInputStream bis = new BufferedInputStream(is);
      byte[] buffer = new byte[100000];
      int numberOfBytes = -1;
      while ((numberOfBytes = bis.read(buffer)) != -1) {
         os.write(buffer, 0, numberOfBytes);
      }
      os.flush();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.util.IHtmlImageSupport#isValid(java.io.InputStream)
    */
   public boolean isValid(InputStream is) {
      return true;
   }
}
