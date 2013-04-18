/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal.event.handlers;

import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBranchEvent1;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerRemote;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Roberto E. Escobar
 */
public class BranchRemoteEventHandler implements EventHandlerRemote<RemoteBranchEvent1> {

   @Override
   public void handle(Transport transport, Sender sender, RemoteBranchEvent1 remoteEvent) throws OseeCoreException {
      BranchEvent branchEvent = FrameworkEventUtil.getBranchEvent(remoteEvent);
      updateBranches(sender, branchEvent);
      transport.send(sender, branchEvent);
   }

   private void updateBranches(Sender sender, BranchEvent branchEvent) {
      BranchEventType eventType = branchEvent.getEventType();
      Branch branch = null;
      try {
         switch (eventType) {
            case Committing:
            case CommitFailed:
            case Deleting:
            case Purging:
               return;
            case Committed:
               branch = BranchManager.getBranchByGuid(branchEvent.getBranchGuid());
               Artifact artifact = BranchManager.getAssociatedArtifact(branch);
               TransactionManager.clearCommitArtifactCacheForAssociatedArtifact(artifact);
               break;
            case Purged:
               handleBranchRemoval(BranchState.PURGED, branchEvent.getBranchGuid());
               break;
            case Deleted:
               handleBranchRemoval(BranchState.DELETED, branchEvent.getBranchGuid());
               break;
            default:
               break;
         }
         // Need to update only branch(s) that changed, not refresh all branches cause it will
         // clear any local flags that have not yet been persisted to the database like DELETED or COMMIT_IN_PROGRESS
         BranchManager.refreshBranches();
      } catch (Exception ex) {
         EventUtil.eventLog("REM: updateBranches", ex);
      }
   }

   private void handleBranchRemoval(BranchState state, String guid) throws OseeCoreException {
      if (BranchManager.branchExists(guid)) {
         Branch branch = BranchManager.getBranchByGuid(guid);
         branch.setBranchState(state);
         branch.setArchived(true);
         branch.clearDirty();
         BranchManager.decache(branch);
      }
   }

}
