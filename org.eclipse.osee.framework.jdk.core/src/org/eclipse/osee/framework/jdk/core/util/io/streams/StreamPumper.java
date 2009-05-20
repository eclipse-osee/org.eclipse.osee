/*
 * Created on May 20, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.jdk.core.util.io.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** 
 * Utility class for convenience methods with getting information from
 * InputStream's to OutputStream's.
 * 
 * @author Robert A. Fisher
 *
 */
public class StreamPumper {

   /**
    * Pumps all data from the InputStream to the OutputStream through an
    * in place 2k buffer.
    * 
    * @param in
    * @param out
    * @throws IOException
    */
   public static void pumpData(InputStream in, OutputStream out) throws IOException {
      if (in == null)
         throw new IllegalArgumentException("in can not be null");
      if (out == null)
         throw new IllegalArgumentException("out can not be null");
      
      int numBytesRead;
      byte[] bytes = new byte[2024];
      while ((numBytesRead = in.read(bytes)) != -1) {
         out.write(bytes, 0, numBytesRead);
      }
   }
}
