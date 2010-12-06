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
import org.eclipse.osee.ats.util.TeamState;
import org.eclipse.osee.ats.workflow.item.AtsStatePercentCompleteWeightDecisionReviewRule;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionDecisionWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionFollowupWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionPrepareWorkPageDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

/**
 * @author Donald G. Dunne
 */
public class DecisionWorkflowDefinition extends WorkFlowDefinition {

   public final static String ID = "osee.ats.decisionReview";
   public final static String DECISION_CANCELLED_STATE_ID = ID + "." + TeamState.Cancelled.getPageName();

   public DecisionWorkflowDefinition() {
      super(ID, ID, null);
      addTransitions();
      startPageId = AtsDecisionPrepareWorkPageDefinition.ID;
      addWorkItem(AtsStatePercentCompleteWeightDecisionReviewRule.ID);
   }

   public DecisionWorkflowDefinition(Artifact artifact) throws OseeCoreException {
      super(artifact);
      throw new OseeStateException("This constructor should never be used.");
   }

   public void config(WriteType writeType, XResultData xResultData) throws OseeCoreException {
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData, getAtsWorkDefinitions());
   }

   public static List<WorkItemDefinition> getAtsWorkDefinitions() {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

      // Add Decision Pages and Workflow Definition
      workItems.add(new AtsDecisionPrepareWorkPageDefinition(1));
      workItems.add(new AtsDecisionDecisionWorkPageDefinition(2));
      workItems.add(new AtsDecisionFollowupWorkPageDefinition(3));
      workItems.add(new AtsDecisionCompletedWorkPageDefinition(4));
      workItems.add(new WorkPageDefinition(TeamState.Cancelled.getPageName(),
         DecisionWorkflowDefinition.DECISION_CANCELLED_STATE_ID, AtsCancelledWorkPageDefinition.ID,
         WorkPageType.Cancelled, 5));

      workItems.add(new DecisionWorkflowDefinition());

      return workItems;
   }

   private void addTransitions() {
      // Add Prepare Transitions
      addPageTransition(AtsDecisionPrepareWorkPageDefinition.ID, AtsDecisionDecisionWorkPageDefinition.ID,
         TransitionType.ToPageAsDefault);
      addPageTransitionToPageAndReturn(AtsDecisionPrepareWorkPageDefinition.ID, DECISION_CANCELLED_STATE_ID);

      // Add Decision Transitions
      addPageTransition(AtsDecisionDecisionWorkPageDefinition.ID, AtsDecisionCompletedWorkPageDefinition.ID,
         TransitionType.ToPageAsDefault);
      addPageTransition(AtsDecisionDecisionWorkPageDefinition.ID, AtsDecisionFollowupWorkPageDefinition.ID,
         TransitionType.ToPage);
      addPageTransition(AtsDecisionDecisionWorkPageDefinition.ID, AtsDecisionPrepareWorkPageDefinition.ID,
         TransitionType.ToPageAsReturn);
      addPageTransitionToPageAndReturn(AtsDecisionDecisionWorkPageDefinition.ID, DECISION_CANCELLED_STATE_ID);

      // Add Followup Transitions
      addPageTransition(AtsDecisionFollowupWorkPageDefinition.ID, AtsDecisionDecisionWorkPageDefinition.ID,
         TransitionType.ToPageAsReturn);
      addPageTransition(AtsDecisionFollowupWorkPageDefinition.ID, AtsDecisionCompletedWorkPageDefinition.ID,
         TransitionType.ToPageAsDefault);
      addPageTransitionToPageAndReturn(AtsDecisionFollowupWorkPageDefinition.ID, DECISION_CANCELLED_STATE_ID);

      // Add Completed Transitions
      addPageTransition(AtsDecisionCompletedWorkPageDefinition.ID, AtsDecisionDecisionWorkPageDefinition.ID,
         TransitionType.ToPageAsReturn);
      addPageTransition(AtsDecisionCompletedWorkPageDefinition.ID, AtsDecisionFollowupWorkPageDefinition.ID,
         TransitionType.ToPageAsReturn);

      // Add Cancelled Transitions
      addPageTransition(DECISION_CANCELLED_STATE_ID, AtsDecisionPrepareWorkPageDefinition.ID,
         TransitionType.ToPageAsReturn);
      addPageTransition(DECISION_CANCELLED_STATE_ID, AtsDecisionDecisionWorkPageDefinition.ID,
         TransitionType.ToPageAsReturn);
      addPageTransition(DECISION_CANCELLED_STATE_ID, AtsDecisionFollowupWorkPageDefinition.ID,
         TransitionType.ToPageAsReturn);

   }

}
