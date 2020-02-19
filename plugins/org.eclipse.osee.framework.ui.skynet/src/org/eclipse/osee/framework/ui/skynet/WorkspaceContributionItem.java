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

import java.io.File;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.osee.framework.ui.plugin.OseeStatusContributionItem;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Donald G. Dunne
 */
public class WorkspaceContributionItem extends OseeStatusContributionItem {
   public WorkspaceContributionItem() {
      super("org.eclipse.osee.framework.ui.skynet.workspace.status", getShortPath().length() + 5);
      setToolTipText(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString());
      setText(getShortPath());
      setActionHandler(new Action() {
         @Override
         public void run() {
            Program.launch(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString());
         }
      });
   }

   private static String getShortPath() {
      String path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
      String elements[] = path.split("\\/");
      if (elements.length >= 2) {
         return elements[elements.length - 2] + File.separator + elements[elements.length - 1];
      }
      return path;
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
               if (item instanceof WorkspaceContributionItem) {
                  return;
               }
            }
            viewPart.getViewSite().getActionBars().getStatusLineManager().add(new WorkspaceContributionItem());
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
}
