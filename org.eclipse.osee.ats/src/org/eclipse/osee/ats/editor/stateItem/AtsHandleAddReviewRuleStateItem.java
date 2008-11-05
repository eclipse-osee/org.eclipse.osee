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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.editor.AtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.workflow.item.AtsAddDecisionReviewRule;
import org.eclipse.osee.ats.workflow.item.AtsAddPeerToPeerReviewRule;
import org.eclipse.osee.ats.workflow.item.StateEventType;
import org.eclipse.osee.ats.workflow.item.AtsAddDecisionReviewRule.DecisionParameter;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsHandleAddReviewRuleStateItem extends AtsStateItem {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getId()
    */
   @Override
   public String getId() {
      return AtsStateItem.ALL_STATE_IDS;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.AtsStateItem#transitioned(org.eclipse.osee.ats.editor.SMAManager,
    *      java.lang.String, java.lang.String, java.util.Collection)
    */
   @Override
   public void transitioned(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees, SkynetTransaction transaction) throws OseeCoreException {
      super.transitioned(smaMgr, fromState, toState, toAssignees, transaction);

      // Create any decision or peerToPeer reviews for transitionTo and transitionFrom
      for (String ruleId : Arrays.asList(AtsAddDecisionReviewRule.ID, AtsAddPeerToPeerReviewRule.ID)) {
         for (WorkRuleDefinition workRuleDef : smaMgr.getWorkRulesStartsWith(ruleId)) {
            StateEventType eventType = AtsAddDecisionReviewRule.getStateEventType(smaMgr, workRuleDef);
            String forState = workRuleDef.getWorkDataValue(DecisionParameter.forState.name());
            if (forState == null || forState.equals("")) {
               continue;
            }
            if (eventType != null && toState.equals(forState) && eventType == StateEventType.TransitionTo) {
               if (ruleId.startsWith(AtsAddDecisionReviewRule.ID)) {
                  DecisionReviewArtifact decArt = AtsAddDecisionReviewRule.createNewDecisionReview(workRuleDef, smaMgr);
                  if (decArt != null) decArt.persistAttributesAndRelations(transaction);
               } else if (ruleId.startsWith(AtsAddPeerToPeerReviewRule.ID)) {
                  PeerToPeerReviewArtifact peerArt =
                        AtsAddPeerToPeerReviewRule.createNewPeerToPeerReview(workRuleDef, smaMgr, transaction);
                  if (peerArt != null) peerArt.persistAttributesAndRelations(transaction);
               }
            }
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getDescription()
    */
   public String getDescription() throws OseeCoreException {
      return "AtsHandleAddReviewRuleStateItem - If AddDecisionReviewRule or AddPeerToPeerReviewRule exists for this state, create review.";
   }

}
