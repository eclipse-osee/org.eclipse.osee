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
import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 */
public class WordChangesBetweenCurrentAndParentHandler extends AbstractHandler {
   private ArtifactChange artifactChange;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      try {
         Artifact secondArtifact =
               ArtifactPersistenceManager.getInstance().getArtifactFromId(artifactChange.getArtifact().getArtId(),
                     artifactChange.getToTransactionId());
         RendererManager.diffInJob(artifactChange.getConflictingModArtifact(), secondArtifact);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }

      boolean isEnabled = false;
      ISelectionProvider selectionProvider =
            AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         List<ArtifactChange> artifactChanges = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);

         if (artifactChanges.size() == 0) {
            return false;
         }

         artifactChange = artifactChanges.get(0);
         try {
            Artifact artifact = artifactChange.getArtifact();

            boolean readPermission = AccessControlManager.checkObjectPermission(artifact, PermissionEnum.READ);
            boolean wordArtifactSelected = artifact instanceof WordArtifact;
            boolean modifiedWordArtifactSelected =
                  wordArtifactSelected && artifactChange.getModType() == ModificationType.CHANGE;
            boolean conflictedWordArtifactSelected =
                  modifiedWordArtifactSelected && artifactChange.getChangeType() == ChangeType.CONFLICTING;
            isEnabled = readPermission && conflictedWordArtifactSelected;
         } catch (Exception ex) {
            OSEELog.logException(getClass(), ex, true);
         }
      }
      return isEnabled;
   }
}
