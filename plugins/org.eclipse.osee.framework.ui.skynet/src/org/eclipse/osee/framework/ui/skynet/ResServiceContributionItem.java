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

import java.util.ArrayList;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.osee.framework.skynet.core.event.IRemoteEventManagerEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.skynet.OseeBuildTypeContributionItem.OpenConfigDetailsAction;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.OverlayImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class ResServiceContributionItem extends OseeContributionItem implements IRemoteEventManagerEventListener {

   public static final String ID = "res.service";
   private static final Image ENABLED_IMAGE = ImageManager.getImageDescriptor(FrameworkImage.RES).createImage();
   private static final Image DISABLED_IMAGE = new OverlayImage(ENABLED_IMAGE,
      ImageManager.getImageDescriptor(FrameworkImage.SLASH_RED_OVERLAY)).createImage();
   private static final String ENABLED_TOOLTIP = "Event Service is connected.";
   private static final String DISABLED_TOOLTIP = "Event Service is disconnected.";
   private static Thread updateThread = null;
   private static ArrayList<ResServiceContributionItem> icons = new ArrayList<ResServiceContributionItem>();
   private static final int FOUR_MINUTES = 1000 * 60 * 4;
   private static Runnable threadRunnable = new Runnable() {
      @Override
      public void run() {
         do {
            boolean status = OseeEventManager.isEventManagerConnected();
            for (ResServiceContributionItem icon : icons) {
               icon.updateStatus(status);
            }
            try {
               Thread.sleep(FOUR_MINUTES);
            } catch (InterruptedException ex) {
               return;
            }
         } while (true);
      }
   };

   public ResServiceContributionItem() {
      super(ID);
      updateStatus(OseeEventManager.isEventManagerConnected());
      OseeEventManager.addListener(this);
      setActionHandler(new OpenConfigDetailsAction());
      icons.add(this);
      createUpdateThread();
   }

   private static void createUpdateThread() {
      if (updateThread == null) {
         System.out.println("Thread added");
         updateThread = new Thread(threadRunnable, "Event Service Icon Updater");
         updateThread.start();
      }
   }

   public static void addTo(IStatusLineManager manager) {
      boolean wasFound = false;
      for (IContributionItem item : manager.getItems()) {
         if (item instanceof ResServiceContributionItem) {
            wasFound = true;
            break;
         }
      }
      if (!wasFound) {
         manager.add(new ResServiceContributionItem());
      }
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   @Override
   public void handleRemoteEventManagerEvent(Sender sender, final RemoteEventServiceEventType remoteEventServiceEventType) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            updateStatus(OseeEventManager.isEventManagerConnected());
         }
      });
   }

   @Override
   protected Image getDisabledImage() {
      return DISABLED_IMAGE;
   }

   @Override
   protected String getDisabledToolTip() {
      return DISABLED_TOOLTIP;
   }

   @Override
   protected Image getEnabledImage() {
      return ENABLED_IMAGE;
   }

   @Override
   protected String getEnabledToolTip() {
      return ENABLED_TOOLTIP;
   }
}
