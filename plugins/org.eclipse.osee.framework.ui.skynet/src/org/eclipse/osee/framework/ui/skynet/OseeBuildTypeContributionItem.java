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
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.OseeStatusContributionItem;
import org.eclipse.osee.framework.ui.skynet.action.OpenConfigDetailsAction;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Roberto E. Escobar
 */
public class OseeBuildTypeContributionItem extends OseeStatusContributionItem {

   private static final String ID = "osee.build.type";

   private static String TOOLTIP = "Version [%s]\nDouble-Click for details";

   public OseeBuildTypeContributionItem() {
      super(ID);
      setActionHandler(new OpenConfigDetailsAction());
      updateStatus(true);
   }

   @Override
   protected Image getDisabledImage() {
      return ImageManager.getImage(FrameworkImage.TOOLS);
   }

   @Override
   protected String getDisabledToolTip() {
      return Strings.emptyString();
   }

   @Override
   protected Image getEnabledImage() {
      return ImageManager.getImage(FrameworkImage.TOOLS);
   }

   @Override
   protected String getEnabledToolTip() {
      return String.format(TOOLTIP, getClientVersion());
   }

   private String getClientVersion() {
      String version = "N/A";
      try {
         version = ClientSessionManager.getSession().getClientVersion();
      } catch (OseeCoreException ex) {
         // Do Nothing
      }
      return version;
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
               if (item instanceof OseeBuildTypeContributionItem) {
                  return;
               }
            }
            viewPart.getViewSite().getActionBars().getStatusLineManager().add(new OseeBuildTypeContributionItem());
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

}
