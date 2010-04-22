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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.change;

import java.util.ArrayList;
import java.util.Collection;
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
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class SingleNativeDiffHandler extends CommandHandler {
   private ArrayList<Change> changes;

   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      boolean enabled = false;

      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }

      try {
         ISelectionProvider selectionProvider =
               AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

         if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();

            changes = new ArrayList<Change>(Handlers.getArtifactChangesFromStructuredSelection(structuredSelection));

            if (changes.size() == 1) {
               ArtifactDelta delta = changes.iterator().next().getDelta();
               Artifact sampleArtifact = delta.getStartArtifact();
               enabled = AccessControlManager.hasPermission(sampleArtifact, PermissionEnum.READ);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
      return enabled;
   }

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      Collection<ArtifactDelta> compareArtifacts = ChangeManager.getCompareArtifacts(changes);
      for (ArtifactDelta delta : compareArtifacts) {
         try {
            RendererManager.diff(delta, true);
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return null;
   }
}