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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Paul K. Waldfogel
 */
public class ShowInArtifactExplorerHandler extends AbstractSelectionHandler {
   private static final BranchPersistenceManager myBranchPersistenceManager = BranchPersistenceManager.getInstance();

   public ShowInArtifactExplorerHandler() {
      super(new String[] {});
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      List<Artifact> mySelectedArtifactList = super.getArtifactList();
      for (Artifact artifact : mySelectedArtifactList) {
         ArtifactExplorer.revealArtifact(artifact);
      }
      return null;
   }


   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.commandHandlers.AbstractArtifactSelectionHandler#permissionLevel()
    */
   @Override
   protected PermissionEnum permissionLevel() {
      return PermissionEnum.READ;
   }

   @Override
   public boolean isEnabled() {
      try {
         List<ArtifactChange> mySelectedArtifactChangeList = super.getArtifactChangeList();
         List<Artifact> mySelectedArtifactList = super.getArtifactList();
         ArtifactChange mySelectedArtifactChange = mySelectedArtifactChangeList.get(0);
         Artifact changedArtifact = mySelectedArtifactChange.getArtifact();
         Branch reportBranch = changedArtifact.getBranch();
         Branch defaultBranch = myBranchPersistenceManager.getDefaultBranch();
         return mySelectedArtifactList.size() > 0 && reportBranch == defaultBranch;
      } catch (SQLException ex) {
         OSEELog.logException(getClass(), ex, true);
         return false;
      }
   }

}
