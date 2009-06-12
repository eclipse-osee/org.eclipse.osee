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
import org.eclipse.osee.framework.skynet.core.event.IRemoteEventManagerEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventManager;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class SkynetServiceContributionItem extends OseeContributionItem implements IRemoteEventManagerEventListener {

   public static final String ID = "skynet.service";
   private static final Image ENABLED_IMAGE =
         new OverlayImage(ImageManager.getImage(FrameworkImage.GEAR),
               ImageManager.getImageDescriptor(FrameworkImage.LASER_OVERLAY)).createImage();
   private static final Image DISABLED_IMAGE =
         new OverlayImage(ENABLED_IMAGE, ImageManager.getImageDescriptor(FrameworkImage.SLASH_RED_OVERLAY)).createImage();
   private static final String ENABLED_TOOLTIP = "Remote event service is connected.";
   private static final String DISABLED_TOOLTIP = "Remote event service is disconnected.";

   public SkynetServiceContributionItem() {
      super(ID);
      updateStatus(RemoteEventManager.isConnected());
      OseeEventManager.addListener(this);
   }

   public static void addTo(IStatusLineManager manager) {
      boolean wasFound = false;
      for (IContributionItem item : manager.getItems()) {
         if (item instanceof SkynetServiceContributionItem) {
            wasFound = true;
            break;
         }
      }
      if (!wasFound) {
         manager.add(new SkynetServiceContributionItem());
      }
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IRemoteEventManagerEventListener#handleRemoteEventManagerEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.eventx.RemoteEventModType)
    */
   @Override
   public void handleRemoteEventManagerEvent(Sender sender, final RemoteEventServiceEventType remoteEventServiceEventType) {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            updateStatus(remoteEventServiceEventType == RemoteEventServiceEventType.Connected);
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeContributionItem#getDisabledImage()
    */
   @Override
   protected Image getDisabledImage() {
      return DISABLED_IMAGE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeContributionItem#getDisabledToolTip()
    */
   @Override
   protected String getDisabledToolTip() {
      return DISABLED_TOOLTIP;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeContributionItem#getEnabledImage()
    */
   @Override
   protected Image getEnabledImage() {
      return ENABLED_IMAGE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeContributionItem#getEnabledToolTip()
    */
   @Override
   protected String getEnabledToolTip() {
      return ENABLED_TOOLTIP;
   }
}
