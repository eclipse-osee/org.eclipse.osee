/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinition;

/**
 * @author Mark Joy
 */
public class DecisionReviewRuleDefinition extends RuleDefinition implements IAtsDecisionReviewRuleDefinition {
   private String relatedState;
   private ReviewBlockType blockingType;
   private boolean autoTransitionToDecision = false;
   private List<IAtsDecisionReviewOption> options = new ArrayList<>();
   private String title = "";

   @Override
   public String getRelatedToState() {
      return relatedState;
   }

   public void setRelatedToState(String relatedState) {
      this.relatedState = relatedState;
   }

   @Override
   public ReviewBlockType getBlockingType() {
      return blockingType;
   }

   public void setBlockingType(ReviewBlockType blockingType) {
      this.blockingType = blockingType;
   }

   @Override
   public boolean isAutoTransitionToDecision() {
      return autoTransitionToDecision;
   }

   public void setAutoTransitionToDecision(boolean autoTransitionToDecision) {
      this.autoTransitionToDecision = autoTransitionToDecision;
   }

   @Override
   public List<IAtsDecisionReviewOption> getOptions() {
      return options;
   }

   public void setOptions(List<IAtsDecisionReviewOption> options) {
      this.options = options;
   }

   @Override
   public void execute(IAtsWorkItem workItem, AtsApi atsServices, IAtsChangeSet changes, RunRuleResults ruleResults) {
      //
   }

   @Override
   public String getTitle() {
      return title;
   }

   @Override
   public void setTitle(String title) {
      this.title = title;
   }

}
