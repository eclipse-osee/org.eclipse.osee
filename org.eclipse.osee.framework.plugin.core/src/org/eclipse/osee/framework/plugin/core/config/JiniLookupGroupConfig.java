/*
 * Created on Sep 5, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.plugin.core.config;

import java.util.logging.Level;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;

/**
 * @author b1541174
 */
public class JiniLookupGroupConfig {
   
   public static String[] getOseeJiniServiceGroups()
   {
      String[] serviceGroups = null;
      try {
         Bundle bundle = Platform.getBundle("org.eclipse.osee.framework.jini");
         serviceGroups = new String[1];
         serviceGroups[0] = (String) bundle.getHeaders().get("Bundle-Version");
      } catch (Exception ex) {
         OseeLog.log(JiniLookupGroupConfig.class, Level.INFO, "Error getting bundle org.eclipse.osee.framework.jini");
      }
      
      String filterGroups = System.getProperty(OseeProperties.OSEE_JINI_SERVICE_GROUPS);
      if (filterGroups != null && filterGroups.length() > 0) {
         String[] values = filterGroups.split(",");
         for (int index = 0; index < values.length; index++) {
            values[index] = values[index].trim();
         }
         serviceGroups = values;
      }
      
      if (serviceGroups == null || serviceGroups.length == 0) {
         OseeLog.log(JiniLookupGroupConfig.class, 
               Level.SEVERE,
               "[-D" + OseeProperties.OSEE_JINI_SERVICE_GROUPS + "] was not set.\n" + "Please enter the Jini Group this service register with.");
         serviceGroups = new String[]{"NO_LOOKUP_GROUP_SPECIFIED"};
      } else {
         String toStore = "";
         for (int index = 0; index < serviceGroups.length; index++) {
            toStore += serviceGroups[index];
            if (index + 1 < serviceGroups.length) {
               toStore += ",";
            }
         }
         OseeLog.log(JiniLookupGroupConfig.class, Level.INFO, "osee.jini.lookup.groups: " + toStore);
         System.setProperty(OseeProperties.OSEE_JINI_SERVICE_GROUPS, toStore);
      }
      
      return serviceGroups;
   }

}
