package org.eclipse.osee.ote.core.environment.interfaces;
public class OteUtil {

   public static String generateBundleVersionString(String bundleSpecificVersion, String symbolicName, String version, byte[] md5){
      StringBuilder sb = new StringBuilder();
      if(bundleSpecificVersion != null){
         sb.append(bundleSpecificVersion);
         sb.append("_");
      }
      sb.append(symbolicName);
      sb.append("_");
      sb.append(version);
      sb.append("_");
      for(byte b:md5){
         sb.append(String.format("%X", b));
      }
      return sb.toString();
   }
}