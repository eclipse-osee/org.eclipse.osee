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

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.listener.IRemoteEventManagerEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.plugin.OseeStatusContributionItem;
import org.eclipse.osee.framework.ui.skynet.action.OpenConfigDetailsAction;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.OverlayImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Jeff C. Phillips
 */
public class ResServiceContributionItem extends OseeStatusContributionItem implements IRemoteEventManagerEventListener {

   public static final String ID = "res.service";
   private static final Image ENABLED_IMAGE = ImageManager.getImageDescriptor(FrameworkImage.RES).createImage();
   private static final Image DISABLED_IMAGE =
      new OverlayImage(ENABLED_IMAGE, ImageManager.getImageDescriptor(FrameworkImage.SLASH_RED_OVERLAY)).createImage();
   private static final String ENABLED_TOOLTIP = "Event Service is connected.";
   private static final String DISABLED_TOOLTIP = "Event Service is disconnected.";

   private static final Timer timer = new Timer("Event Service Update Status", true);
   private static final UpdateStatusTimerTask updateTask = new UpdateStatusTimerTask();
   private static final int FOUR_MINUTES = 1000 * 60 * 4;

   static {
      timer.schedule(updateTask, FOUR_MINUTES);
   }

   public ResServiceContributionItem() {
      super(ID);
      updateStatus(OseeEventManager.isEventManagerConnected());
      OseeEventManager.addListener(this);
      setActionHandler(new OpenConfigDetailsAction());
      updateTask.addItem(this);
   }

   @Override
   public void dispose() {
      updateTask.removeItem(this);
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

   private static final class UpdateStatusTimerTask extends TimerTask {
      private final Set<OseeStatusContributionItem> itemsToUpdate = new CopyOnWriteArraySet<>();

      public void addItem(OseeStatusContributionItem item) {
         itemsToUpdate.add(item);
      }

      public void removeItem(OseeStatusContributionItem item) {
         itemsToUpdate.remove(item);
      }

      protected boolean getStatus() {
         return OseeEventManager.isEventManagerConnected();
      }

      @Override
      public void run() {
         boolean isActive = getStatus();
         Set<OseeStatusContributionItem> toRemove = new HashSet<>();
         for (OseeStatusContributionItem item : itemsToUpdate) {
            if (item.isDisposed()) {
               toRemove.add(item);
            } else {
               item.updateStatus(isActive);
            }
         }
         itemsToUpdate.removeAll(toRemove);
      }
   }

   public static void addToAllViews() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               if (PlatformUI.getWorkbench() == null || PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) {
                  return;
               }
               for (IViewReference viewDesc : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences()) {
                  IViewPart viewPart = viewDesc.getView(false);
                  if (viewPart != null) {
                     addToViewpart((ViewPart) viewPart);
                  }
               }
            } catch (Exception ex) {
               // DO NOTHING
            }
         }
      });
   }

   public static void addToViewpart(ViewPart viewPart) {
      // Attempt to add to PackageExplorerPart
      try {
         if (viewPart != null) {
            for (IContributionItem item : viewPart.getViewSite().getActionBars().getStatusLineManager().getItems()) {
               if (item instanceof ResServiceContributionItem) {
                  return;
               }
            }
            viewPart.getViewSite().getActionBars().getStatusLineManager().add(new ResServiceContributionItem());
         }
      } catch (Exception ex) {
         // do nothing
      }
   }
}
