/*
 * Created on Feb 19, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.plugin.core.db;

import java.io.InputStream;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.PluginCoreActivator;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * @author Donald G. Dunne
 */
public class OseeCodeVersion {

   // Until release, version returned from getOseeVersion will be DEFAULT_DEVELOPMENT_VERSION
   public static String DEFAULT_DEVELOPMENT_VERSION = "Development";
   private String oseeVersion = DEFAULT_DEVELOPMENT_VERSION;
   private static OseeCodeVersion instance = new OseeCodeVersion();

   private OseeCodeVersion() {
   }

   public static OseeCodeVersion getInstance() {
      return instance;
   }

   public String get() {
      if (oseeVersion == null) {
         try {
            if (PluginCoreActivator.getInstance().getBundle().getEntry("/OseeCodeVersion.txt") != null) {
               InputStream is =
                     PluginCoreActivator.getInstance().getBundle().getEntry("/OseeCodeVersion.txt").openStream();
               if (is != null) {
                  oseeVersion = Lib.inputStreamToString(is);
                  oseeVersion = oseeVersion.replace("0=", "");
               }
            }
         } catch (Exception ex) {
            ConfigUtil.getConfigFactory().getLogger(PluginCoreActivator.class).log(Level.SEVERE,
                  "Can't access OseeVersion.txt\n" + Lib.exceptionToString(ex));
         }
      }
      return oseeVersion;
   }

   public boolean isDevelopmentVersion() {
      return get().equals(DEFAULT_DEVELOPMENT_VERSION);
   }

   /**
    * This method public for testing purposes only
    * 
    * @param oseeVersion the oseeVersion to set
    */
   public void set(String oseeVersion) {
      this.oseeVersion = oseeVersion;
   }

}
