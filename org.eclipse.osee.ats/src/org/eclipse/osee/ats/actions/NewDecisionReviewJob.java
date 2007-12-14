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

import java.sql.SQLException;
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
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;

/**
 * @author Donald G. Dunne
 */
public class NewDecisionReviewJob extends Job {
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private final TeamWorkFlowArtifact teamParent;
   private final boolean againstCurrentState;
   private DecisionReviewArtifact decisionReviewArtifact;

   public NewDecisionReviewJob(TeamWorkFlowArtifact teamParent, boolean againstCurrentState) {
      super("Creating New Decision Review");
      this.teamParent = teamParent;
      this.againstCurrentState = againstCurrentState;
   }

   @Override
   public IStatus run(final IProgressMonitor monitor) {
      try {
         AbstractSkynetTxTemplate newDecisionReviewTx = new AbstractSkynetTxTemplate(branchManager.getAtsBranch()) {

            @Override
            protected void handleTxWork() throws Exception {
               decisionReviewArtifact = createNewDecisionReview(teamParent, againstCurrentState);
               decisionReviewArtifact.persist(true);
            }

         };
         newDecisionReviewTx.execute();
         AtsLib.openAtsAction(decisionReviewArtifact, AtsOpenOption.OpenOneOrPopupSelect);
      } catch (Exception ex) {
         monitor.done();
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, "Error creating Decision Review", ex);
      } finally {
         monitor.done();
      }
      return Status.OK_STATUS;
   }

   public static DecisionReviewArtifact createNewDecisionReview(StateMachineArtifact teamParent, boolean againstCurrentState) throws SQLException {
      DecisionReviewArtifact decRev =
            (DecisionReviewArtifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                  DecisionReviewArtifact.ARTIFACT_NAME, branchManager.getAtsBranch()).makeNewArtifact();

      if (teamParent != null) teamParent.relate(RelationSide.TeamWorkflowToReview_Review, decRev);
      if (againstCurrentState) decRev.setSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(),
            teamParent.getCurrentStateName());

      decRev.getLog().addLog(LogType.Originated, "", "");
      decRev.setDescriptiveName("Should we do this?  Yes will require followup, No will not");
      decRev.setSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(),
            "Enter description of the decision, if any");
      decRev.setSoleAttributeValue(
            ATSAttributes.DECISION_REVIEW_OPTIONS_ATTRIBUTE.getStoreName(),
            "Yes;Followup;<" + SkynetAuthentication.getInstance().getAuthenticatedUser().getUserId() + ">\n" + "No;Completed;");

      // Set state
      // Set current state and POCs
      decRev.getCurrentStateDam().setState(
            new SMAState(DecisionReviewArtifact.StateNames.Prepare.name(),
                  SkynetAuthentication.getInstance().getAuthenticatedUser()));
      decRev.getLog().addLog(LogType.StateEntered, DecisionReviewArtifact.StateNames.Prepare.name(), "");

      return decRev;
   }
}
