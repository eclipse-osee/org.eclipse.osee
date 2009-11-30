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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ATSLog;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskEditorSimpleProvider;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.StateManager;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.ats.util.widgets.XCurrentStateDam;
import org.eclipse.osee.ats.util.widgets.XStateDam;
import org.eclipse.osee.ats.world.WorldXNavigateItemAction;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ValidateAtsDatabase extends WorldXNavigateItemAction {

   private boolean fixAssignees = true;
   private boolean fixAttributeValues = true;
   private final Set<String> hrids = new HashSet<String>();
   private final Map<String, String> legacyPcrIdToParentHrid = new HashMap<String, String>();

   /**
    * @param parent
    * @throws OseeArgumentException
    */
   public ValidateAtsDatabase(XNavigateItem parent) throws OseeArgumentException {
      this("Validate ATS Database", parent);
   }

   public ValidateAtsDatabase(String name, XNavigateItem parent) throws OseeArgumentException {
      super(parent, name, FrameworkImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) {
         return;
      }
      Jobs.startJob(new Report(getName()), true);
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
            rd.report(getName());
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   private HashCollection<String, String> testNameToResultsMap = null;

   public void runIt(IProgressMonitor monitor, XResultData xResultData) throws OseeCoreException {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);

      int count = 0;
      // Break artifacts into blocks so don't run out of memory
      List<Collection<Integer>> artIdLists = null;

      // Un-comment to process whole Common branch - Normal Mode
      artIdLists = loadAtsBranchArtifactIds(xResultData, monitor);

      // Un-comment to process specific artifact from common - Test Mode
      //  artIdLists = Arrays.asList((Collection<Integer>) Arrays.asList(new Integer(524575)));

      if (monitor != null) {
         monitor.beginTask(getName(), artIdLists.size());
      }
      testNameToResultsMap = new HashCollection<String, String>();
      boolean testingTest = false;
      int y = 0;
      for (Collection<Integer> artIdList : artIdLists) {
         // Don't process all lists if just trying to test this report
         if (y++ > 5 && testingTest) {
            break;
         }
         Collection<Artifact> artifactsTemp = ArtifactQuery.getArtifactListFromIds(artIdList, AtsUtil.getAtsBranch());
         Collection<Artifact> artifacts = new ArrayList<Artifact>();
         // Don't process all artifacts if just trying to test this report
         if (testingTest) {
            int x = 0;
            for (Artifact art : artifactsTemp) {
               artifacts.add(art);
               if (x++ > 20) break;
            }
         } else {
            artifacts.addAll(artifactsTemp);
         }
         count += artifacts.size();
         testArtifactIds(artifacts);
         testAtsAttributeValues(artifacts);
         testAtsActionsHaveTeamWorkflow(artifacts);
         testAtsWorkflowsHaveAction(artifacts);
         testAtsWorkflowsHaveZeroOrOneVersion(artifacts);
         testTasksHaveParentWorkflow(artifacts);
         testReviewsHaveParentWorkflowOrActionableItems(artifacts);
         testReviewsHaveValidDefectAndRoleXml(artifacts);
         testTeamWorkflows(artifacts);
         testTeamDefinitions(artifacts);
         testVersionArtifacts(artifacts);
         testStateMachineAssignees(artifacts);
         testAtsLogs(artifacts);
         for (IAtsHealthCheck atsHealthCheck : AtsHealthCheck.getAtsHealthCheckItems()) {
            atsHealthCheck.check(artifacts, testNameToResultsMap);
         }
         if (monitor != null) {
            monitor.worked(1);
         }
      }
      // Log resultMap data into xResultData
      String[] keys = testNameToResultsMap.keySet().toArray(new String[testNameToResultsMap.keySet().size()]);
      Arrays.sort(keys);
      for (String testName : keys) {
         xResultData.log(testName);
         for (String result : testNameToResultsMap.getValues(testName)) {
            xResultData.log(result);
         }
      }
      xResultData.reportSevereLoggingMonitor(monitorLog);
      if (monitor != null) {
         xResultData.log(monitor, "Completed processing " + count + " artifacts.");
      }
   }

   private void testArtifactIds(Collection<Artifact> artifacts) throws OseeCoreException {
      this.hrids.clear();
      this.legacyPcrIdToParentHrid.clear();
      for (Artifact artifact : artifacts) {
         // Check that HRIDs not duplicated on Common branch
         if (hrids.contains(artifact.getHumanReadableId())) {
            testNameToResultsMap.put("testArtifactIds", "Error: Duplicate HRIDs: " + XResultData.getHyperlink(artifact));
         }
         // Check that duplicate Legacy PCR IDs team arts do not exist with different parent actions 
         if (artifact instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
            String legacyPcrId =
                  artifact.getSoleAttributeValueAsString(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName(), null);
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
      }
   }

   private void testVersionArtifacts(Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact art : artifacts) {
         if (art instanceof VersionArtifact) {
            VersionArtifact verArt = (VersionArtifact) art;
            try {
               String parentBranchGuid =
                     verArt.getSoleAttributeValueAsString(ATSAttributes.BASELINE_BRANCH_GUID_ATTRIBUTE.getStoreName(),
                           null);
               if (parentBranchGuid != null) {
                  validateBranchGuid(verArt, parentBranchGuid);
               }
            } catch (Exception ex) {
               testNameToResultsMap.put(
                     "testVersionArtifacts",
                     "Error: " + verArt.getArtifactTypeName() + " " + XResultData.getHyperlink(verArt) + " exception testing testVersionArtifacts: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   private void testTeamDefinitions(Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact art : artifacts) {
         if (art instanceof TeamDefinitionArtifact) {
            TeamDefinitionArtifact teamDef = (TeamDefinitionArtifact) art;
            try {
               String parentBranchGuid =
                     teamDef.getSoleAttributeValueAsString(ATSAttributes.BASELINE_BRANCH_GUID_ATTRIBUTE.getStoreName(),
                           null);
               if (parentBranchGuid != null) {
                  validateBranchGuid(teamDef, parentBranchGuid);
               }
            } catch (Exception ex) {
               testNameToResultsMap.put(
                     "testTeamDefinitionss",
                     "Error: " + teamDef.getArtifactTypeName() + " " + XResultData.getHyperlink(teamDef) + " exception testing testTeamDefinitions: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   private void testTeamWorkflows(Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact art : artifacts) {
         if (art instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;
            try {
               if (teamArt.getActionableItemsDam().getActionableItems().size() == 0) {
                  testNameToResultsMap.put("testTeamWorkflows",
                        "Error: TeamWorkflow " + XResultData.getHyperlink(teamArt) + " has 0 ActionableItems");
               }
               if (teamArt.getTeamDefinition() == null) {
                  testNameToResultsMap.put("testTeamWorkflows",
                        "Error: TeamWorkflow " + XResultData.getHyperlink(teamArt) + " has no TeamDefinition");
               }
            } catch (Exception ex) {
               testNameToResultsMap.put(
                     "testTeamWorkflows",
                     teamArt.getArtifactTypeName() + " " + XResultData.getHyperlink(teamArt) + " exception testing testTeamWorkflows: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   private void validateBranchGuid(Artifact art, String parentBranchGuid) throws OseeCoreException {
      try {
         Branch branch = BranchManager.getBranchByGuid(parentBranchGuid);
         if (branch.getArchiveState().isArchived()) {
            testNameToResultsMap.put("validateBranchGuid", String.format(
                  "Error: Parent Branch Id [%s][%s] can't be Archived branch for [%s][%s]", parentBranchGuid, branch,
                  art.getHumanReadableId(), art));
         } else if (branch.getBranchType().isWorkingBranch()) {
            testNameToResultsMap.put(
                  "validateBranchGuid",
                  String.format(
                        "Error: Parent Branch [%s][%s] is WORKING branch and can't be parent branch for [%s][%s]; Switch to BASELINE?",
                        parentBranchGuid, branch, art.getHumanReadableId(), art));
         } else if (!branch.getBranchType().isBaselineBranch()) {
            testNameToResultsMap.put("validateBranchGuid", String.format(
                  "Error: Parent Branch Id [%s][%s] must be Baseline branch for [%s][%s]", parentBranchGuid, branch,
                  art.getHumanReadableId(), art));
         }
      } catch (BranchDoesNotExist ex) {
         testNameToResultsMap.put("validateBranchGuid", String.format(
               "Error: Parent Branch Id [%s] references non-existant branch for [%s][%s]", parentBranchGuid,
               art.getHumanReadableId(), art));
      }
   }

   public static List<Collection<Integer>> loadAtsBranchArtifactIds(XResultData xResultData, IProgressMonitor monitor) throws OseeCoreException {
      if (xResultData == null) {
         xResultData = new XResultData();
      }
      xResultData.log(monitor, "testLoadAllCommonArtifactIds - Started " + XDate.getDateNow(XDate.MMDDYYHHMM));
      List<Integer> artIds = ArtifactQuery.selectArtifactListFromBranch(AtsUtil.getAtsBranch(), false);

      if (artIds.size() == 0) {
         xResultData.logError("Error: Artifact load returned 0 artifacts to check");
      }
      xResultData.log(monitor, "testLoadAllCommonArtifactIds - Completed " + XDate.getDateNow(XDate.MMDDYYHHMM));
      return Collections.subDivide(artIds, 5000);
   }

   private void testAtsAttributeValues(Collection<Artifact> artifacts) throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Validate ATS Database");
      for (Artifact artifact : artifacts) {

         // Test for null attribute values 
         for (Attribute<?> attr : artifact.getAttributes()) {
            if (attr.getValue() == null) {
               testNameToResultsMap.put(
                     "testAtsAttributeValues",
                     "Error: Artifact: " + XResultData.getHyperlink(artifact) + " Types: " + artifact.getArtifactTypeName() + " - Null Attribute");
               if (fixAttributeValues) {
                  attr.delete();
               }
            }
         }

         if (artifact instanceof StateMachineArtifact) {
            checkAndResolveDuplicateAttributesForAttributeNameContains("ats", artifact, fixAttributeValues,
                  testNameToResultsMap, transaction);
         }

         // Test for ats.State Completed;;;<num> or Cancelled;;;<num> and cleanup
         if (artifact instanceof StateMachineArtifact) {
            XStateDam stateDam = new XStateDam((StateMachineArtifact) artifact);
            for (SMAState state : stateDam.getStates()) {
               if (state.getName().equals(DefaultTeamState.Completed.name()) || state.getName().equals(
                     DefaultTeamState.Cancelled.name())) {
                  if (state.getHoursSpent() != 0.0 || state.getPercentComplete() != 0) {
                     testNameToResultsMap.put(
                           "testAtsAttributeValues",
                           "Error: ats.State error for SMA: " + XResultData.getHyperlink(artifact) + " State: " + state.getName() + " Hours Spent: " + state.getHoursSpentStr() + " Percent: " + state.getPercentComplete());
                     if (fixAttributeValues) {
                        state.setHoursSpent(0);
                        state.setPercentComplete(0);
                        stateDam.setState(state);
                        testNameToResultsMap.put("testAtsAttributeValues", "Fixed");
                     }
                  }
               }
            }
         }

         // Test for ats.CurrentState Completed;;;<num> or Cancelled;;;<num> and cleanup
         if (artifact instanceof StateMachineArtifact) {
            XCurrentStateDam currentStateDam = new XCurrentStateDam((StateMachineArtifact) artifact);
            SMAState state = currentStateDam.getState();
            if (state.getName().equals(DefaultTeamState.Completed.name()) || state.getName().equals(
                  DefaultTeamState.Cancelled.name())) {
               if (state.getHoursSpent() != 0.0 || state.getPercentComplete() != 0) {
                  testNameToResultsMap.put(
                        "testAtsAttributeValues",
                        "Error: ats.CurrentState error for SMA: " + XResultData.getHyperlink(artifact) + " State: " + state.getName() + " Hours Spent: " + state.getHoursSpentStr() + " Percent: " + state.getPercentComplete());
                  if (fixAttributeValues) {
                     state.setHoursSpent(0);
                     state.setPercentComplete(0);
                     currentStateDam.setState(state);
                     testNameToResultsMap.put("testAtsAttributeValues", "Fixed");
                  }
               }
            }
         }
         if (artifact.hasDirtyAttributes()) {
            artifact.persist(transaction);
         }
      }
      transaction.execute();
   }

   public static void checkAndResolveDuplicateAttributesForAttributeNameContains(String nameContainsStr, Artifact artifact, boolean fixAttributeValues, HashCollection<String, String> resultsMap, SkynetTransaction transaction) throws OseeCoreException {
      for (AttributeType attrType : AttributeTypeManager.getAllTypes()) {
         if (attrType.getName().contains(nameContainsStr)) {
            int count = artifact.getAttributeCount(attrType.getName());
            if (count > attrType.getMaxOccurrences()) {
               String result =
                     String.format(
                           "Error: Artifact: " + XResultData.getHyperlink(artifact) + " Type [%s] AttrType [%s] Max [%d] Actual [%d] Values [%s] ",
                           artifact.getArtifactTypeName(), attrType.getName(), attrType.getMaxOccurrences(), count,
                           artifact.getAttributesToString(attrType.getName()));
               Map<String, Attribute<?>> valuesAttrMap = new HashMap<String, Attribute<?>>();
               int latestGamma = 0;
               String fixInfo = " - FIX AVAILABLE";
               for (Attribute<?> attr : artifact.getAttributes(attrType.getName())) {
                  if (attr.getGammaId() > latestGamma) {
                     latestGamma = attr.getGammaId();
                  }
                  String info = String.format("[Gamma [%s] Value [%s]]", attr.getGammaId(), attr.getValue());
                  valuesAttrMap.put(info, attr);
                  fixInfo += info;
               }
               fixInfo += " - KEEP Gamma" + latestGamma;
               if (latestGamma != 0) {
                  result += fixInfo;
                  if (fixAttributeValues) {
                     for (Attribute<?> attr : artifact.getAttributes(attrType.getName())) {
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
   }

   private static Attribute<?> getLatestAttribute(List<Attribute<Object>> attributes) {
      int gammaId = 0;
      for (Attribute<?> attr : attributes) {
         if (attr.getGammaId() > gammaId) {
            gammaId = attr.getGammaId();
         }
      }
      if (gammaId != 0) {
         for (Attribute<?> attr : attributes) {
            if (attr.getGammaId() == gammaId) {
               return attr;
            }
         }
      }
      return null;
   }

   private void testAtsActionsHaveTeamWorkflow(Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         if (artifact instanceof ActionArtifact) {
            if (((ActionArtifact) artifact).getTeamWorkFlowArtifacts().size() == 0) {
               testNameToResultsMap.put("testAtsActionsHaveTeamWorkflow",
                     "Error: Action " + XResultData.getHyperlink(artifact) + " has no Team Workflows\n");
            }
         }
      }
   }

   private void testAtsWorkflowsHaveAction(Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TeamWorkFlowArtifact) {
            try {
               if (((TeamWorkFlowArtifact) artifact).getParentActionArtifact() == null) {
                  testNameToResultsMap.put("testAtsWorkflowsHaveAction",
                        "Error: Team " + XResultData.getHyperlink(artifact) + " has no parent Action\n");
               }
            } catch (Exception ex) {
               testNameToResultsMap.put("testAtsWorkflowsHaveAction",
                     "Error: Team " + XResultData.getHyperlink(artifact) + " has no parent Action: exception " + ex);
            }
         }
      }
   }

   private void testAtsWorkflowsHaveZeroOrOneVersion(Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
            if (teamArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version).size() > 1) {
               testNameToResultsMap.put(
                     "testAtsWorkflowsHaveZeroOrOneVersion",
                     "Error: Team workflow " + XResultData.getHyperlink(teamArt) + " has " + teamArt.getRelatedArtifacts(
                           AtsRelationTypes.TeamWorkflowTargetedForVersion_Version).size() + " versions");
            }
         }
      }

   }

   private void testTasksHaveParentWorkflow(Collection<Artifact> artifacts) throws OseeCoreException {
      Set<Artifact> badTasks = new HashSet<Artifact>(30);
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TaskArtifact) {
            TaskArtifact taskArtifact = (TaskArtifact) artifact;
            if (taskArtifact.getRelatedArtifacts(AtsRelationTypes.SmaToTask_Sma).size() != 1) {
               testNameToResultsMap.put(
                     "testTasksHaveParentWorkflow",
                     "Error: Task " + XResultData.getHyperlink(taskArtifact) + " has " + taskArtifact.getRelatedArtifacts(
                           AtsRelationTypes.SmaToTask_Sma).size() + " parents.");
               badTasks.add(taskArtifact);
            }
         }
      }
      if (badTasks.size() > 0) {
         TaskEditor.open(new TaskEditorSimpleProvider("ValidateATSDatabase: Tasks have !=1 parent workflows.", badTasks));
      }
   }

   private void testReviewsHaveValidDefectAndRoleXml(Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         if (artifact instanceof ReviewSMArtifact) {
            ReviewSMArtifact reviewArtifact = (ReviewSMArtifact) artifact;
            if (reviewArtifact.getAttributes(ATSAttributes.REVIEW_DEFECT_ATTRIBUTE.getStoreName()).size() > 0 && reviewArtifact.getDefectManager().getDefectItems().size() == 0) {
               testNameToResultsMap.put(
                     "testReviewsHaveValidDefectAndRoleXml",
                     "Error: Review " + XResultData.getHyperlink(reviewArtifact) + " has defect attribute, but no defects (xml parsing error).");
            }
            if (reviewArtifact.getAttributes(ATSAttributes.ROLE_ATTRIBUTE.getStoreName()).size() > 0 && reviewArtifact.getUserRoleManager().getUserRoles().size() == 0) {
               testNameToResultsMap.put(
                     "testReviewsHaveValidDefectAndRoleXml",
                     "Error: Review " + XResultData.getHyperlink(reviewArtifact) + " has role attribute, but no roles (xml parsing error).");
            }
         }
      }
   }

   private void testReviewsHaveParentWorkflowOrActionableItems(Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         if (artifact instanceof ReviewSMArtifact) {
            ReviewSMArtifact reviewArtifact = (ReviewSMArtifact) artifact;
            if (reviewArtifact.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowToReview_Team).size() == 0 && reviewArtifact.getActionableItemsDam().getActionableItemGuids().size() == 0) {
               testNameToResultsMap.put(
                     "testReviewsHaveParentWorkflowOrActionableItems",
                     "Error: Review " + XResultData.getHyperlink(reviewArtifact) + " has 0 related parents and 0 actionable items.");
            }
         }
      }
   }

   private void testAtsLogs(Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact art : artifacts) {
         if (art instanceof StateMachineArtifact) {
            StateMachineArtifact sma = (StateMachineArtifact) art;
            try {
               ATSLog log = sma.getSmaMgr().getLog();
               if (log.getOriginator() == null) {
                  try {
                     testNameToResultsMap.put(
                           "testAtsLogs",
                           "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " originator == null");
                  } catch (Exception ex) {
                     testNameToResultsMap.put(
                           "testAtsLogs",
                           "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " exception accessing originator: " + ex.getLocalizedMessage());
                  }
               }
               for (String stateName : Arrays.asList("Completed", "Cancelled")) {
                  if (sma.getSmaMgr().getStateMgr().getCurrentStateName().equals(stateName)) {
                     LogItem logItem = log.getStateEvent(LogType.StateEntered, stateName);
                     if (logItem == null) {
                        try {
                           testNameToResultsMap.put(
                                 "testAtsLogs",
                                 "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " state \"" + stateName + "\" logItem == null");
                        } catch (Exception ex) {
                           testNameToResultsMap.put(
                                 "testAtsLogs",
                                 "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " exception accessing logItem: " + ex.getLocalizedMessage());

                        }
                     }
                     if (logItem.getDate() == null) {
                        try {
                           testNameToResultsMap.put(
                                 "testAtsLogs",
                                 "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " state \"" + stateName + "\" logItem.date == null");
                        } catch (Exception ex) {
                           testNameToResultsMap.put(
                                 "testAtsLogs",
                                 "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " exception accessing logItem.date: " + ex.getLocalizedMessage());

                        }
                     }
                  }
               }
               // Generate html log which will exercise all the conversions
               log.getHtml();
               // Verify that all users are resolved
               for (LogItem logItem : sma.getSmaMgr().getLog().getLogItems()) {
                  if (logItem.getUser() == null) {
                     testNameToResultsMap.put(
                           "testAtsLogs",
                           "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " user == null for userId \"" + logItem.getUserId() + "\"");
                  }
               }
            } catch (Exception ex) {
               testNameToResultsMap.put(
                     "testAtsLogs",
                     "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " exception accessing AtsLog: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   private void testStateMachineAssignees(Collection<Artifact> artifacts) throws OseeCoreException {
      User unAssignedUser = UserManager.getUser(SystemUser.UnAssigned);
      User oseeSystemUser = UserManager.getUser(SystemUser.OseeSystem);
      for (Artifact art : artifacts) {
         if (art instanceof StateMachineArtifact) {
            try {
               StateMachineArtifact sma = (StateMachineArtifact) art;
               SMAManager smaMgr = new SMAManager(sma);
               if ((smaMgr.isCompleted() || smaMgr.isCancelled()) && smaMgr.getStateMgr().getAssignees().size() > 0) {
                  testNameToResultsMap.put(
                        "testStateMachineAssignees",
                        "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " cancel/complete with attribute assignees");
                  if (fixAssignees) {
                     smaMgr.getStateMgr().clearAssignees();
                     smaMgr.getSma().persist();
                     testNameToResultsMap.put("testStateMachineAssignees", "Fixed");
                  }
               }
               if (smaMgr.getStateMgr().getAssignees().size() > 1 && smaMgr.getStateMgr().getAssignees().contains(
                     unAssignedUser)) {
                  testNameToResultsMap.put(
                        "testStateMachineAssignees",
                        "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " is unassigned and assigned => " + Artifacts.toString(
                              "; ", smaMgr.getStateMgr().getAssignees()));
                  if (fixAssignees) {
                     smaMgr.getStateMgr().removeAssignee(unAssignedUser);
                     testNameToResultsMap.put("testStateMachineAssignees", "Fixed");
                  }
               }
               if (smaMgr.getStateMgr().getAssignees().contains(oseeSystemUser)) {
                  testNameToResultsMap.put(
                        "testStateMachineAssignees",
                        "Error: " + art.getHumanReadableId() + " is assigned to OseeSystem; invalid assignment - MANUAL FIX REQUIRED");
               }
               if (!smaMgr.isCompleted() && !smaMgr.isCancelled() && smaMgr.getStateMgr().getAssignees().size() == 0) {
                  testNameToResultsMap.put(
                        "testStateMachineAssignees",
                        "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " In Work without assignees");
               }
               List<Artifact> relationAssigned =
                     art.getRelatedArtifacts(CoreRelationTypes.Users_User, Artifact.class);
               if ((smaMgr.isCompleted() || smaMgr.isCancelled()) && relationAssigned.size() > 0) {
                  testNameToResultsMap.put(
                        "testStateMachineAssignees",
                        "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " cancel/complete with related assignees");
                  if (fixAssignees) {
                     try {
                        StateManager.updateAssigneeRelations(smaMgr);
                        art.persist();
                     } catch (OseeCoreException ex) {
                        OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                     }
                     testNameToResultsMap.put("testStateMachineAssignees", "Fixed");
                  }
               } else if (smaMgr.getStateMgr().getAssignees().size() != relationAssigned.size()) {
                  // Make sure this isn't just an UnAssigned user issue (don't relate to unassigned user anymore)
                  if (!(smaMgr.getStateMgr().getAssignees().contains(UserManager.getUser(SystemUser.UnAssigned)) && relationAssigned.size() == 0)) {
                     testNameToResultsMap.put(
                           "testStateMachineAssignees",
                           "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " attribute assignees doesn't match related assignees");
                     if (fixAssignees) {
                        try {
                           StateManager.updateAssigneeRelations(smaMgr);
                           art.persist();
                        } catch (OseeCoreException ex) {
                           OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                        }
                        testNameToResultsMap.put("testStateMachineAssignees", "Fixed");
                     }
                  }
               }
            } catch (OseeCoreException ex) {
               testNameToResultsMap.put("testStateMachineAssignees",
                     "Error: Exception testing assignees: " + ex.getLocalizedMessage());
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      }
   }

   /**
    * @param fixAssignees the fixAssignees to set
    */
   public void setFixAssignees(boolean fixAssignees) {
      this.fixAssignees = fixAssignees;
   }

   /**
    * @param fixAttributeValues the fixAttributeValues to set
    */
   public void setFixAttributeValues(boolean fixAttributeValues) {
      this.fixAttributeValues = fixAttributeValues;
   }

}
