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
import org.eclipse.osee.framework.core.client.IServiceListener;
import org.eclipse.osee.framework.core.client.ServiceHealthManager;
import org.eclipse.osee.framework.core.client.ServiceStatus;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
public class OseeServicesStatusContributionItem extends OseeContributionItem implements IServiceListener {
   private static final String ID = "osee.service.status";
   private static final Image ENABLED_IMAGE = SkynetGuiPlugin.getInstance().getImage("appserver.gif");
   private static final Image DISABLED_IMAGE =
         new OverlayImage(ENABLED_IMAGE, SkynetGuiPlugin.getInstance().getImageDescriptor("red_slash.gif")).createImage();

   private static String errorMessage;
   private static String okMessage;

   public OseeServicesStatusContributionItem() {
      super(ID);
      errorMessage = null;
      okMessage = null;
      ServiceHealthManager.addListener(this);
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.action.ContributionItem#dispose()
    */
   @Override
   public void dispose() {
      ServiceHealthManager.removeListener(this);
      super.dispose();
   }

   public static void addTo(IStatusLineManager manager) {
      for (IContributionItem item : manager.getItems())
         if (item instanceof OseeServicesStatusContributionItem) return;
      manager.add(new OseeServicesStatusContributionItem());
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
      return errorMessage;
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
      return okMessage;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.client.IServiceListener#onStatusChange(org.eclipse.osee.framework.core.client.ServiceStatus)
    */
   @Override
   public void onStatusChange(final ServiceStatus serviceStatus) {
      Display.getDefault().asyncExec(new Runnable() {
         @Override
         public void run() {
            if (serviceStatus.isHealthOk()) {
               okMessage = serviceStatus.getDetails();
            } else {
               Throwable error = serviceStatus.getError();
               errorMessage = error != null ? error.getLocalizedMessage() : "Undefined Error";
            }
            updateStatus(serviceStatus.isHealthOk());
         }
      });
   }
}
