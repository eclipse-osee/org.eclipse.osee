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
package org.eclipse.osee.framework.ui.skynet.action;

import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.compare.CompareItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class WasIsCompareEditorAction extends Action {

   public WasIsCompareEditorAction() {
      super("View Was/Is Comparison");
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.COMPARE_DOCUMENTS);
   }

   @Override
   public void run() {
      try {
         ISelection selection =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
         if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            List<Change> localChanges = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);
            if (localChanges.isEmpty() || localChanges.size() > 1) {
               AWorkbench.popup("Can only show Was/Is for single selection");
               return;
            }
            Change change = localChanges.iterator().next();
            String was = change.getWasValue();
            String is = change.getIsValue();
            if (!Strings.isValid(was)) {
               AWorkbench.popup("\"Was Value\" is not a valid string; Nothing to compare.");
               return;
            }
            if (!Strings.isValid(is)) {
               AWorkbench.popup("\"Is Value\" is not a valid string; Nothing to compare.");
               return;
            }
            CompareHandler compareHandler =
               new CompareHandler(null, new CompareItem("Was", was, System.currentTimeMillis()), new CompareItem("Is",
                  is, System.currentTimeMillis()), null);
            compareHandler.compare();
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private static ISelectionProvider getSelectionProvider() {
      ISelectionProvider selectionProvider = null;
      IWorkbench workbench = PlatformUI.getWorkbench();
      if (!workbench.isStarting() && !workbench.isClosing()) {
         IWorkbenchPage page = AWorkbench.getActivePage();
         if (page != null) {
            IWorkbenchPart part = page.getActivePart();
            if (part != null) {
               IWorkbenchSite site = part.getSite();
               if (site != null) {
                  selectionProvider = site.getSelectionProvider();
               }
            }
         }
      }
      return selectionProvider;
   }

   public static boolean isEnabledStatic() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }
      boolean isEnabled = false;

      ISelectionProvider selectionProvider = getSelectionProvider();
      if (selectionProvider != null) {
         ISelection selection = selectionProvider.getSelection();
         if (selection instanceof IStructuredSelection) {
            isEnabled = ((IStructuredSelection) selection).size() == 1;
         }
      }
      return isEnabled;
   }
}
