/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.core.workdef.builder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateOption;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.task.CreateChangeReportTaskTransitionHook;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class StateDefBuilder {

   StateDefinition state;
   private final WorkDefinition workDef;
   private final List<StateToken> toStateTokens = new ArrayList<>();
   private final List<DecisionReviewDefinitionBuilder> decRevBldrs = new LinkedList<>();
   private final List<PeerReviewDefinitionBuilder> peerRevBldrs = new LinkedList<>();
   private StateToken getLayoutFromState;
   private final XResultData rd;
   private final List<WorkDefBuilderOption> builderOptions;

   public StateDefBuilder(int ordinal, String name, StateType type, WorkDefinition workDef, WorkDefBuilderOption... builderOptions) {
      this.workDef = workDef;
      state = new StateDefinition(name);
      state.setStateType(type);
      state.setOrdinal(ordinal);
      state.setWorkDefinition(workDef);
      workDef.addState(state);
      this.getLayoutFromState = null;
      this.rd = workDef.getResults();
      this.builderOptions = new ArrayList<>();
      for (WorkDefBuilderOption opt : builderOptions) {
         this.builderOptions.add(opt);
      }
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
      if (workDef.getStartState() != null) {
         rd.errorf("Duplicate Start States [%s] and [%s] for Work Def %s\n", workDef.getStartState(), state,
            workDef.getName());
      }
      workDef.setStartState(state);
      return this;
   }

   public StateDefBuilder andRecommendedPercentComplete(int percent) {
      state.setRecommendedPercentComplete(percent);
      return this;
   }

   public StateDefBuilder andStateWeight(int percent) {
      state.setStateWeight(percent);
      return this;
   }

   public StateDefBuilder andToStates(StateToken... stateTokens) {
      if (state.getStateType().isCompletedOrCancelled() && !hasOption(
         WorkDefBuilderOption.OVERRIDE_COMP_CANC_TO_STATE_CHECK)) {
         rd.errorf("Completed/Cancelled sate [%s] shouldn't have toStates for Work Def %s\n", state.getName(),
            workDef.getName());
      }
      List<StateToken> processedStates = new ArrayList<>();
      boolean foundAny = false;
      for (StateToken stateTok : stateTokens) {
         if (stateTok == StateToken.ANY) {
            foundAny = true;
         }
         if (stateTok.getName().equals(state.getName())) {
            rd.errorf("toState [%s] shouldn't match state name for Work Def %s\n", stateTok.getName(),
               workDef.getName());
         }
         if (processedStates.contains(stateTok)) {
            rd.errorf("Should not have duplicate [%s] states in andToState call for Work Def %s\n", stateTok.getName(),
               workDef.getName());
         }
         processedStates.add(stateTok);
         this.toStateTokens.add(stateTok);
      }
      if (foundAny && this.toStateTokens.size() > 1) {
         rd.errorf(
            "Should not use StateToken.ANY with other StateTokens for andToStates in state [%s] for Work Def %s\n",
            state.getName(), workDef.getName());
      }
      return this;
   }

   public StateDefBuilder andRules(RuleDefinitionOption... rules) {
      for (RuleDefinitionOption rule : rules) {
         state.addRule(rule.name());
      }
      return this;
   }

   public StateDefBuilder andLayout(LayoutItem... items) {
      if (this.getAndLayoutFromState() != null) {
         rd.errorf("Cannot add layout items when state already gets layout from other state for Work Def %s\n",
            workDef.getName());
      }
      for (LayoutItem item : items) {
         state.getLayoutItems().add(item);
      }
      return this;
   }

   public void insertLayoutAfter(AttributeTypeToken attrTypeLocation, LayoutItem... addLayoutItems) {
      AtomicBoolean found = new AtomicBoolean(false);
      List<LayoutItem> currLayoutItems = new ArrayList<>(state.getLayoutItems());
      state.getLayoutItems().clear();
      insertLayoutAfter(state.getLayoutItems(), attrTypeLocation, currLayoutItems, found, addLayoutItems);
      if (!found.get()) {
         rd.errorf("Can't find WidgetDef for [%s] for Work Def %s\n", attrTypeLocation, workDef.getName());
      }
   }

   /**
    * @param newItems new layout item list with insertLayoutItems added after attrTypeLocation WidgetDefinition
    * @param currItems to loop through looking for attrTypeLocation; will recurse through CompositeLayoutItems
    * @param found is true if attrTypeLocation was found
    */
   private void insertLayoutAfter(List<LayoutItem> newItems, AttributeTypeToken attrTypeLocation, List<LayoutItem> currItems, AtomicBoolean found, LayoutItem... insertLayoutItems) {
      for (LayoutItem currItem : currItems) {
         newItems.add(currItem);
         if (currItem instanceof WidgetDefinition) {
            WidgetDefinition widgetDef = (WidgetDefinition) currItem;
            if (attrTypeLocation.equals(widgetDef.getAttributeType())) {
               found.set(true);
               for (LayoutItem newItem : insertLayoutItems) {
                  newItems.add(newItem);
               }
            }
         }
         // Recurse through composite layout items
         else if (currItem instanceof CompositeLayoutItem) {
            CompositeLayoutItem compLayoutItem = (CompositeLayoutItem) currItem;
            List<LayoutItem> currLayoutItems = new ArrayList<LayoutItem>(compLayoutItem.getaLayoutItems());
            compLayoutItem.getaLayoutItems().clear();
            insertLayoutAfter(compLayoutItem.getaLayoutItems(), attrTypeLocation, currLayoutItems, found,
               insertLayoutItems);
         }
      }
   }

   public StateDefBuilder andRules(String... rules) {
      for (String rule : rules) {
         state.addRule(rule);
      }
      return this;
   }

   public StateToken getToDefaultStateToken() {
      if (toStateTokens.isEmpty()) {
         return null;
      }
      return toStateTokens.iterator().next();
   }

   public List<StateToken> getToStateTokens() {
      return toStateTokens;
   }

   public StateDefBuilder andLayoutFromState(StateToken fromState) {
      if (state.getOrdinal() == 1) {
         rd.errorf(
            "State [%s] cannot import layout from other state if current state is the start state for Work Def %s\n",
            state.getName(), workDef.getName());
      }
      if (!state.getLayoutItems().isEmpty()) {
         rd.errorf(
            "State [%s] cannot import layout from other state if current state has already defined layout items for Work Def %s\n",
            state.getName(), workDef.getName());
      }
      this.getLayoutFromState = fromState;
      return this;
   }

   public StateToken getAndLayoutFromState() {
      return this.getLayoutFromState;
   }

   public StateDefBuilder addToState(StateDefinition toState) {
      state.getToStates().add(toState);
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

   public StateDefBuilder andTransitionListener(AtsTaskDefToken taskDefToken) {
      state.addTransitionListener(new CreateChangeReportTaskTransitionHook(taskDefToken));
      return this;
   }

   public StateDefBuilder andStateOptions(StateOption... options) {
      for (StateOption option : options) {
         state.getStateOptions().add(option);
      }
      return this;
   }

   public boolean hasOption(WorkDefBuilderOption option) {
      return builderOptions.contains(option);
   }

   public List<LayoutItem> getLayoutItems() {
      return state.getLayoutItems();
   }

   public StateDefBuilder andLayout(List<LayoutItem> layoutItems) {
      state.getLayoutItems().addAll(layoutItems);
      return this;
   }
}