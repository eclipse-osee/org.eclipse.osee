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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public class StateDefBuilder {

   StateDefinition state;
   private final WorkDefinition workDef;
   private StateToken toDefaultStateToken;
   private final List<StateToken> toStateTokens = new ArrayList<>();
   private final List<StateToken> overrideValidationStateTokens = new ArrayList<>();
   private final List<DecisionReviewDefinitionBuilder> decRevBldrs = new LinkedList<>();
   private final List<PeerReviewDefinitionBuilder> peerRevBldrs = new LinkedList<>();
   private StateToken getLayoutFromState;
   private final List<ITransitionListener> transitionListeners = new ArrayList<>();

   public StateDefBuilder(int ordinal, String name, StateType type, WorkDefinition workDef) {
      this.workDef = workDef;
      state = new StateDefinition(name);
      state.setStateType(type);
      state.setOrdinal(ordinal);
      state.setWorkDefinition(workDef);
      workDef.addState(state);
      this.getLayoutFromState = null;
   }

   public StateDefBuilder andDescription(String desc) {
      state.setDescription(desc);
      return this;
   }

   public StateDefBuilder andColor(StateColor color) {
      state.setColor(color);
      return this;
   }

   public StateDefBuilder isStartState() {
      workDef.setStartState(state);
      return this;
   }

   public StateDefBuilder andRecommendedPercentComplete(int percent) {
      state.setRecommendedPercentComplete(percent);
      return this;
   }

   public StateDefBuilder andOverrideValidationStates(StateToken... toStates) {
      for (StateToken state : toStates) {
         this.overrideValidationStateTokens.add(state);
      }
      return this;
   }

   public StateDefBuilder andStateWeight(int percent) {
      state.setStateWeight(percent);
      return this;
   }

   public StateDefBuilder andToDefaultState(StateToken stateToken) {
      toDefaultStateToken = stateToken;
      return this;
   }

   public StateDefBuilder andToStates(StateToken... stateTokens) {
      for (StateToken tok : stateTokens) {
         this.toStateTokens.add(tok);
      }
      return this;
   }

   public StateDefBuilder andRules(RuleDefinitionOption... rules) {
      for (RuleDefinitionOption rule : rules) {
         state.addRule(rule.name());
      }
      return this;
   }

   public StateDefBuilder andLayout(IAtsLayoutItem... items) {
      if (this.getAndLayoutFromState() != null) {
         throw new OseeArgumentException("Cannot add layout items when state already gets layout from other state.");
      }
      for (IAtsLayoutItem item : items) {
         state.getLayoutItems().add(item);
      }
      return this;
   }

   public StateDefBuilder andRules(String... rules) {
      for (String rule : rules) {
         state.addRule(rule);
      }
      return this;
   }

   public StateToken getToDefaultStateToken() {
      return toDefaultStateToken;
   }

   public List<StateToken> getToStateTokens() {
      return toStateTokens;
   }

   public List<StateToken> getOverrideValidationStateTokens() {
      return overrideValidationStateTokens;
   }

   public StateDefBuilder andLayoutFromState(StateToken fromState) {
      if (state.getOrdinal() == 1) {
         throw new OseeArgumentException("Cannot import layout from other state if current state is the start state.");
      }
      if (!state.getLayoutItems().isEmpty()) {
         throw new OseeArgumentException(
            "Cannot import layout from other state if current state has already defined layout items.");
      }
      this.getLayoutFromState = fromState;
      return this;
   }

   public StateToken getAndLayoutFromState() {
      return this.getLayoutFromState;
   }

   public StateDefBuilder setDefaultToState(IAtsStateDefinition defaultToState) {
      state.setDefaultToState(defaultToState);
      return this;
   }

   public StateDefBuilder addToState(IAtsStateDefinition toState) {
      state.getToStates().add(toState);
      return this;
   }

   public StateDefBuilder addOverrideState(IAtsStateDefinition overrideState) {
      state.getOverrideAttributeValidationStates().add(overrideState);
      return this;
   }

   @Override
   public String toString() {
      return state.getName();
   }

   public StateDefBuilder andDecisionReviewBuilder(DecisionReviewDefinitionBuilder decRevBldr) {
      state.addDecisionReview(decRevBldr.getReviewDefinition());
      decRevBldrs.add(decRevBldr);
      return this;
   }

   public List<DecisionReviewDefinitionBuilder> getDecRevBldrs() {
      return decRevBldrs;
   }

   public StateDefBuilder andPeerReviewBuilder(PeerReviewDefinitionBuilder peerRevBldr) {
      state.addPeerReview(peerRevBldr.getReviewDefinition());
      peerRevBldrs.add(peerRevBldr);
      return this;
   }

   public List<PeerReviewDefinitionBuilder> getPeerRevBldrs() {
      return peerRevBldrs;
   }

   public String getName() {
      return state.getName();
   }

   public void andTransitionListener(AtsTaskDefToken taskDefToken) {
      state.addTransitionListener(new CreateChangeReportTaskTransitionListener(taskDefToken));
   }

}