/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.core.test;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.StateTypeAdapter;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.ats.core.workdef.SimpleDecisionReviewOption;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Test utility that will create a new work definition, team definition, versions and allow tests to easily
 * create/cleanup team workflows, tasks and reviews.
 *
 * @author Donald G. Dunne
 */
public class AtsTestUtilCore {

   private static IAtsTeamWorkflow teamWf, teamArt2, teamArt3, teamArt4;
   private static TeamDefinition teamDef;
   private static Version verArt1, verArt2, verArt3, verArt4;
   private static IAtsDecisionReview decRevArt;
   private static IAtsPeerToPeerReview peerRev;
   private static IAtsTask taskArtWf1, taskArtWf2;
   private static ActionableItem testAi, testAi2, testAi3, testAi4;
   private static IAtsAction actionArt, actionArt2, actionArt3, actionArt4;
   private static StateDefinition analyze, implement, completed, cancelled;
   private static WidgetDefinition estHoursWidgetDef, workPackageWidgetDef;
   private static String postFixName;
   private static Boolean productionDatastore;
   private static AtsUser asUser;

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
      validateObjectsNull("peerRevArt", peerRev);
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

   public static StateDefinition getAnalyzeStateDef() {
      ensureLoaded();
      if (analyze == null) {
         analyze = teamWf.getWorkDefinition().getStateByName("Analyze");
      }
      return analyze;
   }

   public static WidgetDefinition getEstHoursWidgetDef() {
      ensureLoaded();
      if (estHoursWidgetDef == null) {
         for (LayoutItem item : getAnalyzeStateDef().getLayoutItems()) {
            if (item.getName().equals("ats.Estimated Hours")) {
               estHoursWidgetDef = (WidgetDefinition) item;
               break;
            }
         }
      }
      return estHoursWidgetDef;
   }

   public static WidgetDefinition getWorkPackageWidgetDef() {
      ensureLoaded();
      if (workPackageWidgetDef == null) {
         for (LayoutItem item : getAnalyzeStateDef().getLayoutItems()) {
            if (item.getName().equals("ats.Work Package")) {
               workPackageWidgetDef = (WidgetDefinition) item;
               break;
            }
         }
      }
      return workPackageWidgetDef;
   }

   public static StateDefinition getImplementStateDef() {
      ensureLoaded();
      if (implement == null) {
         implement = teamWf.getWorkDefinition().getStateByName("Implement");
      }
      return implement;
   }

   public static StateDefinition getCompletedStateDef() {
      ensureLoaded();
      if (completed == null) {
         completed = teamWf.getWorkDefinition().getStateByName("Completed");
      }
      return completed;
   }

   public static StateDefinition getCancelledStateDef() {
      ensureLoaded();
      if (cancelled == null) {
         cancelled = teamWf.getWorkDefinition().getStateByName("Cancelled");
      }
      return cancelled;
   }

   public static void ensureLoaded() {
      if (teamDef == null) {
         throw new OseeStateException("Must call cleanAndReset before using this method");
      }
   }

   public static void clearCaches() {
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
      peerRev = null;
   }

   public static String getTitle(String objectName, String postFixName) {
      return String.format("%s - %s [%s]", AtsTestUtilCore.class.getSimpleName(), objectName, postFixName);
   }

   /**
    * Clear workDef from cache, clear all objects and create new objects with postFixName in titles
    */
   public static void reset(String postFixName, boolean clearCaches) {
      if (isProductionDataStore()) {
         throw new OseeStateException("AtsTestUtil should not be run on production.");
      }

      AtsTestUtilCore.postFixName = postFixName;

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(AtsTestUtilCore.class.getSimpleName(), getUser());

      IAtsActionableItem topAi =
         AtsApiService.get().getActionableItemService().getActionableItemById(AtsArtifactToken.TopActionableItem);

      testAi = AtsApiService.get().getActionableItemService().createActionableItem(getTitle("AI", postFixName),
         Lib.generateArtifactIdAsInt(), changes);
      changes.setSoleAttributeValue(testAi, AtsAttributeTypes.Active, true);
      changes.setSoleAttributeValue(testAi, AtsAttributeTypes.Actionable, true);
      testAi.setActive(true);
      testAi.setActionable(true);
      changes.addChild(topAi, testAi);

      testAi2 = AtsApiService.get().getActionableItemService().createActionableItem(getTitle("AI2", postFixName),
         Lib.generateArtifactIdAsInt(), changes);
      changes.setSoleAttributeValue(testAi2, AtsAttributeTypes.Active, true);
      changes.setSoleAttributeValue(testAi2, AtsAttributeTypes.Actionable, true);
      testAi2.setActive(true);
      testAi2.setActionable(true);
      changes.addChild(testAi, testAi2);

      testAi3 = AtsApiService.get().getActionableItemService().createActionableItem(getTitle("AI3", postFixName),
         Lib.generateArtifactIdAsInt(), changes);
      changes.setSoleAttributeValue(testAi3, AtsAttributeTypes.Active, true);
      changes.setSoleAttributeValue(testAi3, AtsAttributeTypes.Actionable, true);
      testAi3.setActive(true);
      testAi3.setActionable(true);
      changes.addChild(testAi, testAi3);

      testAi4 = AtsApiService.get().getActionableItemService().createActionableItem(getTitle("AI4", postFixName),
         Lib.generateArtifactIdAsInt(), changes);
      changes.setSoleAttributeValue(testAi4, AtsAttributeTypes.Active, true);
      changes.setSoleAttributeValue(testAi4, AtsAttributeTypes.Actionable, true);
      testAi4.setActive(true);
      testAi4.setActionable(true);
      changes.addChild(testAi, testAi4);

      teamDef = AtsApiService.get().getTeamDefinitionService().createTeamDefinition(getTitle("Team Def", postFixName),
         Lib.generateArtifactIdAsInt(), changes);
      AtsApiService.get().getConfigService().getConfigurations().getIdToTeamDef().put(teamDef.getId(), teamDef);

      // All tests use the same Work Definition so it doesn't have to be re-created and imported each time
      AtsApiService.get().getWorkDefinitionService().setWorkDefinitionAttrs(teamDef,
         AtsWorkDefinitionTokens.WorkDef_Team_Simple_Analyze, changes);

      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.Active, true);
      changes.relate(teamDef, AtsRelationTypes.TeamLead_Lead, getUser());
      changes.relate(teamDef, AtsRelationTypes.TeamActionableItem_ActionableItem, testAi);
      changes.relate(teamDef, AtsRelationTypes.TeamActionableItem_ActionableItem, testAi2);
      changes.relate(teamDef, AtsRelationTypes.TeamActionableItem_ActionableItem, testAi3);
      changes.relate(teamDef, AtsRelationTypes.TeamActionableItem_ActionableItem, testAi4);
      testAi.setTeamDefId(teamDef.getId());
      testAi2.setTeamDefId(teamDef.getId());
      testAi3.setTeamDefId(teamDef.getId());
      testAi4.setTeamDefId(teamDef.getId());
      IAtsTeamDefinition topTeamDef =
         AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(AtsArtifactToken.TopTeamDefinition);
      changes.addChild(topTeamDef, teamDef);

      verArt1 = AtsApiService.get().getVersionService().createVersion(getTitle("ver 1.0", postFixName),
         Lib.generateArtifactIdAsInt(), changes);
      changes.relate(teamDef, AtsRelationTypes.TeamDefinitionToVersion_Version, verArt1);
      verArt1.setTeamDefId(teamDef.getId());
      teamDef.getVersions().add(verArt1.getId());

      verArt2 = AtsApiService.get().getVersionService().createVersion(getTitle("ver 2.0", postFixName),
         Lib.generateArtifactIdAsInt(), changes);
      changes.relate(teamDef, AtsRelationTypes.TeamDefinitionToVersion_Version, verArt2);
      verArt2.setTeamDefId(teamDef.getId());
      teamDef.getVersions().add(verArt2.getId());

      verArt3 = AtsApiService.get().getVersionService().createVersion(getTitle("ver 3.0", postFixName),
         Lib.generateArtifactIdAsInt(), changes);
      changes.relate(teamDef, AtsRelationTypes.TeamDefinitionToVersion_Version, verArt3);
      verArt3.setTeamDefId(teamDef.getId());
      teamDef.getVersions().add(verArt3.getId());

      verArt4 = AtsApiService.get().getVersionService().createVersion(getTitle("ver 4.0", postFixName),
         Lib.generateArtifactIdAsInt(), changes);
      changes.relate(teamDef, AtsRelationTypes.TeamDefinitionToVersion_Version, verArt4);
      verArt4.setTeamDefId(teamDef.getId());
      teamDef.getVersions().add(verArt4.getId());

      ActionResult result =
         AtsApiService.get().getActionService().createAction(null, getTitle("Team WF", postFixName), "description",
            ChangeTypes.Improvement, "1", false, null, Arrays.asList(testAi), new Date(), getUser(), null, changes);

      actionArt = AtsApiService.get().getWorkItemService().getAction(result.getAction().getStoreObject());
      teamWf = AtsApiService.get().getWorkItemService().getTeamWf(result.getFirstTeam().getStoreObject());

      changes.execute();

      if (clearCaches) {
         AtsApiService.get().reloadServerAndClientCaches();
      }
   }

   private static AtsUser getUser() {
      if (asUser == null) {
         return AtsApiService.get().getUserService().getCurrentUser();
      }
      return asUser;
   }

   public static boolean isProductionDataStore() {
      if (productionDatastore == null) {
         productionDatastore = AtsApiService.get().getStoreService().isProductionDb();
      }
      return productionDatastore;
   }

   public static IAtsTask getOrCreateTaskOffTeamWf1() {
      ensureLoaded();
      if (taskArtWf1 == null) {
         NewTaskData newTaskData = NewTaskData.create(teamWf, Arrays.asList(getTitle("Task", postFixName)), null,
            new Date(), getUser(), teamWf.getCurrentStateName(), null, null);
         NewTaskSet newTaskSet = NewTaskSet.create(newTaskData, getName() + " Create Task", getUser().getUserId());
         newTaskSet = AtsApiService.get().getTaskService().createTasks(newTaskSet);

         taskArtWf1 = (IAtsTask) newTaskSet.getTaskData().getTasks().iterator().next().getStoreObject();
      }
      return taskArtWf1;
   }

   public static IAtsDecisionReview getOrCreateDecisionReview(ReviewBlockType reviewBlockType,
      AtsTestUtilState relatedToState, IAtsChangeSet changes) {
      ensureLoaded();
      if (decRevArt == null) {
         List<IAtsDecisionReviewOption> options = new ArrayList<>();
         options.add(new SimpleDecisionReviewOption(DecisionReviewState.Completed.getName(), false, null));
         options.add(new SimpleDecisionReviewOption(DecisionReviewState.Followup.getName(), true,
            Arrays.asList(getUser().getUserId())));
         decRevArt = (IAtsDecisionReview) AtsApiService.get().getReviewService().createNewDecisionReview(teamWf,
            reviewBlockType, AtsTestUtilCore.class.getSimpleName() + " Test Decision Review", relatedToState.getName(),
            "Decision Review", options, Arrays.asList(getUser()), new Date(), getUser(), changes).getStoreObject();
      }
      return decRevArt;
   }

   public static IAtsTeamWorkflow getTeamWf() {
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
      cleanupAndReset(name, false);
   }

   /**
    * All team defs, AIs, action and workflows will be deleted and new ones created with "name" as part of object
    * names/titles. In addition, ArtifactCache will validate that it is not dirty or display errors if it is.
    */
   public static void cleanupAndReset(String name, boolean clearCaches) {
      cleanup();
      reset(name, clearCaches);
   }

   public static void cleanupAndReset(String name, boolean clearCaches, AtsUser asUser) {
      AtsTestUtilCore.asUser = asUser;
      cleanup();
      reset(name, clearCaches);
   }

   public static void delete(IAtsChangeSet changes, Object artifact) {
      if (artifact != null) {
         changes.addToDelete(artifact);
      }
   }

   public static void deleteTeamWf(IAtsTeamWorkflow teamWfToDelete) {
      if (teamWfToDelete != null) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(
            AtsTestUtilCore.class.getSimpleName() + " - cleanup deleteTeamWf", getUser());

         BranchToken workingBranch = AtsApiService.get().getBranchService().getWorkingBranch(teamWfToDelete);
         if (workingBranch.isValid()) {
            XResultData result = AtsApiService.get().getBranchService().deleteBranch(workingBranch);
            if (result.isErrors()) {
               throw new OseeStateException("Error deleting working branch [%s]", result.toString());
            }
         }
         for (IAtsTask task : AtsApiService.get().getTaskService().getTasks(teamWfToDelete)) {
            changes.addToDelete(task);
         }
         for (IAtsAbstractReview revArt : AtsApiService.get().getReviewService().getReviews(teamWfToDelete)) {
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
      if (teamWf != null) {
         BranchId workingBranch = AtsApiService.get().getBranchService().getWorkingBranch(teamWf);
         if (workingBranch.isValid()) {
            AtsApiService.get().getBranchService().deleteBranch(workingBranch);
         }
      }

      IAtsChangeSet changes =
         AtsApiService.get().createChangeSet(AtsTestUtilCore.class.getSimpleName() + " - cleanup 1", getUser());
      delete(changes, peerRev);
      delete(changes, decRevArt);
      delete(changes, taskArtWf1);
      delete(changes, taskArtWf2);
      delete(changes, actionArt);
      delete(changes, actionArt2);
      delete(changes, actionArt3);
      delete(changes, actionArt4);
      if (verArt1 != null) {
         delete(changes, verArt1);
      }
      if (verArt2 != null) {
         delete(changes, verArt2);
      }
      if (verArt3 != null) {
         delete(changes, verArt3);
      }
      if (verArt4 != null) {
         delete(changes, verArt4);
      }
      if (!changes.isEmpty()) {
         changes.execute();
      }

      if (teamWf != null) {
         BranchToken branch = AtsApiService.get().getBranchService().getBranch(teamWf);
         if (branch.isValid()) {
            AtsApiService.get().getBranchService().setAssociatedArtId(branch, ArtifactId.SENTINEL);
            XResultData rd = AtsApiService.get().getBranchService().deleteBranch(branch);
            if (rd.isErrors()) {
               throw new OseeCoreException(rd.toString());
            }
         }
      }

      deleteTeamWf(teamWf);
      deleteTeamWf(teamArt2);
      deleteTeamWf(teamArt3);
      deleteTeamWf(teamArt4);

      changes =
         AtsApiService.get().createChangeSet(AtsTestUtilCore.class.getSimpleName() + " - cleanup config", getUser());
      for (IAtsConfigObject config : Arrays.asList(teamDef, testAi, testAi2, testAi3, testAi4)) {
         if (config != null) {
            changes.deleteArtifact(config.getStoreObject());
         }
      }
      changes.executeIfNeeded();

      clearCaches();

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
      List<ArtifactToken> artifacts = new ArrayList<>();
      for (String title : titles) {
         artifacts.addAll(AtsApiService.get().getQueryService().getArtifactsFromName(title,
            AtsApiService.get().getAtsBranch(), EXCLUDE_DELETED, QueryOption.CONTAINS_MATCH_OPTIONS));
      }
      AtsApiService.get().getStoreService().purgeArtifacts(artifacts);
   }

   public static Result transitionTo(AtsTestUtilState atsTestUtilState, AtsUser user,
      TransitionOption... transitionOptions) {
      return transitionTo(teamWf, atsTestUtilState, user, transitionOptions);
   }

   public static Result transitionTo(IAtsTeamWorkflow teamWf, AtsTestUtilState atsTestUtilState, AtsUser user,
      TransitionOption... transitionOptions) {
      if (atsTestUtilState == AtsTestUtilState.Analyze && teamWf.getStateMgr().isInState(AtsTestUtilState.Analyze)) {
         return Result.TrueResult;
      }

      if (atsTestUtilState == AtsTestUtilState.Cancelled) {
         Result result = transitionToState(teamWf, AtsTestUtilState.Cancelled, user, transitionOptions);
         if (result.isFalse()) {
            return result;
         }
         return Result.TrueResult;
      }

      Result result = transitionToState(teamWf, AtsTestUtilState.Implement, user, transitionOptions);
      if (result.isFalse()) {
         return result;
      }

      if (atsTestUtilState == AtsTestUtilState.Implement) {
         return Result.TrueResult;
      }

      if (atsTestUtilState == AtsTestUtilState.Completed) {
         result = transitionToState(teamWf, AtsTestUtilState.Completed, user, transitionOptions);
         if (result.isFalse()) {
            return result;
         }

      }
      return Result.TrueResult;

   }

   public static Result transitionToState(IAtsTeamWorkflow teamWf, IStateToken toState, AtsUser user,
      TransitionOption... transitionOptions) {
      TransitionData transData = new TransitionData("Transition to " + toState.getName(), Arrays.asList(teamWf),
         toState.getName(), Arrays.asList(user), null, null, transitionOptions);
      TransitionResults results = AtsApiService.get().getWorkItemService().transition(transData);
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

      public AtsTestUtilState(String pageName, StateType StateType) {
         super(AtsTestUtilState.class, pageName, StateType);
      }

      public static AtsTestUtilState valueOf(String pageName) {
         return StateTypeAdapter.valueOfPage(AtsTestUtilState.class, pageName);
      }

      public static List<AtsTestUtilState> values() {
         return StateTypeAdapter.pages(AtsTestUtilState.class);
      }
   }

   public static IAtsPeerToPeerReview getOrCreatePeerReview(ReviewBlockType reviewBlockType,
      AtsTestUtilState relatedToState, IAtsChangeSet changes) {
      ensureLoaded();
      try {
         if (peerRev == null) {
            peerRev = AtsApiService.get().getReviewService().createNewPeerToPeerReview(
               AtsApiService.get().getWorkDefinitionService().getDefaultPeerToPeerWorkflowDefinition(), teamWf,
               AtsTestUtilCore.class.getSimpleName() + " Test Peer Review", relatedToState.getName(), changes);
            changes.setSoleAttributeValue(peerRev, AtsAttributeTypes.ReviewBlocks, reviewBlockType.name());
         }
      } catch (OseeCoreException ex) {
         throw OseeCoreException.wrap(ex);
      }
      return peerRev;
   }

   public static IAtsTeamWorkflow getTeamWf2() {
      ensureLoaded();
      if (teamArt2 == null) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(AtsTestUtilCore.class.getSimpleName());
         ActionResult result =
            AtsApiService.get().getActionService().createAction(null, getTitle("Team WF2", postFixName), "description",
               ChangeTypes.Improvement, "1", false, null, Arrays.asList(testAi2), new Date(), getUser(), null, changes);
         actionArt2 = (IAtsAction) result.getAction().getStoreObject();
         teamArt2 = (IAtsTeamWorkflow) result.getFirstTeam().getStoreObject();
         changes.execute();
      }
      return teamArt2;
   }

   public static IAtsActionableItem getTestAi2() {
      ensureLoaded();
      return testAi2;
   }

   public static IAtsTeamWorkflow getTeamWf3() {
      ensureLoaded();
      if (teamArt3 == null) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(AtsTestUtilCore.class.getSimpleName());
         ActionResult result =
            AtsApiService.get().getActionService().createAction(null, getTitle("Team WF3", postFixName), "description",
               ChangeTypes.Improvement, "1", false, null, Arrays.asList(testAi3), new Date(), getUser(), null, changes);
         actionArt3 = (IAtsAction) result.getAction().getStoreObject();
         teamArt3 = (IAtsTeamWorkflow) result.getFirstTeam().getStoreObject();
         changes.execute();
      }
      return teamArt3;
   }

   public static IAtsActionableItem getTestAi3() {
      ensureLoaded();
      return testAi3;
   }

   public static IAtsTeamWorkflow getTeamWf4() {
      ensureLoaded();
      if (teamArt4 == null) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(AtsTestUtilCore.class.getSimpleName());
         ActionResult result =
            AtsApiService.get().getActionService().createAction(null, getTitle("Team WF4", postFixName), "description",
               ChangeTypes.Improvement, "1", false, null, Arrays.asList(testAi4), new Date(), getUser(), null, changes);
         actionArt4 = (IAtsAction) result.getAction().getStoreObject();
         teamArt4 = (IAtsTeamWorkflow) result.getFirstTeam().getStoreObject();
         AtsApiService.get().getVersionService().setTargetedVersion(teamArt4, verArt4, changes);
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
   public static IAtsAction getActionArt2() {
      ensureLoaded();
      if (actionArt2 == null) {
         getTeamWf2();
      }
      return actionArt2;
   }

   /**
    * @return 3rd Action with single Team Workflow not tied to other ActionArt or TeamWf
    */
   public static IAtsAction getActionArt3() {
      ensureLoaded();
      if (actionArt3 == null) {
         getTeamWf3();
      }
      return actionArt3;
   }

   /**
    * @return 4rd Action with single Team Workflow not tied to other ActionArt or TeamWf
    */
   public static IAtsAction getActionArt4() {
      ensureLoaded();
      if (actionArt4 == null) {
         getTeamWf4();
      }
      return actionArt4;
   }

   public static IAtsAction getActionArt() {
      ensureLoaded();
      return actionArt;
   }

   public static void configureVer1ForWorkingBranch() {
      IAtsVersion version = getVerArt1();
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("configureVer1ForWorkingBranch");
      changes.setSoleAttributeValue(version, AtsAttributeTypes.AllowCreateBranch, true);
      changes.setSoleAttributeValue(version, AtsAttributeTypes.AllowCommitBranch, true);
      changes.setSoleAttributeValue(version, AtsAttributeTypes.BaselineBranchId, SAW_Bld_1.getIdString());
      changes.setRelations(version, AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow,
         Arrays.asList(teamWf));
      changes.execute();
   }

   public static String getName() {
      return postFixName;
   }

   public static boolean hasTeamWf() {
      return teamWf != null;
   }

   public static void clearVersions() {
      verArt1 = null;
      verArt2 = null;
      verArt3 = null;
      verArt4 = null;
   }

}
