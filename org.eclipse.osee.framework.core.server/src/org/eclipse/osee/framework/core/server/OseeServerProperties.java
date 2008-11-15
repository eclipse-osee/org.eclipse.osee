/*
 * Created on Nov 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.server;

import org.eclipse.osee.framework.jdk.core.util.OseeProperties;

/**
 * @author Roberto E. Escobar
 */
public class OseeServerProperties {

   private OseeServerProperties() {
      super();
   }

   /**
    * Get location for OSEE application server binary data
    * 
    * @return OSEE application server binary data path
    */
   public static String getOseeApplicationServerData() {
      return OseeProperties.getOseeApplicationServerData();
   }

   /**
    * Retrieve the application server port
    * 
    * @return the application server port
    */
   public static int getOseeApplicationServerPort() {
      return OseeProperties.getOseeApplicationServerPort();
   }
}
