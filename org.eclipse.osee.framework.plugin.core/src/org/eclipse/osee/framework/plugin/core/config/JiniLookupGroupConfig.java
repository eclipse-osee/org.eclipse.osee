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

   private JiniLookupGroupConfig() {
   }

   public static String[] getOseeJiniServiceGroups() {
      String[] serviceGroups = null;
      try {
         Bundle bundle = Platform.getBundle("org.eclipse.osee.framework.jini");
         serviceGroups = new String[1];
         serviceGroups[0] = (String) bundle.getHeaders().get("Bundle-Version");
      } catch (Exception ex) {
         OseeLog.log(JiniLookupGroupConfig.class, Level.INFO, "Error getting bundle org.eclipse.osee.framework.jini");
      }

      String filterGroups = OseeProperties.getOseeJiniServiceGroups();
      if (filterGroups != null && filterGroups.length() > 0) {
         String[] values = filterGroups.split(",");
         for (int index = 0; index < values.length; index++) {
            values[index] = values[index].trim();
         }
         serviceGroups = values;
      }

      if (serviceGroups == null || serviceGroups.length == 0) {
         OseeLog.log(JiniLookupGroupConfig.class, Level.SEVERE,
               "[-D" + filterGroups + "] was not set.\n" + "Please enter the Jini Group this service register with.");
         serviceGroups = new String[] {"NO_LOOKUP_GROUP_SPECIFIED"};
      } else {
         String toStore = "";
         for (int index = 0; index < serviceGroups.length; index++) {
            toStore += serviceGroups[index];
            if (index + 1 < serviceGroups.length) {
               toStore += ",";
            }
         }
         OseeLog.log(JiniLookupGroupConfig.class, Level.INFO, "osee.jini.lookup.groups: " + toStore);
         OseeProperties.setOseeJiniServiceGroups(toStore);
      }

      return serviceGroups;
   }

}
