/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.plugin.util;

import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.internal.UiPluginConstants;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ryan D. Brooks
 */
public abstract class CommandHandler extends AbstractHandler {

   @Override
   public boolean isEnabled() {
      boolean result = false;
      IWorkbench workbench = PlatformUI.getWorkbench();
      if (PlatformUI.isWorkbenchRunning() && !workbench.isStarting() && !workbench.isClosing()) {
         try {
            IStructuredSelection selection = getCurrentSelection();
            if (selection != null) {
               result = isEnabledWithException(selection);
            }
         } catch (Exception ex) {
            OseeLog.log(UiPluginConstants.class, Level.SEVERE, ex);
         }
      }
      return result;
   }

   protected abstract boolean isEnabledWithException(IStructuredSelection structuredSelection);

   @Override
   public final Object execute(ExecutionEvent event) throws ExecutionException {
      try {
         IStructuredSelection selection = getCurrentSelection();
         Object result = null;
         if (selection != null) {
            result = executeWithException(event, selection);
         }
         return result;
      } catch (Exception ex) {
         throw new ExecutionException("Error executing command handler: ", ex);
      }
   }

   protected abstract Object executeWithException(ExecutionEvent event, IStructuredSelection selection);

   protected static ISelectionProvider getSelectionProvider() {
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

   protected static IStructuredSelection getCurrentSelection() throws Exception {
      IStructuredSelection structuredSelection = null;
      ISelectionProvider selectionProvider = getSelectionProvider();
      if (selectionProvider != null) {
         ISelection selection = selectionProvider.getSelection();
         if (selection instanceof IStructuredSelection) {
            structuredSelection = (IStructuredSelection) selection;
         }
      }

      return structuredSelection;
   }
}
