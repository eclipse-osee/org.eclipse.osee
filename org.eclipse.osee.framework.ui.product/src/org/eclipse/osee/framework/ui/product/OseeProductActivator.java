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
package org.eclipse.osee.framework.ui.product;

import java.io.InputStream;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.plugin.util.OseeConsole;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class OseeProductActivator extends OseeUiActivator implements IStartup {

   public static final String USER_NAME_CHECK_BOX_PREFERENCE = "USER_NAME_CHECK_BOX_PREFERENCE";
   public static final String USER_NAME_TEXT_BOX_PREFERENCE = "USER_NAME_TEXT_BOX_PREFERENCE";
   public static final String SPELL_CHECK_ENABLED_PEFERENCE = "SPELL_CHECK_ENABLED_PEFERENCE";
   public static final boolean DEFAULT_SPELL_CHECK_ENABLED_PEFERENCE = true;
   private static OseeConsole console = null;
   public static final String EMAIL_GROUPS_PREFERENCE = "EMAIL_GROUPS_PREFERENCE";
   private static OseeProductActivator pluginInstance; // The shared instance.
   private boolean prefsLoaded = false;

   private String oseeVersion = "Development";

   /**
    * The constructor.
    */
   public OseeProductActivator() {
      super();
      pluginInstance = this;
   }

   public String getOseeVersion() {
      return oseeVersion;
   }

   /**
    * This method is called upon plug-in activation
    * 
    * @throws Exception
    */
   public void start(BundleContext context) throws Exception {
      super.start(context);
      try {
         InputStream is = this.getInputStream("/plugin.mappings");
         if (is != null) {
            oseeVersion = Lib.inputStreamToString(is);
            oseeVersion = oseeVersion.replace("0=", "");
         }
      } catch (Exception ex) {

      }
   }

   /**
    * This method is called when the plug-in is stopped
    */
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
   }

   /**
    * Returns the shared instance.
    */
   public static OseeProductActivator getInstance() {
      return pluginInstance;
   }

   public void loadPrefs() {
      if (prefsLoaded) return;

      prefsLoaded = true;
   }

   /**
    * Called directly after workbench initialization
    */
   public void earlyStartup() {

      Display.getDefault().asyncExec(new Runnable() {

         public void run() {

            Display.getDefault().asyncExec(new Runnable() {

               public void run() {
                  final String version = System.getProperty("java.version");
                  if (!version.contains("1.5")) {
                     console = new OseeConsole("OSEE Core Plugin Console");
                     Display.getDefault().timerExec(5 * 1000, new Runnable() {
                        public void run() {
                           console.popup();
                           console.writeError("Incorrect Java Version \"" + version + "\".  You must have 1.5.0 or higher installed.\n");
                        }
                     });
                  }
               }
            });
         }
      });
   }
}
