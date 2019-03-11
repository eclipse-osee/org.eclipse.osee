/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.OseeStatusContributionItem;
import org.eclipse.osee.framework.ui.plugin.util.WorkbenchTargetProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

/**
 * @author Branden W. Phillips
 */
public class OseeTargetContributionItem extends OseeStatusContributionItem {

   public OseeTargetContributionItem() {
      super(OseeProperties.OSEE_TARGET, 5);
      setTargetImage();
   }

   public void setTargetImage() {
      Image image = null;
      String text = "OSEE";

      WorkbenchTargetProvider provider = getWorkbenchProvider();

      if (provider != null) {
         image = provider.getWorkbenchImage();
         text = provider.getText();
      }

      if (image != null) {
         setImage(image);
         setToolTipText(text);
      } else {
         setText(text);
         setToolTipText(text);
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
               if (item instanceof OseeTargetContributionItem) {
                  return;
               }
            }
            // System.err.println("Adding to " + viewPart);
            viewPart.getViewSite().getActionBars().getStatusLineManager().add(new OseeTargetContributionItem());
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

   @Override
   protected String getEnabledToolTip() {
      return null;
   }

   @Override
   protected String getDisabledToolTip() {
      return null;
   }

   @Override
   protected Image getEnabledImage() {
      return null;
   }

   @Override
   protected Image getDisabledImage() {
      return null;
   }

   public static WorkbenchTargetProvider getWorkbenchProvider() {
      WorkbenchTargetProvider provider = null;

      IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(
         "org.eclipse.osee.framework.ui.plugin.WorkbenchTargetProvider");

      IExtension[] extensions = point.getExtensions();

      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("WorkbenchTargetProvider")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> taskClass = bundle.loadClass(classname);
                     provider = (WorkbenchTargetProvider) taskClass.newInstance();

                     return provider;

                  } catch (Exception ex) {
                     OseeLog.log(OseeTargetContributionItem.class, OseeLevel.SEVERE_POPUP,
                        "Error loading AtsHealthCheck extension", ex);
                  }
               }
            }
         }
      }
      return provider;
   }
}
