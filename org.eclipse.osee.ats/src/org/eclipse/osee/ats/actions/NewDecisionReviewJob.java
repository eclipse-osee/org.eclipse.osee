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

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;

/**
 * @author Donald G. Dunne
 */
public class NewDecisionReviewJob extends Job {
   private final TeamWorkFlowArtifact teamParent;
   private final boolean againstCurrentState;
   private DecisionReviewArtifact decisionReviewArtifact;
   private final ReviewBlockType reviewBlockType;

   public NewDecisionReviewJob(TeamWorkFlowArtifact teamParent, ReviewBlockType reviewBlockType, boolean againstCurrentState) {
      super("Creating New Decision Review");
      this.teamParent = teamParent;
      this.reviewBlockType = reviewBlockType;
      this.againstCurrentState = againstCurrentState;
   }

   @Override
   public IStatus run(final IProgressMonitor monitor) {
      try {
         decisionReviewArtifact = createNewDecisionReview(teamParent, reviewBlockType, againstCurrentState);
         decisionReviewArtifact.persistAttributesAndRelations();
         AtsLib.openAtsAction(decisionReviewArtifact, AtsOpenOption.OpenOneOrPopupSelect);
      } catch (Exception ex) {
         monitor.done();
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, "Error creating Decision Review", ex);
      } finally {
         monitor.done();
      }
      return Status.OK_STATUS;
   }

   public static DecisionReviewArtifact createNewDecisionReview(StateMachineArtifact teamParent, ReviewBlockType reviewBlockType, boolean againstCurrentState) throws OseeCoreException {
      return createNewDecisionReview(teamParent, reviewBlockType,
            "Should we do this?  Yes will require followup, No will not",
            againstCurrentState ? teamParent.getSmaMgr().getStateMgr().getCurrentStateName() : null,
            "Enter description of the decision, if any",
            "Yes;Followup;<" + UserManager.getUser().getUserId() + ">\n" + "No;Completed;", null);
   }

   public static DecisionReviewArtifact createNewDecisionReview(StateMachineArtifact teamParent, ReviewBlockType reviewBlockType, String title, String relatedToState, String description, String options, Collection<User> assignees) throws OseeCoreException {
      DecisionReviewArtifact decRev =
            (DecisionReviewArtifact) ArtifactTypeManager.addArtifact(DecisionReviewArtifact.ARTIFACT_NAME,
                  AtsPlugin.getAtsBranch(), title);

      if (teamParent != null) {
         teamParent.addRelation(AtsRelation.TeamWorkflowToReview_Review, decRev);
      }
      if (relatedToState != null && !relatedToState.equals("")) {
         decRev.setSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), relatedToState);
      }
      decRev.getSmaMgr().getLog().addLog(LogType.Originated, "", "");
      if (description != null && !description.equals("")) {
         decRev.setSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), description);
      }
      if (options != null && !options.equals("")) {
         decRev.setSoleAttributeValue(ATSAttributes.DECISION_REVIEW_OPTIONS_ATTRIBUTE.getStoreName(), options);
      }
      if (reviewBlockType != null) {
         decRev.setSoleAttributeFromString(ATSAttributes.REVIEW_BLOCKS_ATTRIBUTE.getStoreName(), reviewBlockType.name());
      }

      // Initialize state machine
      decRev.getSmaMgr().getStateMgr().initializeStateMachine(DecisionReviewArtifact.DecisionReviewState.Prepare.name());
      decRev.getSmaMgr().getLog().addLog(LogType.StateEntered,
            DecisionReviewArtifact.DecisionReviewState.Prepare.name(), "");
      if (assignees != null && assignees.size() > 0) {
         decRev.getSmaMgr().getStateMgr().setAssignees(assignees);
      }

      return decRev;
   }
}
