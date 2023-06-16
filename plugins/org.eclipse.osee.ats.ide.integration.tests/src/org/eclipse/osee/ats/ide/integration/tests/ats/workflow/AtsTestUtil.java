/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.test.AtsTestUtilCore;
import org.eclipse.osee.ats.core.test.AtsTestUtilCore.AtsTestUtilState;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskEditor;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * Test utility that will create a new work definition, team definition, versions and allow tests to easily
 * create/cleanup team workflows, tasks and reviews.
 *
 * @author Donald G. Dunne
 */
public class AtsTestUtil {

   public static AtsTestUtilState Analyze = new AtsTestUtilState("Analyze", StateType.Working);
   public static AtsTestUtilState Implement = new AtsTestUtilState("Implement", StateType.Working);
   public static AtsTestUtilState Completed = new AtsTestUtilState("Completed", StateType.Completed);
   public static AtsTestUtilState Cancelled = new AtsTestUtilState("Cancelled", StateType.Cancelled);

   public static void validateArtifactCache() {
      final Collection<Artifact> dirtyArtifacts = ArtifactCache.getDirtyArtifacts();
      if (!dirtyArtifacts.isEmpty()) {
         XResultData results = new XResultData();
         results.log("\n");
         for (Artifact artifact : dirtyArtifacts) {
            results.errorf("Dirty Artifact [%s] attribute [%s] \n%s", artifact.toStringWithId(),
               Artifacts.getDirtyReport(artifact), results.toString());
         }
         throw new OseeStateException("[%d] Dirty Artifacts found after populate [%s]", dirtyArtifacts.size(), results);
      }
   }

   public static void validateObjectsNull() {
      AtsTestUtilCore.validateObjectsNull();
   }

   public static StateDefinition getAnalyzeStateDef() {
      return AtsTestUtilCore.getAnalyzeStateDef();
   }

   public static WidgetDefinition getEstHoursWidgetDef() {
      return AtsTestUtilCore.getEstHoursWidgetDef();
   }

   public static WidgetDefinition getWorkPackageWidgetDef() {
      return AtsTestUtilCore.getWorkPackageWidgetDef();
   }

   public static StateDefinition getImplementStateDef() {
      return AtsTestUtilCore.getImplementStateDef();
   }

   public static StateDefinition getCompletedStateDef() {
      return AtsTestUtilCore.getCompletedStateDef();
   }

   public static StateDefinition getCancelledStateDef() {
      return AtsTestUtilCore.getCancelledStateDef();
   }

   private static void clearCaches() {
      AtsTestUtilCore.clearCaches();
   }

   public static TaskArtifact getOrCreateTaskOffTeamWf1() {
      return (TaskArtifact) AtsTestUtilCore.getOrCreateTaskOffTeamWf1();
   }

   public static DecisionReviewArtifact getOrCreateDecisionReview(ReviewBlockType reviewBlockType,
      AtsTestUtilState relatedToState, IAtsChangeSet changes) {
      return (DecisionReviewArtifact) AtsTestUtilCore.getOrCreateDecisionReview(reviewBlockType, relatedToState,
         changes);
   }

   public static TeamWorkFlowArtifact getTeamWf() {
      return (TeamWorkFlowArtifact) AtsTestUtilCore.getTeamWf();
   }

   public static IAtsActionableItem getTestAi() {
      return AtsTestUtilCore.getTestAi();
   }

   public static IAtsTeamDefinition getTestTeamDef() {
      return AtsTestUtilCore.getTestTeamDef();
   }

   /**
    * All team defs, AIs, action and workflows will be deleted and new ones created with "name" as part of object
    * names/titles. In addition, ArtifactCache will validate that it is not dirty or display errors if it is.
    */
   public static void cleanupAndReset(String name) {
      AtsTestUtilCore.cleanupAndReset(name, false);
   }

   /**
    * All team defs, AIs, action and workflows will be deleted and new ones created with "name" as part of object
    * names/titles. In addition, ArtifactCache will validate that it is not dirty or display errors if it is.
    */
   public static void cleanupAndReset(String name, boolean clearCaches) {
      AtsTestUtilCore.cleanupAndReset(name, clearCaches);
   }

   /**
    * Cleanup all artifacts and confirm that ArtifactCache has no dirty artifacts. Should be called at beginning at end
    * of each test.
    */
   public static void cleanup() {
      WorldEditor.closeAll();
      WorkflowEditor.closeAll();
      TaskEditor.closeAll();

      if (AtsTestUtilCore.hasTeamWf()) {
         BranchId workingBranch = getTeamWf().getWorkingBranch();
         if (workingBranch.isValid()) {
            BranchManager.deleteBranchAndPend(workingBranch);
         }
      }

      AtsTestUtilCore.cleanup();

      clearCaches();

      // validate that there are no dirty artifacts in cache
      AtsTestUtil.validateArtifactCache();
   }

   public static IAtsVersion getVerArt1() {
      return AtsTestUtilCore.getVerArt1();
   }

   public static IAtsVersion getVerArt2() {
      return AtsTestUtilCore.getVerArt2();
   }

   public static IAtsVersion getVerArt3() {
      return AtsTestUtilCore.getVerArt3();
   }

   public static IAtsVersion getVerArt4() {
      return AtsTestUtilCore.getVerArt4();
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
         artifacts.addAll(ArtifactQuery.getArtifactListFromName(title, AtsApiService.get().getAtsBranch(),
            EXCLUDE_DELETED, QueryOption.CONTAINS_MATCH_OPTIONS));
      }
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artifacts));
   }

   public static Result transitionTo(AtsTestUtilState atsTestUtilState, AtsUser user,
      TransitionOption... transitionOptions) {
      return AtsTestUtilCore.transitionTo(atsTestUtilState, user, transitionOptions);
   }

   public static Result transitionTo(IAtsTeamWorkflow teamWf, AtsTestUtilState atsTestUtilState, AtsUser user,
      TransitionOption... transitionOptions) {
      return AtsTestUtilCore.transitionTo(teamWf, atsTestUtilState, user, transitionOptions);
   }

   public static IAtsPeerToPeerReview getOrCreatePeerReview(ReviewBlockType reviewBlockType,
      AtsTestUtilState relatedToState, IAtsChangeSet changes) {
      return AtsTestUtilCore.getOrCreatePeerReview(reviewBlockType, relatedToState, changes);
   }

   public static TeamWorkFlowArtifact getTeamWf2() {
      return (TeamWorkFlowArtifact) AtsTestUtilCore.getTeamWf2();
   }

   public static IAtsActionableItem getTestAi2() {
      return AtsTestUtilCore.getTestAi2();
   }

   public static TeamWorkFlowArtifact getTeamWf3() {
      return (TeamWorkFlowArtifact) AtsTestUtilCore.getTeamWf3();
   }

   public static IAtsActionableItem getTestAi3() {
      return AtsTestUtilCore.getTestAi3();
   }

   public static TeamWorkFlowArtifact getTeamWf4() {
      return (TeamWorkFlowArtifact) AtsTestUtilCore.getTeamWf4();
   }

   public static IAtsActionableItem getTestAi4() {
      return AtsTestUtilCore.getTestAi4();
   }

   /**
    * @return 2nd Action with single Team Workflow not tied to other ActionArt or TeamWf
    */
   public static IAtsAction getActionArt2() {
      return AtsTestUtilCore.getActionArt2();
   }

   /**
    * @return 3rd Action with single Team Workflow not tied to other ActionArt or TeamWf
    */
   public static IAtsAction getActionArt3() {
      return AtsTestUtilCore.getActionArt3();
   }

   /**
    * @return 4rd Action with single Team Workflow not tied to other ActionArt or TeamWf
    */
   public static IAtsAction getActionArt4() {
      return AtsTestUtilCore.getActionArt4();
   }

   public static IAtsAction getActionArt() {
      return AtsTestUtilCore.getActionArt();
   }

   public static Result createWorkingBranchFromTeamWf() {
      TeamWorkFlowArtifact teamWf = getTeamWf();
      configureVer1ForWorkingBranch();

      AtsApiService.get().reloadServerAndClientCaches();
      AtsApiService.get().clearCaches();

      Result result = AtsApiService.get().getBranchServiceIde().createWorkingBranch_Validate(teamWf);
      if (result.isFalse()) {
         return result;
      }
      AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(teamWf, true);
      teamWf.getWorkingBranchForceCacheUpdate();
      return Result.TrueResult;
   }

   public static void configureVer1ForWorkingBranch() {
      AtsTestUtilCore.configureVer1ForWorkingBranch();
      ((Artifact) AtsTestUtilCore.getVerArt1().getStoreObject()).reloadAttributesAndRelations();
   }

   public static String getName() {
      return AtsTestUtilCore.getName();
   }

   public static ISelectedAtsArtifacts getSelectedAtsArtifactsForTeamWf() {
      return new ISelectedAtsArtifacts() {

         @Override
         public Set<Artifact> getSelectedWorkflowArtifacts() {
            return Collections.singleton(getTeamWf());
         }

         @Override
         public List<Artifact> getSelectedAtsArtifacts() {
            return Arrays.asList(AtsApiService.get().getQueryServiceIde().getArtifact(getTeamWf()));
         }

         @Override
         public List<TaskArtifact> getSelectedTaskArtifacts() {
            return Collections.emptyList();
         }

      };
   }

   public static void clearVersions() {
      AtsTestUtilCore.clearVersions();
   }

}
