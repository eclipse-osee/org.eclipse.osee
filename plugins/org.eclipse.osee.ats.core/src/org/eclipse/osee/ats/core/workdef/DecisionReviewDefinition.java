/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class DecisionReviewDefinition {

   public String name;
   public String reviewTitle;
   public String description = "";
   public String relatedToState;
   public ReviewBlockType blockingType;
   public StateEventType stateEventType;
   public List<String> assignees = new ArrayList<String>();
   public boolean autoTransitionToDecision = false;
   public List<DecisionReviewOption> options = new ArrayList<DecisionReviewOption>();

   public DecisionReviewDefinition(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public ReviewBlockType getBlockingType() {
      return blockingType;
   }

   public void setBlockingType(ReviewBlockType blockingType) {
      this.blockingType = blockingType;
   }

   public StateEventType getStateEventType() {
      return stateEventType;
   }

   public void setStateEventType(StateEventType stateEventType) {
      this.stateEventType = stateEventType;
   }

   public boolean isAutoTransitionToDecision() {
      return autoTransitionToDecision;
   }

   public void setAutoTransitionToDecision(boolean autoTransitionToDecision) {
      this.autoTransitionToDecision = autoTransitionToDecision;
   }

   public List<String> getAssignees() {
      return assignees;
   }

   public List<DecisionReviewOption> getOptions() {
      return options;
   }

   @Override
   public String toString() {
      return name;
   }

   public String getReviewTitle() {
      return reviewTitle;
   }

   public void setReviewTitle(String reviewTitle) {
      this.reviewTitle = reviewTitle;
   }

   public String getRelatedToState() {
      return relatedToState;
   }

   public void setRelatedToState(String relatedToState) {
      this.relatedToState = relatedToState;
   }

}
