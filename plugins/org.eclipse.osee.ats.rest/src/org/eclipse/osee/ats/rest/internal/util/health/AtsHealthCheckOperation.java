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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.health.HealthCheckResults;
import org.eclipse.osee.ats.api.util.health.IAtsHealthCheck;
import org.eclipse.osee.ats.api.util.health.IAtsHealthCheckProvider;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.rest.internal.util.health.check.AtsHealthQueries;
import org.eclipse.osee.ats.rest.internal.util.health.check.TestDuplicateAttributesWithPersist;
import org.eclipse.osee.ats.rest.internal.util.health.check.TestTaskParent;
import org.eclipse.osee.ats.rest.internal.util.health.check.TestWorkflowVersions;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.util.MailStatus;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.mail.api.MailMessage;
import org.eclipse.osee.mail.api.MailService;

/**
 * @author Donald G. Dunne
 */
public class AtsHealthCheckOperation {

   private final AtsApi atsApi;
   private final JdbcService jdbcService;
   private final MailService mailService;
   boolean inTest = false;
   boolean persist = false;

   public AtsHealthCheckOperation(AtsApi atsApi, JdbcService jdbcService, MailService mailService) {
      this.atsApi = atsApi;
      this.jdbcService = jdbcService;
      this.mailService = mailService;
   }

   public XResultData run() {
      XResultData rd = new XResultData();
      try {
         ElapsedTime time = new ElapsedTime("ATS Health Check");
         runIt(rd);
         String elapsedStr = time.end(Units.SEC);
         rd.log("\n\n" + elapsedStr);
         time.end();
         emailResults(rd);
      } catch (Exception ex) {
         rd.errorf("Exception running reports [%s]", Lib.exceptionToString(ex));
      }
      return rd;
   }

   private void emailResults(XResultData rd) {
      if (mailService != null) {
         String dbName = "";
         String configDbName = atsApi.getConfigValue("DatabaseName");
         if (Strings.isValid(configDbName)) {
            dbName = " " + configDbName;
         }
         MailMessage msg = MailMessage.newBuilder() //
            .from("noop@osee.com") //
            .recipients(Arrays.asList("donald.g.dunne@boeing.com")) //
            .subject(dbName + " ATS Health Check") //
            .addHtml(AHTML.simplePage(rd.toString().replaceAll("\n", "</br>")))//
            .build();

         List<MailStatus> sendMessages = mailService.sendMessages(msg);
         System.out.println(sendMessages);
      }
   }

   public void runIt(XResultData rd) {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);

      int count = 0;
      HealthCheckResults vResults = new HealthCheckResults();
      if (inTest) {
         vResults.log("testMap1", "blah blah");
         vResults.log("testMap2", "blah blah");
         vResults.log("testMap1", "blah blah");
         vResults.log("testMap3", "blah blah");
      } else {
         List<IAtsHealthCheck> checks = getHealthChecks();

         // Run single run health checks
         for (IAtsHealthCheck check : checks) {
            check.check(vResults, atsApi);
         }

         IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());

         // Break artifacts into blocks so don't run out of memory
         List<Collection<Long>> artIdLists = loadWorkingWorkItemIds(rd);
         for (Collection<Long> artIdList : artIdLists) {

            Collection<ArtifactToken> allArtifacts = atsApi.getQueryService().getArtifacts(artIdList);

            // remove all deleted/purged artifacts first
            List<ArtifactToken> artifacts = new ArrayList<>(allArtifacts.size());
            for (ArtifactToken artifact : allArtifacts) {
               if (!atsApi.getStoreService().isDeleted(artifact)) {
                  artifacts.add(artifact);
               }
            }
            count += artifacts.size();

            for (ArtifactToken artifact : artifacts) {
               for (IAtsHealthCheck check : checks) {
                  Date date = new Date();
                  try {
                     if (atsApi.getStoreService().isDeleted(artifact)) {
                        continue;
                     }
                     IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(artifact);
                     check.check(artifact, workItem, vResults, atsApi, (persist ? changes : null));
                  } catch (Exception ex) {
                     vResults.log(artifact, check.getName(), "Error: Exception: " + Lib.exceptionToString(ex));
                  }
                  vResults.logTestTimeSpent(date, check.getName());
               }
            }
         }
         if (persist) {
            changes.executeIfNeeded();
         }
         // Throw away checks so caches will get garbage collected
         checks = null;
      }
      // Log resultMap data into xResultData
      vResults.addResultsMapToResultData(rd);
      vResults.addTestTimeMapToResultData(rd);

      rd.logf("Completed processing %s work items.", count);
   }

   /**
    * Health checks can have caches to improve performance. Create new checks to ensure caches are cleared.
    */
   private List<IAtsHealthCheck> getHealthChecks() {
      List<IAtsHealthCheck> healthChecks = new LinkedList<>();
      healthChecks.add(new TestTaskParent());
      healthChecks.add(new TestWorkflowTeamDefinition());
      healthChecks.add(new TestWorkflowVersions());
      healthChecks.add(new TestWorkflowDefinition());
      healthChecks.add(new TestStateMgr()); // TBD Need duplicate state check
      // TBD Need Work Definition attr check
      healthChecks.add(new TestCurrentStateIsInWorkDef());
      healthChecks.add(new TestWorkflowHasAction());
      healthChecks.add(new TestTeamDefinitions());
      healthChecks.add(new TestActionableItems());
      healthChecks.add(new TestVersions());
      healthChecks.add(new TestTeamWorkflows());
      healthChecks.add(new TestBranches());
      for (IAtsHealthCheckProvider provider : AtsHealthCheckProviderService.getHealthCheckProviders()) {
         healthChecks.addAll(provider.getHealthChecks());
      }
      healthChecks.add(new TestDuplicateAttributesWithPersist());
      healthChecks.add(new TestDuplicateArtEntries());
      return healthChecks;
   }

   private class TestWorkflowHasAction implements IAtsHealthCheck {

      @Override
      public void check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes) {
         if (workItem.isReview() && atsApi.getReviewService().isStandAloneReview(workItem)) {
            return;
         }
         if (workItem.hasAction() && workItem.getParentAction() == null) {
            results.log(artifact, "TestWorkflowHasAction",
               String.format("Error: Workflow %s has no parent Action", workItem.toStringWithId()));
         }
      }
   }

   private class TestBranches implements IAtsHealthCheck {

      @Override
      public void check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes) {
         if (workItem.isTeamWorkflow()) {
            IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
            try {
               BranchId workingBranch = atsApi.getBranchService().getWorkingBranch(teamWf);
               if (workingBranch != null && workingBranch.isValid() && !atsApi.getBranchService().getBranchType(
                  workingBranch).isBaselineBranch()) {
                  if (!atsApi.getBranchService().getBranchState(workingBranch).isCommitted()) {
                     Collection<BranchId> branchesCommittedTo =
                        atsApi.getBranchService().getBranchesCommittedTo(teamWf);
                     if (!branchesCommittedTo.isEmpty()) {
                        results.log(artifact, "testAtsBranchManagerA",
                           "Error: TeamWorkflow " + teamWf.toStringWithId() + " has committed branches but working branch [" + workingBranch + "] != COMMITTED");
                     }
                  } else if (!atsApi.getBranchService().isArchived(workingBranch)) {
                     Collection<BranchId> branchesLeftToCommit =
                        atsApi.getBranchService().getBranchesLeftToCommit(teamWf);
                     if (branchesLeftToCommit.isEmpty()) {
                        results.log(artifact, "testAtsBranchManagerA",
                           "Error: TeamWorkflow " + teamWf.toStringWithId() + " has committed all branches but working branch [" + workingBranch + "] != ARCHIVED");
                     }
                  }
               }
            } catch (Exception ex) {
               results.log("testAtsBranchManager",
                  teamWf.getArtifactTypeName() + " exception: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   private class TestTeamWorkflows implements IAtsHealthCheck {

      @Override
      public void check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes) {
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
      }
   }

   private List<Long> getInvalidIds(List<Long> ids) {
      List<Long> badIds = new ArrayList<>();
      for (Long id : ids) {
         if (atsApi.getQueryService().getArtifact(id) == null) {
            badIds.add(id);
         }
      }
      return badIds;
   }

   private class TestCurrentStateIsInWorkDef implements IAtsHealthCheck {

      @Override
      public void check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes) {
         if (workItem.isInWork()) {
            String currentStatename = workItem.getStateMgr().getCurrentStateName();
            IAtsWorkDefinition workDef = workItem.getWorkDefinition();
            if (workDef.getStateByName(currentStatename) == null) {
               results.log(artifact, "TestCurrentStateIsInWorkDef",
                  String.format("Error: Current State [%s] not valid for Work Definition [%s] for " + //
                     artifact.toStringWithId(), currentStatename, workDef.getName()));
            }
         }
      }
   }

   private class TestActionableItems implements IAtsHealthCheck {

      @Override
      public void check(HealthCheckResults results, AtsApi atsApi) {

         // Actionable Items
         for (ArtifactToken aiArt : atsApi.getQueryService().getArtifacts(AtsArtifactTypes.ActionableItem)) {
            for (AttributeTypeGeneric<? extends Object> artType : Arrays.asList(AtsAttributeTypes.TeamDefinition)) {
               for (Object obj : atsApi.getAttributeResolver().getAttributeValues(aiArt, artType)) {
                  ArtifactId artId = (ArtifactId) obj;
                  ArtifactToken refArt = atsApi.getQueryService().getArtifact(artId);
                  if (refArt == null) {
                     results.log("TestActionableItems", String.format("Invalid %s %s for Actionable Item %s",
                        artType.getName(), artId.getId(), aiArt.toStringWithId()));
                  }
               }
            }

            // Program Id
            ArtifactId progId = atsApi.getAttributeResolver().getSoleAttributeValue(aiArt, AtsAttributeTypes.ProgramId,
               ArtifactId.SENTINEL);
            if (progId.isValid()) {
               ArtifactToken progArt = atsApi.getQueryService().getArtifact(progId);
               if (progArt == null) {
                  results.log("TestTeamDefinitions",
                     String.format("Invalid Program Id %s for Actionable Item %s", progId, aiArt.toStringWithId()));
               }
            }
         }
      }
   }

   private class TestTeamDefinitions implements IAtsHealthCheck {

      @Override
      public void check(HealthCheckResults results, AtsApi atsApi) {

         for (ArtifactToken teamDefArt : atsApi.getQueryService().getArtifacts(AtsArtifactTypes.TeamDefinition)) {

            // Actionable Items
            for (AttributeTypeGeneric<? extends Object> artType : Arrays.asList(AtsAttributeTypes.ActionableItem)) {
               for (Object obj : atsApi.getAttributeResolver().getAttributeValues(teamDefArt, artType)) {
                  ArtifactId artId = (ArtifactId) obj;
                  ArtifactToken refArt = atsApi.getQueryService().getArtifact(artId);
                  if (refArt == null) {
                     results.log("TestTeamDefinitions", String.format("Invalid %s %s for Team Def %s",
                        artType.getName(), artId.getId(), teamDefArt.toStringWithId()));
                  }
               }
            }

            // Baseline Branch Id valid and Baseline
            String branchId = atsApi.getAttributeResolver().getSoleAttributeValue(teamDefArt,
               AtsAttributeTypes.BaselineBranchId, "-1");
            BranchId branch = BranchId.valueOf(branchId);
            if (branch.isValid()) {
               IOseeBranch branch2 = atsApi.getBranchService().getBranch(branch);
               if (branch2 == null) {
                  results.log("TestTeamDefinitions",
                     String.format("Invalid Branch %s for Team Def %s", branch.getId(), teamDefArt.toStringWithId()));
               } else if (atsApi.getBranchService().getBranchType(branch2) != BranchType.BASELINE) {
                  results.log("TestTeamDefinitions",
                     String.format("Invalid BranchType %s for Branch %s for Team Def %s",
                        atsApi.getBranchService().getBranchType(branch2), branch.getId(), teamDefArt.toStringWithId()));
               }
            }

            // WorkflowDefinition References
            for (AttributeTypeGeneric<? extends Object> artType : Arrays.asList(
               AtsAttributeTypes.WorkflowDefinitionReference, AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference,
               AtsAttributeTypes.RelatedTaskWorkflowDefinitionReference)) {
               for (Object obj : atsApi.getAttributeResolver().getAttributeValues(teamDefArt, artType)) {
                  ArtifactId workDefId = (ArtifactId) obj;
                  if (workDefId.isValid()) {
                     if (atsApi.getWorkDefinitionService().getWorkDefinition(workDefId) == null) {
                        results.log("TestTeamDefinitions", String.format("Invalid WorkDefRef %s for Team Def %s",
                           workDefId.getId(), teamDefArt.toStringWithId()));
                     }
                  }
               }
            }

            // Program Id
            ArtifactId progId = atsApi.getAttributeResolver().getSoleAttributeValue(teamDefArt,
               AtsAttributeTypes.ProgramId, ArtifactId.SENTINEL);
            if (progId.isValid()) {
               ArtifactToken progArt = atsApi.getQueryService().getArtifact(progId);
               if (progArt == null) {
                  results.log("TestTeamDefinitions",
                     String.format("Invalid Program Id %s for Team Def %s", progId, teamDefArt.toStringWithId()));
               }
            }
         }
      }
   }

   private class TestVersions implements IAtsHealthCheck {

      @Override
      public void check(HealthCheckResults results, AtsApi atsApi) {

         for (ArtifactToken verArt : atsApi.getQueryService().getArtifacts(AtsArtifactTypes.Version)) {
            IAtsVersion version = atsApi.getVersionService().getVersionById(verArt);

            // Baseline Branch Id valid and Baseline
            String branchId =
               atsApi.getAttributeResolver().getSoleAttributeValue(verArt, AtsAttributeTypes.BaselineBranchId, "-1");
            BranchId branch = BranchId.valueOf(branchId);
            if (branch.isValid()) {
               IOseeBranch branch2 = atsApi.getBranchService().getBranch(branch);
               if (branch2 == null) {
                  results.log("TestTeamDefinitions",
                     String.format("Invalid Branch %s for Team Def %s", branch.getId(), verArt.toStringWithId()));
               } else if (atsApi.getBranchService().getBranchType(branch2) != BranchType.BASELINE) {
                  results.log("TestTeamDefinitions",
                     String.format("Invalid BranchType %s for Branch %s for Team Def %s",
                        atsApi.getBranchService().getBranchType(branch2), branch.getId(), verArt.toStringWithId()));
               }
            }

            // Parallel Config
            for (IAtsVersion parallelVersion : atsApi.getVersionService().getParallelVersions(version)) {
               if (parallelVersion != null) {
                  try {
                     if (parallelVersion.isBranchInvalid()) {
                        results.log(verArt, "testParallelConfig",
                           "Error: [" + parallelVersion.toStringWithId() + "] in parallel config without parent branch id");
                     }
                  } catch (Exception ex) {
                     results.log(verArt, "testParallelConfig",
                        "Error: " + verArt.getName() + " exception testing testVersionArtifacts: " + ex.getLocalizedMessage());
                  }
               }
            }

         }
      }
   }

   private class TestDuplicateArtEntries implements IAtsHealthCheck {

      @Override
      public void check(HealthCheckResults results, AtsApi atsApi) {
         List<ArtifactId> artIds =
            atsApi.getQueryService().getArtifactIdsFromQuery(AtsHealthQueries.getMultipleArtEntriesonCommon(atsApi));
         if (!artIds.isEmpty()) {
            results.log("TestDuplicateArtEntries",
               String.format("Error: Duplicate Art Ids [%s]", Collections.toString(",", artIds)));
         }
      }

   }

   private class TestStateMgr implements IAtsHealthCheck {

      @Override
      public void check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes) {
         workItem.getStateMgr();
      }
   }

   private class TestWorkflowDefinition implements IAtsHealthCheck {

      @Override
      public void check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes) {
         if (workItem.getWorkDefinition() == null) {
            error(results, workItem, "Workflow has no Work Definition");
         } else if (workItem.getStateDefinition() == null) {
            error(results, workItem, "Workflow can not get State Definition");
         } else if (workItem.getStateMgr().getCurrentState() == null) {
            error(results, workItem, "Workflow can not get current state");
         }
      }
   }

   private class TestWorkflowTeamDefinition implements IAtsHealthCheck {

      @Override
      public void check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes) {
         if (workItem.isTeamWorkflow() && workItem.getParentTeamWorkflow().getTeamDefinition() == null) {
            error(results, workItem, "Team workflow has no Team Definition (re-run conversion?)");
         }
      }
   }

   private List<Collection<Long>> loadWorkingWorkItemIds(XResultData rd) {
      rd.log("testLoadAllCommonArtifactIds - Started " + DateUtil.getMMDDYYHHMM());
      List<Long> artIds = getCommonArtifactIds(rd);
      if (artIds.isEmpty()) {
         rd.error("Error: Artifact load returned 0 artifacts to check");
      }
      rd.log("testLoadAllCommonArtifactIds - Completed " + DateUtil.getMMDDYYHHMM());
      return Collections.subDivide(artIds, 4000);
   }

   private List<Long> getCommonArtifactIds(XResultData rd) {
      List<Long> artIds = new ArrayList<>();
      // For single or re-runs of subset
      //      artIds.addAll(Arrays.asList(8381138L, 600305128L, 9115994L, 8003310L, 8646243L, 8660113L, 9036993L, 8646249L));
      // OR
      // Load all in work workflows
      rd.log("getCommonArtifactIds - Started " + DateUtil.getMMDDYYHHMM());
      JdbcStatement chStmt = jdbcService.getClient().getStatement();
      try {
         chStmt.runPreparedQuery(AtsHealthQueries.getWorkItemsInCurrentStateType(atsApi, StateType.Working));
         while (chStmt.next()) {
            artIds.add(Long.valueOf(chStmt.getInt(1)));
         }
      } finally {
         chStmt.close();
         rd.log("getCommonArtifactIds - Completed " + DateUtil.getMMDDYYHHMM());
      }
      return artIds;
   }

}
