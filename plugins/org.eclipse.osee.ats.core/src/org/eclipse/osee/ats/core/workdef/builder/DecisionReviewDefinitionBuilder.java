/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef.builder;

import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.StateEventType;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.model.DecisionReviewDefinition;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * @author Donald G. Dunne
 */
public class DecisionReviewDefinitionBuilder {

   DecisionReviewDefinition decRev = new DecisionReviewDefinition();

   public DecisionReviewDefinitionBuilder(String name) {
      decRev.setName(name);
   }

   public DecisionReviewDefinitionBuilder andTitle(String title) {
      decRev.setReviewTitle(title);
      return this;
   }

   public DecisionReviewDefinitionBuilder andDescription(String desc) {
      decRev.setDescription(desc);
      return this;
   }

   public DecisionReviewDefinitionBuilder andRelatedToState(StateToken state) {
      decRev.setRelatedToState(state.getName());
      return this;
   }

   public DecisionReviewDefinitionBuilder andBlockingType(ReviewBlockType blockType) {
      decRev.setBlockingType(blockType);
      return this;
   }

   public DecisionReviewDefinitionBuilder andEvent(StateEventType stateEventType) {
      decRev.setStateEventType(stateEventType);
      return this;
   }

   public DecisionReviewDefinitionBuilder andAssignees(AtsUser... assignees) {
      for (AtsUser assignee : assignees) {
         decRev.addAssignee(assignee);
      }
      return this;
   }

   public DecisionReviewDefinitionBuilder andAutoTransitionToDecision() {
      decRev.setAutoTransitionToDecision(true);
      return this;
   }

   public DecisionReviewDefinition getDecisionReview() {
      return decRev;
   }

   public DecisionReviewDefinition getReviewDefinition() {
      return decRev;
   }

   public DecisionReviewOptionBuilder andOption(String name) {
      return new DecisionReviewOptionBuilder(name, this);
   }

   public DecisionReviewDefinitionBuilder andAssignees(UserToken user) {
      decRev.assignees.add(user.getUserId());
      return this;
   }

}
