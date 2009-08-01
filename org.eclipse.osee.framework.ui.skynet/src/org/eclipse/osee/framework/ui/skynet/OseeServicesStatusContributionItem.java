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
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.IStatusListener;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
public class OseeServicesStatusContributionItem extends OseeContributionItem implements IStatusListener {
   private static final String ID = "osee.service.status";
   private static final Image DISABLED_IMAGE =
         new OverlayImage(ImageManager.getImage(FrameworkImage.APPLICATION_SERVER),
               ImageManager.getImageDescriptor(FrameworkImage.SLASH_RED_OVERLAY)).createImage();

   private static String errorMessage;
   private static String okMessage;

   private OseeServicesStatusContributionItem() {
      super(ID);
      errorMessage = null;
      okMessage = null;
      updateStatus(true);
      OseeLog.register(this);
   }

   @Override
   public void dispose() {
      OseeLog.deregister(this);
      super.dispose();
   }

   public static void addTo(IStatusLineManager manager) {
      boolean wasFound = false;
      for (IContributionItem item : manager.getItems()) {
         if (item instanceof OseeServicesStatusContributionItem) {
            wasFound = true;
            break;
         }
      }
      if (!wasFound) {
         manager.add(new OseeServicesStatusContributionItem());
      }
   }

   @Override
   protected Image getDisabledImage() {
      return DISABLED_IMAGE;
   }

   @Override
   protected String getDisabledToolTip() {
      return errorMessage;
   }

   @Override
   protected Image getEnabledImage() {
      return ImageManager.getImage(FrameworkImage.APPLICATION_SERVER);
   }

   @Override
   protected String getEnabledToolTip() {
      return okMessage;
   }

   @Override
   public void onStatus(final IHealthStatus status) {
      Display.getDefault().asyncExec(new Runnable() {
         @Override
         public void run() {
            if (status.isOk()) {
               okMessage = status.getMessage();
            } else {
               Throwable error = status.getException();
               errorMessage = error != null ? error.getLocalizedMessage() : "Undefined Error";
            }
            updateStatus(status.isOk());
         }
      });

   }
}
