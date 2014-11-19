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
package org.eclipse.osee.ats.client.integration.tests.ats.core.client;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.client.demo.DemoSawBuilds;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.core.client.branch.AtsBranchUtil;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewManager;
import org.eclipse.osee.ats.core.client.review.DecisionReviewState;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workdef.SimpleDecisionReviewOption;
import org.eclipse.osee.ats.core.workflow.state.StateTypeAdapter;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.mocks.MockStateDefinition;
import org.eclipse.osee.ats.mocks.MockWidgetDefinition;
import org.eclipse.osee.ats.mocks.MockWorkDefinition;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * Test utility that will create a new work definition, team definition, versions and allow tests to easily
 * create/cleanup team workflows, tasks and reviews.
 * 
 * @author Donald G. Dunne
 */
public class AtsTestUtil {

   private static TeamWorkFlowArtifact teamArt = null, teamArt2 = null, teamArt3 = null, teamArt4 = null;
   private static IAtsTeamDefinition teamDef = null;
   private static IAtsVersion verArt1 = null, verArt2 = null, verArt3 = null, verArt4 = null;
   private static DecisionReviewArtifact decRevArt = null;
   private static PeerToPeerReviewArtifact peerRevArt = null;
   private static TaskArtifact taskArtWf1 = null, taskArtWf2 = null;
   private static IAtsActionableItem testAi = null, testAi2 = null, testAi3 = null, testAi4 = null;
   private static ActionArtifact actionArt = null, actionArt2 = null, actionArt3 = null, actionArt4 = null;
   private static MockStateDefinition analyze, implement, completed, cancelled = null;
   private static MockWorkDefinition workDef = null;
   public static String WORK_DEF_NAME = "Test_Team _Workflow_Definition";
   private static MockWidgetDefinition estHoursWidgetDef, workPackageWidgetDef;
   private static String postFixName;

   public static void validateArtifactCache() throws OseeStateException {
      final Collection<Artifact> dirtyArtifacts = ArtifactCache.getDirtyArtifacts();
      if (!dirtyArtifacts.isEmpty()) {
         for (Artifact artifact : dirtyArtifacts) {
            System.err.println(String.format("Artifact [%s] is dirty [%s]", artifact.toStringWithId(),
               Artifacts.getDirtyReport(artifact)));
         }
         throw new OseeStateException("[%d] Dirty Artifacts found after populate (see console for details)",
            dirtyArtifacts.size());
      }
   }

   public static void validateObjectsNull() throws OseeStateException {
      validateObjectsNull("teamArt", teamArt);
      validateObjectsNull("teamArt2", teamArt2);
      validateObjectsNull("teamArt3", teamArt3);
      validateObjectsNull("teamArt4", teamArt4);
      validateObjectsNull("teamDef", teamDef);
      validateObjectsNull("verArt1", verArt1);
      validateObjectsNull("verArt2", verArt2);
      validateObjectsNull("verArt3", verArt3);
      validateObjectsNull("verArt4", verArt4);
      validateObjectsNull("decRevArt", decRevArt);
      validateObjectsNull("peerRevArt", peerRevArt);
      validateObjectsNull("taskArt1", taskArtWf1);
      validateObjectsNull("taskArt2", taskArtWf2);
      validateObjectsNull("testAi", testAi);
      validateObjectsNull("testAi2", testAi2);
      validateObjectsNull("testAi3", testAi3);
      validateObjectsNull("testAi4", testAi4);
      validateObjectsNull("actionArt", actionArt);
      validateObjectsNull("actionArt2", actionArt2);
      validateObjectsNull("actionArt3", actionArt3);
      validateObjectsNull("actionArt4", actionArt4);
      validateObjectsNull("analyze", analyze);
      validateObjectsNull("implement", implement);
      validateObjectsNull("completed", completed);
      validateObjectsNull("cancelled", cancelled);
      validateObjectsNull("workDef", workDef);
      validateObjectsNull("estHoursWidgetDef", estHoursWidgetDef);
      validateObjectsNull("workPackageWidgetDef", workPackageWidgetDef);
   }

   private static void validateObjectsNull(String name, Object obj) throws OseeStateException {
      if (obj != null) {
         throw new OseeStateException("[%s] objects should be null but is not", name);
      }
   }

   public static IAtsWorkDefinition getWorkDef() throws OseeCoreException {
      ensureLoaded();
      return workDef;
   }

   public static MockStateDefinition getAnalyzeStateDef() throws OseeCoreException {
      ensureLoaded();
      return analyze;
   }

   public static IAtsWidgetDefinition getEstHoursWidgetDef() throws OseeCoreException {
      ensureLoaded();
      return estHoursWidgetDef;
   }

   public static IAtsWidgetDefinition getWorkPackageWidgetDef() throws OseeCoreException {
      ensureLoaded();
      return workPackageWidgetDef;
   }

   public static MockStateDefinition getImplementStateDef() throws OseeCoreException {
      ensureLoaded();
      return implement;
   }

   public static MockStateDefinition getCompletedStateDef() throws OseeCoreException {
      ensureLoaded();
      return completed;
   }

   public static MockStateDefinition getCancelledStateDef() throws OseeCoreException {
      ensureLoaded();
      return cancelled;
   }

   private static void ensureLoaded() throws OseeCoreException {
      if (workDef == null) {
         throw new OseeStateException("Must call cleanAndReset before using this method");
      }
   }

   private static void clearCaches() throws OseeCoreException {
      if (workDef != null) {
         AtsClientService.get().getWorkDefinitionAdmin().removeWorkDefinition(workDef);
      }
      analyze = null;
      implement = null;
      completed = null;
      cancelled = null;
      workDef = null;
      estHoursWidgetDef = null;
      workPackageWidgetDef = null;
      teamArt = null;
      teamArt2 = null;
      teamArt3 = null;
      teamArt4 = null;
      teamDef = null;
      taskArtWf1 = null;
      taskArtWf2 = null;
      testAi = null;
      testAi2 = null;
      testAi3 = null;
      testAi4 = null;
      actionArt = null;
      actionArt2 = null;
      actionArt3 = null;
      actionArt4 = null;
      verArt1 = null;
      verArt2 = null;
      verArt3 = null;
      verArt4 = null;
      decRevArt = null;
      peerRevArt = null;
      for (IAtsActionableItem aia : AtsClientService.get().getConfig().get(IAtsActionableItem.class)) {
         if (aia.getName().contains("AtsTestUtil")) {
            AtsClientService.get().getConfig().invalidate(aia);
         }
      }
      for (IAtsTeamDefinition aia : AtsClientService.get().getConfig().get(IAtsTeamDefinition.class)) {
         if (aia.getName().contains("AtsTestUtil")) {
            AtsClientService.get().getConfig().invalidate(aia);
         }
      }
      for (IAtsVersion ver : AtsClientService.get().getConfig().get(IAtsVersion.class)) {
         if (ver.getName().contains("AtsTestUtil")) {
            AtsClientService.get().getConfig().invalidate(ver);
         }
      }
   }

   private static String getTitle(String objectName, String postFixName) {
      return String.format("%s - %s [%s]", AtsTestUtil.class.getSimpleName(), objectName, postFixName);
   }

   /**
    * Clear workDef from cache, clear all objects and create new objects with postFixName in titles
    */
   private static void reset(String postFixName) throws OseeCoreException {
      AtsBulkLoad.reloadConfig(true);
      AtsTestUtil.postFixName = postFixName;
      AtsChangeSet changes = new AtsChangeSet(AtsTestUtil.class.getSimpleName());
      workDef = new MockWorkDefinition(WORK_DEF_NAME);

      analyze = new MockStateDefinition("Analyze");
      analyze.setWorkDefinition(workDef);
      analyze.setStateType(StateType.Working);
      analyze.setOrdinal(1);
      workDef.addState(analyze);

      workDef.setStartState(analyze);

      implement = new MockStateDefinition("Implement");
      implement.setWorkDefinition(workDef);
      implement.setStateType(StateType.Working);
      implement.setOrdinal(2);
      workDef.addState(implement);

      completed = new MockStateDefinition("Completed");
      completed.setWorkDefinition(workDef);
      completed.setStateType(StateType.Completed);
      completed.setOrdinal(3);
      workDef.addState(completed);

      cancelled = new MockStateDefinition("Cancelled");
      cancelled.setWorkDefinition(workDef);
      cancelled.setStateType(StateType.Cancelled);
      cancelled.setOrdinal(4);
      workDef.addState(cancelled);

      analyze.setDefaultToState(implement);
      analyze.getToStates().addAll(Arrays.asList(implement, completed, cancelled));
      analyze.getOverrideAttributeValidationStates().addAll(Arrays.asList(cancelled));

      implement.setDefaultToState(completed);
      implement.getToStates().addAll(Arrays.asList(analyze, completed, cancelled));
      implement.getOverrideAttributeValidationStates().addAll(Arrays.asList(cancelled, analyze));

      completed.setDefaultToState(completed);
      completed.getToStates().addAll(Arrays.asList(implement));
      completed.getOverrideAttributeValidationStates().addAll(Arrays.asList(implement));

      cancelled.getToStates().addAll(Arrays.asList(analyze, implement));
      cancelled.getOverrideAttributeValidationStates().addAll(Arrays.asList(analyze, implement));

      estHoursWidgetDef = new MockWidgetDefinition(AtsAttributeTypes.EstimatedHours.getUnqualifiedName());
      estHoursWidgetDef.setAttributeName(AtsAttributeTypes.EstimatedHours.getName());
      estHoursWidgetDef.setXWidgetName("XFloatDam");

      workPackageWidgetDef = new MockWidgetDefinition(AtsAttributeTypes.WorkPackage.getUnqualifiedName());
      workPackageWidgetDef.setAttributeName(AtsAttributeTypes.WorkPackage.getName());
      workPackageWidgetDef.setXWidgetName("XTextDam");

      AtsClientService.get().getWorkDefinitionAdmin().addWorkDefinition(workDef);

      testAi =
         AtsClientService.get().createActionableItem(GUID.create(), getTitle("AI", postFixName),
            AtsUtilClient.createConfigObjectUuid());
      testAi.setActive(true);
      testAi.setActionable(true);

      testAi2 =
         AtsClientService.get().createActionableItem(GUID.create(), getTitle("AI2", postFixName),
            AtsUtilClient.createConfigObjectUuid());
      testAi2.setActive(true);
      testAi2.setActionable(true);

      testAi3 =
         AtsClientService.get().createActionableItem(GUID.create(), getTitle("AI3", postFixName),
            AtsUtilClient.createConfigObjectUuid());
      testAi3.setActive(true);
      testAi3.setActionable(true);

      testAi4 =
         AtsClientService.get().createActionableItem(GUID.create(), getTitle("AI4", postFixName),
            AtsUtilClient.createConfigObjectUuid());
      testAi4.setActive(true);
      testAi4.setActionable(true);

      teamDef =
         AtsClientService.get().createTeamDefinition(GUID.create(), getTitle("Team Def", postFixName),
            AtsUtilClient.createConfigObjectUuid());
      teamDef.setWorkflowDefinition(WORK_DEF_NAME);
      teamDef.setActive(true);
      teamDef.getLeads().add(AtsClientService.get().getUserService().getCurrentUser());

      testAi.setTeamDefinition(teamDef);
      testAi2.setTeamDefinition(teamDef);
      testAi3.setTeamDefinition(teamDef);
      testAi4.setTeamDefinition(teamDef);

      verArt1 =
         AtsClientService.get().createVersion(GUID.create(), getTitle("ver 1.0", postFixName),
            AtsUtilClient.createConfigObjectUuid());
      teamDef.getVersions().add(verArt1);

      verArt2 =
         AtsClientService.get().createVersion(GUID.create(), getTitle("ver 2.0", postFixName),
            AtsUtilClient.createConfigObjectUuid());
      teamDef.getVersions().add(verArt2);

      verArt3 =
         AtsClientService.get().createVersion(GUID.create(), getTitle("ver 3.0", postFixName),
            AtsUtilClient.createConfigObjectUuid());
      teamDef.getVersions().add(verArt3);

      verArt4 =
         AtsClientService.get().createVersion(GUID.create(), getTitle("ver 4.0", postFixName),
            AtsUtilClient.createConfigObjectUuid());
      teamDef.getVersions().add(verArt4);

      actionArt =
         ActionManager.createAction(null, getTitle("Team WF", postFixName), "description", ChangeType.Improvement, "1",
            false, null, Arrays.asList(testAi), new Date(), AtsClientService.get().getUserService().getCurrentUser(),
            null, changes);

      teamArt = actionArt.getFirstTeam();

      changes.execute();
   }

   public static TaskArtifact getOrCreateTaskOffTeamWf1(AtsChangeSet changes) throws OseeCoreException {
      ensureLoaded();
      if (taskArtWf1 == null) {
         taskArtWf1 =
            teamArt.createNewTask(getTitle("Task", postFixName), new Date(),
               AtsClientService.get().getUserService().getCurrentUser(), changes);
         taskArtWf1.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, teamArt.getCurrentStateName());
      }
      return taskArtWf1;
   }

   public static TaskArtifact getOrCreateTaskOffTeamWf2(AtsChangeSet changes) throws OseeCoreException {
      ensureLoaded();
      if (taskArtWf2 == null) {
         taskArtWf2 =
            teamArt.createNewTask(getTitle("Task", postFixName), new Date(),
               AtsClientService.get().getUserService().getCurrentUser(), changes);
         taskArtWf2.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, teamArt.getCurrentStateName());
      }
      return taskArtWf2;
   }

   public static DecisionReviewArtifact getOrCreateDecisionReview(ReviewBlockType reviewBlockType, AtsTestUtilState relatedToState, IAtsChangeSet changes) throws OseeCoreException {
      ensureLoaded();
      if (decRevArt == null) {
         List<IAtsDecisionReviewOption> options = new ArrayList<IAtsDecisionReviewOption>();
         options.add(new SimpleDecisionReviewOption(DecisionReviewState.Completed.getName(), false, null));
         options.add(new SimpleDecisionReviewOption(DecisionReviewState.Followup.getName(), true,
            Arrays.asList(AtsClientService.get().getUserService().getCurrentUser().getUserId())));
         decRevArt =
            DecisionReviewManager.createNewDecisionReview(teamArt, reviewBlockType,
               AtsTestUtil.class.getSimpleName() + " Test Decision Review", relatedToState.getName(),
               "Decision Review", options, Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()),
               new Date(), AtsClientService.get().getUserService().getCurrentUser(), changes);
      }
      return decRevArt;
   }

   public static TeamWorkFlowArtifact getTeamWf() throws OseeCoreException {
      ensureLoaded();
      return teamArt;
   }

   public static IAtsActionableItem getTestAi() throws OseeCoreException {
      ensureLoaded();
      return testAi;

   }

   public static IAtsTeamDefinition getTestTeamDef() throws OseeCoreException {
      ensureLoaded();
      return teamDef;
   }

   /**
    * All team defs, AIs, action and workflows will be deleted and new ones created with "name" as part of object
    * names/titles. In addition, ArtifactCache will validate that it is not dirty or display errors if it is.
    */
   public static void cleanupAndReset(String name) throws OseeCoreException {
      cleanup();
      reset(name);
   }

   private static void delete(AtsChangeSet changes, Artifact artifact) throws OseeCoreException {
      if (artifact != null) {
         changes.addToDelete(artifact);
      }
   }

   private static void deleteTeamWf(TeamWorkFlowArtifact teamWfToDelete) throws OseeCoreException {
      if (teamWfToDelete != null) {
         AtsChangeSet changes = new AtsChangeSet(AtsTestUtil.class.getSimpleName() + " - cleanup deleteTeamWf");

         if (teamWfToDelete.getWorkingBranch() != null) {
            Result result = AtsBranchUtil.deleteWorkingBranch(teamWfToDelete, true);
            if (result.isFalse()) {
               throw new OseeStateException("Error deleting working branch [%s]", result.getText());
            }
         }
         for (TaskArtifact taskArt : teamWfToDelete.getTaskArtifacts()) {
            changes.addToDelete(taskArt);
         }
         for (AbstractReviewArtifact revArt : ReviewManager.getReviews(teamWfToDelete)) {
            changes.addToDelete(revArt);
         }

         changes.addToDelete(teamWfToDelete);
         if (!changes.isEmpty()) {
            changes.execute();
         }
      }
   }

   /**
    * Cleanup all artifacts and confirm that ArtifactCache has no dirty artifacts. Should be called at beginning at end
    * of each test.
    */
   public static void cleanup() throws OseeCoreException {
      WorldEditor.closeAll();
      SMAEditor.closeAll();
      TaskEditor.closeAll();

      if (teamArt != null) {
         Branch workingBranch = teamArt.getWorkingBranch();
         if (workingBranch != null) {
            BranchManager.deleteBranchAndPend(workingBranch);
         }
      }

      AtsChangeSet changes = new AtsChangeSet(AtsTestUtil.class.getSimpleName() + " - cleanup 1");
      delete(changes, peerRevArt);
      delete(changes, decRevArt);
      delete(changes, taskArtWf1);
      delete(changes, taskArtWf2);
      delete(changes, actionArt);
      delete(changes, actionArt2);
      delete(changes, actionArt3);
      delete(changes, actionArt4);
      if (!changes.isEmpty()) {
         changes.execute();
      }

      deleteTeamWf(teamArt);
      deleteTeamWf(teamArt2);
      deleteTeamWf(teamArt3);
      deleteTeamWf(teamArt4);

      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(),
            AtsTestUtil.class.getSimpleName() + " - cleanup config");
      for (IAtsConfigObject config : Arrays.asList(teamDef, testAi, testAi2, testAi3, testAi4)) {
         if (config != null && config.getStoreObject() instanceof Artifact) {
            Artifact art = (Artifact) config.getStoreObject();
            art.deleteAndPersist(transaction);
         }
      }
      transaction.execute();

      clearCaches();

      // validate that there are no dirty artifacts in cache
      AtsTestUtil.validateArtifactCache();
   }

   public static IAtsVersion getVerArt1() {
      return verArt1;
   }

   public static IAtsVersion getVerArt2() {
      return verArt2;
   }

   public static IAtsVersion getVerArt3() {
      return verArt3;
   }

   public static IAtsVersion getVerArt4() {
      return verArt4;
   }

   /**
    * Deletes any artifact with name that starts with title
    */
   public static void cleanupSimpleTest(String title) throws Exception {
      cleanupSimpleTest(Arrays.asList(title));
   }

   /**
    * Deletes all artifacts with names that start with any title given
    */
   public static void cleanupSimpleTest(Collection<String> titles) throws Exception {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      for (String title : titles) {
         artifacts.addAll(ArtifactQuery.getArtifactListFromName(title, AtsUtilCore.getAtsBranch(), EXCLUDE_DELETED,
            QueryOption.CONTAINS_MATCH_OPTIONS));
      }
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artifacts));
      TestUtil.sleep(4000);
   }

   public static Result transitionTo(AtsTestUtilState atsTestUtilState, IAtsUser user, IAtsChangeSet changes, TransitionOption... transitionOptions) throws OseeCoreException {
      if (atsTestUtilState == AtsTestUtilState.Analyze && teamArt.isInState(AtsTestUtilState.Analyze)) {
         return Result.TrueResult;
      }

      if (atsTestUtilState == AtsTestUtilState.Cancelled) {
         Result result = transitionToState(teamArt, AtsTestUtilState.Cancelled, user, changes, transitionOptions);
         if (result.isFalse()) {
            return result;
         }
         return Result.TrueResult;
      }

      Result result = transitionToState(teamArt, AtsTestUtilState.Implement, user, changes, transitionOptions);
      if (result.isFalse()) {
         return result;
      }

      if (atsTestUtilState == AtsTestUtilState.Implement) {
         return Result.TrueResult;
      }

      if (atsTestUtilState == AtsTestUtilState.Completed) {
         result = transitionToState(teamArt, AtsTestUtilState.Completed, user, changes, transitionOptions);
         if (result.isFalse()) {
            return result;
         }

      }
      return Result.TrueResult;

   }

   private static Result transitionToState(TeamWorkFlowArtifact teamArt, IStateToken toState, IAtsUser user, IAtsChangeSet changes, TransitionOption... transitionOptions) {
      TransitionHelper helper =
         new TransitionHelper("Transition to " + toState.getName(), Arrays.asList(teamArt), toState.getName(),
            Arrays.asList(user), null, changes, AtsClientService.get().getServices(), transitionOptions);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      if (results.isEmpty()) {
         return Result.TrueResult;
      }
      return new Result("Transition Error %s", results.toString());
   }

   public static class AtsTestUtilState extends StateTypeAdapter {
      public static AtsTestUtilState Analyze = new AtsTestUtilState("Analyze", StateType.Working);
      public static AtsTestUtilState Implement = new AtsTestUtilState("Implement", StateType.Working);
      public static AtsTestUtilState Completed = new AtsTestUtilState("Completed", StateType.Completed);
      public static AtsTestUtilState Cancelled = new AtsTestUtilState("Cancelled", StateType.Cancelled);

      private AtsTestUtilState(String pageName, StateType StateType) {
         super(AtsTestUtilState.class, pageName, StateType);
      }

      public static AtsTestUtilState valueOf(String pageName) {
         return StateTypeAdapter.valueOfPage(AtsTestUtilState.class, pageName);
      }

      public static List<AtsTestUtilState> values() {
         return StateTypeAdapter.pages(AtsTestUtilState.class);
      }
   }

   public static PeerToPeerReviewArtifact getOrCreatePeerReview(ReviewBlockType reviewBlockType, AtsTestUtilState relatedToState, IAtsChangeSet changes) throws OseeCoreException {
      ensureLoaded();
      try {
         if (peerRevArt == null) {
            peerRevArt =
               PeerToPeerReviewManager.createNewPeerToPeerReview(
                  AtsClientService.get().getWorkDefinitionAdmin().getDefaultPeerToPeerWorkflowDefinitionMatch().getWorkDefinition(),
                  teamArt, AtsTestUtil.class.getSimpleName() + " Test Peer Review", relatedToState.getName(), changes);
            peerRevArt.setSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, reviewBlockType.name());
         }
      } catch (OseeCoreException ex) {
         throw new OseeWrappedException(ex);
      }
      return peerRevArt;
   }

   public static TeamWorkFlowArtifact getTeamWf2() throws OseeCoreException {
      ensureLoaded();
      if (teamArt2 == null) {
         AtsChangeSet changes = new AtsChangeSet(AtsTestUtil.class.getSimpleName());
         actionArt2 =
            ActionManager.createAction(null, getTitle("Team WF2", postFixName), "description", ChangeType.Improvement,
               "1", false, null, Arrays.asList(testAi2), new Date(),
               AtsClientService.get().getUserService().getCurrentUser(), null, changes);
         teamArt2 = actionArt2.getFirstTeam();
         changes.execute();
      }
      return teamArt2;
   }

   public static IAtsActionableItem getTestAi2() throws OseeCoreException {
      ensureLoaded();
      return testAi2;
   }

   public static TeamWorkFlowArtifact getTeamWf3() throws OseeCoreException {
      ensureLoaded();
      if (teamArt3 == null) {
         AtsChangeSet changes = new AtsChangeSet(AtsTestUtil.class.getSimpleName());
         actionArt3 =
            ActionManager.createAction(null, getTitle("Team WF3", postFixName), "description", ChangeType.Improvement,
               "1", false, null, Arrays.asList(testAi3), new Date(),
               AtsClientService.get().getUserService().getCurrentUser(), null, changes);
         teamArt3 = actionArt3.getFirstTeam();
         changes.execute();
      }
      return teamArt3;
   }

   public static IAtsActionableItem getTestAi3() throws OseeCoreException {
      ensureLoaded();
      return testAi3;
   }

   public static TeamWorkFlowArtifact getTeamWf4() throws OseeCoreException {
      ensureLoaded();
      if (teamArt4 == null) {
         AtsChangeSet changes = new AtsChangeSet(AtsTestUtil.class.getSimpleName());
         actionArt4 =
            ActionManager.createAction(null, getTitle("Team WF4", postFixName), "description", ChangeType.Improvement,
               "1", false, null, Arrays.asList(testAi4), new Date(),
               AtsClientService.get().getUserService().getCurrentUser(), null, changes);

         teamArt4 = actionArt4.getFirstTeam();
         AtsClientService.get().getVersionService().setTargetedVersion(teamArt4, verArt4);
         changes.execute();
      }
      return teamArt4;
   }

   public static IAtsActionableItem getTestAi4() throws OseeCoreException {
      ensureLoaded();
      return testAi4;
   }

   /**
    * @return 2nd Action with single Team Workflow not tied to other ActionArt or TeamWf
    */
   public static ActionArtifact getActionArt2() throws OseeCoreException {
      ensureLoaded();
      if (actionArt2 == null) {
         getTeamWf2();
      }
      return actionArt2;
   }

   /**
    * @return 3rd Action with single Team Workflow not tied to other ActionArt or TeamWf
    */
   public static ActionArtifact getActionArt3() throws OseeCoreException {
      ensureLoaded();
      if (actionArt3 == null) {
         getTeamWf3();
      }
      return actionArt3;
   }

   /**
    * @return 4rd Action with single Team Workflow not tied to other ActionArt or TeamWf
    */
   public static ActionArtifact getActionArt4() throws OseeCoreException {
      ensureLoaded();
      if (actionArt4 == null) {
         getTeamWf4();
      }
      return actionArt4;
   }

   public static ActionArtifact getActionArt() throws OseeCoreException {
      ensureLoaded();
      return actionArt;
   }

   public static ISelectedAtsArtifacts getSelectedAtsArtifactsForTeamWf() {
      return new ISelectedAtsArtifacts() {

         @Override
         public Set<? extends Artifact> getSelectedSMAArtifacts() throws OseeCoreException {
            return Collections.singleton(getTeamWf());
         }

         @Override
         public List<Artifact> getSelectedAtsArtifacts() throws OseeCoreException {
            return Arrays.asList((Artifact) getTeamWf());
         }

         @Override
         public List<TaskArtifact> getSelectedTaskArtifacts() {
            return Collections.emptyList();
         }

      };
   }

   public static Result createWorkingBranchFromTeamWf() throws OseeCoreException {
      configureVer1ForWorkingBranch();
      Result result = AtsBranchUtil.createWorkingBranch_Validate(teamArt);
      if (result.isFalse()) {
         return result;
      }
      AtsBranchUtil.createWorkingBranch_Create(teamArt, true);
      teamArt.getWorkingBranchForceCacheUpdate();
      return Result.TrueResult;
   }

   public static void configureVer1ForWorkingBranch() throws OseeCoreException {
      IAtsVersion verArt = getVerArt1();
      verArt.setAllowCreateBranch(true);
      verArt.setAllowCommitBranch(true);
      verArt.setBaselineBranchUuid(BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1).getUuid());
      if (!AtsClientService.get().getVersionService().hasTargetedVersion(getTeamWf())) {
         AtsClientService.get().getVersionService().setTargetedVersion(getTeamWf(), getVerArt1());
         getTeamWf().persist(AtsTestUtil.class.getSimpleName() + "-SetTeamWfTargetedVer1");
      }
   }

   public static String getName() {
      return postFixName;
   }

}
