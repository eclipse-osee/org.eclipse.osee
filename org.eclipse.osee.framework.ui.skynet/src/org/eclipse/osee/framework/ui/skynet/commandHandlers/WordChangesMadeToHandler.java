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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.DELETE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ModificationType.NEW;
import java.sql.SQLException;
import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 */
public class WordChangesMadeToHandler extends AbstractSelectionChangedHandler {
   private static final ArtifactPersistenceManager myArtifactPersistenceManager =
         ArtifactPersistenceManager.getInstance();
   private static final String DIFF_ARTIFACT = "DIFF_ARTIFACT";
   private static final AccessControlManager myAccessControlManager = AccessControlManager.getInstance();
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
         Artifact firstArtifact =
               selectedArtifactChange.getModType() == NEW ? null : myArtifactPersistenceManager.getArtifactFromId(
                     selectedArtifactChange.getArtifact().getArtId(), selectedArtifactChange.getBaselineTransactionId());
         Artifact secondArtifact =
               selectedArtifactChange.getModType() == DELETE ? null : myArtifactPersistenceManager.getArtifactFromId(
                     selectedArtifactChange.getArtifact().getArtId(), selectedArtifactChange.getToTransactionId());
         RendererManager.getInstance().compareInJob(firstArtifact, secondArtifact, DIFF_ARTIFACT);

      } catch (Exception ex) {
         OSEELog.logException(getClass(), ex, false);
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
         mySelectedArtifactChangeList = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);

         if (mySelectedArtifactChangeList.size() == 0) {
            return false;
         }
         ArtifactChange mySelectedArtifactChange = mySelectedArtifactChangeList.get(0);

         try {
            Artifact changedArtifact = mySelectedArtifactChange.getArtifact();
            boolean readPermission = myAccessControlManager.checkObjectPermission(changedArtifact, PermissionEnum.READ);
            boolean wordArtifactSelected = changedArtifact instanceof WordArtifact;
            isEnabled = readPermission && wordArtifactSelected;
         } catch (SQLException ex) {
            OSEELog.logException(getClass(), ex, true);
         }
      }
      return isEnabled;
   }
}
