/*
 * Created on May 20, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.jdk.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** 
 * Utility class for convenience methods with MD5.
 * 
 * @author Robert A. Fisher
 */
public class MD5Util {
   
   /**
    * Reads all information from an InputStream and uses the MessageDigest class
    * to get an MD5 hash of the data.
    * 
    * @param inputStream
    * @return the MD5 hash of the inputStream
    * @throws IOException
    * @throws NoSuchAlgorithmException
    */
   public static byte[] getDigest(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
      if (inputStream == null)
         throw new IllegalArgumentException("inputStream can not be null");
      
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      
      int numBytesRead;
      byte[] bytes = new byte[2024];
      while ((numBytesRead = inputStream.read(bytes)) != -1) {
         md5.update(bytes, 0, numBytesRead);
      }
      
      return md5.digest();
   }
}
