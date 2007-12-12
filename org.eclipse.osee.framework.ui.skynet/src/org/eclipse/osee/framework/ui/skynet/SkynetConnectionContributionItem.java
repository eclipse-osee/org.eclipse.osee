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

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.config.data.DbDetailData;
import org.eclipse.osee.framework.ui.plugin.event.ConnectionEvent;
import org.eclipse.osee.framework.ui.plugin.event.CoreEventManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Jeff C. Phillips
 */
public class SkynetConnectionContributionItem extends SkynetContributionItem {
   private static final DbDetailData dbData =
         ConfigUtil.getConfigFactory().getOseeConfig().getDefaultClientData().getDatabaseDetails();
   private static final String dbName = dbData.getFieldValue(DbDetailData.ConfigField.DatabaseName);
   private static final String userName = dbData.getFieldValue(DbDetailData.ConfigField.UserName);
   private static final String ID = "skynet.connection";
   private static final SkynetGuiPlugin skynetGuiPlugin = SkynetGuiPlugin.getInstance();
   private static final Image ENABLED = skynetGuiPlugin.getImage("repository.gif");
   private static final Image DISABLED =
         new OverlayImage(ENABLED, skynetGuiPlugin.getImageDescriptor("red_slash.gif")).createImage();
   private static final String ENABLED_TOOLTIP = "Database is connected to " + dbName + " as " + userName + ".";
   private static final String DISABLED_TOOLTIP = "Database is disconnected.";
   private static final CoreEventManager eventManager = CoreEventManager.getInstance();

   public SkynetConnectionContributionItem() {
      super(ID, ENABLED, DISABLED, ENABLED_TOOLTIP, DISABLED_TOOLTIP, eventManager);
      init();
   }

   private void init() {
      updateStatus(ConnectionHandler.isOpen());
      eventManager.register(ConnectionEvent.class, this);
   }

   public static void addTo(IStatusLineManager manager) {
      for (IContributionItem item : manager.getItems())
         if (item instanceof SkynetConnectionContributionItem) return;
      manager.add(new SkynetConnectionContributionItem());
   }

   public static void addTo(ViewPart view, boolean update) {
      addTo(view.getViewSite().getActionBars().getStatusLineManager());

      if (update) view.getViewSite().getActionBars().updateActionBars();
   }

   public void onEvent(Event event) {
      if (event instanceof ConnectionEvent) updateStatus(ConnectionHandler.isOpen());
   }

   public boolean runOnEventInDisplayThread() {
      return true;
   }
}
