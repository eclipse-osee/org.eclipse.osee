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

package org.eclipse.osee.ats;

import org.eclipse.osee.ats.util.AtsAdmin;
import org.eclipse.osee.ats.util.AtsBranchAccessHandler;
import org.eclipse.osee.ats.util.AtsPreSaveCacheRemoteEventHandler;
import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Donald G. Dunne
 */
public class AtsPlugin extends OseeUiActivator {
   private static AtsPlugin pluginInstance;
   private static Cursor handCursor;
   private static Cursor waitCursor;
   public static ActionDebug debug = new ActionDebug(false, "AtsPlugin");
   public static final String PLUGIN_ID = "org.eclipse.osee.ats";
   private static boolean emailEnabled = true;
   public static Color ACTIVE_COLOR = new Color(null, 206, 212, 241);

   /**
    * The constructor.
    */
   public AtsPlugin() {
      super();
      pluginInstance = this;
      AtsBranchAccessHandler.getInstance();
      AtsPreSaveCacheRemoteEventHandler.getInstance();
   }

   public static boolean isEmailEnabled() {
      return emailEnabled;
   }

   public static void setEmailEnabled(boolean enabled) {
      if (!SkynetDbInit.isDbInit()) System.out.println("Email " + (enabled ? "Enabled" : "Disabled"));
      emailEnabled = enabled;
   }

   public static boolean isProductionDb() {
      return DatabaseActivator.getInstance().isProductionDb();
   }

   public static Cursor getHandCursor() {
      if (handCursor == null) handCursor = new Cursor(null, SWT.CURSOR_HAND);
      return handCursor;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.util.plugin.OseePlugin#getPluginName()
    */
   @Override
   protected String getPluginName() {
      return PLUGIN_ID;
   }

   /**
    * Returns the shared instance.
    */
   public static AtsPlugin getInstance() {
      return pluginInstance;
   }

   public static Cursor getWaitCursor() {
      if (waitCursor == null) waitCursor = new Cursor(null, SWT.CURSOR_WAIT);
      return waitCursor;
   }

   public static boolean isAtsAdmin() {
      return AtsAdmin.isAtsAdmin();
   }

   public static boolean isAtsIgnoreConfigUpgrades() {
      return System.getProperty("AtsIgnoreConfigUpgrades") != null;
   }

   public static boolean isAtsDisableEmail() {
      return System.getProperty("AtsDisableEmail") != null;
   }

   public static boolean isAtsAlwaysEmailMe() {
      return System.getProperty("AtsAlwaysEmailMe") != null;
   }

   public static Branch getAtsBranch() throws OseeCoreException {
      return BranchManager.getAtsBranch();
   }
}
