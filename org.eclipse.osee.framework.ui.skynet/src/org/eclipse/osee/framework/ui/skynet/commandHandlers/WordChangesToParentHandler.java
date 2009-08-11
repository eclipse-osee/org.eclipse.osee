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
import static org.eclipse.osee.framework.core.enums.ModificationType.MODIFIED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 */
public class WordChangesToParentHandler extends AbstractHandler {
   private List<Change> mySelectedArtifactChangeList;

   public WordChangesToParentHandler() {
   }

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (mySelectedArtifactChangeList.size() > 0) {
         Change selectedArtifactChange = mySelectedArtifactChangeList.get(0);
         try {
            Artifact firstArtifact =
                  selectedArtifactChange.getModificationType() == NEW ? null : ArtifactQuery.getHistoricalArtifactFromId(
                        selectedArtifactChange.getArtifact().getArtId(), selectedArtifactChange.getFromTransactionId(),
                        true);

            Artifact secondArtifact = null;
            Branch parentBranch = firstArtifact.getBranch().getParentBranch();

            secondArtifact =
                  selectedArtifactChange.getModificationType() == DELETED ? null : ArtifactQuery.getArtifactFromId(
                        selectedArtifactChange.getArtifact().getArtId(), parentBranch);

            RendererManager.diffInJob(firstArtifact, secondArtifact);

         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }

      boolean isEnabled = false;

      try {
         ISelectionProvider selectionProvider =
               AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

         if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
            mySelectedArtifactChangeList = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);

            if (mySelectedArtifactChangeList.size() == 0) {
               return false;
            }
            Change mySelectedArtifactChange = mySelectedArtifactChangeList.get(0);

            if (mySelectedArtifactChange.getModificationType() == NEW || mySelectedArtifactChange.getModificationType() == DELETED) {
               return false;
            }

            Artifact changedArtifact = mySelectedArtifactChange.getArtifact();
            Branch reportBranch = changedArtifact.getBranch();
            boolean wordArtifactSelected =
                  changedArtifact.isAttributeTypeValid(CoreAttributes.WHOLE_WORD_CONTENT.getName()) || changedArtifact.isAttributeTypeValid(CoreAttributes.WORD_TEMPLATE_CONTENT.getName());
            boolean validDiffParent = wordArtifactSelected && reportBranch.hasParentBranch();

            boolean readPermission = AccessControlManager.hasPermission(changedArtifact, PermissionEnum.READ);
            boolean modifiedWordArtifactSelected =
                  wordArtifactSelected && mySelectedArtifactChange.getModificationType() == MODIFIED;
            isEnabled = validDiffParent && modifiedWordArtifactSelected && readPermission;
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
      return isEnabled;
   }
}