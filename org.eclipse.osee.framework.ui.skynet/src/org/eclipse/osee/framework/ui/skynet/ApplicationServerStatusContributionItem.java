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
package org.eclipse.osee.framework.ui.skynet;

import java.sql.SQLException;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.osee.framework.db.connection.IApplicationServerConnectionListener;
import org.eclipse.osee.framework.db.connection.core.OseeApplicationServer;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerStatusContributionItem extends SkynetContributionItem implements IApplicationServerConnectionListener {
   private static final String ID = "app.server.connection";
   private static final SkynetGuiPlugin skynetGuiPlugin = SkynetGuiPlugin.getInstance();
   private static final Image ENABLED = skynetGuiPlugin.getImage("appserver.gif");
   private static final Image DISABLED =
         new OverlayImage(ENABLED, skynetGuiPlugin.getImageDescriptor("red_slash.gif")).createImage();

   public ApplicationServerStatusContributionItem() {
      super(ID, ENABLED, DISABLED, "", "");
      init();
   }

   private void init() {
      updateStatus(OseeApplicationServer.isApplicationServerAlive());
      OseeApplicationServer.addListener(this);
   }

   public static void addTo(IStatusLineManager manager) {
      for (IContributionItem item : manager.getItems())
         if (item instanceof ApplicationServerStatusContributionItem) return;
      manager.add(new ApplicationServerStatusContributionItem());
   }

   public static void addTo(ViewPart view, boolean update) {
      addTo(view.getViewSite().getActionBars().getStatusLineManager());
      if (update) view.getViewSite().getActionBars().updateActionBars();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.SkynetContributionItem#getDisabledToolTip()
    */
   @Override
   protected String getDisabledToolTip() {
      String message = null;
      try {
         message = OseeApplicationServer.getOseeApplicationServer();
      } catch (SQLException ex) {
      }
      return String.format("Not connected to application server [%s]", message);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.SkynetContributionItem#getEnabledToolTip()
    */
   @Override
   protected String getEnabledToolTip() {
      String message = null;
      try {
         message = OseeApplicationServer.getOseeApplicationServer();
      } catch (SQLException ex) {
      }
      return String.format("Connected to application server [%s]", message);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IApplicationServerConnectionListener#onConnectionStatusChange(boolean)
    */
   @Override
   public void onConnectionStatusChange(final boolean status) {
      Display.getDefault().asyncExec(new Runnable() {
         @Override
         public void run() {
            updateStatus(status);
         }
      });
   }
}
