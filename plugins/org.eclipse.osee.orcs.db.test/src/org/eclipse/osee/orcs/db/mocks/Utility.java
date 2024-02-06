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

package org.eclipse.osee.orcs.db.mocks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Zip;

/**
 * @author Roberto E. Escobar
 */
public final class Utility {

   private Utility() {
      //Utility Class
   }

   public static byte[] asZipped(String data, String name) throws IOException {
      InputStream inputStream = null;
      try {
         inputStream = new ByteArrayInputStream(data.getBytes("UTF-8"));
         return Zip.compressStream(inputStream, name);
      } finally {
         Lib.close(inputStream);
      }
   }

   public static String generateData(int size) {
      char[] data = new char[size];
      for (int index = 0; index < size; index++) {
         int rnd = (int) (Math.random() * 52);
         char base = rnd < 26 ? 'A' : 'a';
         data[index] = (char) (base + rnd % 26);
      }
      return new String(data);
   }
}
