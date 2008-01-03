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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.CHANGE;
import java.sql.SQLException;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.ChangeReportInput;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.ChangeType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Paul K. Waldfogel
 */
public class WordChangesBetweenCurrentAndParentHandler extends AbstractSelectionChangedHandler {
   private static final ArtifactPersistenceManager myArtifactPersistenceManager =
         ArtifactPersistenceManager.getInstance();
   private static final String DIFF_ARTIFACT = "DIFF_ARTIFACT";
   private static final AccessControlManager myAccessControlManager = AccessControlManager.getInstance();
   private List<ArtifactChange> mySelectedArtifactChangeList;

   public WordChangesBetweenCurrentAndParentHandler() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      IStructuredSelection myIStructuredSelection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();

      List<ChangeReportInput> getChangeReportInputNewList =
            Handlers.getChangeReportInputNewListFromStructuredSelection(myIStructuredSelection);
      if (mySelectedArtifactChangeList.size() > 0) {
         ArtifactChange selectedArtifactChange = mySelectedArtifactChangeList.get(0);
         TransactionId toTransactionId = getChangeReportInputNewList.get(0).getToTransaction();
         try {
            Artifact secondArtifact =
                  myArtifactPersistenceManager.getArtifactFromId(selectedArtifactChange.getArtifact().getArtId(),
                        toTransactionId);
            RendererManager.getInstance().compareInJob(selectedArtifactChange.getConflictingModArtifact(),
                  secondArtifact, DIFF_ARTIFACT);
         } catch (Exception ex) {
            OSEELog.logException(getClass(), ex, false);
         }
      }

      return null;
   }

   @Override
   public boolean isEnabled() {
      IStructuredSelection myIStructuredSelection =
            (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      mySelectedArtifactChangeList = Handlers.getArtifactChangeListFromStructuredSelection(myIStructuredSelection);
      if (mySelectedArtifactChangeList.size() == 0) {
         return false;
      }
      ArtifactChange mySelectedArtifactChange = mySelectedArtifactChangeList.get(0);
      Artifact changedArtifact = null;
      try {
         changedArtifact = mySelectedArtifactChange.getArtifact();
         boolean readPermission = myAccessControlManager.checkObjectPermission(changedArtifact, PermissionEnum.READ);
         boolean wordArtifactSelected = changedArtifact instanceof WordArtifact;
         boolean modifiedWordArtifactSelected = wordArtifactSelected && mySelectedArtifactChange.getModType() == CHANGE;
         boolean conflictedWordArtifactSelected =
               modifiedWordArtifactSelected && mySelectedArtifactChange.getChangeType() == ChangeType.CONFLICTING;
         return readPermission && conflictedWordArtifactSelected;
      } catch (SQLException ex) {
         OSEELog.logException(getClass(), ex, true);
         return (false);
      }
   }
}
