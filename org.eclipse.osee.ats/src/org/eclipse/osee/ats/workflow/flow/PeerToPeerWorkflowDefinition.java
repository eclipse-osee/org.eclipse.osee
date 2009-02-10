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
package org.eclipse.osee.ats.workflow.flow;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.item.AtsStatePercentCompleteWeightPeerToPeerReviewRule;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsPeerPrepareWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsPeerReviewWorkPageDefinition;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class PeerToPeerWorkflowDefinition extends WorkFlowDefinition {

   public static String ID = "osee.ats.peerToPeerReview";
   public static String PEER_REVIEW_COMPLETED_STATE_ID = ID + "." + DefaultTeamState.Completed.name();
   public static String PEER_REVIEW_CANCELLED_STATE_ID = ID + "." + DefaultTeamState.Cancelled.name();

   public PeerToPeerWorkflowDefinition() {
      super(ID, ID, null);
      addTransitions();
      startPageId = AtsPeerPrepareWorkPageDefinition.ID;
      addWorkItem(AtsStatePercentCompleteWeightPeerToPeerReviewRule.ID);
   }

   public PeerToPeerWorkflowDefinition(Artifact artifact) throws OseeCoreException {
      super(artifact);
      throw new OseeStateException("This constructor should never be used.");
   }

   public void config(WriteType writeType, XResultData xResultData) throws OseeCoreException {
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData, getAtsWorkDefinitions());
   }

   public static List<WorkItemDefinition> getAtsWorkDefinitions() {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

      // Add PeerToPeer Pages and Workflow Definition
      workItems.add(new AtsPeerPrepareWorkPageDefinition());
      workItems.add(new AtsPeerReviewWorkPageDefinition());
      workItems.add(new WorkPageDefinition(DefaultTeamState.Completed.name(),
            PeerToPeerWorkflowDefinition.PEER_REVIEW_COMPLETED_STATE_ID, AtsCompletedWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Cancelled.name(),
            PeerToPeerWorkflowDefinition.PEER_REVIEW_CANCELLED_STATE_ID, AtsCancelledWorkPageDefinition.ID));
      workItems.add(new PeerToPeerWorkflowDefinition());

      return workItems;
   }

   private void addTransitions() {
      // Add default transitions
      addPageTransition(AtsPeerPrepareWorkPageDefinition.ID, AtsPeerReviewWorkPageDefinition.ID,
            TransitionType.ToPageAsDefault);
      addPageTransition(AtsPeerReviewWorkPageDefinition.ID, PEER_REVIEW_COMPLETED_STATE_ID,
            TransitionType.ToPageAsDefault);

      // Add return transitions
      addPageTransition(PEER_REVIEW_COMPLETED_STATE_ID, AtsPeerReviewWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);
      addPageTransition(AtsPeerReviewWorkPageDefinition.ID, AtsPeerPrepareWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);
      addPageTransition(PEER_REVIEW_CANCELLED_STATE_ID, AtsPeerReviewWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);
      addPageTransition(PEER_REVIEW_CANCELLED_STATE_ID, AtsPeerPrepareWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);

      // Add cancelled transitions
      addPageTransitionToPageAndReturn(AtsPeerPrepareWorkPageDefinition.ID, PEER_REVIEW_CANCELLED_STATE_ID);
      addPageTransitionToPageAndReturn(AtsPeerReviewWorkPageDefinition.ID, PEER_REVIEW_CANCELLED_STATE_ID);
   }

}
