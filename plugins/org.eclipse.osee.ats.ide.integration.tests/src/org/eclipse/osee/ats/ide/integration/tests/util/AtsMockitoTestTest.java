/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.util;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsMockitoTestTest extends AtsMockitoTest {

   public AtsMockitoTestTest() {
      super("MyTestClass");
   }

   @Test
   public void testAtsMockitoTest() {
      Assert.assertEquals("AMT - Class:[MyTestClass]", toString());
   }

   @Test
   public void testSetTestName() {
      setTestName("testReset");
      Assert.assertEquals("AMT - Test:[testReset]  Class:[MyTestClass]", toString());
   }

   @Test
   public void testSetup() {
      setup();
   }

   @Test
   public void testGetWorkDef() {
      Assert.assertEquals("Mock_Team_Workflow_Definition", workDef.getName());
      Assert.assertEquals(4, workDef.getStates().size());
      Assert.assertEquals(analyze, workDef.getStartState());
   }

   @Test
   public void testGetAnalyzeStateDef() {
      Assert.assertEquals("Analyze", analyze.getName());
      Assert.assertEquals(StateType.Working, analyze.getStateType());
      Assert.assertEquals(1, analyze.getOrdinal());
      Assert.assertEquals(implement, analyze.getDefaultToState());
   }

   @Test
   public void testGetEstHoursWidgetDef() {
      Assert.assertEquals("Estimated Hours", estHoursWidgetDef.getName());
      Assert.assertEquals(AtsAttributeTypes.EstimatedHours, estHoursWidgetDef.getAttributeType());
      Assert.assertEquals("XFloatDam", estHoursWidgetDef.getXWidgetName());
   }

   @Test
   public void testGetWorkPackageWidgetDef() {
      Assert.assertEquals("Work Package", workPackageWidgetDef.getName());
      Assert.assertEquals(AtsAttributeTypes.WorkPackage.getName(), workPackageWidgetDef.getAttributeType());
      Assert.assertEquals("XTextDam", workPackageWidgetDef.getXWidgetName());
   }

   @Test
   public void testGetImplementStateDef() {
      Assert.assertEquals("Implement", implement.getName());
      Assert.assertEquals(StateType.Working, implement.getStateType());
      Assert.assertEquals(2, implement.getOrdinal());
      Assert.assertEquals(completed, implement.getDefaultToState());
   }

   @Test
   public void testGetCompletedStateDef() {
      Assert.assertEquals("Completed", completed.getName());
      Assert.assertEquals(StateType.Completed, completed.getStateType());
      Assert.assertEquals(3, completed.getOrdinal());
      Assert.assertEquals(completed, completed.getDefaultToState());
   }

   @Test
   public void testGetCancelledStateDef() {
      Assert.assertEquals("Cancelled", cancelled.getName());
      Assert.assertEquals(StateType.Cancelled, cancelled.getStateType());
      Assert.assertEquals(4, cancelled.getOrdinal());
      Assert.assertNull(cancelled.getDefaultToState());
   }

   @Test
   public void testGetTeamWf() {
      Assert.assertEquals("Test Team Wf", teamWf.getName());
      Assert.assertEquals(teamDef, teamWf.getTeamDefinition());
      Assert.assertEquals("ATS0008", teamWf.getAtsId());
      Assert.assertEquals(AtsArtifactTypes.TeamWorkflow.getName(), teamWf.getArtifactTypeName());
   }

   @Test
   public void testGetTestAi() {
      Assert.assertEquals("AI", testAi.getName());
      Assert.assertEquals(teamDef, testAi.getTeamDefinition());
   }

   @Test
   public void testToString() {
      Assert.assertEquals("AMT - Class:[MyTestClass]", toString());
      setTestName("testToString");
      Assert.assertEquals("AMT - Test:[testToString]  Class:[MyTestClass]", toString());
   }

   @Test
   public void testGetTitle() {
      setTestName("testToString");
      String title = getTitle("Action");
      Assert.assertEquals("AMT - Obj:[Action]  Test:[testToString]  Class:[MyTestClass]", title);
   }

   @Test
   public void testGetCurrentUser() {
      Assert.assertEquals("User1", currentUser.getName());
      Assert.assertEquals("1324", currentUser.getUserId());
      Assert.assertTrue(currentUser.isActive());
   }

   @Test
   public void testGetTeamDef() {
      Assert.assertEquals("AMT - Obj:[Test Team Def]  Class:[MyTestClass]", teamDef.getName());
      Assert.assertTrue(teamDef.isActive());
   }

   @Test
   public void testGetVer1() {
      Assert.assertEquals("ver 1.0", ver1.getName());
   }

   @Test
   public void testGetVer2() {
      Assert.assertEquals("ver 2.0", ver2.getName());
   }

   @Test
   public void testGetVer3() {
      Assert.assertEquals("ver 3.0", ver3.getName());
   }

   @Test
   public void testGetVer4() {
      Assert.assertEquals("ver 4.0", ver4.getName());
   }

   @Test
   public void testGetDecRev() {
      Assert.assertEquals("AMT - Obj:[Test Dec Rev]  Class:[MyTestClass]", decRev.getName());
      Assert.assertEquals(teamWf, decRev.getParentTeamWorkflow());
      Assert.assertTrue(decRev.getAssignees().contains(currentUser));
   }

   @Test
   public void testGetPeerRev() {
      Assert.assertEquals("AMT - Obj:[Test Peer Rev]  Class:[MyTestClass]", peerRev.getName());
      Assert.assertEquals(teamWf, peerRev.getParentTeamWorkflow());
      Assert.assertTrue(peerRev.getAssignees().contains(currentUser));
   }

   @Test
   public void testGetTask1() {
      Assert.assertEquals("AMT - Obj:[Test Task 1]  Class:[MyTestClass]", task1.getName());
   }

   @Test
   public void testGetTask2() {
      Assert.assertEquals("AMT - Obj:[Test Task 2]  Class:[MyTestClass]", task2.getName());
   }

   @Test
   public void testGetTestAi2() {
      Assert.assertEquals("AI", testAi.getName());
   }

   @Test
   public void testGetAction() {
      Assert.assertEquals("AI2", testAi2.getName());
   }

}
