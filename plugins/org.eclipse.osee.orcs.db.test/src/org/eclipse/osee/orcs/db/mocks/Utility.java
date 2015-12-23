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
package org.eclipse.osee.orcs.db.mocks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.framework.jdk.core.util.Lib;

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
         return Lib.compressStream(inputStream, name);
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
