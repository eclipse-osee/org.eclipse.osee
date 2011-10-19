/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.health;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.core.action.ActionManager;
import org.eclipse.osee.ats.core.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionManagerCore;
import org.eclipse.osee.ats.core.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.review.defect.ReviewDefectManager;
import org.eclipse.osee.ats.core.review.role.UserRoleManager;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.workdef.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionFactory;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.log.AtsLog;
import org.eclipse.osee.ats.core.workflow.log.LogItem;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.WorldXNavigateItemAction;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.skynet.core.utility.ElapsedTime;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ValidateAtsDatabase extends WorldXNavigateItemAction {

   private boolean fixAssignees = true;
   private boolean fixAttributeValues = true;
   private final Set<String> hrids = new HashSet<String>();
   private final Map<String, String> legacyPcrIdToParentHrid = new HashMap<String, String>();
   private final CountingMap<String> testNameToTimeSpentMap = new CountingMap<String>();
   private HashCollection<String, String> testNameToResultsMap = null;
   private String emailOnComplete = null;

   public ValidateAtsDatabase(XNavigateItem parent) {
      this("Validate ATS Database", parent);
   }

   public ValidateAtsDatabase(String name, XNavigateItem parent) {
      super(parent, name, PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }
      Jobs.startJob(new Report(getName()), true);
   }

   public void performTaskAndPend() throws InterruptedException {
      Report job = new Report(getName());
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
      job.join();
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {

            XResultData rd = new XResultData();

            runIt(monitor, rd);
            XResultDataUI.report(rd, getName());
            if (Strings.isValid(emailOnComplete)) {
               String html = XResultDataUI.getReport(rd, getName()).getManipulatedHtml();
               OseeEmail.emailHtml(java.util.Collections.singleton(emailOnComplete),
                  String.format("Sync - %s [%s]", DateUtil.getDateNow(), getName()), html);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   public void runIt(IProgressMonitor monitor, XResultData xResultData) throws OseeCoreException {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      AtsBulkLoad.loadConfig(true);

      int count = 0;
      // Break artifacts into blocks so don't run out of memory
      List<Collection<Integer>> artIdLists = null;

      // Un-comment to process whole Common branch - Normal Mode
      ElapsedTime elapsedTime = new ElapsedTime("ValidateAtsDatabase - load ArtIds");
      artIdLists = loadAtsBranchArtifactIds(xResultData, monitor);
      elapsedTime.end();

      // Un-comment to process specific artifact from common - Test Mode
      //      artIdLists = new ArrayList<Collection<Integer>>();
      //      List<Integer> ids = new ArrayList<Integer>();
      //      ids.add(new Integer(1070598));
      //      artIdLists.add(ids);

      if (monitor != null) {
         monitor.beginTask(getName(), artIdLists.size());
      }

      // Remove this after 0.9.7 release and last sync
      OseeEventManager.setDisableEvents(true);
      try {

         testNameToResultsMap = new HashCollection<String, String>();
         hrids.clear();
         legacyPcrIdToParentHrid.clear();

         //         int artSetNum = 1;
         for (Collection<Integer> artIdList : artIdLists) {
            // Don't process all lists if just trying to test this report
            //            elapsedTime =
            //               new ElapsedTime(String.format("ValidateAtsDatabase - load Artifact set %d/%d", artSetNum++,
            //                  artIdLists.size()));
            Collection<Artifact> artifacts = ArtifactQuery.getArtifactListFromIds(artIdList, AtsUtil.getAtsBranch());
            //            elapsedTime.end();
            count += artifacts.size();

            testArtifactIds(artifacts);
            testAtsAttributeValues(artifacts);
            testStateInWorkDefinition(artifacts);
            testAttributeSetWorkDefinitionsExist(artifacts);
            testAtsActionsHaveTeamWorkflow(artifacts);
            testAtsWorkflowsHaveAction(artifacts);
            testAtsWorkflowsHaveZeroOrOneVersion(artifacts);
            testTasksHaveParentWorkflow(artifacts);
            testReviewsHaveParentWorkflowOrActionableItems(artifacts);
            testReviewsHaveValidDefectAndRoleXml(artifacts);
            testTeamWorkflows(artifacts);
            testAtsBranchManager(artifacts);
            testTeamDefinitions(artifacts);
            testVersionArtifacts(artifacts);
            testStateMachineAssignees(artifacts);
            testAtsLogs(artifacts);
            testActionableItemToTeamDefinition(artifacts);

            for (IAtsHealthCheck atsHealthCheck : AtsHealthCheck.getAtsHealthCheckItems()) {
               atsHealthCheck.validateAtsDatabase(artifacts, testNameToResultsMap, testNameToTimeSpentMap);
            }

            if (monitor != null) {
               monitor.worked(1);
            }
         }
         // Log resultMap data into xResultData
         addResultsMapToResultData(xResultData, testNameToResultsMap);
         addTestTimeMapToResultData(xResultData);
      } finally {
         OseeEventManager.setDisableEvents(false);
      }
      xResultData.reportSevereLoggingMonitor(monitorLog);
      if (monitor != null) {
         xResultData.log(monitor, "Completed processing " + count + " artifacts.");
      }
   }

   private void addTestTimeMapToResultData(XResultData xResultData) {
      xResultData.log("\n\nTime Spent in Tests");
      long totalTime = 0;
      for (Entry<String, MutableInteger> entry : testNameToTimeSpentMap.getCounts()) {
         xResultData.log(entry.getKey() + " - " + entry.getValue() + " ms");
         totalTime += entry.getValue().getValue();
      }
      xResultData.log("TOTAL - " + totalTime + " ms");

      xResultData.log("\n");
   }

   private void testAttributeSetWorkDefinitionsExist(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         try {
            String workDefName = artifact.getSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, "");
            if (Strings.isValid(workDefName) && WorkDefinitionFactory.getWorkDefinition(workDefName) == null) {
               testNameToResultsMap.put(
                  "testAttributeSetWorkDefinitionsExist",
                  String.format(
                     "Error: ats.Work Definition attribute value [%s] not valid work definition for " + XResultDataUI.getHyperlink(artifact),
                     workDefName));
            }
         } catch (Exception ex) {
            testNameToResultsMap.put(
               "testAttributeSetWorkDefinitionsExist",
               "Error: " + artifact.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(artifact) + " exception: " + ex.getLocalizedMessage());
         }
      }
      logTestTimeSpent(date, "testAttributeSetWorkDefinitionsExist", testNameToTimeSpentMap);
   }

   private void testStateInWorkDefinition(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         try {
            if (artifact instanceof AbstractWorkflowArtifact) {
               AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
               if (awa.isInWork()) {
                  String currentStatename = awa.getCurrentStateName();
                  WorkDefinition workDef = awa.getWorkDefinition();
                  if (workDef.getStateByName(currentStatename) == null) {
                     testNameToResultsMap.put(
                        "testStateInWorkDefinition",
                        String.format(
                           "Error: Current State [%s] not valid for Work Definition [%s] for " + XResultDataUI.getHyperlink(artifact),
                           currentStatename, workDef.getName()));
                  }
               }
            }
         } catch (Exception ex) {
            testNameToResultsMap.put(
               "testStateInWorkDefinition",
               "Error: " + artifact.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(artifact) + " exception: " + ex.getLocalizedMessage());
         }
      }
      logTestTimeSpent(date, "testStateInWorkDefinition", testNameToTimeSpentMap);
   }

   public static void addResultsMapToResultData(XResultData xResultData, HashCollection<String, String> testNameToResultsMap) {
      String[] keys = testNameToResultsMap.keySet().toArray(new String[testNameToResultsMap.keySet().size()]);
      Arrays.sort(keys);
      for (String testName : keys) {
         xResultData.log(testName);
         for (String result : testNameToResultsMap.getValues(testName)) {
            xResultData.log(result);
         }
      }
   }

   private void testArtifactIds(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         try {
            // Check that HRIDs not duplicated on Common branch
            if (hrids.contains(artifact.getHumanReadableId())) {
               testNameToResultsMap.put("testArtifactIds",
                  "Error: Duplicate HRIDs: " + XResultDataUI.getHyperlink(artifact));
            }
            // Check that duplicate Legacy PCR IDs team arts do not exist with different parent actions
            if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
               String legacyPcrId = artifact.getSoleAttributeValueAsString(AtsAttributeTypes.LegacyPcrId, null);
               if (legacyPcrId != null) {
                  if (legacyPcrIdToParentHrid.containsKey(legacyPcrId)) {
                     if (!legacyPcrIdToParentHrid.get(legacyPcrId).equals(
                        teamArt.getParentActionArtifact().getHumanReadableId())) {
                        testNameToResultsMap.put("testArtifactIds",
                           "Error: Duplicate Legacy PCR Ids in Different Actions: " + legacyPcrId);
                     }
                  } else {
                     legacyPcrIdToParentHrid.put(legacyPcrId, teamArt.getParentActionArtifact().getHumanReadableId());
                  }
               }
            }
         } catch (Exception ex) {
            testNameToResultsMap.put(
               "testArtifactIds",
               "Error: " + artifact.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(artifact) + " exception: " + ex.getLocalizedMessage());
         }
      }
      logTestTimeSpent(date, "testArtifactIds", testNameToTimeSpentMap);
   }

   public static void logTestTimeSpent(Date date, String testName, CountingMap<String> testNameToTimeSpentMap) {
      Date now = new Date();
      int spent = new Long(now.getTime() - date.getTime()).intValue();
      testNameToTimeSpentMap.put(testName, spent);
   }

   private void testVersionArtifacts(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isOfType(AtsArtifactTypes.Version)) {
            Artifact verArt = artifact;
            try {
               String parentBranchGuid =
                  verArt.getSoleAttributeValueAsString(AtsAttributeTypes.BaselineBranchGuid, null);
               if (parentBranchGuid != null) {
                  validateBranchGuid(verArt, parentBranchGuid);
               }
            } catch (Exception ex) {
               testNameToResultsMap.put(
                  "testVersionArtifacts",
                  "Error: " + verArt.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(verArt) + " exception testing testVersionArtifacts: " + ex.getLocalizedMessage());
            }
         }
      }
      logTestTimeSpent(date, "testVersionArtifacts", testNameToTimeSpentMap);
   }

   private void testTeamDefinitions(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact art : artifacts) {
         if (art instanceof TeamDefinitionArtifact) {
            TeamDefinitionArtifact teamDef = (TeamDefinitionArtifact) art;
            try {
               String parentBranchGuid =
                  teamDef.getSoleAttributeValueAsString(AtsAttributeTypes.BaselineBranchGuid, null);
               if (parentBranchGuid != null) {
                  validateBranchGuid(teamDef, parentBranchGuid);
               }
            } catch (Exception ex) {
               testNameToResultsMap.put(
                  "testTeamDefinitionss",
                  "Error: " + teamDef.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(teamDef) + " exception testing testTeamDefinitions: " + ex.getLocalizedMessage());
            }
         }
      }
      logTestTimeSpent(date, "testTeamDefinitions", testNameToTimeSpentMap);
   }

   private void testTeamWorkflows(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;
            try {
               if (teamArt.getActionableItemsDam().getActionableItems().isEmpty()) {
                  testNameToResultsMap.put("testTeamWorkflows",
                     "Error: TeamWorkflow " + XResultDataUI.getHyperlink(teamArt) + " has 0 ActionableItems");
               }
               if (teamArt.getTeamDefinition() == null) {
                  testNameToResultsMap.put("testTeamWorkflows",
                     "Error: TeamWorkflow " + XResultDataUI.getHyperlink(teamArt) + " has no TeamDefinition");
               }
            } catch (Exception ex) {
               testNameToResultsMap.put(
                  "testTeamWorkflows",
                  teamArt.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(teamArt) + " exception: " + ex.getLocalizedMessage());
            }
         }
      }
      logTestTimeSpent(date, "testTeamWorkflows", testNameToTimeSpentMap);
   }

   private void testAtsBranchManager(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;
            try {
               Branch workingBranch = AtsBranchManagerCore.getWorkingBranch(teamArt);
               if (workingBranch != null && workingBranch.getBranchState() != BranchState.COMMITTED && workingBranch.getBranchType() != BranchType.BASELINE) {
                  Collection<Branch> branchesCommittedTo = AtsBranchManagerCore.getBranchesCommittedTo(teamArt);
                  if (branchesCommittedTo.size() > 0) {
                     testNameToResultsMap.put(
                        "testAtsBranchManagerA",
                        "Error: TeamWorkflow " + XResultDataUI.getHyperlink(teamArt) + " has committed branches but working branch [" + workingBranch.getGuid() + "] != COMMITTED");
                  }
               }
            } catch (Exception ex) {
               testNameToResultsMap.put(
                  "testAtsBranchManager",
                  teamArt.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(teamArt) + " exception: " + ex.getLocalizedMessage());
            }
         }
      }
      logTestTimeSpent(date, "testAtsBranchManager", testNameToTimeSpentMap);
   }

   private void validateBranchGuid(Artifact artifact, String parentBranchGuid) {
      Date date = new Date();
      try {
         Branch branch = BranchManager.getBranchByGuid(parentBranchGuid);
         if (branch.getArchiveState().isArchived()) {
            testNameToResultsMap.put("validateBranchGuid", String.format(
               "Error: [%s][%s][%s] has Parent Branch Id attribute set to Archived Branch [%s] named [%s]",
               artifact.getArtifactTypeName(), artifact.getHumanReadableId(), artifact, parentBranchGuid, branch));
         } else if (!branch.getBranchType().isBaselineBranch()) {
            testNameToResultsMap.put(
               "validateBranchGuid",
               String.format(
                  "Error: [%s][%s][%s] has Parent Branch Id attribute [%s][%s] that is a [%s] branch; should be a BASLINE branch",
                  artifact.getArtifactTypeName(), artifact.getHumanReadableId(), artifact,
                  branch.getBranchType().name(), parentBranchGuid, branch));
         }
      } catch (BranchDoesNotExist ex) {
         testNameToResultsMap.put("validateBranchGuid", String.format(
            "Error: [%s][%s][%s] has Parent Branch Id attribute [%s] that references a non-existant",
            artifact.getArtifactTypeName(), artifact.getHumanReadableId(), artifact, parentBranchGuid));
      } catch (Exception ex) {
         testNameToResultsMap.put(
            "validateBranchGuid",
            "Error: " + artifact.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(artifact) + " exception: " + ex.getLocalizedMessage());
      }
      logTestTimeSpent(date, "validateBranchGuid", testNameToTimeSpentMap);
   }

   public static List<Collection<Integer>> loadAtsBranchArtifactIds(XResultData xResultData, IProgressMonitor monitor) throws OseeCoreException {
      if (xResultData == null) {
         xResultData = new XResultData();
      }
      xResultData.log(monitor, "testLoadAllCommonArtifactIds - Started " + DateUtil.getMMDDYYHHMM());
      List<Integer> artIds = ArtifactQuery.selectArtifactListFromBranch(AtsUtil.getAtsBranch(), EXCLUDE_DELETED);

      if (artIds.isEmpty()) {
         xResultData.logError("Error: Artifact load returned 0 artifacts to check");
      }
      xResultData.log(monitor, "testLoadAllCommonArtifactIds - Completed " + DateUtil.getMMDDYYHHMM());
      return Collections.subDivide(artIds, 5000);
   }

   private void testAtsAttributeValues(Collection<Artifact> artifacts) {
      Date date = new Date();
      try {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Validate ATS Database");
         for (Artifact artifact : artifacts) {

            try {
               // Test for null attribute values
               for (Attribute<?> attr : artifact.getAttributes()) {
                  if (attr.getValue() == null) {
                     testNameToResultsMap.put(
                        "testAtsAttributeValues",
                        "Error: Artifact: " + XResultDataUI.getHyperlink(artifact) + " Types: " + artifact.getArtifactTypeName() + " - Null Attribute");
                     if (fixAttributeValues) {
                        attr.delete();
                     }
                  }
               }

               if (artifact instanceof AbstractWorkflowArtifact) {
                  checkAndResolveDuplicateAttributes(artifact, fixAttributeValues, testNameToResultsMap, transaction);
               }

               if (artifact.hasDirtyAttributes()) {
                  artifact.persist(transaction);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
               testNameToResultsMap.put(
                  "testAtsAttributeValues",
                  "Error: Artifact: " + XResultDataUI.getHyperlink(artifact) + " Exception: " + ex.getLocalizedMessage());
            }
         }
         transaction.execute();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         testNameToResultsMap.put("testAtsAttributeValues", "Error: Exception: " + ex.getLocalizedMessage());
      }
      logTestTimeSpent(date, "testAtsAttributeValues", testNameToTimeSpentMap);
   }

   private void checkAndResolveDuplicateAttributes(Artifact artifact, boolean fixAttributeValues, HashCollection<String, String> resultsMap, SkynetTransaction transaction) throws OseeCoreException {
      for (AttributeType attrType : artifact.getAttributeTypesUsed()) {
         int count = artifact.getAttributeCount(attrType);
         if (count > attrType.getMaxOccurrences()) {
            String result =
               String.format(
                  "Error: Artifact: " + XResultDataUI.getHyperlink(artifact) + " Type [%s] AttrType [%s] Max [%d] Actual [%d] Values [%s] ",
                  artifact.getArtifactTypeName(), attrType.getName(), attrType.getMaxOccurrences(), count,
                  artifact.getAttributesToString(attrType));
            Map<String, Attribute<?>> valuesAttrMap = new HashMap<String, Attribute<?>>();
            int latestGamma = 0;
            StringBuffer fixInfo = new StringBuffer(" - FIX AVAILABLE");
            for (Attribute<?> attr : artifact.getAttributes(attrType)) {
               if (attr.getGammaId() > latestGamma) {
                  latestGamma = attr.getGammaId();
               }
               String info = String.format("[Gamma [%s] Value [%s]]", attr.getGammaId(), attr.getValue());
               valuesAttrMap.put(info, attr);
               fixInfo.append(info);
            }
            fixInfo.append(" - KEEP Gamma");
            fixInfo.append(latestGamma);
            if (latestGamma != 0) {
               result += fixInfo;
               if (fixAttributeValues) {
                  for (Attribute<?> attr : artifact.getAttributes(attrType)) {
                     if (attr.getGammaId() != latestGamma) {
                        attr.delete();
                     }
                  }
                  artifact.persist(transaction);
                  resultsMap.put("checkAndResolveDuplicateAttributesForAttributeNameContains", "Fixed");
               }
            }
            resultsMap.put("checkAndResolveDuplicateAttributesForAttributeNameContains", result);
         }
      }
   }

   private void testAtsActionsHaveTeamWorkflow(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         try {
            if (artifact.isOfType(AtsArtifactTypes.Action) && ActionManager.getTeams(artifact).isEmpty()) {
               testNameToResultsMap.put("testAtsActionsHaveTeamWorkflow",
                  "Error: Action " + XResultDataUI.getHyperlink(artifact) + " has no Team Workflows\n");
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            testNameToResultsMap.put("testAtsActionsHaveTeamWorkflow", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      logTestTimeSpent(date, "testAtsActionsHaveTeamWorkflow", testNameToTimeSpentMap);
   }

   private void testAtsWorkflowsHaveAction(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            try {
               if (((TeamWorkFlowArtifact) artifact).getParentActionArtifact() == null) {
                  testNameToResultsMap.put("testAtsWorkflowsHaveAction",
                     "Error: Team " + XResultDataUI.getHyperlink(artifact) + " has no parent Action\n");
               }
            } catch (Exception ex) {
               testNameToResultsMap.put("testAtsWorkflowsHaveAction",
                  "Error: Team " + XResultDataUI.getHyperlink(artifact) + " has no parent Action: exception " + ex);
            }
         }
      }
      logTestTimeSpent(date, "testAtsWorkflowsHaveAction", testNameToTimeSpentMap);
   }

   private void testAtsWorkflowsHaveZeroOrOneVersion(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         try {
            if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
               if (teamArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version).size() > 1) {
                  testNameToResultsMap.put(
                     "testAtsWorkflowsHaveZeroOrOneVersion",
                     "Error: Team workflow " + XResultDataUI.getHyperlink(teamArt) + " has " + teamArt.getRelatedArtifacts(
                        AtsRelationTypes.TeamWorkflowTargetedForVersion_Version).size() + " versions");
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            testNameToResultsMap.put("testAtsWorkflowsHaveZeroOrOneVersion",
               "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      logTestTimeSpent(date, "testAtsWorkflowsHaveZeroOrOneVersion", testNameToTimeSpentMap);
   }

   private void testTasksHaveParentWorkflow(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         try {
            if (artifact.isOfType(AtsArtifactTypes.Task)) {
               TaskArtifact taskArtifact = (TaskArtifact) artifact;
               if (taskArtifact.getRelatedArtifactsCount(AtsRelationTypes.SmaToTask_Sma) != 1) {
                  testNameToResultsMap.put(
                     "testTasksHaveParentWorkflow",
                     "Error: Task " + XResultDataUI.getHyperlink(taskArtifact) + " has " + taskArtifact.getRelatedArtifacts(
                        AtsRelationTypes.SmaToTask_Sma).size() + " parents.");
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            testNameToResultsMap.put("testTasksHaveParentWorkflow", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      logTestTimeSpent(date, "testTasksHaveParentWorkflow", testNameToTimeSpentMap);
   }

   public void testActionableItemToTeamDefinition(Collection<Artifact> artifacts) {
      Date date = new Date();
      testActionableItemToTeamDefinition(testNameToResultsMap, artifacts);
      logTestTimeSpent(date, "testActionableItemToTeamDefinition", testNameToTimeSpentMap);
   }

   public static void testActionableItemToTeamDefinition(HashCollection<String, String> testNameToResultsMap, Collection<Artifact> artifacts) {
      for (Artifact artifact : artifacts) {
         try {
            if (artifact instanceof ActionableItemArtifact) {
               ActionableItemArtifact aia = (ActionableItemArtifact) artifact;
               if (aia.isActionable() && TeamDefinitionManagerCore.getImpactedTeamDefs(Arrays.asList(aia)).isEmpty()) {
                  testNameToResultsMap.put(
                     "testActionableItemToTeamDefinition",
                     "Error: ActionableItem " + XResultDataUI.getHyperlink(artifact.getName(), artifact) + " has to related TeamDefinition and is set to Actionable");
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            testNameToResultsMap.put("testActionableItemToTeamDefinition",
               "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
   }

   private void testReviewsHaveValidDefectAndRoleXml(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         if (artifact instanceof AbstractReviewArtifact) {
            AbstractReviewArtifact reviewArtifact = (AbstractReviewArtifact) artifact;
            try {
               if (reviewArtifact.getAttributes(AtsAttributeTypes.ReviewDefect).size() > 0 && ReviewDefectManager.getDefectItems(
                  reviewArtifact).isEmpty()) {
                  testNameToResultsMap.put(
                     "testReviewsHaveValidDefectAndRoleXml",
                     "Error: Review " + XResultDataUI.getHyperlink(reviewArtifact) + " has defect attribute, but no defects (xml parsing error).");
               }
               if (reviewArtifact.getAttributes(AtsAttributeTypes.Role).size() > 0 && UserRoleManager.getUserRoles(
                  reviewArtifact).isEmpty()) {
                  testNameToResultsMap.put(
                     "testReviewsHaveValidDefectAndRoleXml",
                     "Error: Review " + XResultDataUI.getHyperlink(reviewArtifact) + " has role attribute, but no roles (xml parsing error).");
               }
            } catch (OseeCoreException ex) {
               testNameToResultsMap.put(
                  "testReviewsHaveValidDefectAndRoleXml",
                  "Error: Exception processing Review " + XResultDataUI.getHyperlink(reviewArtifact) + " defect test " + ex.getLocalizedMessage());
            }
         }
      }
      logTestTimeSpent(date, "testReviewsHaveValidDefectAndRoleXml", testNameToTimeSpentMap);
   }

   private void testReviewsHaveParentWorkflowOrActionableItems(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact artifact : artifacts) {
         try {
            if (artifact instanceof AbstractReviewArtifact) {
               AbstractReviewArtifact reviewArtifact = (AbstractReviewArtifact) artifact;
               if (reviewArtifact.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowToReview_Team).isEmpty() && reviewArtifact.getActionableItemsDam().getActionableItemGuids().isEmpty()) {
                  testNameToResultsMap.put(
                     "testReviewsHaveParentWorkflowOrActionableItems",
                     "Error: Review " + XResultDataUI.getHyperlink(reviewArtifact) + " has 0 related parents and 0 actionable items.");
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            testNameToResultsMap.put("testTeamDefinitionHasWorkflow", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      logTestTimeSpent(date, "testReviewsHaveParentWorkflowOrActionableItems", testNameToTimeSpentMap);
   }

   private void testAtsLogs(Collection<Artifact> artifacts) {
      Date date = new Date();
      for (Artifact art : artifacts) {
         try {
            if (art instanceof AbstractWorkflowArtifact) {
               AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) art;
               try {
                  AtsLog log = awa.getLog();
                  if (awa.getCreatedBy() == null) {
                     try {
                        testNameToResultsMap.put(
                           "testAtsLogs",
                           "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " originator == null");
                     } catch (Exception ex) {
                        testNameToResultsMap.put(
                           "testAtsLogs",
                           "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " exception accessing originator: " + ex.getLocalizedMessage());
                     }
                  }
                  for (IWorkPage state : Arrays.asList(TeamState.Completed, TeamState.Cancelled)) {
                     if (awa.isInState(state)) {
                        LogItem logItem = awa.getStateStartedData(state);
                        if (logItem == null) {
                           try {
                              testNameToResultsMap.put(
                                 "testAtsLogs",
                                 "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " state \"" + state + "\" logItem == null");
                           } catch (Exception ex) {
                              testNameToResultsMap.put(
                                 "testAtsLogs",
                                 "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " exception accessing logItem: " + ex.getLocalizedMessage());

                           }
                        } else if (logItem.getDate() == null) {
                           try {
                              testNameToResultsMap.put(
                                 "testAtsLogs",
                                 "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " state \"" + state + "\" logItem.date == null");
                           } catch (Exception ex) {
                              testNameToResultsMap.put(
                                 "testAtsLogs",
                                 "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " exception accessing logItem.date: " + ex.getLocalizedMessage());

                           }
                        }
                     }
                  }
                  // Generate html log which will exercise all the conversions
                  log.getHtml();
                  // Verify that all users are resolved
                  for (LogItem logItem : awa.getLog().getLogItems()) {
                     if (logItem.getUser() == null) {
                        testNameToResultsMap.put(
                           "testAtsLogs",
                           "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " user == null for userId \"" + logItem.getUserId() + "\"");
                     }
                  }
               } catch (Exception ex) {
                  testNameToResultsMap.put(
                     "testAtsLogs",
                     "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " exception accessing AtsLog: " + ex.getLocalizedMessage());
               }
            }
         } catch (Exception ex) {
            testNameToResultsMap.put(
               "testAtsLogs",
               "Error: " + art.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(art) + " exception accessing logItem: " + ex.getLocalizedMessage());

         }

      }
      logTestTimeSpent(date, "testAtsLogs", testNameToTimeSpentMap);
   }

   private static User unAssignedUser;
   private static User oseeSystemUser;

   private void testStateMachineAssignees(Collection<Artifact> artifacts) {
      Date date = new Date();
      if (unAssignedUser == null) {
         try {
            unAssignedUser = UserManager.getUser(SystemUser.UnAssigned);
            oseeSystemUser = UserManager.getUser(SystemUser.OseeSystem);
         } catch (OseeCoreException ex) {
            testNameToResultsMap.put("testStateMachineAssignees",
               "Error: Exception retrieving users: " + ex.getLocalizedMessage());
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      for (Artifact art : artifacts) {
         if (art instanceof AbstractWorkflowArtifact) {
            try {
               AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) art;
               if ((awa.isCompleted() || awa.isCancelled()) && awa.getStateMgr().getAssignees().size() > 0) {
                  testNameToResultsMap.put(
                     "testStateMachineAssignees",
                     "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " cancel/complete with attribute assignees");
                  if (fixAssignees) {
                     awa.getStateMgr().clearAssignees();
                     awa.persist(getClass().getSimpleName());
                     testNameToResultsMap.put("testStateMachineAssignees", "Fixed");
                  }
               }
               if (awa.getStateMgr().getAssignees().size() > 1 && awa.getStateMgr().getAssignees().contains(
                  unAssignedUser)) {
                  testNameToResultsMap.put(
                     "testStateMachineAssignees",
                     "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " is unassigned and assigned => " + Artifacts.toString(
                        "; ", awa.getStateMgr().getAssignees()));
                  if (fixAssignees) {
                     awa.getStateMgr().removeAssignee(unAssignedUser);
                     testNameToResultsMap.put("testStateMachineAssignees", "Fixed");
                  }
               }
               if (awa.getStateMgr().getAssignees().contains(oseeSystemUser)) {
                  testNameToResultsMap.put(
                     "testStateMachineAssignees",
                     "Error: " + art.getHumanReadableId() + " is assigned to OseeSystem; invalid assignment - MANUAL FIX REQUIRED");
               }
               if (!awa.isCompleted() && !awa.isCancelled() && awa.getStateMgr().getAssignees().isEmpty()) {
                  testNameToResultsMap.put(
                     "testStateMachineAssignees",
                     "Error: " + awa.getArtifactTypeName() + " " + XResultDataUI.getHyperlink(awa) + " In Work without assignees");
               }
            } catch (OseeCoreException ex) {
               testNameToResultsMap.put("testStateMachineAssignees",
                  "Error: Exception testing assignees: " + ex.getLocalizedMessage());
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
      logTestTimeSpent(date, "testStateMachineAssignees", testNameToTimeSpentMap);
   }

   public void setFixAssignees(boolean fixAssignees) {
      this.fixAssignees = fixAssignees;
   }

   public void setFixAttributeValues(boolean fixAttributeValues) {
      this.fixAttributeValues = fixAttributeValues;
   }

   public void setEmailOnComplete(String emailOnComplete) {
      this.emailOnComplete = emailOnComplete;
   }

}
