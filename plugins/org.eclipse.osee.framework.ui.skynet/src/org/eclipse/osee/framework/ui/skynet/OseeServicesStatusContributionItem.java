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

import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.IStatusListener;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.OseeStatusContributionItem;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.OverlayImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class OseeServicesStatusContributionItem extends OseeStatusContributionItem implements IStatusListener {
   private static final String ID = "osee.service.status";
   private static final Image DISABLED_IMAGE = new OverlayImage(
      ImageManager.getImage(FrameworkImage.APPLICATION_SERVER),
      ImageManager.getImageDescriptor(FrameworkImage.SLASH_RED_OVERLAY)).createImage();

   private static String errorMessage;
   private static String okMessage;

   public OseeServicesStatusContributionItem() {
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
      Displays.ensureInDisplayThread(new Runnable() {
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
