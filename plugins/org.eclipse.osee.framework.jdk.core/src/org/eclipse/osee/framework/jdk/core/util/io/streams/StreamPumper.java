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

package org.eclipse.osee.framework.jdk.core.util.io.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for convenience methods with getting information from InputStream's to OutputStream's.
 * 
 * @author Robert A. Fisher
 */
public class StreamPumper {

   /**
    * Pumps all data from the InputStream to the OutputStream through an in place 2k buffer.
    */
   public static void pumpData(InputStream in, OutputStream out) throws IOException {
      if (in == null) {
         throw new IllegalArgumentException("in can not be null");
      }
      if (out == null) {
         throw new IllegalArgumentException("out can not be null");
      }

      int numBytesRead;
      byte[] bytes = new byte[2024];
      while ((numBytesRead = in.read(bytes)) != -1) {
         out.write(bytes, 0, numBytesRead);
      }
   }
}
