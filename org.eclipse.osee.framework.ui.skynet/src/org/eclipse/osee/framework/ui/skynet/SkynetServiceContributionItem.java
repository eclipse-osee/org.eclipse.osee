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
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.SkynetServiceEvent;
import org.eclipse.osee.framework.skynet.core.remoteEvent.RemoteEventManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Jeff C. Phillips
 */
public class SkynetServiceContributionItem extends SkynetContributionItem {

   public static final String ID = "skynet.service";
   private static final SkynetGuiPlugin skynetGuiPlugin = SkynetGuiPlugin.getInstance();
   private static final RemoteEventManager remoteManager = RemoteEventManager.getInstance();
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private static final Image ENABLED =
         new OverlayImage(skynetGuiPlugin.getImage("gear.gif"), skynetGuiPlugin.getImageDescriptor("laser_8_8.gif")).createImage();
   private static final Image DISABLED =
         new OverlayImage(ENABLED, skynetGuiPlugin.getImageDescriptor("red_slash.gif")).createImage();
   private static final String ENABLED_TOOLTIP = "Remote event service is connected.";
   private static final String DISABLED_TOOLTIP = "Remote event service is disconnected.";

   public SkynetServiceContributionItem() {
      super(ID, ENABLED, DISABLED, ENABLED_TOOLTIP, DISABLED_TOOLTIP, eventManager);
      init();
   }

   public void onEvent(Event event) {
      if (event instanceof SkynetServiceEvent) {
         SkynetServiceEvent skynetServiceEvent = (SkynetServiceEvent) event;
         updateStatus(skynetServiceEvent.isActive());
      }
   }

   public boolean runOnEventInDisplayThread() {
      return true;
   }

   private void init() {
      updateStatus(remoteManager.isConnected());
      eventManager.register(SkynetServiceEvent.class, this);
   }

   public static void addTo(IStatusLineManager manager) {
      for (IContributionItem item : manager.getItems())
         if (item instanceof SkynetServiceContributionItem) return;
      manager.add(new SkynetServiceContributionItem());
   }

   public static void addTo(ViewPart view, boolean update) {
      addTo(view.getViewSite().getActionBars().getStatusLineManager());

      if (update) view.getViewSite().getActionBars().updateActionBars();
   }
}
