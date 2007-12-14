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

package org.eclipse.osee.ats.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;

/**
 * @author Donald G. Dunne
 */
public class NewPeerToPeerReviewJob extends Job {
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();

   private final TeamWorkFlowArtifact teamParent;
   private final String againstState;
   private PeerToPeerReviewArtifact peerToPeerReviewArtifact;

   public NewPeerToPeerReviewJob(TeamWorkFlowArtifact teamParent, String againstState) {
      super("Creating New PeerToPeer Review");
      this.teamParent = teamParent;
      this.againstState = againstState;
   }

   @Override
   public IStatus run(final IProgressMonitor monitor) {
      try {
         AbstractSkynetTxTemplate newPeerToPeerTx = new AbstractSkynetTxTemplate(branchManager.getAtsBranch()) {

            @Override
            protected void handleTxWork() throws Exception {
               peerToPeerReviewArtifact =
                     teamParent.getSmaMgr().getReviewManager().createNewPeerToPeerReview(againstState);
               peerToPeerReviewArtifact.persist(true);
            }

         };
         newPeerToPeerTx.execute();

         AtsLib.openAtsAction(peerToPeerReviewArtifact, AtsOpenOption.OpenOneOrPopupSelect);
      } catch (Exception ex) {
         monitor.done();
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, "Error creating PeerToPeer Review", ex);
      } finally {
         monitor.done();
      }
      return Status.OK_STATUS;
   }
}
