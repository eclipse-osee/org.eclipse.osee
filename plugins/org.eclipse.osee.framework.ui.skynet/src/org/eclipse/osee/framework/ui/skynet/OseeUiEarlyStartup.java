/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet;

import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

/**
 * @author Ryan Schmitt
 */
public class OseeUiEarlyStartup implements IStartup {

   @Override
   public void earlyStartup() {
      if (PlatformUI.isWorkbenchRunning()) {
         Display.getDefault().asyncExec(new Runnable() {
            public void run() {
               PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(
                     new IPartListener() {

                        @Override
                        public void partActivated(IWorkbenchPart part) {
                           //                           WorkspaceContributionItem.addToAllViews();
                           if (part instanceof ViewPart) {
                              WorkspaceContributionItem.addToViewpart((ViewPart) part);
                           }

                        }

                        @Override
                        public void partBroughtToTop(IWorkbenchPart part) {
                           //                           WorkspaceContributionItem.addToAllViews();
                           if (part instanceof ViewPart) {
                              WorkspaceContributionItem.addToViewpart((ViewPart) part);
                           }

                        }

                        @Override
                        public void partClosed(IWorkbenchPart part) {
                        }

                        @Override
                        public void partDeactivated(IWorkbenchPart part) {
                        }

                        @Override
                        public void partOpened(IWorkbenchPart part) {
                           //                           WorkspaceContributionItem.addToAllViews();
                           if (part instanceof ViewPart) {
                              WorkspaceContributionItem.addToViewpart((ViewPart) part);
                           }

                        }

                     });
               PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {

                  @Override
                  public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
                     //                     WorkspaceContributionItem.addToAllViews();
                     if (page instanceof ViewPart) {
                        WorkspaceContributionItem.addToViewpart((ViewPart) page);
                     }

                  }

                  @Override
                  public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
                     //                     WorkspaceContributionItem.addToAllViews();
                     if (page instanceof ViewPart) {
                        WorkspaceContributionItem.addToViewpart((ViewPart) page);
                     }

                  }

               });
            }
         });
         startOseeTypes();
      }
      WorkspaceContributionItem.addToAllViews();
   }

   private void startOseeTypes() {
      try {
         Bundle bundle = Platform.getBundle("org.eclipse.osee.framework.types.bridge");
         if (bundle.getState() != Bundle.ACTIVE) {
            bundle.start();
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }
}
