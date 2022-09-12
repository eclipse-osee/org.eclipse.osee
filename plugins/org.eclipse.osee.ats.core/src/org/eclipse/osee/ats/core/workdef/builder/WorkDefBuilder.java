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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.review.ReviewRole;
import org.eclipse.osee.ats.api.review.ReviewRoleType;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefOption;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class WorkDefBuilder {

   WorkDefinition workDef;
   XResultData rd;
   List<StateDefBuilder> stateDefBuilders = new ArrayList<>();

   public WorkDefBuilder(AtsWorkDefinitionToken workDefToken) {
      this(workDefToken, null);
   }

   public WorkDefBuilder(AtsWorkDefinitionToken workDefToken, ArtifactTypeToken artType) {
      workDef = new WorkDefinition(workDefToken.getId(), workDefToken.getName());
      workDef.setArtType(artType);
      rd = workDef.getResults();
   }

   public StateDefBuilder andState(int ordinal, StateToken state, StateType type, WorkDefBuilderOption... builderOptions) {
      return this.andState(ordinal, state.getName(), type, builderOptions);
   }

   public StateDefBuilder andState(int ordinal, String name, StateType type, WorkDefBuilderOption... builderOptions) {
      if (ordinal <= 0) {
         rd.errorf("Ordinal must be > 1 for state [%s]\n", name);
      }
      StateDefinition stateByName = workDef.getStateByName(name);
      if (stateByName != null) {
         rd.errorf("State with name [%s] already exists\n", name);
      }
      for (StateDefinition state : workDef.getStates()) {
         if (state.getOrdinal() == ordinal) {
            rd.errorf("Ordinal [%s] already exists in state [%s]\n", ordinal, name);
         }
      }
      StateDefBuilder stateDefBuilder = new StateDefBuilder(ordinal, name, type, workDef, builderOptions);
      stateDefBuilders.add(stateDefBuilder);
      return stateDefBuilder;
   }

   public WorkDefinition getWorkDefinition() {
      // Resolve all layouts formatted from getLayoutFromState
      // Resolve all state definitions from state tokens
      for (StateDefBuilder stateDefBuilder : stateDefBuilders) {
         // getLayoutFromState
         StateToken fromLayoutStateToken = stateDefBuilder.getAndLayoutFromState();
         if (fromLayoutStateToken != null) {
            StateDefinition copyState = getStateDefinition(fromLayoutStateToken.getName());
            if (stateDefBuilder.state.getOrdinal() < copyState.getOrdinal()) {
               workDef.getResults().errorf("Cannot import layout from undefined state.");
            }
            stateDefBuilder.state.setLayoutItems(copyState.getLayoutItems());
         }
         StateToken defaultToStateToken = stateDefBuilder.getToDefaultStateToken();
         // toStates
         for (StateToken toStateToken : stateDefBuilder.getToStateTokens()) {
            if (defaultToStateToken != StateToken.ANY) {
               StateDefinition toState = getStateDefinition(toStateToken.getName());
               Conditions.assertNotNull(toState,
                  String.format("toState [%s] can't be null in state [%s] and work def [%s]", toStateToken,
                     stateDefBuilder.getName(), workDef.getName()));
               stateDefBuilder.addToState(toState);
            }
         }
      }

      // Resolve any states with StateToken.ANY as toStateToken
      for (StateDefBuilder stateDefBuilder : stateDefBuilders) {
         List<StateToken> states = stateDefBuilder.getToStateTokens();
         if (states.size() == 1 && states.iterator().next() == StateToken.ANY) {
            stateDefBuilder.state.getToStates().addAll(workDef.getStates());
            stateDefBuilder.state.getToStates().remove(stateDefBuilder.state);
         }
      }

      // Construct duplicates attrs map
      CountingMap<String> labelCount = workDef.getLabelCount();
      Set<String> headerLayoutItemNames = new HashSet<String>();
      allLayoutItemsToStringSet(headerLayoutItemNames, workDef.getHeaderDef().getLayoutItems());

      // Title is always at top of the header so it is being added in here.
      labelCount.put("Title");

      // Loop through headers
      for (String label : headerLayoutItemNames) {
         labelCount.put(label);
      }

      // Loop through states
      Set<String> currStateLayoutItemNames;
      for (StateDefinition currState : workDef.getStates()) {
         currStateLayoutItemNames = new HashSet<String>();
         allLayoutItemsToStringSet(currStateLayoutItemNames, currState.getLayoutItems());
         for (String label : currStateLayoutItemNames) {
            labelCount.put(label);
         }
      }

      return workDef;
   }

   private void allLayoutItemsToStringSet(Set<String> ret, List<LayoutItem> inputList) {
      for (LayoutItem layItem : inputList) {
         if (layItem instanceof CompositeLayoutItem) {
            for (LayoutItem compItem : ((CompositeLayoutItem) layItem).getLayoutItems()) {
               ret.add(compItem.getName());
            }
         } else {
            ret.add(layItem.getName());
         }
      }
   }

   private StateDefinition getStateDefinition(String name) {
      for (StateDefBuilder stateDefBldr : stateDefBuilders) {
         if (stateDefBldr.state.getName().equals(name)) {
            return stateDefBldr.state;
         }
      }
      return null;
   }

   public DecisionReviewDefinitionBuilder createDecisionReview(String name) {
      DecisionReviewDefinitionBuilder decRevBldr = new DecisionReviewDefinitionBuilder(name);
      return decRevBldr;
   }

   public PeerReviewDefinitionBuilder createPeerReview(String name) {
      PeerReviewDefinitionBuilder peerRevBldr = new PeerReviewDefinitionBuilder(name);
      return peerRevBldr;
   }

   public HeaderDefinitionBuilder andHeader() {
      HeaderDefinitionBuilder bldr = new HeaderDefinitionBuilder(workDef);
      workDef.setHeaderDefinition(bldr.getHeaderDefinition());
      return bldr;
   }

   public void isShowStateMetrics() {
      workDef.setShowStateMetrics(true);
   }

   public void isShowStateMetrics(boolean show) {
      workDef.setShowStateMetrics(show);
   }

   public CreateTasksDefinitionBuilder createTasksSetDefinitionBuilder(Long id, String name) {
      CreateTasksDefinitionBuilder taskSetDef = new CreateTasksDefinitionBuilder(id, name);
      return taskSetDef;
   }

   public void setReviewDefectColumns(XViewerColumn... columns) {
      for (XViewerColumn col : columns) {
         workDef.getReviewDefectColumns().add(col);
      }
   }

   public StateDefBuilder getStateDefBuilder(StateToken state) {
      for (StateDefBuilder sBld : stateDefBuilders) {
         if (sBld.getName().equals(state.getName())) {
            return sBld;
         }
      }
      return null;
   }

   public WorkDefBuilder andReviewRole(ReviewRole role, int minimum) {
      workDef.addReviewRole(role, minimum);
      return this;
   }

   public WorkDefBuilder andWorkDefOption(WorkDefOption... workDefOptions) {
      for (WorkDefOption opt : workDefOptions) {
         workDef.getOptions().add(opt);
      }
      return this;
   }

   // Default to 0 if minimum is not provided
   public WorkDefBuilder andReviewRole(ReviewRole role) {
      return andReviewRole(role, 0);
   }

   public WorkDefBuilder andReviewRoleTypeMinimum(ReviewRoleType reviewRoleType, int minimum) {
      workDef.andReviewRoleTypeMinimum(reviewRoleType, minimum);
      return this;
   }

   public WorkDefBuilder andArtType(ArtifactTypeToken artType) {
      workDef.setArtType(artType);
      return this;
   }

   public WorkDefBuilder andChangeTypes(ChangeTypes... changeTypes) {
      for (ChangeTypes type : changeTypes) {
         workDef.getChangeTypes().add(type);
      }
      return this;
   }

   public void andPointsAttributeType(AttributeTypeToken pointsAttrType) {
      workDef.setPointsAttrType(pointsAttrType);
   }

   public WorkDefBuilder andPriorities(Priorities... priorities) {
      for (Priorities type : priorities) {
         workDef.getPriorities().add(type);
      }
      return this;
   }
}
