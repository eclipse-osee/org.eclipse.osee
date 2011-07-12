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
package org.eclipse.osee.framework.ui.plugin.util;

import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.internal.OseePluginUiActivator;
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
      try {
         IStructuredSelection selection = getCurrentSelection();
         if (selection != null) {
            result = isEnabledWithException(selection);
         }
      } catch (Exception ex) {
         OseeLog.log(OseePluginUiActivator.class, Level.SEVERE, ex);
      }
      return result;
   }

   public abstract boolean isEnabledWithException(IStructuredSelection structuredSelection) throws OseeCoreException;

   @Override
   public final Object execute(ExecutionEvent event) throws ExecutionException {
      try {
         return executeWithException(event);
      } catch (OseeCoreException ex) {
         //         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         throw new ExecutionException("Error executing command handler: ", ex);
      }
   }

   public abstract Object executeWithException(ExecutionEvent event) throws OseeCoreException;

   public static IStructuredSelection getCurrentSelection() throws Exception {
      IStructuredSelection structuredSelection = null;
      IWorkbench workbench = PlatformUI.getWorkbench();
      if (!workbench.isClosing() || !workbench.isStarting()) {
         IWorkbenchPage page = AWorkbench.getActivePage();
         if (page != null) {
            IWorkbenchPart part = page.getActivePart();
            if (part != null) {
               IWorkbenchSite site = part.getSite();
               if (site != null) {
                  ISelectionProvider selectionProvider = site.getSelectionProvider();
                  if (selectionProvider != null) {
                     ISelection selection = selectionProvider.getSelection();
                     if (selection instanceof IStructuredSelection) {
                        structuredSelection = (IStructuredSelection) selection;
                     }
                  }
               }
            }
         }
      }
      return structuredSelection;
   }
}
