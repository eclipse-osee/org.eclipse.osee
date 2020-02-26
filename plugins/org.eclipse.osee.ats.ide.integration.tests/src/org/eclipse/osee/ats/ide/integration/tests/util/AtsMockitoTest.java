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

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Class in support of testing ATS using Mocks. Sets up a standard configuration of mocks to use.
 *
 * @author Donald G. Dunne
 */
public class AtsMockitoTest {

   // @formatter:off
   @Mock protected  AtsUser currentUser;
   @Mock protected IAtsTeamWorkflow teamWf;
   @Mock protected IAtsTeamDefinition teamDef;
   @Mock protected IAtsVersion ver1, ver2, ver3, ver4;
   @Mock protected IAtsDecisionReview decRev;
   @Mock protected IAtsPeerToPeerReview peerRev;
   @Mock protected IAtsTask task1, task2;
   @Mock protected ActionableItem testAi, testAi2;
   @Mock protected IAtsAction action;
   @Mock protected IAtsStateDefinition analyze, implement, completed, cancelled;
   @Mock protected IAtsWorkDefinition workDef;
   @Mock protected IAtsWidgetDefinition estHoursWidgetDef, workPackageWidgetDef;
   @Mock protected IAttributeResolver attrResolver;
   @Mock protected IAtsVersionService versionService;
   @Mock protected IAtsBranchService branchService;
   @Mock protected IAtsWorkDefinitionService workDefService;
   @Mock protected IAtsWorkItemService workItemService;
   @Mock protected IAtsActionableItemService actionableItemService;
   @Mock protected IAtsTeamDefinitionService teamDefinitionService;
   @Mock protected AtsApi atsApi;
   // @formatter:on

   public String WORK_DEF_NAME = "Mock_Team_Workflow_Definition";
   public ArtifactId WORK_DEF_ART = ArtifactId.valueOf(345L);
   private final String className;
   private String testName = "";

   public AtsMockitoTest(String className) {
      this.className = className;
   }

   public void setTestName(String testName) {
      this.testName = testName;
   }

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      when(atsApi.getAttributeResolver()).thenReturn(attrResolver);
      when(atsApi.getVersionService()).thenReturn(versionService);
      when(atsApi.getBranchService()).thenReturn(branchService);
      when(atsApi.getWorkDefinitionService()).thenReturn(workDefService);
      when(atsApi.getWorkItemService()).thenReturn(workItemService);
      when(atsApi.getActionableItemService()).thenReturn(actionableItemService);
      when(atsApi.getTeamDefinitionService()).thenReturn(teamDefinitionService);

      when(currentUser.getName()).thenReturn("User1");
      when(currentUser.getId()).thenReturn(Lib.generateId());
      when(currentUser.getUserId()).thenReturn("1324");
      when(currentUser.isActive()).thenReturn(true);

      initializeState(analyze, "Analyze", StateType.Working, 1, implement,
         Arrays.asList(implement, completed, cancelled), Arrays.asList(cancelled));
      initializeState(implement, "Implement", StateType.Working, 2, completed,
         Arrays.asList(analyze, completed, cancelled), Arrays.asList(cancelled, analyze));
      initializeState(completed, "Completed", StateType.Completed, 3, completed, Arrays.asList(implement),
         Arrays.asList(implement));
      initializeState(cancelled, "Cancelled", StateType.Cancelled, 4, null, Arrays.asList(analyze, implement),
         Arrays.asList(analyze, implement));

      when(workDef.getName()).thenReturn(WORK_DEF_NAME);
      when(workDef.getStartState()).thenReturn(analyze);
      when(workDef.getStates()).thenReturn(Arrays.asList(cancelled, analyze, completed, implement));

      when(estHoursWidgetDef.getName()).thenReturn(AtsAttributeTypes.EstimatedHours.getUnqualifiedName());
      when(estHoursWidgetDef.getAttributeType()).thenReturn(AtsAttributeTypes.EstimatedHours);
      when(estHoursWidgetDef.getXWidgetName()).thenReturn("XFloatDam");

      when(workPackageWidgetDef.getName()).thenReturn(AtsAttributeTypes.WorkPackage.getUnqualifiedName());
      when(workPackageWidgetDef.getAttributeType()).thenReturn(AtsAttributeTypes.WorkPackage);
      when(workPackageWidgetDef.getXWidgetName()).thenReturn("XTextDam");

      initializeAi(testAi, "AI", true, true);
      initializeAi(testAi2, "AI2", true, true);
      Collection<ActionableItem> aias = new HashSet<>();
      aias.add(testAi);
      aias.add(testAi2);

      when(teamDef.getName()).thenReturn(getTitle("Test Team Def"));
      when(teamDef.getId()).thenReturn(Lib.generateId());
      when(teamDef.isActive()).thenReturn(true);
      when(teamDef.getAtsApi()).thenReturn(atsApi);
      when(atsApi.getTeamDefinitionService()).thenReturn(teamDefinitionService);
      when(teamDefinitionService.getLeads(teamDef)).thenReturn(Arrays.asList(currentUser));
      when(actionableItemService.getActionableItems(teamDef)).thenReturn(aias);
      when(attrResolver.getSoleAttributeValue(teamDef, AtsAttributeTypes.WorkflowDefinition, "")).thenReturn(
         WORK_DEF_NAME);
      when(attrResolver.getSoleArtifactIdReference(teamDef, AtsAttributeTypes.WorkflowDefinitionReference,
         ArtifactId.SENTINEL)).thenReturn(WORK_DEF_ART);

      inializeVersion(ver1, "ver 1.0");
      inializeVersion(ver2, "ver 2.0");
      inializeVersion(ver3, "ver 3.0");
      inializeVersion(ver4, "ver 4.0");

      Set<IAtsVersion> vers = new HashSet<>();
      vers.addAll(Arrays.asList(ver1, ver2, ver3, ver4));
      when(teamDefinitionService.getVersions(teamDef)).thenReturn(vers);

      when(action.getTeamWorkflows()).thenReturn(Arrays.asList(teamWf));
      when(action.getId()).thenReturn(45L);
      when(action.getName()).thenReturn(getTitle("Action"));

      Set<IAtsActionableItem> aias2 = new HashSet<>();
      aias2.add(testAi);
      aias2.add(testAi2);

      when(teamWf.getName()).thenReturn("Test Team Wf");
      when(teamWf.getActionableItems()).thenReturn(aias2);
      when(teamWf.getAtsId()).thenReturn("ATS0008");
      when(teamWf.getArtifactTypeName()).thenReturn(AtsArtifactTypes.TeamWorkflow.getName());
      when(teamWf.getTeamDefinition()).thenReturn(teamDef);

      inializeTask(task1, "Test Task 1");
      inializeTask(task2, "Test Task 2");

      when(decRev.getParentTeamWorkflow()).thenReturn(teamWf);
      when(decRev.getName()).thenReturn(getTitle("Test Dec Rev"));
      when(decRev.getAssignees()).thenReturn(Arrays.asList(currentUser));

      when(peerRev.getParentTeamWorkflow()).thenReturn(teamWf);
      when(peerRev.getName()).thenReturn(getTitle("Test Peer Rev"));
      when(peerRev.getAssignees()).thenReturn(Arrays.asList(currentUser));
   }

   private void inializeTask(IAtsTask task, String name) {
      when(task.getName()).thenReturn(getTitle(name));
      when(task.getId()).thenReturn(Lib.generateId());
      when(task.getParentTeamWorkflow()).thenReturn(teamWf);
   }

   private void inializeVersion(IAtsVersion version, String name) {
      when(version.getName()).thenReturn(name);
      when(version.getId()).thenReturn(Lib.generateId());
   }

   private void initializeState(IAtsStateDefinition state, String name, StateType type, int ordinal, IAtsStateDefinition defaultToState, List<IAtsStateDefinition> toStates, List<IAtsStateDefinition> overrideValidationStates) {
      when(state.getName()).thenReturn(name);
      when(state.getWorkDefinition()).thenReturn(workDef);
      when(state.getStateType()).thenReturn(type);
      when(state.getOrdinal()).thenReturn(ordinal);
      if (defaultToState != null) {
         when(state.getDefaultToState()).thenReturn(defaultToState);
      }
      when(state.getToStates()).thenReturn(toStates);
      when(state.getOverrideAttributeValidationStates()).thenReturn(overrideValidationStates);
   }

   private void initializeAi(IAtsActionableItem ai, String name, boolean active, boolean actionable) {
      when(ai.getId()).thenReturn(Lib.generateId());
      when(ai.getName()).thenReturn(name);
      when(ai.isActionable()).thenReturn(actionable);
      when(ai.isActive()).thenReturn(active);
      when(ai.getTeamDefinition()).thenReturn(teamDef);
   }

   public IAtsWorkDefinition getWorkDef() {
      return workDef;
   }

   public IAtsStateDefinition getAnalyzeStateDef() {
      return analyze;
   }

   public IAtsWidgetDefinition getEstHoursWidgetDef() {
      return estHoursWidgetDef;
   }

   public IAtsWidgetDefinition getWorkPackageWidgetDef() {
      return workPackageWidgetDef;
   }

   public IAtsStateDefinition getImplementStateDef() {
      return implement;
   }

   public IAtsStateDefinition getCompletedStateDef() {
      return completed;
   }

   public IAtsStateDefinition getCancelledStateDef() {
      return cancelled;
   }

   public IAtsTeamWorkflow getTeamWf() {
      return teamWf;
   }

   public IAtsActionableItem getTestAi() {
      return testAi;
   }

   public IAtsTeamDefinition getTestTeamDef() {
      return teamDef;
   }

   @Override
   public String toString() {
      return getTitle("");
   }

   public AtsUser getCurrentUser() {
      return currentUser;
   }

   public IAtsTeamDefinition getTeamDef() {
      return teamDef;
   }

   public IAtsVersion getVer1() {
      return ver1;
   }

   public IAtsVersion getVer2() {
      return ver2;
   }

   public IAtsVersion getVer3() {
      return ver3;
   }

   public IAtsVersion getVer4() {
      return ver4;
   }

   public IAtsDecisionReview getDecRev() {
      return decRev;
   }

   public IAtsPeerToPeerReview getPeerRev() {
      return peerRev;
   }

   public IAtsTask getTask1() {
      return task1;
   }

   public IAtsTask getTask2() {
      return task2;
   }

   public IAtsActionableItem getTestAi2() {
      return testAi2;
   }

   public IAtsAction getAction() {
      return action;
   }

   protected String getTitle(String objectName) {
      StringBuilder sb = new StringBuilder("AMT -");
      if (Strings.isValid(objectName)) {
         sb.append(" Obj:[");
         sb.append(objectName);
         sb.append("] ");
      }
      if (Strings.isValid(testName)) {
         sb.append(" Test:[");
         sb.append(testName);
         sb.append("] ");
      }
      if (Strings.isValid(className)) {
         sb.append(" Class:[");
         sb.append(className);
         sb.append("] ");
      }
      return sb.toString().replaceFirst(" $", "");
   }

}
