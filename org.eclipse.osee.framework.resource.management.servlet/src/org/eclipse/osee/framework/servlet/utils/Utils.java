/*
 * Created on Apr 10, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.servlet.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Roberto E. Escobar
 */
public class Utils {

   public static void sendInputToOutputStream(InputStream inputStream, OutputStream outputStream) throws IOException {
      byte[] buf = new byte[10000];
      int count = -1;
      while ((count = inputStream.read(buf)) != -1) {
         outputStream.write(buf, 0, count);
      }
      inputStream.close();
   }
}
