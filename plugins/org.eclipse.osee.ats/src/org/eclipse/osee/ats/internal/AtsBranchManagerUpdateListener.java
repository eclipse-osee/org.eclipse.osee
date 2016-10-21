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
package org.eclipse.osee.ats.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchManagerUpdateListener implements IBranchEventListener {

   private static final List<BranchEventType> EVENT_TYPES = Arrays.asList(BranchEventType.Added,
      BranchEventType.CommitFailed, BranchEventType.Committed, BranchEventType.Committing);

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
      if (!EVENT_TYPES.contains(branchEvent.getEventType())) {
         return;
      }
      try {
         ArtifactId associatedArtifact = BranchManager.getAssociatedArtifactId(branchEvent.getSourceBranch());
         Artifact assocArtInCache = ArtifactCache.getActive(associatedArtifact, COMMON);
         if (assocArtInCache != null && assocArtInCache instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) assocArtInCache;
            if (branchEvent.getEventType() == BranchEventType.Added) {
               teamArt.setWorkingBranchCreationInProgress(false);
            } else if (branchEvent.getEventType() == BranchEventType.Committing) {
               teamArt.setWorkingBranchCommitInProgress(true);
            } else if (branchEvent.getEventType() == BranchEventType.Committed || branchEvent.getEventType() == BranchEventType.CommitFailed) {
               teamArt.setWorkingBranchCommitInProgress(false);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }
}
