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

package org.eclipse.osee.framework.jini;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jini.discovery.EclipseJiniClassloader;
import org.eclipse.osee.framework.jini.discovery.ServiceDataStore;
import org.osgi.framework.BundleContext;

/**
 * The main plug-in class to be used in the desktop.
 */
public class JiniPlugin extends Plugin {

   private static JiniPlugin plugin;

   /**
    * The constructor.
    */
   public JiniPlugin() {
      JiniPlugin.plugin = this;
      try {
         JiniClassServer.getInstance();
      } catch (Exception e) {
         e.printStackTrace();
      }

  // String[] lookupLocations =
	// ConfigUtil.getConfigFactory().getOseeConfig().getServiceLookups();
	//ServiceDataStore.getEclipseInstance(EclipseJiniClassloader.getInstance
	// ());// .addLookupLocators(lookupLocations);
	//ServiceDataStore.getEclipseInstance(EclipseJiniClassloader.getInstance
	// ()).addLookupLocators(lookupLocations);
   }

   
   public static String[] getLookupVersion()
   {

      String serviceGroup = System.getProperty(OseeProperties.OSEE_JINI_SERVICE_GROUPS);
      if( serviceGroup == null || serviceGroup.length() <= 0)
      {
         serviceGroup = (String)Platform.getBundle("").getHeaders().get("Bundle-Version");
      }
      // String[] filterGroups = null;
      if (serviceGroup != null && serviceGroup.length() > 0) {
         String[] values = serviceGroup.split(",");
         for (int index = 0; index < values.length; index++) {
            values[index] = values[index].trim();
         }
         return values;
      }
      return null;
    
   }
  
   
   
   /**
    * This method is called when the plug-in is stopped
    */
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      plugin = null;
      JiniClassServer.stopServer();
   }

   /**
    * Returns the shared instance.
    */
   public static JiniPlugin getInstance() {
      return plugin;
   }

   public void earlyStartup() {
      // so that the jini stuff gets started up
      ServiceDataStore.getEclipseInstance(EclipseJiniClassloader.getInstance());
      System.out.println("JiniPlugin early startup");
   }

}
