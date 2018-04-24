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
package org.eclipse.osee.ats.client.integration.tests.ats.workflow;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.branch.AtsBranchUtil;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.workflow.transition.TransitionManagerTest;
import org.eclipse.osee.ats.core.workdef.SimpleDecisionReviewOption;
import org.eclipse.osee.ats.core.workflow.state.StateTypeAdapter;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.editor.WorkflowEditor;
import org.eclipse.osee.ats.util.AtsUtilClient;
import org.eclipse.osee.ats.workflow.action.ActionArtifact;
import org.eclipse.osee.ats.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.workflow.review.DecisionReviewState;
import org.eclipse.osee.ats.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.workflow.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.workflow.review.ReviewManager;
import org.eclipse.osee.ats.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.workflow.task.TaskEditor;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * Test utility that will create a new work definition, team definition, versions and allow tests to easily
 * create/cleanup team workflows, tasks and reviews.
 *
 * @author Donald G. Dunne
 */
public class AtsTestUtil {

   private static TeamWorkFlowArtifact teamWf = null, teamArt2 = null, teamArt3 = null, teamArt4 = null;
   private static IAtsTeamDefinition teamDef = null;
   private static IAtsVersion verArt1 = null, verArt2 = null, verArt3 = null, verArt4 = null;
   private static DecisionReviewArtifact decRevArt = null;
   private static PeerToPeerReviewArtifact peerRevArt = null;
   private static TaskArtifact taskArtWf1 = null, taskArtWf2 = null;
   private static IAtsActionableItem testAi = null, testAi2 = null, testAi3 = null, testAi4 = null;
   private static ActionArtifact actionArt = null, actionArt2 = null, actionArt3 = null, actionArt4 = null;
   private static IAtsStateDefinition analyze, implement, completed, cancelled = null;
   private static IAtsWidgetDefinition estHoursWidgetDef, workPackageWidgetDef;
   private static String postFixName;

   public static void validateArtifactCache() {
      final Collection<Artifact> dirtyArtifacts = ArtifactCache.getDirtyArtifacts();
      if (!dirtyArtifacts.isEmpty()) {
         for (Artifact artifact : dirtyArtifacts) {
            System.err.println(String.format("Dirty Artifact [%s] attribute [%s]", artifact.toStringWithId(),
               Artifacts.getDirtyReport(artifact)));
         }
         throw new OseeStateException("[%d] Dirty Artifacts found after populate (see console for details)",
            dirtyArtifacts.size());
      }
   }

   public static void validateObjectsNull() {
      validateObjectsNull("teamArt", teamWf);
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
      validateObjectsNull("estHoursWidgetDef", estHoursWidgetDef);
      validateObjectsNull("workPackageWidgetDef", workPackageWidgetDef);
   }

   private static void validateObjectsNull(String name, Object obj) {
      if (obj != null) {
         throw new OseeStateException("[%s] objects should be null but is not", name);
      }
   }

   public static IAtsStateDefinition getAnalyzeStateDef() {
      ensureLoaded();
      if (analyze == null) {
         analyze = teamWf.getWorkDefinition().getStateByName("Analyze");
      }
      return analyze;
   }

   public static IAtsWidgetDefinition getEstHoursWidgetDef() {
      ensureLoaded();
      if (estHoursWidgetDef == null) {
         for (IAtsLayoutItem item : getAnalyzeStateDef().getLayoutItems()) {
            if (item.getName().equals("ats.Estimated Hours")) {
               estHoursWidgetDef = (IAtsWidgetDefinition) item;
               break;
            }
         }
      }
      return estHoursWidgetDef;
   }

   public static IAtsWidgetDefinition getWorkPackageWidgetDef() {
      ensureLoaded();
      if (workPackageWidgetDef == null) {
         for (IAtsLayoutItem item : getAnalyzeStateDef().getLayoutItems()) {
            if (item.getName().equals("ats.Work Package")) {
               workPackageWidgetDef = (IAtsWidgetDefinition) item;
               break;
            }
         }
      }
      return workPackageWidgetDef;
   }

   public static IAtsStateDefinition getImplementStateDef() {
      ensureLoaded();
      if (implement == null) {
         implement = teamWf.getWorkDefinition().getStateByName("Implement");
      }
      return implement;
   }

   public static IAtsStateDefinition getCompletedStateDef() {
      ensureLoaded();
      if (completed == null) {
         completed = teamWf.getWorkDefinition().getStateByName("Completed");
      }
      return completed;
   }

   public static IAtsStateDefinition getCancelledStateDef() {
      ensureLoaded();
      if (cancelled == null) {
         cancelled = teamWf.getWorkDefinition().getStateByName("Cancelled");
      }
      return cancelled;
   }

   private static void ensureLoaded() {
      if (teamDef == null) {
         throw new OseeStateException("Must call cleanAndReset before using this method");
      }
   }

   private static void clearCaches() {
      analyze = null;
      implement = null;
      completed = null;
      cancelled = null;
      estHoursWidgetDef = null;
      workPackageWidgetDef = null;
      teamWf = null;
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
      for (IAtsActionableItem aia : AtsClientService.get().getQueryService().createQuery(
         AtsArtifactTypes.ActionableItem).getItems(IAtsActionableItem.class)) {
         if (aia.getName().contains("AtsTestUtil")) {
            AtsClientService.get().getCache().deCacheAtsObject(aia);
         }
      }
      for (IAtsTeamDefinition aia : AtsClientService.get().getQueryService().createQuery(
         AtsArtifactTypes.TeamDefinition).getItems(IAtsTeamDefinition.class)) {
         if (aia.getName().contains("AtsTestUtil")) {
            AtsClientService.get().getCache().deCacheAtsObject(aia);
         }
      }
      for (IAtsVersion ver : AtsClientService.get().getQueryService().createQuery(AtsArtifactTypes.Version).getItems(
         IAtsVersion.class)) {
         if (ver.getName().contains("AtsTestUtil")) {
            AtsClientService.get().getCache().deCacheAtsObject(ver);
         }
      }
   }

   private static String getTitle(String objectName, String postFixName) {
      return String.format("%s - %s [%s]", AtsTestUtil.class.getSimpleName(), objectName, postFixName);
   }

   /**
    * Clear workDef from cache, clear all objects and create new objects with postFixName in titles
    */
   private static void reset(String postFixName) {
      if (ClientSessionManager.isProductionDataStore()) {
         throw new OseeStateException("AtsTestUtil should not be run on production.");
      }

      AtsTestUtil.postFixName = postFixName;

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(AtsTestUtil.class.getSimpleName());

      IAtsActionableItem topAi =
         AtsClientService.get().getActionableItemService().getActionableItemById(AtsArtifactToken.TopActionableItem);

      testAi = AtsClientService.get().createActionableItem(getTitle("AI", postFixName),
         AtsUtilClient.createConfigObjectId(), changes, AtsClientService.get());
      changes.setSoleAttributeValue(testAi, AtsAttributeTypes.Active, true);
      changes.setSoleAttributeValue(testAi, AtsAttributeTypes.Actionable, true);
      changes.addChild(topAi, testAi);

      testAi2 = AtsClientService.get().createActionableItem(getTitle("AI2", postFixName),
         AtsUtilClient.createConfigObjectId(), changes, AtsClientService.get());
      changes.setSoleAttributeValue(testAi2, AtsAttributeTypes.Active, true);
      changes.setSoleAttributeValue(testAi2, AtsAttributeTypes.Actionable, true);
      changes.addChild(testAi, testAi2);

      testAi3 = AtsClientService.get().createActionableItem(getTitle("AI3", postFixName),
         AtsUtilClient.createConfigObjectId(), changes, AtsClientService.get());
      changes.setSoleAttributeValue(testAi3, AtsAttributeTypes.Active, true);
      changes.setSoleAttributeValue(testAi3, AtsAttributeTypes.Actionable, true);
      changes.addChild(testAi, testAi3);

      testAi4 = AtsClientService.get().createActionableItem(getTitle("AI4", postFixName),
         AtsUtilClient.createConfigObjectId(), changes, AtsClientService.get());
      changes.setSoleAttributeValue(testAi4, AtsAttributeTypes.Active, true);
      changes.setSoleAttributeValue(testAi4, AtsAttributeTypes.Actionable, true);
      changes.addChild(testAi, testAi4);

      teamDef = AtsClientService.get().createTeamDefinition(getTitle("Team Def", postFixName),
         AtsUtilClient.createConfigObjectId(), changes, AtsClientService.get());

      // All tests use the same Work Definition so it doesn't have to be re-created and imported each time
      AtsClientService.get().getWorkDefinitionService().setWorkDefinitionAttrs(teamDef,
         TransitionManagerTest.WorkDefTeamAtsTestUtil, changes);

      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.Active, true);
      changes.relate(teamDef, AtsRelationTypes.TeamLead_Lead, AtsClientService.get().getUserService().getCurrentUser());
      changes.relate(teamDef, AtsRelationTypes.TeamActionableItem_ActionableItem, testAi);
      changes.relate(teamDef, AtsRelationTypes.TeamActionableItem_ActionableItem, testAi2);
      changes.relate(teamDef, AtsRelationTypes.TeamActionableItem_ActionableItem, testAi3);
      changes.relate(teamDef, AtsRelationTypes.TeamActionableItem_ActionableItem, testAi4);
      IAtsTeamDefinition topTeamDef =
         AtsClientService.get().getTeamDefinitionService().getTeamDefinitionById(AtsArtifactToken.TopTeamDefinition);
      changes.addChild(topTeamDef, teamDef);

      verArt1 = AtsClientService.get().getVersionService().createVersion(getTitle("ver 1.0", postFixName),
         AtsUtilClient.createConfigObjectId(), changes);
      changes.relate(teamDef, AtsRelationTypes.TeamDefinitionToVersion_Version, verArt1);

      verArt2 = AtsClientService.get().getVersionService().createVersion(getTitle("ver 2.0", postFixName),
         AtsUtilClient.createConfigObjectId(), changes);
      changes.relate(teamDef, AtsRelationTypes.TeamDefinitionToVersion_Version, verArt2);

      verArt3 = AtsClientService.get().getVersionService().createVersion(getTitle("ver 3.0", postFixName),
         AtsUtilClient.createConfigObjectId(), changes);
      changes.relate(teamDef, AtsRelationTypes.TeamDefinitionToVersion_Version, verArt3);

      verArt4 = AtsClientService.get().getVersionService().createVersion(getTitle("ver 4.0", postFixName),
         AtsUtilClient.createConfigObjectId(), changes);
      changes.relate(teamDef, AtsRelationTypes.TeamDefinitionToVersion_Version, verArt4);

      ActionResult result = AtsClientService.get().getActionFactory().createAction(null,
         getTitle("Team WF", postFixName), "description", ChangeType.Improvement, "1", false, null,
         Arrays.asList(testAi), new Date(), AtsClientService.get().getUserService().getCurrentUser(), null, changes);

      actionArt = (ActionArtifact) result.getAction().getStoreObject();
      teamWf = (TeamWorkFlowArtifact) result.getFirstTeam().getStoreObject();

      changes.execute();
   }

   public static TaskArtifact getOrCreateTaskOffTeamWf1() {
      ensureLoaded();
      if (taskArtWf1 == null) {
         Collection<IAtsTask> createTasks =
            AtsClientService.get().getTaskService().createTasks(teamWf, Arrays.asList(getTitle("Task", postFixName)),
               null, new Date(), AtsClientService.get().getUserService().getCurrentUser(), teamWf.getCurrentStateName(),
               null, null, getName() + " Create Task");
         taskArtWf1 = (TaskArtifact) createTasks.iterator().next().getStoreObject();
      }
      return taskArtWf1;
   }

   public static DecisionReviewArtifact getOrCreateDecisionReview(ReviewBlockType reviewBlockType, AtsTestUtilState relatedToState, IAtsChangeSet changes) {
      ensureLoaded();
      if (decRevArt == null) {
         List<IAtsDecisionReviewOption> options = new ArrayList<>();
         options.add(new SimpleDecisionReviewOption(DecisionReviewState.Completed.getName(), false, null));
         options.add(new SimpleDecisionReviewOption(DecisionReviewState.Followup.getName(), true,
            Arrays.asList(AtsClientService.get().getUserService().getCurrentUser().getUserId())));
         decRevArt = (DecisionReviewArtifact) AtsClientService.get().getReviewService().createNewDecisionReview(teamWf,
            reviewBlockType, AtsTestUtil.class.getSimpleName() + " Test Decision Review", relatedToState.getName(),
            "Decision Review", options, Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()),
            new Date(), AtsClientService.get().getUserService().getCurrentUser(), changes).getStoreObject();
      }
      return decRevArt;
   }

   public static TeamWorkFlowArtifact getTeamWf() {
      ensureLoaded();
      return teamWf;
   }

   public static IAtsActionableItem getTestAi() {
      ensureLoaded();
      return testAi;

   }

   public static IAtsTeamDefinition getTestTeamDef() {
      ensureLoaded();
      return teamDef;
   }

   /**
    * All team defs, AIs, action and workflows will be deleted and new ones created with "name" as part of object
    * names/titles. In addition, ArtifactCache will validate that it is not dirty or display errors if it is.
    */
   public static void cleanupAndReset(String name) {
      cleanup();
      reset(name);
   }

   private static void delete(IAtsChangeSet changes, Artifact artifact) {
      if (artifact != null) {
         changes.addToDelete(artifact);
      }
   }

   private static void deleteTeamWf(TeamWorkFlowArtifact teamWfToDelete) {
      if (teamWfToDelete != null) {
         IAtsChangeSet changes =
            AtsClientService.get().createChangeSet(AtsTestUtil.class.getSimpleName() + " - cleanup deleteTeamWf");

         if (teamWfToDelete.getWorkingBranch().isValid()) {
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
   public static void cleanup() {
      WorldEditor.closeAll();
      WorkflowEditor.closeAll();
      TaskEditor.closeAll();

      if (teamWf != null) {
         BranchId workingBranch = teamWf.getWorkingBranch();
         if (workingBranch.isValid()) {
            BranchManager.deleteBranchAndPend(workingBranch);
         }
      }

      IAtsChangeSet changes =
         AtsClientService.get().createChangeSet(AtsTestUtil.class.getSimpleName() + " - cleanup 1");
      delete(changes, peerRevArt);
      delete(changes, decRevArt);
      delete(changes, taskArtWf1);
      delete(changes, taskArtWf2);
      delete(changes, actionArt);
      delete(changes, actionArt2);
      delete(changes, actionArt3);
      delete(changes, actionArt4);
      if (verArt1 != null) {
         delete(changes, (Artifact) AtsClientService.get().getQueryService().getArtifact(verArt1));
      }
      if (verArt2 != null) {
         delete(changes, (Artifact) AtsClientService.get().getQueryService().getArtifact(verArt2));
      }
      if (verArt3 != null) {
         delete(changes, (Artifact) AtsClientService.get().getQueryService().getArtifact(verArt3));
      }
      if (verArt4 != null) {
         delete(changes, (Artifact) AtsClientService.get().getQueryService().getArtifact(verArt4));
      }
      if (!changes.isEmpty()) {
         changes.execute();
      }

      deleteTeamWf(teamWf);
      deleteTeamWf(teamArt2);
      deleteTeamWf(teamArt3);
      deleteTeamWf(teamArt4);

      SkynetTransaction transaction = TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(),
         AtsTestUtil.class.getSimpleName() + " - cleanup config");
      for (IAtsConfigObject config : Arrays.asList(teamDef, testAi, testAi2, testAi3, testAi4)) {
         if (config != null) {
            Artifact art = (Artifact) AtsClientService.get().getQueryService().getArtifact(config);
            if (art != null) {
               art.deleteAndPersist(transaction);
            }
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
      List<Artifact> artifacts = new ArrayList<>();
      for (String title : titles) {
         artifacts.addAll(ArtifactQuery.getArtifactListFromName(title, AtsClientService.get().getAtsBranch(),
            EXCLUDE_DELETED, QueryOption.CONTAINS_MATCH_OPTIONS));
      }
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artifacts));
   }

   public static Result transitionTo(AtsTestUtilState atsTestUtilState, IAtsUser user, IAtsChangeSet changes, TransitionOption... transitionOptions) {
      return transitionTo(teamWf, atsTestUtilState, user, changes, transitionOptions);
   }

   public static Result transitionTo(IAtsTeamWorkflow teamWf, AtsTestUtilState atsTestUtilState, IAtsUser user, IAtsChangeSet changes, TransitionOption... transitionOptions) {
      if (atsTestUtilState == AtsTestUtilState.Analyze && teamWf.getStateMgr().isInState(AtsTestUtilState.Analyze)) {
         return Result.TrueResult;
      }

      if (atsTestUtilState == AtsTestUtilState.Cancelled) {
         Result result = transitionToState(teamWf, AtsTestUtilState.Cancelled, user, changes, transitionOptions);
         if (result.isFalse()) {
            return result;
         }
         return Result.TrueResult;
      }

      Result result = transitionToState(teamWf, AtsTestUtilState.Implement, user, changes, transitionOptions);
      if (result.isFalse()) {
         return result;
      }

      if (atsTestUtilState == AtsTestUtilState.Implement) {
         return Result.TrueResult;
      }

      if (atsTestUtilState == AtsTestUtilState.Completed) {
         result = transitionToState(teamWf, AtsTestUtilState.Completed, user, changes, transitionOptions);
         if (result.isFalse()) {
            return result;
         }

      }
      return Result.TrueResult;

   }

   private static Result transitionToState(IAtsTeamWorkflow teamWf, IStateToken toState, IAtsUser user, IAtsChangeSet changes, TransitionOption... transitionOptions) {
      TransitionHelper helper =
         new TransitionHelper("Transition to " + toState.getName(), Arrays.asList(teamWf), toState.getName(),
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

   public static PeerToPeerReviewArtifact getOrCreatePeerReview(ReviewBlockType reviewBlockType, AtsTestUtilState relatedToState, IAtsChangeSet changes) {
      ensureLoaded();
      try {
         if (peerRevArt == null) {
            peerRevArt = PeerToPeerReviewManager.createNewPeerToPeerReview(
               AtsClientService.get().getWorkDefinitionService().getDefaultPeerToPeerWorkflowDefinition(), teamWf,
               AtsTestUtil.class.getSimpleName() + " Test Peer Review", relatedToState.getName(), changes);
            peerRevArt.setSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, reviewBlockType.name());
         }
      } catch (OseeCoreException ex) {
         throw new OseeWrappedException(ex);
      }
      return peerRevArt;
   }

   public static TeamWorkFlowArtifact getTeamWf2() {
      ensureLoaded();
      if (teamArt2 == null) {
         IAtsChangeSet changes = AtsClientService.get().createChangeSet(AtsTestUtil.class.getSimpleName());
         ActionResult result =
            AtsClientService.get().getActionFactory().createAction(null, getTitle("Team WF2", postFixName),
               "description", ChangeType.Improvement, "1", false, null, Arrays.asList(testAi2), new Date(),
               AtsClientService.get().getUserService().getCurrentUser(), null, changes);
         actionArt2 = (ActionArtifact) result.getAction().getStoreObject();
         teamArt2 = (TeamWorkFlowArtifact) result.getFirstTeam().getStoreObject();
         changes.execute();
      }
      return teamArt2;
   }

   public static IAtsActionableItem getTestAi2() {
      ensureLoaded();
      return testAi2;
   }

   public static TeamWorkFlowArtifact getTeamWf3() {
      ensureLoaded();
      if (teamArt3 == null) {
         IAtsChangeSet changes = AtsClientService.get().createChangeSet(AtsTestUtil.class.getSimpleName());
         ActionResult result =
            AtsClientService.get().getActionFactory().createAction(null, getTitle("Team WF3", postFixName),
               "description", ChangeType.Improvement, "1", false, null, Arrays.asList(testAi3), new Date(),
               AtsClientService.get().getUserService().getCurrentUser(), null, changes);
         actionArt3 = (ActionArtifact) result.getAction().getStoreObject();
         teamArt3 = (TeamWorkFlowArtifact) result.getFirstTeam().getStoreObject();
         changes.execute();
      }
      return teamArt3;
   }

   public static IAtsActionableItem getTestAi3() {
      ensureLoaded();
      return testAi3;
   }

   public static TeamWorkFlowArtifact getTeamWf4() {
      ensureLoaded();
      if (teamArt4 == null) {
         IAtsChangeSet changes = AtsClientService.get().createChangeSet(AtsTestUtil.class.getSimpleName());
         ActionResult result =
            AtsClientService.get().getActionFactory().createAction(null, getTitle("Team WF4", postFixName),
               "description", ChangeType.Improvement, "1", false, null, Arrays.asList(testAi4), new Date(),
               AtsClientService.get().getUserService().getCurrentUser(), null, changes);
         actionArt4 = (ActionArtifact) result.getAction().getStoreObject();
         teamArt4 = (TeamWorkFlowArtifact) result.getFirstTeam().getStoreObject();
         AtsClientService.get().getVersionService().setTargetedVersion(teamArt4, verArt4, changes);
         changes.execute();
      }
      return teamArt4;
   }

   public static IAtsActionableItem getTestAi4() {
      ensureLoaded();
      return testAi4;
   }

   /**
    * @return 2nd Action with single Team Workflow not tied to other ActionArt or TeamWf
    */
   public static ActionArtifact getActionArt2() {
      ensureLoaded();
      if (actionArt2 == null) {
         getTeamWf2();
      }
      return actionArt2;
   }

   /**
    * @return 3rd Action with single Team Workflow not tied to other ActionArt or TeamWf
    */
   public static ActionArtifact getActionArt3() {
      ensureLoaded();
      if (actionArt3 == null) {
         getTeamWf3();
      }
      return actionArt3;
   }

   /**
    * @return 4rd Action with single Team Workflow not tied to other ActionArt or TeamWf
    */
   public static ActionArtifact getActionArt4() {
      ensureLoaded();
      if (actionArt4 == null) {
         getTeamWf4();
      }
      return actionArt4;
   }

   public static ActionArtifact getActionArt() {
      ensureLoaded();
      return actionArt;
   }

   public static ISelectedAtsArtifacts getSelectedAtsArtifactsForTeamWf() {
      return new ISelectedAtsArtifacts() {

         @Override
         public Set<Artifact> getSelectedWorkflowArtifacts() {
            return Collections.singleton(getTeamWf());
         }

         @Override
         public List<Artifact> getSelectedAtsArtifacts() {
            return Arrays.asList((Artifact) getTeamWf());
         }

         @Override
         public List<TaskArtifact> getSelectedTaskArtifacts() {
            return Collections.emptyList();
         }

      };
   }

   public static Result createWorkingBranchFromTeamWf() {
      configureVer1ForWorkingBranch();
      Result result = AtsBranchUtil.createWorkingBranch_Validate(teamWf);
      if (result.isFalse()) {
         return result;
      }
      AtsBranchUtil.createWorkingBranch_Create(teamWf, true);
      teamWf.getWorkingBranchForceCacheUpdate();
      return Result.TrueResult;
   }

   public static void configureVer1ForWorkingBranch() {
      IAtsVersion version = getVerArt1();
      Artifact verArt = ((Artifact) version.getStoreObject());
      verArt.setSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, true);
      verArt.setSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, true);
      verArt.setSoleAttributeValue(AtsAttributeTypes.BaselineBranchId, SAW_Bld_1.getIdString());
      verArt.setRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow,
         Arrays.asList((Artifact) getTeamWf().getStoreObject()));
      verArt.persist(AtsTestUtil.class.getSimpleName() + "-SetTeamWfTargetedVer1");
      AtsClientService.get().getCache().deCacheAtsObject(version);
   }

   public static String getName() {
      return postFixName;
   }

}
