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

import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import java.util.ArrayList;
import java.util.Date;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class SingleNativeDiffHandler extends CommandHandler {
   private ArrayList<ArtifactChange> artifactChanges;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.util.CommandHandler#isEnabledWithException()
    */
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

            artifactChanges =
                  new ArrayList<ArtifactChange>(Handlers.getArtifactChangesFromStructuredSelection(structuredSelection));

            enabled =
                  artifactChanges.size() == 1 && AccessControlManager.checkObjectPermission(artifactChanges.get(0).getArtifact(),
                        PermissionEnum.READ);
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
      return enabled;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      ArtifactChange artifactChange = artifactChanges.iterator().next();
      try {
         Artifact baseArtifact =
               (artifactChange.getModType() == NEW || artifactChange.getModType() == ModificationType.INTRODUCED) ? null : ArtifactPersistenceManager.getInstance().getArtifactFromId(
                     artifactChange.getArtifact().getArtId(), artifactChange.getBaselineTransactionId());

         Artifact newerArtifact =
               artifactChange.getModType() == DELETED ? null : (artifactChange.isHistorical() ? ArtifactPersistenceManager.getInstance().getArtifactFromId(
                     artifactChange.getArtifact().getArtId(), artifactChange.getToTransactionId()) : artifactChange.getArtifact());

         VariableMap variableMap = new VariableMap();
         String fileName = baseArtifact != null ? baseArtifact.getSafeName() : newerArtifact.getSafeName();
         variableMap.setValue("fileName", fileName + "_" + (new Date()).toString().replaceAll(":", ";") + ".xml");

         RendererManager.diff(baseArtifact, newerArtifact, true);

      } catch (OseeCoreException ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
      return null;
   }

}
