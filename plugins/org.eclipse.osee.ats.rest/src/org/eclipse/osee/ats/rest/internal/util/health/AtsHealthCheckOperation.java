/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.rest.internal.util.health;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsOperationCache;
import org.eclipse.osee.ats.api.util.health.HealthCheckResults;
import org.eclipse.osee.ats.api.util.health.IAtsHealthCheck;
import org.eclipse.osee.ats.api.util.health.IAtsHealthCheckProvider;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.rest.internal.notify.OseeEmailServer;
import org.eclipse.osee.ats.rest.internal.util.AtsOperationCache;
import org.eclipse.osee.ats.rest.internal.util.health.check.AtsHealthQueries;
import org.eclipse.osee.ats.rest.internal.util.health.check.TestDuplicateAttributesWithPersist;
import org.eclipse.osee.ats.rest.internal.util.health.check.TestWorkflowVersions;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.framework.core.util.OseeEmail.BodyType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsHealthCheckOperation {

   private final AtsApi atsApi;
   private final JdbcService jdbcService;
   boolean inTest = false;
   boolean persist = false;
   boolean debug = false; // Set to true to enable debugging; false for commit/production
   private Set<Long> aiIds;
   private IAtsOperationCache cache;
   private HealthCheckResults vResults;
   private final OrcsApi orcsApi;

   public AtsHealthCheckOperation(OrcsApi orcsApi, AtsApi atsApi, JdbcService jdbcService) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
      this.jdbcService = jdbcService;
   }

   /**
    * Health checks can have caches to improve performance. Create new checks to ensure caches are cleared.
    */
   private List<IAtsHealthCheck> getHealthChecks() {
      List<IAtsHealthCheck> healthChecks = new LinkedList<>();

      // These load cache, do first
      healthChecks.add(new TestTeamDefinitionsLoad());
      healthChecks.add(new TestActionableItemsLoad());
      healthChecks.add(new TestWorkflowTeamDefinition());
      healthChecks.add(new TestWorkflowVersions());
      healthChecks.add(new TestWorkflowDefinition());
      healthChecks.add(new TestStateMgrAndDupStates());
      healthChecks.add(new TestCurrentStateIsInWorkDef());
      healthChecks.add(new TestWorkflowHasAction());
      healthChecks.add(new TestTeamDefinitionsBaslineBranch());
      healthChecks.add(new TestTeamDefinitionsWorkDefRef());
      healthChecks.add(new TestTeamDefinitionsProgram());
      healthChecks.add(new TestActionableItemsTeamDefRef());
      healthChecks.add(new TestActionableItemsProgram());
      healthChecks.add(new TestVersions());
      healthChecks.add(new TestTeamWorkflows());
      healthChecks.add(new TestBranches());
      healthChecks.add(new TestDuplicateAttributesWithPersist());
      healthChecks.add(new TestDuplicateArtEntries());
      healthChecks.add(new TestReviews());
      healthChecks.add(new TestTasks());

      for (IAtsHealthCheckProvider provider : AtsHealthCheckProviderService.getHealthCheckProviders()) {
         healthChecks.addAll(provider.getHealthChecks());
      }
      return healthChecks;
   }

   public XResultData run() {
      cache = new AtsOperationCache(atsApi, debug);
      XResultData rd = new XResultData();

      String configDbName = atsApi.getConfigValue("DatabaseName");
      if (Strings.isInValid(configDbName)) {
         configDbName = "unknown AtsConfig.DatabaseName";
      }
      configDbName = configDbName.toUpperCase();
      rd.log(AHTML.heading(4, configDbName + " - ATS Health Check"));
      aiIds = atsApi.getConfigService().getConfigurations().getIdToAi().keySet();
      try {
         ElapsedTime time = new ElapsedTime("ATS Health Check", false, true);
         runIt(rd);
         cache = null;
         String elapsedStr = time.end(Units.MIN);
         rd.log("\n\n" + elapsedStr);
         outputResults(rd);
      } catch (Exception ex) {
         rd.errorf("Exception running reports [%s]", Lib.exceptionToString(ex));
      }
      return rd;
   }

   public void runIt(XResultData rd) {

      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);

      int count = 0;
      vResults = new HealthCheckResults();
      if (inTest) {
         vResults.log("testMap1", "blah blah");
         vResults.log("testMap2", "blah blah");
         vResults.log("testMap1", "blah blah");
         vResults.log("testMap3", "blah blah");
      } else {
         List<IAtsHealthCheck> checks = getHealthChecks();

         IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());

         handleCheckBefores(checks, changes);
         count = handleChecks(rd, count, checks, changes);
         handleCheckAfters(checks);

         if (persist) {
            changes.executeIfNeeded();
         }
         // Throw away any caches
         checks = null;
         cache = null;
         for (IAtsHealthCheckProvider provider : AtsHealthCheckProviderService.getHealthCheckProviders()) {
            provider.clearCaches();
         }
      }
      // Log resultMap data into xResultData
      vResults.addResultsMapToResultData(rd);
      vResults.addTestTimeMapToResultData(rd);

      rd.logf("Completed processing %s open work items.", count);
   }

   private void handleCheckBefores(List<IAtsHealthCheck> checks, IAtsChangeSet changes) {
      // Run single run health checks and allow for caching queries
      for (IAtsHealthCheck check : checks) {
         Date date = new Date();
         boolean done = check.checkBefore(vResults, atsApi, cache);
         if (done) {
            if (debug) {
               System.err.println(String.format("Processing CheckBefore - %s", check.getName()));
            }
            vResults.logTestTimeSpent(date, "CheckBefore - " + check.getName());
         }
      }
   }

   private int handleChecks(XResultData rd, int count, List<IAtsHealthCheck> checks, IAtsChangeSet changes) {
      // Break artifacts into blocks so don't run out of memory
      List<Collection<Long>> artIdLists = loadWorkingWorkItemIds(rd);
      // Uncomment and set ids to run singles
      //         List<Collection<Long>> artIdLists = Arrays.asList(Arrays.asList(8661508L, 8661509L));
      int x = 1;
      for (Collection<Long> artIdList : artIdLists) {
         if (debug) {
            System.err.println(String.format("Processing art check set %s/%s", x++, artIdLists.size()));
         }

         Date date = new Date();
         Collection<ArtifactToken> allArtifacts = atsApi.getQueryService().getArtifacts(artIdList);
         vResults.logTestTimeSpent(date, "Load Artifacts");

         // remove all deleted/purged artifacts first
         List<ArtifactToken> artifacts = new ArrayList<>(allArtifacts.size());
         for (ArtifactToken artifact : allArtifacts) {
            if (!atsApi.getStoreService().isDeleted(artifact)) {
               artifacts.add(artifact);
            }
         }
         count += artifacts.size();

         for (ArtifactToken artifact : artifacts) {
            if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               cache.addTeamWf(artifact);
            }
         }

         for (ArtifactToken artifact : artifacts) {
            for (IAtsHealthCheck check : checks) {
               try {
                  date = new Date();
                  if (atsApi.getStoreService().isDeleted(artifact)) {
                     continue;
                  }
                  IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(artifact);
                  if (workItem.isTask()) {
                     cache.addTask((IAtsTask) workItem);
                  }
                  boolean done = check.check(artifact, workItem, vResults, atsApi, (persist ? changes : null), cache);
                  if (done) {
                     vResults.logTestTimeSpent(date, check.getName());
                  }
               } catch (Exception ex) {
                  vResults.log(artifact, check.getName(), "Error: Exception: " + Lib.exceptionToString(ex));
               }
            }
         }
      }
      return count;
   }

   private void handleCheckAfters(List<IAtsHealthCheck> checks) {
      // Run post checks.  Good for items that need other checks to cache data for performance reasons
      for (IAtsHealthCheck check : checks) {
         Date date = new Date();
         boolean done = check.checkAfter(vResults, atsApi, cache);
         if (done) {
            if (debug) {
               System.err.println(String.format("Processing CheckAfter - %s", check.getName()));
            }
            vResults.logTestTimeSpent(date, "CheckAfter - " + check.getName());
         }
      }
   }

   private class TestTasks implements IAtsHealthCheck {

      /**
       * checkAfter all other checks so parent TeamWfs are cached
       */
      @Override
      public boolean checkAfter(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {
         Collection<IAtsTask> tasks = cache.getTasks().values();
         int x = 1;
         int size = tasks.size();
         for (IAtsTask task : tasks) {
            if (debug) {
               System.err.println("Processing task " + x++ + "/" + size);
            }
            IAtsTeamWorkflow parent = cache.getParentTeamWorkflow(task, results);
            if (parent == null) {
               results.log(task.getStoreObject(), "TestTasks",
                  String.format("Error: Open task with no parent %s", task.toStringWithId()));
            } else if (parent != null) {
               if (parent.isCompletedOrCancelled()) {
                  results.log(task.getStoreObject(), "TestTasks",
                     String.format("Error: Open task with comp/cancel parent %s", task.toStringWithId()));
               }
            }
         }
         return true;
      }

   }

   private class TestReviews implements IAtsHealthCheck {

      @Override
      public boolean checkAfter(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {
         Collection<IAtsAbstractReview> reviews = cache.getReviews().values();
         int x = 1;
         int size = reviews.size();
         for (IAtsAbstractReview review : reviews) {
            if (debug) {
               System.err.println(String.format("Processing review %s/%s", x++, size));
            }
            if (review.isStandAloneReview()) {
               return true;
            }
            IAtsTeamWorkflow parent = cache.getParentTeamWorkflow(review, results);
            if (parent != null) {
               if (parent.isCompletedOrCancelled()) {
                  results.log(review.getStoreObject(), "TestOpenReviewsClosedCancelledParents",
                     String.format("Error: Open review with comp/cancel parent %s", review.toStringWithId()));
               }
            }
         }
         return true;
      }

   }

   private class TestWorkflowHasAction implements IAtsHealthCheck {

      @Override
      public boolean checkBefore(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {
         for (ArtifactToken art : orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.TeamWorkflow).andRelationNotExists(
               AtsRelationTypes.ActionToWorkflow_TeamWorkflow).getResults().getList()) {
            results.log(art, "TestWorkflowHasAction",
               String.format("Error: Workflow %s has no parent Action", art.toStringWithId()));
         }
         return true;
      }

   }

   private class TestBranches implements IAtsHealthCheck {

      @Override
      public boolean checkAfter(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {
         for (Branch workingBranch : orcsApi.getQueryFactory().branchQuery().andIsOfType(BranchType.WORKING).andStateIs(
            BranchState.COMMITTED).excludeArchived().getResults().getList()) {
            ArtifactId assocArt = workingBranch.getAssociatedArtifact();
            if (assocArt.isValid()) {
               IAtsTeamWorkflow teamWf = atsApi.getQueryService().getTeamWf(assocArt);
               if (teamWf == null) {
                  results.log("TestBranches", String.format("Error: Unexpected Assoc Id [%s] for Working Branch %s",
                     assocArt.getIdString(), workingBranch.toStringWithId()));
               } else {
                  if (!atsApi.getBranchService().getBranchState(workingBranch).isCommitted()) {
                     Collection<BranchToken> branchesCommittedTo =
                        atsApi.getBranchService().getBranchesCommittedTo(teamWf);
                     if (!branchesCommittedTo.isEmpty()) {
                        results.log(teamWf.getStoreObject(), "TestBranches",
                           "Error: TeamWorkflow " + teamWf.getAtsId() + " has committed branches but working branch [" + workingBranch + "] != COMMITTED");
                     }
                  } else if (!atsApi.getBranchService().isArchived(workingBranch)) {
                     Collection<BranchId> branchesLeftToCommit =
                        atsApi.getBranchService().getBranchesLeftToCommit(teamWf);
                     if (branchesLeftToCommit.isEmpty()) {
                        results.log(teamWf.getStoreObject(), "TestBranches",
                           "Error: TeamWorkflow " + teamWf.getAtsId() + " has committed all branches but working branch [" + workingBranch + "] != ARCHIVED");
                     }
                  }
               }
            }
         }
         return true;
      }

   }

   private class TestTeamWorkflows implements IAtsHealthCheck {

      @Override
      public boolean check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes, IAtsOperationCache cache) {
         if (workItem.isTeamWorkflow()) {
            IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
            try {
               if (!atsApi.getActionableItemService().hasActionableItems(teamWf)) {
                  results.log(artifact, "TestTeamWorkflows",
                     "Error: TeamWorkflow " + teamWf.toStringWithId() + " has 0 ActionableItems");
               }
               if (teamWf.getTeamDefinition() == null) {
                  results.log(artifact, "TestTeamWorkflows",
                     "Error: TeamWorkflow " + teamWf.toStringWithId() + " has no TeamDefinition");
               }
               List<Long> badIds =
                  getInvalidIds(AtsObjects.toIds(atsApi.getActionableItemService().getActionableItems(teamWf)));
               if (!badIds.isEmpty()) {
                  results.log(artifact, "TestTeamWorkflows",
                     "Error: TeamWorkflow " + teamWf.toStringWithId() + " has AI ids that don't exisit " + badIds);
               }
            } catch (Exception ex) {
               results.log(artifact, "TestTeamWorkflows",
                  teamWf.getArtifactTypeName() + " exception: " + ex.getLocalizedMessage());
            }
         }
         return true;
      }

   }

   private List<Long> getInvalidIds(List<Long> ids) {
      List<Long> badIds = new ArrayList<>();
      for (Long id : ids) {
         if (!aiIds.contains(id)) {
            badIds.add(id);
         }
      }
      return badIds;
   }

   private class TestCurrentStateIsInWorkDef implements IAtsHealthCheck {

      @Override
      public boolean check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes, IAtsOperationCache cache) {
         if (workItem.isInWork()) {
            String currentStatename =
               atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, "unknown");
            currentStatename = currentStatename.replaceFirst(";.*$", "");
            WorkDefinition workDef = workItem.getWorkDefinition();
            if (workDef.getStateByName(currentStatename) == null) {
               results.log(artifact, "TestCurrentStateIsInWorkDef",
                  String.format("Error: Current State [%s] not valid for Work Definition [%s] for " + //
                     artifact.toStringWithId(), currentStatename, workDef.getName()));
            }
         }
         return true;
      }

   }
   private class TestActionableItemsLoad implements IAtsHealthCheck {

      @Override
      public boolean checkBefore(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {
         cache.getActionableItems();
         return true;
      }

   }

   private class TestActionableItemsTeamDefRef implements IAtsHealthCheck {

      @Override
      public boolean checkBefore(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {

         for (ArtifactToken aiArt : cache.getActionableItems()) {
            ArtifactId teamDefArt = atsApi.getAttributeResolver().getSoleArtifactIdReference(aiArt,
               AtsAttributeTypes.TeamDefinitionReference, ArtifactToken.SENTINEL);
            if (teamDefArt.isValid()) {
               results.log("TestActionableItemsTeamDefRef",
                  String.format("Invalid Team Def Ref for Actionable Item %s", aiArt.toStringWithId()));
            }
         }
         return true;
      }

   }

   private class TestActionableItemsProgram implements IAtsHealthCheck {

      @Override
      public boolean checkBefore(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {

         for (ArtifactToken aiArt : cache.getActionableItems()) {
            // Program Id
            ArtifactId progId = atsApi.getAttributeResolver().getSoleAttributeValue(aiArt, AtsAttributeTypes.ProgramId,
               ArtifactId.SENTINEL);
            if (progId.isValid()) {
               if (!atsApi.getConfigService().getConfigurations().getIdToProgram().containsKey(progId.getId())) {
                  results.log("TestActionableItemsProgram",
                     String.format("Invalid Program Id %s for Actionable Item %s", progId, aiArt.toStringWithId()));
               }
            }
         }
         return true;
      }

   }

   private class TestTeamDefinitionsLoad implements IAtsHealthCheck {

      @Override
      public boolean checkBefore(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {
         cache.getTeamDefinitions();
         return true;
      }

   }

   private class TestTeamDefinitionsBaslineBranch implements IAtsHealthCheck {

      @Override
      public boolean checkBefore(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {
         for (ArtifactToken teamDefArt : cache.getTeamDefinitions()) {
            String branchId = atsApi.getAttributeResolver().getSoleAttributeValue(teamDefArt,
               AtsAttributeTypes.BaselineBranchId, "-1");
            BranchId branch = BranchId.valueOf(branchId);
            if (branch.isValid()) {
               BranchToken branch2 = atsApi.getBranchService().getBranch(branch);
               if (branch2 == null) {
                  results.log("TestTeamDefinitionsBaslineBranch",
                     String.format("Invalid Branch %s for Team Def %s", branch.getId(), teamDefArt.toStringWithId()));
               } else if (atsApi.getBranchService().getBranchType(branch2) != BranchType.BASELINE) {
                  results.log("TestTeamDefinitionsBaslineBranch",
                     String.format("Invalid BranchType %s for Branch %s for Team Def %s",
                        atsApi.getBranchService().getBranchType(branch2), branch.getId(), teamDefArt.toStringWithId()));
               }
            }
         }
         return true;
      }

   }

   private class TestTeamDefinitionsWorkDefRef implements IAtsHealthCheck {

      @Override
      public boolean checkBefore(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {
         for (ArtifactToken teamDefArt : cache.getTeamDefinitions()) {
            for (AttributeTypeGeneric<? extends Object> artType : Arrays.asList(
               AtsAttributeTypes.WorkflowDefinitionReference, AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference,
               AtsAttributeTypes.RelatedTaskWorkflowDefinitionReference)) {
               for (Object obj : atsApi.getAttributeResolver().getAttributeValues(teamDefArt, artType)) {
                  ArtifactId workDefId = (ArtifactId) obj;
                  if (workDefId.isValid()) {
                     if (atsApi.getWorkDefinitionService().getWorkDefinition(workDefId) == null) {
                        results.log("TestTeamDefinitionsWorkDefRef", String.format(
                           "Invalid WorkDefRef %s for Team Def %s", workDefId.getId(), teamDefArt.toStringWithId()));
                     }
                  }
               }
            }
         }
         return true;
      }

   }

   private class TestTeamDefinitionsProgram implements IAtsHealthCheck {

      @Override
      public boolean checkBefore(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {
         for (ArtifactToken teamDefArt : cache.getTeamDefinitions()) {
            ArtifactId progId = atsApi.getAttributeResolver().getSoleAttributeValue(teamDefArt,
               AtsAttributeTypes.ProgramId, ArtifactId.SENTINEL);
            if (progId.isValid()) {
               if (!atsApi.getConfigService().getConfigurations().getIdToProgram().containsKey(progId.getId())) {
                  results.log("TestTeamDefinitionsProgram",
                     String.format("Invalid Program Id %s for Team Def %s", progId, teamDefArt.toStringWithId()));
               }
            }
         }
         return true;
      }

   }

   private class TestVersions implements IAtsHealthCheck {

      @Override
      public boolean checkBefore(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {

         for (ArtifactToken verArt : atsApi.getQueryService().getArtifacts(AtsArtifactTypes.Version)) {
            IAtsVersion version = atsApi.getVersionService().getVersionById(verArt);

            // Baseline Branch Id valid and Baseline
            String branchId =
               atsApi.getAttributeResolver().getSoleAttributeValue(verArt, AtsAttributeTypes.BaselineBranchId, "-1");
            BranchId branch = BranchId.valueOf(branchId);
            if (branch.isValid()) {
               BranchToken branch2 = null;
               try {
                  branch2 = atsApi.getBranchService().getBranch(branch);
               } catch (ItemDoesNotExist ex) {
                  // do nothing
               }
               if (branch2 == null) {
                  results.log("TestVersions",
                     String.format("Invalid Branch %s for Team Def %s", branch.getId(), verArt.toStringWithId()));
               } else if (atsApi.getBranchService().getBranchType(branch2) != BranchType.BASELINE) {
                  results.log("TestVersions", String.format("Invalid BranchType %s for Branch %s for Team Def %s",
                     atsApi.getBranchService().getBranchType(branch2), branch.getId(), verArt.toStringWithId()));
               }
            }

            // Parallel Config
            for (IAtsVersion parallelVersion : atsApi.getVersionService().getParallelVersions(version)) {
               if (parallelVersion != null) {
                  try {
                     if (parallelVersion.isBranchInvalid()) {
                        results.log(verArt, "TestVersions",
                           "Error: [" + parallelVersion.toStringWithId() + "] in parallel config without parent branch id");
                     }
                  } catch (Exception ex) {
                     results.log(verArt, "TestVersions",
                        "Error: " + verArt.getName() + " exception testing testVersionArtifacts: " + ex.getLocalizedMessage());
                  }
               }
            }
         }
         return true;
      }

   }

   private class TestDuplicateArtEntries implements IAtsHealthCheck {

      @Override
      public boolean checkBefore(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {
         List<ArtifactId> artIds =
            atsApi.getQueryService().getArtifactIdsFromQuery(AtsHealthQueries.getMultipleArtEntriesonCommon(atsApi));
         if (!artIds.isEmpty()) {
            results.log("TestDuplicateArtEntries",
               String.format("Error: Duplicate Art Ids [%s]", Collections.toString(",", artIds)));
         }
         return true;
      }

   }

   private class TestStateMgrAndDupStates implements IAtsHealthCheck {

      @Override
      public boolean check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes, IAtsOperationCache cache) {
         List<String> foundStates = new ArrayList<>();
         for (String stateAttr : atsApi.getAttributeResolver().getAttributesToStringList(workItem,
            AtsAttributeTypes.State)) {
            String state = stateAttr.replaceFirst(";.*$", "");
            if (foundStates.contains(state)) {
               results.log(artifact, "TestStateMgrAndDupStates",
                  String.format("Error: Duplicate state [%s] for %s", state, workItem.toStringWithId()));
            } else {
               foundStates.add(state);
            }
         }
         return true;
      }

   }

   private class TestWorkflowDefinition implements IAtsHealthCheck {

      @Override
      public boolean checkBefore(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {
         XResultData rd = new XResultData();
         Map<Long, String> artIdToworkDefId = getInWorkWorkItemsWorkDefId(rd);
         for (Entry<Long, String> entry : artIdToworkDefId.entrySet()) {
            String workDefId = entry.getValue();
            WorkDefinition workDef = atsApi.getWorkDefinitionService().getWorkDefinition(Long.valueOf(workDefId));
            if (workDef == null) {
               results.log(ArtifactId.valueOf(entry.getKey()), "TestWorkflowDefinition",
                  String.format("Error: Invalid Work Def Ref for workflow %s", entry.getKey()));
            }
         }
         return true;
      }

   }

   private class TestWorkflowTeamDefinition implements IAtsHealthCheck {

      @Override
      public boolean check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes, IAtsOperationCache cache) {
         ArtifactId teamDefArt = atsApi.getAttributeResolver().getSoleArtifactIdReference(artifact,
            AtsAttributeTypes.TeamDefinitionReference, ArtifactToken.SENTINEL);
         if (teamDefArt.isValid() && !atsApi.getConfigService().getConfigurations().getIdToTeamDef().containsKey(
            teamDefArt.getId())) {
            results.log("TestWorkflowTeamDefinition",
               String.format("Invalid Team Def Ref for workflow %s", workItem.toStringWithId()));
         }
         return true;
      }

   }

   private Map<Long, String> getInWorkWorkItemsWorkDefId(XResultData rd) {
      Map<Long, String> artIdWorkDefId = new HashMap<>();
      rd.log("getInWorkWorkItemsWorkDefId - Started " + DateUtil.getMMDDYYHHMM());
      JdbcStatement chStmt = jdbcService.getClient().getStatement();
      try {
         chStmt.runPreparedQuery(AtsHealthQueries.getInWorkWorkItemsWorkDefId(atsApi));
         while (chStmt.next()) {
            artIdWorkDefId.put(chStmt.getLong(1), chStmt.getString(2));
         }
      } finally {
         chStmt.close();
         rd.log("getInWorkWorkItemsWorkDefId - Completed " + DateUtil.getMMDDYYHHMM());
      }
      return artIdWorkDefId;
   }

   private List<Collection<Long>> loadWorkingWorkItemIds(XResultData rd) {
      Date date = new Date();
      List<Long> artIds = getCommonArtifactIds(rd);
      if (artIds.isEmpty()) {
         rd.error("Error: Artifact load returned 0 artifacts to check");
      }
      List<Collection<Long>> subDivide = Collections.subDivide(artIds, 4000);
      vResults.logTestTimeSpent(date, "Load Art Ids");
      return subDivide;
   }

   private List<Long> getCommonArtifactIds(XResultData rd) {
      List<Long> artIds = new ArrayList<>();
      // For single or re-runs of subset
      //      artIds.addAll(Arrays.asList(8381138L, 600305128L, 9115994L, 8003310L, 8646243L, 8660113L, 9036993L, 8646249L));
      // OR
      // Load all in work workflows
      JdbcStatement chStmt = jdbcService.getClient().getStatement();
      try {
         chStmt.runPreparedQuery(AtsHealthQueries.getWorkItemsInCurrentStateType(atsApi, StateType.Working));
         while (chStmt.next()) {
            artIds.add(chStmt.getLong(1));
         }
      } finally {
         chStmt.close();
      }
      return artIds;
   }

   private void outputResults(XResultData rd) {
      if (OseeProperties.isInTest() & rd.toString().contains("Error: ")) {
         throw new OseeStateException("Error in ATS Health Check: " + rd.toString());
      } else {
         String html = AHTML.simplePage(rd.toString().replaceAll("\n", "</br>"));
         html = html.replaceAll("Error: ", AHTML.color("red", "Error: "));
         try {
            emailResults(html);
         } catch (Exception ex) {
            OseeLog.log(AtsHealthCheckOperation.class, Level.SEVERE, "Can't Email ATS Health Check results", ex);
         }
         try {
            saveResults(html);
         } catch (Exception ex) {
            OseeLog.log(AtsHealthCheckOperation.class, Level.SEVERE, "Can't Save ATS Health Check results", ex);
         }
      }
   }

   private void saveResults(String html) {
      String serverData = System.getProperty("osee.application.server.data");
      if (!Strings.isValid(serverData)) {
         serverData = System.getProperty("user.home");
      }
      String outputDirName = serverData + File.separator + "atsHealth";
      File outDir = new File(outputDirName);
      outDir.mkdir();

      String outputFileName = outputDirName + //
         File.separator + Lib.getDateTimeString() + ".html";
      File outFile = new File(outputFileName);
      try {
         Lib.writeStringToFile(html, outFile);
      } catch (Exception ex) {
         String exStr = AHTML.simplePage(Lib.exceptionToString(ex));
         try {
            Lib.writeStringToFile(exStr, outFile);
         } catch (IOException ex1) {
            System.err.println(Lib.exceptionToString(ex1));
         }
      }
   }

   private void emailResults(String html) {
      IUserGroup userGroup = atsApi.userService().getUserGroup(AtsArtifactToken.AtsHealthUsers);
      if (userGroup == null) {
         return;
      }
      Collection<String> emails = userGroup.getActiveMemberEmails();
      if (emails.isEmpty()) {
         return;
      }
      String dbName = "";
      String configDbName = atsApi.getConfigValue("DatabaseName");
      if (Strings.isValid(configDbName)) {
         dbName = " " + configDbName;
      }
      String fromReplyEmail = atsApi.getConfigValue("NoReplyEmail");
      if (Strings.isInValid(fromReplyEmail)) {
         fromReplyEmail = emails.iterator().next();
      }
      OseeEmail emailMessage = OseeEmailServer.create(emails, fromReplyEmail, fromReplyEmail,
         dbName + " - ATS Health Check", html, BodyType.Html);
      emailMessage.send();
   }

}
