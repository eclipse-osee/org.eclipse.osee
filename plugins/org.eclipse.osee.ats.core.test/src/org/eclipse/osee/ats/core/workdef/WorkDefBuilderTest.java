/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for WorkDefBuilder
 *
 * @author Donald G. Dunne
 */
public class WorkDefBuilderTest {

   public static AtsWorkDefinitionToken WorkDef_Team_Test = new AtsWorkDefinitionToken(4352354L, "WorkDef_Team_Test");
   private WorkDefBuilder bld;

   @Before
   public void setup() {
      bld = new WorkDefBuilder(WorkDef_Team_Test);
      bld.andState(1, "Analyze", StateType.Working).isStartState() //
         .andToStates(StateToken.Completed);
      bld.andState(2, "Completed", StateType.Completed);
      bld.andState(3, "Cancelled", StateType.Cancelled);

   }

   @Test
   public void testGetWorkDefinition() {
      WorkDefinition workDef = bld.getWorkDefinition();
      Assert.assertNotNull(workDef);
      Assert.assertTrue(workDef.getResults().isSuccess());
   }

   @Test
   public void testDuplicateStartStates() {
      bld.andState(4, "Implement", StateType.Working).isStartState() //
         .andToStates(StateToken.Completed);

      WorkDefinition workDef = bld.getWorkDefinition();
      Assert.assertTrue(workDef.getResults().isFailed());
      Assert.assertTrue(workDef.getResults().toString().contains("Duplicate Start States"));
   }

   @Test
   public void testDuplicateOrdinal() {
      bld.andState(3, "Implement", StateType.Working) //
         .andToStates(StateToken.Completed);

      WorkDefinition workDef = bld.getWorkDefinition();
      Assert.assertTrue(workDef.getResults().isFailed());
      Assert.assertTrue(workDef.getResults().toString().contains("Ordinal [3] already exists"));
   }

   @Test
   public void testDuplicateNames() {
      bld.andState(4, "Completed", StateType.Working) //
         .andToStates(StateToken.Analyze);

      WorkDefinition workDef = bld.getWorkDefinition();
      Assert.assertTrue(workDef.getResults().isFailed());
      Assert.assertTrue(workDef.getResults().toString().contains("State with name [Completed] already exists"));
   }

   @Test
   public void testToStatesCanNotHaveThisStateName() {
      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Implement);

      WorkDefinition workDef = bld.getWorkDefinition();
      Assert.assertTrue(workDef.getResults().isFailed());
      Assert.assertTrue(workDef.getResults().toString().contains("toState [Implement] shouldn't match state name"));
   }

   @Test
   public void testToStatesDoesNotHaveDups() {
      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.Analyze, StateToken.Analyze);

      WorkDefinition workDef = bld.getWorkDefinition();
      Assert.assertTrue(workDef.getResults().isFailed());
      Assert.assertTrue(workDef.getResults().toString().contains("Should not have duplicate [Analyze] states"));
   }

   @Test
   public void testCompleteStateHasNoToStates() {
      bld.andState(4, "Complete2", StateType.Completed) //
         .andToStates(StateToken.Analyze);

      WorkDefinition workDef = bld.getWorkDefinition();
      Assert.assertTrue(workDef.getResults().isFailed());
      Assert.assertTrue(
         workDef.getResults().toString().contains("Completed/Cancelled sate [Complete2] shouldn't have toStates"));
   }

   @Test
   public void testCancelledStateHasNoToStates() {
      bld.andState(4, "Cancelled2", StateType.Cancelled) //
         .andToStates(StateToken.Analyze);

      WorkDefinition workDef = bld.getWorkDefinition();
      Assert.assertTrue(workDef.getResults().isFailed());
      Assert.assertTrue(
         workDef.getResults().toString().contains("Completed/Cancelled sate [Cancelled2] shouldn't have toStates"));
   }

   @Test
   public void testStateTokenAnyWithOthers() {
      bld.andState(4, "Implement", StateType.Working) //
         .andToStates(StateToken.ANY, StateToken.Analyze);

      WorkDefinition workDef = bld.getWorkDefinition();
      Assert.assertTrue(workDef.getResults().isFailed());
      Assert.assertTrue(
         workDef.getResults().toString().contains("Should not use StateToken.ANY with other StateTokens"));
   }

   @Test
   public void testStateTokenAny() {
      WorkDefBuilder anyBld = new WorkDefBuilder(WorkDef_Team_Test);
      anyBld.andState(1, "Analyze", StateType.Working).isStartState() //
         .andToStates(StateToken.ANY);
      anyBld.andState(2, "Completed", StateType.Completed);
      anyBld.andState(3, "Cancelled", StateType.Cancelled);

      WorkDefinition workDef = anyBld.getWorkDefinition();
      Assert.assertTrue(workDef.getResults().isSuccess());

      IAtsStateDefinition state = workDef.getStateByName(StateToken.Analyze.getName());
      Assert.assertNotNull(state);
      Assert.assertEquals(2, state.getToStates().size());
      IAtsStateDefinition completed = workDef.getStateByName(StateToken.Completed.getName());
      IAtsStateDefinition cancelled = workDef.getStateByName(StateToken.Cancelled.getName());
      Assert.assertTrue(state.getToStates().contains(completed));
      Assert.assertTrue(state.getToStates().contains(cancelled));
   }

}
