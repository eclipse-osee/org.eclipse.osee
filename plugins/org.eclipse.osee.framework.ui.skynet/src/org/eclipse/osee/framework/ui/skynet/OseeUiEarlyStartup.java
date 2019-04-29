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

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.OrcsTypeChangeListener;
import org.eclipse.osee.framework.ui.skynet.blam.operation.SetWorkbenchOverrideIconBlam;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Ryan Schmitt
 */
public class OseeUiEarlyStartup implements IStartup {

   @Override
   public void earlyStartup() {
      if (PlatformUI.isWorkbenchRunning()) {

         OseeLog.registerLoggerListener(new DialogPopupLoggerListener());

         // Start listening for ORCS types changes
         OrcsTypeChangeListener listener = new OrcsTypeChangeListener();
         OseeEventManager.addListener(listener);

         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               SetWorkbenchOverrideIconBlam.reloadOverrideImage();
            }
         });

         IWorkbench workbench = PlatformUI.getWorkbench();
         workbench.addWorkbenchListener(new IWorkbenchListener() {

            @Override
            public void postShutdown(IWorkbench workbench) {
               // do nothing
            }

            @Override
            public boolean preShutdown(IWorkbench workbench, boolean forced) {
               if (!DbUtil.isDbInit()) {
                  try {
                     // Make sure we have latest artifact
                     UserManager.getUser().reloadAttributesAndRelations();
                     UserManager.getUser().saveSettings();
                     for (IEditorReference editor : AWorkbench.getEditors()) {
                        if (editor instanceof ResultsEditor) {
                           AWorkbench.getActivePage().closeEditor(editor.getEditor(false), false);
                        }
                     }
                  } catch (Throwable th) {
                     th.printStackTrace();
                  }
               }
               return true;
            }
         });

         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(
                  new IPartListener() {

                     @Override
                     public void partActivated(IWorkbenchPart part) {
                        if (part instanceof ViewPart) {
                           OseeBuildTypeContributionItem.addToViewpart((ViewPart) part);
                           ResServiceContributionItem.addToViewpart((ViewPart) part);
                           SessionContributionItem.addToViewpart((ViewPart) part);
                           WorkspaceContributionItem.addToViewpart((ViewPart) part);
                           OseeTargetContributionItem.addToViewpart((ViewPart) part);
                        }

                     }

                     @Override
                     public void partBroughtToTop(IWorkbenchPart part) {
                        if (part instanceof ViewPart) {
                           OseeBuildTypeContributionItem.addToViewpart((ViewPart) part);
                           ResServiceContributionItem.addToViewpart((ViewPart) part);
                           SessionContributionItem.addToViewpart((ViewPart) part);
                           WorkspaceContributionItem.addToViewpart((ViewPart) part);
                           OseeTargetContributionItem.addToViewpart((ViewPart) part);
                        }

                     }

                     @Override
                     public void partClosed(IWorkbenchPart part) {
                        // do nothing
                     }

                     @Override
                     public void partDeactivated(IWorkbenchPart part) {
                        // do nothing
                     }

                     @Override
                     public void partOpened(IWorkbenchPart part) {
                        if (part instanceof ViewPart) {
                           OseeBuildTypeContributionItem.addToViewpart((ViewPart) part);
                           ResServiceContributionItem.addToViewpart((ViewPart) part);
                           SessionContributionItem.addToViewpart((ViewPart) part);
                           WorkspaceContributionItem.addToViewpart((ViewPart) part);
                           OseeTargetContributionItem.addToViewpart((ViewPart) part);
                        }

                     }

                  });

               PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {

                  @Override
                  public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
                     if (page instanceof ViewPart) {
                        OseeBuildTypeContributionItem.addToViewpart((ViewPart) page);
                        ResServiceContributionItem.addToViewpart((ViewPart) page);
                        SessionContributionItem.addToViewpart((ViewPart) page);
                        WorkspaceContributionItem.addToViewpart((ViewPart) page);
                        OseeTargetContributionItem.addToViewpart((ViewPart) page);
                     }

                  }

                  @Override
                  public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
                     if (page instanceof ViewPart) {
                        OseeBuildTypeContributionItem.addToViewpart((ViewPart) page);
                        ResServiceContributionItem.addToViewpart((ViewPart) page);
                        SessionContributionItem.addToViewpart((ViewPart) page);
                        WorkspaceContributionItem.addToViewpart((ViewPart) page);
                        OseeTargetContributionItem.addToViewpart((ViewPart) page);
                     }

                  }

               });
            }
         });
      }
      OseeBuildTypeContributionItem.addToAllViews();
      ResServiceContributionItem.addToAllViews();
      SessionContributionItem.addToAllViews();
      WorkspaceContributionItem.addToAllViews();
      OseeTargetContributionItem.addToAllViews();
   }
}
