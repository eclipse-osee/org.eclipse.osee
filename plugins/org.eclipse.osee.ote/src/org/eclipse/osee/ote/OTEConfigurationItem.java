package org.eclipse.osee.ote;

/**
 * This defines the bundle to be loaded by the OTE Server.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class OTEConfigurationItem {

   private String url;
   private String version;
   private String symbolicName;
   private String md5;
   
   public OTEConfigurationItem(String url, String version, String symbolicName, String md5) {
      this.url = url;
      this.version = version;
      this.symbolicName = symbolicName;
      this.md5 = md5;
   }

   public String getSymbolicName() {
      return symbolicName;
   }

   public String getMd5Digest() {
      return md5;
   }

   public String getVersion() {
      return version;
   }

   public String getLocationUrl() {
      return url;
   }

}
