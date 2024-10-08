/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.workdef.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsPeerReviewDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateOption;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class StateDefinition extends AbstractWorkDefItem implements IStateToken {

   private StateType StateType;
   private int ordinal = 0;
   private List<LayoutItem> stateItems = new ArrayList<>(5);
   @JsonIgnore
   private final RuleManager ruleMgr = new RuleManager();
   @JsonIgnore
   private final List<StateDefinition> toStates = new ArrayList<>(5);
   @JsonIgnore
   private final List<IAtsDecisionReviewDefinition> decisionReviews = new ArrayList<>();
   @JsonIgnore
   private final List<IAtsPeerReviewDefinition> peerReviews = new ArrayList<>();
   @JsonIgnore
   private WorkDefinition workDefinition;
   private Integer recommendedPercentComplete = null;
   private StateColor color = null;
   @JsonIgnore
   private final List<IAtsTransitionHook> transitionListeners = new ArrayList<>();
   @JsonIgnore
   private final List<IAtsWorkItemHook> workItemListeners = new ArrayList<>();
   private List<StateOption> stateOptions = new ArrayList<>();
   private IUserGroupArtifactToken transitionUserGroup = null;

   public StateDefinition(String name) {
      super(Long.valueOf(name.hashCode()), name);
   }

   public List<LayoutItem> getLayoutItems() {
      return stateItems;
   }

   @Override
   public StateType getStateType() {
      return StateType;
   }

   @Override
   public boolean isCompleted() {
      return getStateType().isCompleted();
   }

   @Override
   public boolean isCancelled() {
      return getStateType().isCancelled();
   }

   public IUserGroupArtifactToken getTransitionUserGroup() {
      return transitionUserGroup;
   }

   public void setTransitionUserGroup(IUserGroupArtifactToken transitionUserGroup) {
      this.transitionUserGroup = transitionUserGroup;
   }

   @Override
   public boolean isWorking() {
      return getStateType().isWorking();
   }

   @Override
   public boolean isCompletedOrCancelled() {
      return getStateType().isCompletedOrCancelled();
   }

   public void setLayoutItems(List<LayoutItem> layoutToSet) {
      this.stateItems = layoutToSet;
   }

   public void setStateType(StateType StateType) {
      this.StateType = StateType;
   }

   public int getOrdinal() {
      return ordinal;
   }

   public void setOrdinal(int ordinal) {
      this.ordinal = ordinal;
   }

   public List<StateDefinition> getToStates() {
      return toStates;
   }

   public WorkDefinition getWorkDefinition() {
      return workDefinition;
   }

   public void setWorkDefinition(WorkDefinition workDefinition) {
      this.workDefinition = workDefinition;
   }

   @Override
   public String toString() {
      return String.format("%s  - (%s)", getName(), getStateType());
   }

   /**
    * Returns fully qualified name of <work definition name>.<this state name
    */

   public String getFullName() {
      if (workDefinition != null) {
         return workDefinition.getName() + "." + getName();
      }
      return getName();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (getName() == null ? 0 : getName().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      super.equals(obj);
      boolean equals = false;
      if (this == obj) {
         equals = true;
      } else if (obj != null) {
         if (getClass() == obj.getClass()) {
            StateDefinition other = (StateDefinition) obj;
            if (Strings.isValid(getName()) && Strings.isValid(other.getName()) && getName().equals(other.getName())) {
               equals = true;
            }
         }
      }
      return equals;
   }

   public List<IAtsDecisionReviewDefinition> getDecisionReviews() {
      return decisionReviews;
   }

   public List<IAtsPeerReviewDefinition> getPeerReviews() {
      return peerReviews;
   }

   public void setRecommendedPercentComplete(int recommendedPercentComplete) {
      this.recommendedPercentComplete = recommendedPercentComplete;
   }

   public Integer getRecommendedPercentComplete() {
      return recommendedPercentComplete;
   }

   public void setColor(StateColor stateColor) {
      this.color = stateColor;
   }

   public StateColor getColor() {
      return color;
   }

   public void removeRule(String rule) {
      ruleMgr.removeRule(rule);
   }

   public List<String> getRules() {
      return ruleMgr.getRules();
   }

   public void addRule(String rule) {
      ruleMgr.addRule(rule);
   }

   public boolean hasRule(String rule) {
      return ruleMgr.hasRule(rule);
   }

   public void addDecisionReview(DecisionReviewDefinition reviewDefinition) {
      decisionReviews.add(reviewDefinition);
   }

   public void addPeerReview(PeerReviewDefinition reviewDefinition) {
      peerReviews.add(reviewDefinition);
   }

   public void addTransitionListener(IAtsTransitionHook transitionListener) {
      transitionListeners.add(transitionListener);
   }

   public void addWorkItemListener(IAtsWorkItemHook workItemHook) {
      workItemListeners.add(workItemHook);
   }

   public List<IAtsTransitionHook> getTransitionListeners() {
      return transitionListeners;
   }

   public List<StateOption> getStateOptions() {
      return stateOptions;
   }

   public void setStateOptions(List<StateOption> stateOptions) {
      this.stateOptions = stateOptions;
   }

   public List<IAtsWorkItemHook> getWorkItemListeners() {
      return workItemListeners;
   }

   public List<String> getToStateNames() {
      List<String> stateNames = new ArrayList<>();
      for (StateDefinition state : getToStates()) {
         stateNames.add(state.getName());
      }
      return stateNames;
   }

}
