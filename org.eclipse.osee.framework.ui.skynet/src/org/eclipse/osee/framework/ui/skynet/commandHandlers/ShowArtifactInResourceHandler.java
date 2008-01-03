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

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Jeff C. Phillips
 */
public class ShowArtifactInResourceHandler extends AbstractSelectionChangedHandler {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ShowArtifactInResourceHandler.class);
   private static final BranchPersistenceManager myBranchPersistenceManager = BranchPersistenceManager.getInstance();
   private static final AccessControlManager myAccessControlManager = AccessControlManager.getInstance();

   public ShowArtifactInResourceHandler() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      List<Artifact> artifacts =
            Handlers.getArtifactsFromStructuredSelection((IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection());
      for (Artifact artifact : artifacts) {
         IWorkbenchPage page = AWorkbench.getActivePage();
         try {
            RevisionHistoryView revisionHistoryView =
                  (RevisionHistoryView) page.showView(RevisionHistoryView.VIEW_ID, artifact.getGuid(),
                        IWorkbenchPage.VIEW_ACTIVATE);
            revisionHistoryView.explore(artifact);
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      try {
         IStructuredSelection myIStructuredSelection =
               (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
         List<ArtifactChange> mySelectedArtifactChangeList =
               Handlers.getArtifactChangeListFromStructuredSelection(myIStructuredSelection);
         ArtifactChange mySelectedArtifactChange = mySelectedArtifactChangeList.get(0);
         Artifact changedArtifact = mySelectedArtifactChange.getArtifact();
         Branch reportBranch = changedArtifact.getBranch();
         boolean readPermission = myAccessControlManager.checkObjectPermission(changedArtifact, PermissionEnum.READ);
         return readPermission && reportBranch == myBranchPersistenceManager.getDefaultBranch();

      } catch (SQLException ex) {
         OSEELog.logException(getClass(), ex, true);
         return (false);
      }
   }

}
