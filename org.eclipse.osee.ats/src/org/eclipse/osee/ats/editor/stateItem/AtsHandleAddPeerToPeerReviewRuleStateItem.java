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
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.editor.AtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.workflow.item.AtsAddPeerToPeerReviewRule;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsHandleAddPeerToPeerReviewRuleStateItem extends AtsStateItem {

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
   public void transitioned(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees) throws OseeCoreException {
      super.transitioned(smaMgr, fromState, toState, toAssignees);
      // Create any decision reviews
      for (WorkRuleDefinition workRuleDef : smaMgr.getWorkRulesStartsWith(AtsAddPeerToPeerReviewRule.ID)) {
         PeerToPeerReviewArtifact peerArt = AtsAddPeerToPeerReviewRule.createNewPeerToPeerReview(workRuleDef, smaMgr);
         if (peerArt != null) peerArt.persistAttributesAndRelations();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getDescription()
    */
   public String getDescription() throws OseeCoreException {
      return "AtsHandleAddPeerToPeerReviewRuleStateItem - If AtsAddPeerToPeerReviewRule exists for this state, create review.";
   }

}
