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
package org.eclipse.osee.ats.editor.stateItem;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.workdef.DecisionReviewDefinition;
import org.eclipse.osee.ats.workdef.PeerReviewDefinition;
import org.eclipse.osee.ats.workdef.StateEventType;
import org.eclipse.osee.ats.workflow.item.AtsAddDecisionReviewRule;
import org.eclipse.osee.ats.workflow.item.AtsAddPeerToPeerReviewRule;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;

/**
 * @author Donald G. Dunne
 */
public class AtsHandleAddReviewRuleStateItem extends AtsStateItem {

   public AtsHandleAddReviewRuleStateItem() {
      super(AtsHandleAddReviewRuleStateItem.class.getSimpleName());
   }

   @Override
   public String getDescription() {
      return "Create review if AddDecisionReviewRule or AddPeerToPeerReviewRule exists for this state.";
   }

   @Override
   public void transitioned(AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<User> toAssignees, SkynetTransaction transaction) throws OseeCoreException {
      // Create any decision or peerToPeer reviews for transitionTo and transitionFrom
      if (!sma.isTeamWorkflow()) {
         return;
      }
      Date createdDate = new Date();
      User createdBy = UserManager.getUser(SystemUser.OseeSystem);
      TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) sma;

      for (DecisionReviewDefinition decRevDef : teamArt.getStateDefinition().getDecisionReviews()) {
         if (decRevDef.getStateEventType() != null && decRevDef.getStateEventType().equals(StateEventType.TransitionTo)) {
            DecisionReviewArtifact decArt =
               AtsAddDecisionReviewRule.createNewDecisionReview(decRevDef, transaction, teamArt, createdDate, createdBy);
            if (decArt != null) {
               decArt.persist(transaction);
            }
         }
      }

      for (PeerReviewDefinition peerRevDef : teamArt.getStateDefinition().getPeerReviews()) {
         if (peerRevDef.getStateEventType() != null && peerRevDef.getStateEventType().equals(
            StateEventType.TransitionTo)) {
            PeerToPeerReviewArtifact decArt =
               AtsAddPeerToPeerReviewRule.createNewPeerToPeerReview(peerRevDef, transaction, teamArt, createdDate,
                  createdBy);
            if (decArt != null) {
               decArt.persist(transaction);
            }
         }
      }
   }

}
