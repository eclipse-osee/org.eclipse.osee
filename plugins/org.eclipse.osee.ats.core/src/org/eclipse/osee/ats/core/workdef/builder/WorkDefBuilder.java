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
import java.util.List;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class WorkDefBuilder {

   WorkDefinition workDef;
   List<StateDefBuilder> stateDefBuilders = new ArrayList<>();

   public WorkDefBuilder(AtsWorkDefinitionToken workDefToken) {
      workDef = new WorkDefinition(workDefToken.getId(), workDefToken.getName());
   }

   public StateDefBuilder andState(int ordinal, String name, StateType type) {
      Conditions.assertTrue(ordinal > 0, "ordinal must be > 1");
      StateDefBuilder stateDefBuilder = new StateDefBuilder(ordinal, name, type, workDef);
      stateDefBuilders.add(stateDefBuilder);
      return stateDefBuilder;
   }

   public WorkDefinition getWorkDefinition() {
      // Resolve all layouts formatted from getLayoutFromState
      // Resolve all state definitions from state tokens
      for (StateDefBuilder stateDefBuilder : stateDefBuilders) {
         //getLayoutFromState
         StateToken fromLayoutStateToken = stateDefBuilder.getAndLayoutFromState();
         if (fromLayoutStateToken != null) {
            IAtsStateDefinition copyState = getStateDefinition(fromLayoutStateToken.getName());
            if (stateDefBuilder.state.getOrdinal() < copyState.getOrdinal()) {
               throw new OseeStateException("Cannot import layout from undefined state.");
            }
            stateDefBuilder.state.setLayoutItems(copyState.getLayoutItems());
         }
         // defaultToState
         StateToken defaultToStateToken = stateDefBuilder.getToDefaultStateToken();
         if (defaultToStateToken != null) {
            IAtsStateDefinition defaultToState = getStateDefinition(defaultToStateToken.getName());
            Conditions.assertNotNull(defaultToState, "defaultToState [%s] not defined in workDef [%s]",
               defaultToState.getName(), workDef.getName());
            stateDefBuilder.setDefaultToState(defaultToState);
         }
         // toStates
         for (StateToken toStateToken : stateDefBuilder.getToStateTokens()) {
            IAtsStateDefinition toState = getStateDefinition(toStateToken.getName());
            Conditions.assertNotNull(toState,
               String.format("toState [%s] can't be null in state [%s] and work def [%s]", toStateToken,
                  stateDefBuilder.getName(), workDef.getName()));
            stateDefBuilder.addToState(toState);
         }
         // overrideValidationStates
         for (StateToken overrideStateToken : stateDefBuilder.getOverrideValidationStateTokens()) {
            IAtsStateDefinition overrideState = getStateDefinition(overrideStateToken.getName());
            Conditions.assertNotNull(overrideState,
               String.format("overrideState [%s] can't be null in state [%s] and work def [%s]", overrideStateToken,
                  stateDefBuilder.getName(), workDef.getName()));
            stateDefBuilder.addOverrideState(overrideState);
         }
      }

      return workDef;
   }

   private IAtsStateDefinition getStateDefinition(String name) {
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

}
