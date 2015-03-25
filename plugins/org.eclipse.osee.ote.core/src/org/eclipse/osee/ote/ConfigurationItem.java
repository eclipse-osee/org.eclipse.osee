package org.eclipse.osee.ote;

import java.io.Serializable;

/**
 * This defines the bundle to be loaded by the OTE Server.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class ConfigurationItem implements Serializable {

   private static final long serialVersionUID = -2805353994429454202L;
   private String url;
   private String version;
   private String symbolicName;
   private String md5;
   private boolean isOsgiBundle;
   
   public ConfigurationItem(String url, String version, String symbolicName, String md5, boolean isOsgiBundle) {
      this.url = url;
      this.version = version;
      this.symbolicName = symbolicName;
      this.md5 = md5;
   }

   public boolean isOsgiBundle() {
      return isOsgiBundle;
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
