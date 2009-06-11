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

import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 */
public class WordChangesMadeToHandler extends AbstractHandler {
   private List<ArtifactChange> mySelectedArtifactChangeList;

   public WordChangesMadeToHandler() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      ArtifactChange selectedArtifactChange = mySelectedArtifactChangeList.get(0);
      try {
         execute(selectedArtifactChange);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      return null;
   }

   public void execute(ArtifactChange artifactChange) throws Exception {
      Artifact firstArtifact =
            artifactChange.getModificationType() == NEW ? null : ArtifactPersistenceManager.getInstance().getArtifactFromId(
                  artifactChange.getArtifact().getArtId(), artifactChange.getBaselineTransactionId());
      Artifact secondArtifact =
            artifactChange.getModificationType() == DELETED ? null : ArtifactPersistenceManager.getInstance().getArtifactFromId(
                  artifactChange.getArtifact().getArtId(), artifactChange.getToTransactionId());

      RendererManager.diffInJob(firstArtifact, secondArtifact);
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
         mySelectedArtifactChangeList = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);

         if (mySelectedArtifactChangeList.size() == 0) {
            return false;
         }
         ArtifactChange mySelectedArtifactChange = mySelectedArtifactChangeList.get(0);

         try {
            Artifact changedArtifact = mySelectedArtifactChange.getArtifact();
            boolean readPermission = AccessControlManager.checkObjectPermission(changedArtifact, PermissionEnum.READ);
            boolean wordArtifactSelected = changedArtifact.isOfType(WordArtifact.ARTIFACT_NAME);
            isEnabled = readPermission && wordArtifactSelected;
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return isEnabled;
   }
}
