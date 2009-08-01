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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class ShowArtifactInResourceHandler extends CommandHandler {
   private List<Artifact> artifacts;

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      for (Artifact artifact : artifacts) {
         try {
            HistoryView.open(artifact);
            
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return null;
   }

   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }
      boolean isEnabled = false;

      ISelectionProvider selectionProvider =
            AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);

         if (artifacts.isEmpty()) {
            return false;
         }

         boolean readPermission = true;
         for (Artifact artifact : artifacts) {
            readPermission &= AccessControlManager.hasPermission(artifact, PermissionEnum.READ);
         }
         isEnabled = readPermission;
      }
      return isEnabled;
   }
}
