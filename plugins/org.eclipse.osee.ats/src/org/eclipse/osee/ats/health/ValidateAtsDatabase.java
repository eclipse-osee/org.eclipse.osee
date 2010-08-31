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

import static org.eclipse.osee.framework.skynet.core.artifact.DeletionFlag.EXCLUDE_DELETED;
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
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.artifact.ATSLog;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskEditorSimpleProvider;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.ats.util.widgets.XCurrentStateDam;
import org.eclipse.osee.ats.util.widgets.XStateDam;
import org.eclipse.osee.ats.world.WorldXNavigateItemAction;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.OseeGroup;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.skynet.core.utility.ElapsedTime;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailUtil;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class ValidateAtsDatabase extends WorldXNavigateItemAction {

   private boolean fixAssignees = true;
   private boolean fixAttributeValues = true;
   private final Set<String> hrids = new HashSet<String>();
   private final Map<String, String> legacyPcrIdToParentHrid = new HashMap<String, String>();
   private String emailOnComplete = null;
   private static ActionArtifact tempParentAction;

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

            // TODO this can be removed after 0.9.6; all relations between smas and user artifacts can be purged after 0.9.6
            removeRelatedAssignees(rd);

            runIt(monitor, rd);
            rd.report(getName());
            if (Strings.isValid(emailOnComplete)) {
               String html = rd.getReport(getName()).getManipulatedHtml();
               EmailUtil.emailHtml(java.util.Collections.singleton(emailOnComplete),
                  String.format("Sync - %s [%s]", DateUtil.getDateNow(), getName()), html);
            }
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
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
      ElapsedTime elapsedTime = new ElapsedTime("ValidateAtsDatabase - load ArtIds");
      artIdLists = loadAtsBranchArtifactIds(xResultData, monitor);
      elapsedTime.end();

      // Un-comment to process specific artifact from common - Test Mode
      // artIdLists = Arrays.asList((Collection<Integer>) Arrays.asList(new Integer(524575)));

      if (monitor != null) {
         monitor.beginTask(getName(), artIdLists.size());
      }
      testNameToResultsMap = new HashCollection<String, String>();
      boolean testingTest = false;
      int y = 0, artSetNum = 1;
      for (Collection<Integer> artIdList : artIdLists) {
         // Don't process all lists if just trying to test this report
         if (y++ > 5 && testingTest) {
            break;
         }
         elapsedTime =
            new ElapsedTime(String.format("ValidateAtsDatabase - load Artifact set %d/%d", artSetNum++,
               artIdLists.size()));
         Collection<Artifact> artifactsTemp = ArtifactQuery.getArtifactListFromIds(artIdList, AtsUtil.getAtsBranch());
         elapsedTime.end();
         Collection<Artifact> artifacts = new ArrayList<Artifact>();
         // Don't process all artifacts if just trying to test this report
         if (testingTest) {
            int x = 0;
            for (Artifact art : artifactsTemp) {
               artifacts.add(art);
               if (x++ > 20) {
                  break;
               }
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
         testAtsBranchManager(artifacts);
         testTeamDefinitions(artifacts);
         testVersionArtifacts(artifacts);
         testStateMachineAssignees(artifacts);
         testAtsLogs(artifacts);
         testActionableItemToTeamDefinition(artifacts);
         testTeamDefinitionHasWorkflow(artifacts);
         for (IAtsHealthCheck atsHealthCheck : AtsHealthCheck.getAtsHealthCheckItems()) {
            atsHealthCheck.validateAtsDatabase(artifacts, testNameToResultsMap);
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

   // TODO this can be removed after 0.9.6; all relations between smas and user artifacts can be purged after 0.9.6
   private void removeRelatedAssignees(XResultData rd) {
      try {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "remove related assignees");
         OseeGroup atsAdminGroup = AtsUtil.getAtsAdminGroup();
         rd.log("Only removing assignee relations for ATS Admin first...switch to all after a few days 8/31");
         for (User user : UserManager.getUsers()) {
            if (!atsAdminGroup.isMember(user)) {
               continue;
            }
            Set<Artifact> smasToRemove = new HashSet<Artifact>();
            for (Artifact art : user.getRelatedArtifacts(CoreRelationTypes.Users_Artifact)) {
               if (art instanceof StateMachineArtifact) {
                  user.deleteRelation(CoreRelationTypes.Users_Artifact, art);
                  smasToRemove.add(art);
               }
            }
            if (smasToRemove.size() > 0) {
               rd.log(String.format("Removed [%d] sma relations from [%s]", smasToRemove.size(), user));
               user.persist(transaction);
            }
         }
         transaction.execute();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void testArtifactIds(Collection<Artifact> artifacts) {
      this.hrids.clear();
      this.legacyPcrIdToParentHrid.clear();
      for (Artifact artifact : artifacts) {
         try {
            // Check that HRIDs not duplicated on Common branch
            if (hrids.contains(artifact.getHumanReadableId())) {
               testNameToResultsMap.put("testArtifactIds",
                  "Error: Duplicate HRIDs: " + XResultData.getHyperlink(artifact));
            }
            // Check that duplicate Legacy PCR IDs team arts do not exist with different parent actions
            if (artifact instanceof TeamWorkFlowArtifact) {
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
               "Error: " + artifact.getArtifactTypeName() + " " + XResultData.getHyperlink(artifact) + " exception: " + ex.getLocalizedMessage());
         }
      }
   }

   private void testVersionArtifacts(Collection<Artifact> artifacts) {
      for (Artifact artifact : artifacts) {
         if (artifact instanceof VersionArtifact) {
            VersionArtifact verArt = (VersionArtifact) artifact;
            try {
               String parentBranchGuid =
                  verArt.getSoleAttributeValueAsString(AtsAttributeTypes.BaselineBranchGuid, null);
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

   private void testTeamDefinitions(Collection<Artifact> artifacts) {
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
                  "Error: " + teamDef.getArtifactTypeName() + " " + XResultData.getHyperlink(teamDef) + " exception testing testTeamDefinitions: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   private void testTeamWorkflows(Collection<Artifact> artifacts) {
      for (Artifact art : artifacts) {
         if (art instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;
            try {
               if (teamArt.getActionableItemsDam().getActionableItems().isEmpty()) {
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
                  teamArt.getArtifactTypeName() + " " + XResultData.getHyperlink(teamArt) + " exception: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   private void testAtsBranchManager(Collection<Artifact> artifacts) {
      for (Artifact art : artifacts) {
         if (art instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;
            AtsBranchManager mgr = teamArt.getBranchMgr();
            try {
               Collection<Branch> branchesCommittedTo = mgr.getBranchesCommittedTo();
               Branch workingBranch = mgr.getWorkingBranch();
               if (workingBranch != null && branchesCommittedTo.size() > 0 && workingBranch.getBranchState() != BranchState.COMMITTED) {
                  testNameToResultsMap.put(
                     "testAtsBranchManagerA",
                     "Error: TeamWorkflow " + XResultData.getHyperlink(teamArt) + " has committed branches but working branch [" + workingBranch.getGuid() + "] != COMMITTED");
               }
               if (workingBranch != null && workingBranch.getBranchState() == BranchState.COMMITTED && workingBranch.getArchiveState() == BranchArchivedState.UNARCHIVED) {
                  String fixStr = "";
                  if (teamArt.isCompleted()) {
                     fixStr = " - Fix: Workflow Completed, Branch can be Archived";
                  } else {
                     fixStr = " - Workflow not completed, verify manually";
                  }
                  testNameToResultsMap.put(
                     "testAtsBranchManagerB",
                     "Error: TeamWorkflow " + XResultData.getHyperlink(teamArt) + " has committed working branch [" + workingBranch.getGuid() + "] but not archived" + fixStr);
               }
            } catch (Exception ex) {
               testNameToResultsMap.put(
                  "testAtsBranchManager",
                  teamArt.getArtifactTypeName() + " " + XResultData.getHyperlink(teamArt) + " exception: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   private void validateBranchGuid(Artifact artifact, String parentBranchGuid) {
      try {
         Branch branch = BranchManager.getBranchByGuid(parentBranchGuid);
         if (branch.getArchiveState().isArchived()) {
            testNameToResultsMap.put("validateBranchGuid", String.format(
               "Error: Parent Branch Id [%s][%s] can't be Archived branch for [%s][%s]", parentBranchGuid, branch,
               artifact.getHumanReadableId(), artifact));
         } else if (branch.getBranchType().isWorkingBranch()) {
            testNameToResultsMap.put(
               "validateBranchGuid",
               String.format(
                  "Error: Parent Branch [%s][%s] is WORKING branch and can't be parent branch for [%s][%s]; Switch to BASELINE?",
                  parentBranchGuid, branch, artifact.getHumanReadableId(), artifact));
         } else if (!branch.getBranchType().isBaselineBranch()) {
            testNameToResultsMap.put("validateBranchGuid", String.format(
               "Error: Parent Branch Id [%s][%s] must be Baseline branch for [%s][%s]", parentBranchGuid, branch,
               artifact.getHumanReadableId(), artifact));
         }
      } catch (BranchDoesNotExist ex) {
         testNameToResultsMap.put("validateBranchGuid", String.format(
            "Error: Parent Branch Id [%s] references non-existant branch for [%s][%s]", parentBranchGuid,
            artifact.getHumanReadableId(), artifact));
      } catch (Exception ex) {
         testNameToResultsMap.put(
            "validateBranchGuid",
            "Error: " + artifact.getArtifactTypeName() + " " + XResultData.getHyperlink(artifact) + " exception: " + ex.getLocalizedMessage());
      }
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
      try {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Validate ATS Database");
         for (Artifact artifact : artifacts) {

            try {
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
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               testNameToResultsMap.put("testAtsAttributeValues",
                  "Error: Artifact: " + XResultData.getHyperlink(artifact) + " Exception: " + ex.getLocalizedMessage());
            }
         }
         transaction.execute();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         testNameToResultsMap.put("testAtsAttributeValues", "Error: Exception: " + ex.getLocalizedMessage());
      }
   }

   public static void checkAndResolveDuplicateAttributesForAttributeNameContains(String nameContainsStr, Artifact artifact, boolean fixAttributeValues, HashCollection<String, String> resultsMap, SkynetTransaction transaction) throws OseeCoreException {
      for (AttributeType attrType : AttributeTypeManager.getAllTypes()) {
         if (attrType.getName().contains(nameContainsStr)) {
            int count = artifact.getAttributeCount(attrType);
            if (count > attrType.getMaxOccurrences()) {
               String result =
                  String.format(
                     "Error: Artifact: " + XResultData.getHyperlink(artifact) + " Type [%s] AttrType [%s] Max [%d] Actual [%d] Values [%s] ",
                     artifact.getArtifactTypeName(), attrType.getName(), attrType.getMaxOccurrences(), count,
                     artifact.getAttributesToString(attrType));
               Map<String, Attribute<?>> valuesAttrMap = new HashMap<String, Attribute<?>>();
               int latestGamma = 0;
               String fixInfo = " - FIX AVAILABLE";
               for (Attribute<?> attr : artifact.getAttributes(attrType)) {
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
   }

   private void testAtsActionsHaveTeamWorkflow(Collection<Artifact> artifacts) {
      for (Artifact artifact : artifacts) {
         try {
            if (artifact instanceof ActionArtifact) {
               if (((ActionArtifact) artifact).getTeamWorkFlowArtifacts().isEmpty()) {
                  testNameToResultsMap.put("testAtsActionsHaveTeamWorkflow",
                     "Error: Action " + XResultData.getHyperlink(artifact) + " has no Team Workflows\n");
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            testNameToResultsMap.put("testAtsActionsHaveTeamWorkflow", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
   }

   private void testAtsWorkflowsHaveAction(Collection<Artifact> artifacts) {
      for (Artifact artifact : artifacts) {
         try {
            if (artifact instanceof TeamWorkFlowArtifact) {
               boolean noParent = false;
               try {
                  if (((TeamWorkFlowArtifact) artifact).getParentActionArtifact() == null) {
                     testNameToResultsMap.put("testAtsWorkflowsHaveAction",
                        "Error: Team " + XResultData.getHyperlink(artifact) + " has no parent Action\n");
                     noParent = true;
                  }
               } catch (Exception ex) {
                  testNameToResultsMap.put("testAtsWorkflowsHaveAction",
                     "Error: Team " + XResultData.getHyperlink(artifact) + " has no parent Action: exception " + ex);
                  noParent = true;
               }
               // Create temporary action so these can be either purged or re-assigned
               if (noParent) {
                  if (tempParentAction == null) {
                     tempParentAction =
                        (ActionArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.Action,
                           AtsUtil.getAtsBranch());
                     tempParentAction.setName("Temp Parent Action");
                     testNameToResultsMap.put(
                        "testAtsWorkflowsHaveAction",
                        "Error: Temp Parent Action " + XResultData.getHyperlink(tempParentAction) + " created for orphaned teams.");

                  }
                  tempParentAction.addRelation(AtsRelationTypes.ActionToWorkflow_WorkFlow, artifact);
                  tempParentAction.persist();
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            testNameToResultsMap.put("testAtsWorkflowsHaveAction", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      if (tempParentAction != null) {
         AtsUtil.openATSAction(tempParentAction, AtsOpenOption.AtsWorld);
      }
   }

   private void testAtsWorkflowsHaveZeroOrOneVersion(Collection<Artifact> artifacts) {
      for (Artifact artifact : artifacts) {
         try {
            if (artifact instanceof TeamWorkFlowArtifact) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
               if (teamArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version).size() > 1) {
                  testNameToResultsMap.put(
                     "testAtsWorkflowsHaveZeroOrOneVersion",
                     "Error: Team workflow " + XResultData.getHyperlink(teamArt) + " has " + teamArt.getRelatedArtifacts(
                        AtsRelationTypes.TeamWorkflowTargetedForVersion_Version).size() + " versions");
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            testNameToResultsMap.put("testAtsWorkflowsHaveZeroOrOneVersion",
               "Error: Exception: " + ex.getLocalizedMessage());
         }
      }

   }

   private void testTasksHaveParentWorkflow(Collection<Artifact> artifacts) {
      Set<Artifact> badTasks = new HashSet<Artifact>(30);
      for (Artifact artifact : artifacts) {
         try {
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
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            testNameToResultsMap.put("testTasksHaveParentWorkflow", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
      try {
         if (badTasks.size() > 0) {
            TaskEditor.open(new TaskEditorSimpleProvider("ValidateATSDatabase: Tasks have !=1 parent workflows.",
               badTasks));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         testNameToResultsMap.put("testTasksHaveParentWorkflow", "Error: Exception: " + ex.getLocalizedMessage());
      }
   }

   private void testActionableItemToTeamDefinition(Collection<Artifact> artifacts) {
      for (Artifact artifact : artifacts) {
         try {
            if (artifact instanceof ActionableItemArtifact) {
               ActionableItemArtifact aia = (ActionableItemArtifact) artifact;
               if (aia.isActionable()) {
                  if (TeamDefinitionArtifact.getImpactedTeamDefs(Arrays.asList(aia)).isEmpty()) {
                     testNameToResultsMap.put(
                        "testActionableItemToTeamDefinition",
                        "Error: ActionableItem " + XResultData.getHyperlink(artifact.getName(), artifact) + " has to related TeamDefinition and is set to Actionable");
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            testNameToResultsMap.put("testActionableItemToTeamDefinition",
               "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
   }

   private void testTeamDefinitionHasWorkflow(Collection<Artifact> artifacts) {
      for (Artifact artifact : artifacts) {
         try {
            if (artifact instanceof TeamDefinitionArtifact) {
               TeamDefinitionArtifact teamDef = (TeamDefinitionArtifact) artifact;
               if (teamDef.isActionable() && teamDef.getWorkFlowDefinition() == null) {
                  testNameToResultsMap.put(
                     "testTeamDefinitionHasWorkflow",
                     "Error: TeamDefintion " + XResultData.getHyperlink(artifact.getName(), artifact) + " has no related workflow and is set to Actionable");
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            testNameToResultsMap.put("testTeamDefinitionHasWorkflow", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
   }

   private void testReviewsHaveValidDefectAndRoleXml(Collection<Artifact> artifacts) {
      for (Artifact artifact : artifacts) {
         if (artifact instanceof ReviewSMArtifact) {
            ReviewSMArtifact reviewArtifact = (ReviewSMArtifact) artifact;
            try {
               if (reviewArtifact.getAttributes(AtsAttributeTypes.ReviewDefect).size() > 0 && reviewArtifact.getDefectManager().getDefectItems().isEmpty()) {
                  testNameToResultsMap.put(
                     "testReviewsHaveValidDefectAndRoleXml",
                     "Error: Review " + XResultData.getHyperlink(reviewArtifact) + " has defect attribute, but no defects (xml parsing error).");
               }
               if (reviewArtifact.getAttributes(AtsAttributeTypes.Role).size() > 0 && reviewArtifact.getUserRoleManager().getUserRoles().isEmpty()) {
                  testNameToResultsMap.put(
                     "testReviewsHaveValidDefectAndRoleXml",
                     "Error: Review " + XResultData.getHyperlink(reviewArtifact) + " has role attribute, but no roles (xml parsing error).");
               }
            } catch (OseeCoreException ex) {
               testNameToResultsMap.put(
                  "testReviewsHaveValidDefectAndRoleXml",
                  "Error: Exception processing Review " + XResultData.getHyperlink(reviewArtifact) + " defect test " + ex.getLocalizedMessage());
            }
         }
      }
   }

   private void testReviewsHaveParentWorkflowOrActionableItems(Collection<Artifact> artifacts) {
      for (Artifact artifact : artifacts) {
         try {
            if (artifact instanceof ReviewSMArtifact) {
               ReviewSMArtifact reviewArtifact = (ReviewSMArtifact) artifact;
               if (reviewArtifact.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowToReview_Team).isEmpty() && reviewArtifact.getActionableItemsDam().getActionableItemGuids().isEmpty()) {
                  testNameToResultsMap.put(
                     "testReviewsHaveParentWorkflowOrActionableItems",
                     "Error: Review " + XResultData.getHyperlink(reviewArtifact) + " has 0 related parents and 0 actionable items.");
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            testNameToResultsMap.put("testTeamDefinitionHasWorkflow", "Error: Exception: " + ex.getLocalizedMessage());
         }
      }
   }

   private void testAtsLogs(Collection<Artifact> artifacts) {
      for (Artifact art : artifacts) {
         if (art instanceof StateMachineArtifact) {
            StateMachineArtifact sma = (StateMachineArtifact) art;
            try {
               ATSLog log = sma.getLog();
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
                  if (sma.getStateMgr().getCurrentStateName().equals(stateName)) {
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
               for (LogItem logItem : sma.getLog().getLogItems()) {
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

   private static User unAssignedUser;
   private static User oseeSystemUser;

   private void testStateMachineAssignees(Collection<Artifact> artifacts) {
      if (unAssignedUser == null) {
         try {
            unAssignedUser = UserManager.getUser(SystemUser.UnAssigned);
            oseeSystemUser = UserManager.getUser(SystemUser.OseeSystem);
         } catch (OseeCoreException ex) {
            testNameToResultsMap.put("testStateMachineAssignees",
               "Error: Exception retrieving users: " + ex.getLocalizedMessage());
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      for (Artifact art : artifacts) {
         if (art instanceof StateMachineArtifact) {
            try {
               StateMachineArtifact sma = (StateMachineArtifact) art;
               if ((sma.isCompleted() || sma.isCancelled()) && sma.getStateMgr().getAssignees().size() > 0) {
                  testNameToResultsMap.put(
                     "testStateMachineAssignees",
                     "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " cancel/complete with attribute assignees");
                  if (fixAssignees) {
                     sma.getStateMgr().clearAssignees();
                     sma.persist();
                     testNameToResultsMap.put("testStateMachineAssignees", "Fixed");
                  }
               }
               if (sma.getStateMgr().getAssignees().size() > 1 && sma.getStateMgr().getAssignees().contains(
                  unAssignedUser)) {
                  testNameToResultsMap.put(
                     "testStateMachineAssignees",
                     "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " is unassigned and assigned => " + Artifacts.toString(
                        "; ", sma.getStateMgr().getAssignees()));
                  if (fixAssignees) {
                     sma.getStateMgr().removeAssignee(unAssignedUser);
                     testNameToResultsMap.put("testStateMachineAssignees", "Fixed");
                  }
               }
               if (sma.getStateMgr().getAssignees().contains(oseeSystemUser)) {
                  testNameToResultsMap.put(
                     "testStateMachineAssignees",
                     "Error: " + art.getHumanReadableId() + " is assigned to OseeSystem; invalid assignment - MANUAL FIX REQUIRED");
               }
               if (!sma.isCompleted() && !sma.isCancelled() && sma.getStateMgr().getAssignees().isEmpty()) {
                  testNameToResultsMap.put(
                     "testStateMachineAssignees",
                     "Error: " + sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " In Work without assignees");
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

   /**
    * set to email if desire email on completion
    */
   public void setEmailOnComplete(String emailOnComplete) {
      this.emailOnComplete = emailOnComplete;
   }

}
