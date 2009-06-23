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
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.ats.util.widgets.XCurrentStateDam;
import org.eclipse.osee.ats.util.widgets.XStateDam;
import org.eclipse.osee.ats.world.WorldXNavigateItemAction;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
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

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new Report(getName()), true);
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            final XResultData rd = new XResultData();
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

   private Collection<Artifact> artifacts;
   private XResultData xResultData;
   private IProgressMonitor monitor;

   public void runIt(IProgressMonitor monitor, XResultData xResultData) throws OseeCoreException {
      this.monitor = monitor;
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      this.xResultData = xResultData;
      if (monitor != null) monitor.beginTask(getName(), 20);
      loadAtsBranchArtifacts();
      //      testArtifactIds();
      testAtsAttributeValues();
      //      testAtsActionsHaveTeamWorkflow();
      //      testAtsWorkflowsHaveAction();
      //      testAtsWorkflowsHaveZeroOrOneVersion();
      //      testTasksHaveParentWorkflow();
      //      testReviewsHaveParentWorkflowOrActionableItems();
      //      testReviewsHaveValidDefectAndRoleXml();
      //      testTeamWorkflows();
      //      testTeamDefinitions();
      //      testVersionArtifacts();
      //      testStateMachineAssignees();
      //      testAtsLogs();
      this.xResultData.reportSevereLoggingMonitor(monitorLog);
      if (monitor != null) xResultData.log(monitor, "Completed processing " + artifacts.size() + " artifacts.");
   }

   private void testArtifactIds() throws OseeCoreException {
      xResultData.log(monitor, "testArtifactIds");
      this.hrids.clear();
      this.legacyPcrIdToParentHrid.clear();
      for (Artifact artifact : artifacts) {
         // Check that HRIDs not duplicated on Common branch
         if (hrids.contains(artifact.getHumanReadableId())) {
            xResultData.logError("Duplicate HRIDs: " + XResultData.getHyperlink(artifact));
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
                     xResultData.logError("Duplicate Legacy PCR Ids in Different Actions: " + legacyPcrId);
                  }
               } else {
                  legacyPcrIdToParentHrid.put(legacyPcrId, teamArt.getParentActionArtifact().getHumanReadableId());
               }
            }
         }
      }
   }

   private void testVersionArtifacts() throws OseeCoreException {
      xResultData.log(monitor, "testVersionArtifacts");
      for (Artifact art : artifacts) {
         if (art instanceof VersionArtifact) {
            VersionArtifact verArt = (VersionArtifact) art;
            try {
               String parentBranchId =
                     verArt.getSoleAttributeValueAsString(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName(), null);
               if (parentBranchId != null) {
                  validateBranchId(verArt, parentBranchId);
               }
            } catch (Exception ex) {
               xResultData.logError(verArt.getArtifactTypeName() + " " + XResultData.getHyperlink(verArt) + " exception testing testVersionArtifacts: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   private void testTeamDefinitions() throws OseeCoreException {
      xResultData.log(monitor, "testTeamDefinitions");
      for (Artifact art : artifacts) {
         if (art instanceof TeamDefinitionArtifact) {
            TeamDefinitionArtifact teamDef = (TeamDefinitionArtifact) art;
            try {
               String parentBranchId =
                     teamDef.getSoleAttributeValueAsString(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName(),
                           null);
               if (parentBranchId != null) {
                  validateBranchId(teamDef, parentBranchId);
               }
            } catch (Exception ex) {
               xResultData.logError(teamDef.getArtifactTypeName() + " " + XResultData.getHyperlink(teamDef) + " exception testing testTeamDefinitions: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   private void testTeamWorkflows() throws OseeCoreException {
      xResultData.log(monitor, "testTeamWorkflows");
      for (Artifact art : artifacts) {
         if (art instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;
            try {
               if (teamArt.getActionableItemsDam().getActionableItems().size() == 0) {
                  xResultData.logError("TeamWorkflow " + XResultData.getHyperlink(teamArt) + " has 0 ActionableItems");
               }
               if (teamArt.getTeamDefinition() == null) {
                  xResultData.logError("TeamWorkflow " + XResultData.getHyperlink(teamArt) + " has no TeamDefinition");
               }
            } catch (Exception ex) {
               xResultData.logError(teamArt.getArtifactTypeName() + " " + XResultData.getHyperlink(teamArt) + " exception testing testTeamWorkflows: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   private void validateBranchId(Artifact art, String parentBranchId) throws OseeCoreException {
      try {
         Branch branch = BranchManager.getBranch(new Integer(parentBranchId));
         if (branch.isArchived()) {
            xResultData.logError(String.format("Parent Branch Id [%s][%s] can't be Archived branch for [%s][%s]",
                  parentBranchId, branch, art.getHumanReadableId(), art));
         } else if (branch.isWorkingBranch()) {
            xResultData.logError(String.format("Parent Branch Id [%s][%s] can't be Working branch for [%s][%s]",
                  parentBranchId, branch, art.getHumanReadableId(), art));
         } else if (!branch.isBaselineBranch()) {
            xResultData.logError(String.format("Parent Branch Id [%s][%s] must be Baseline branch for [%s][%s]",
                  parentBranchId, branch, art.getHumanReadableId(), art));
         }
      } catch (BranchDoesNotExist ex) {
         xResultData.logError(String.format("Parent Branch Id [%s] references non-existant branch for [%s][%s]",
               parentBranchId, art.getHumanReadableId(), art));
      }
   }

   private void loadAtsBranchArtifacts() throws OseeCoreException {
      xResultData.log(monitor, "testLoadAllCommonArtifacts - Started " + XDate.getDateNow(XDate.MMDDYYHHMM));
      artifacts = ArtifactQuery.getArtifactsFromBranch(AtsPlugin.getAtsBranch(), false);
      if (xResultData == null) {
         xResultData = new XResultData();
      }
      if (artifacts.size() == 0) {
         xResultData.logError("Artifact load returned 0 artifacts to check");
      }
      xResultData.log(monitor, "testLoadAllCommonArtifacts - Completed " + XDate.getDateNow(XDate.MMDDYYHHMM));
   }

   private void testAtsAttributeValues() throws OseeCoreException {
      xResultData.log(monitor, "testAtsAttributeValues");
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      for (Artifact artifact : artifacts) {

         // Test for null attribute values 
         for (Attribute<?> attr : artifact.getAttributes(false)) {
            if (attr.getValue() == null) {
               xResultData.logError("Artifact: " + XResultData.getHyperlink(artifact) + " Types: " + artifact.getArtifactTypeName() + " - Null Attribute");
               if (fixAttributeValues) {
                  attr.delete();
               }
            }
         }

         if (artifact instanceof StateMachineArtifact) {
            try {
               artifact.getSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
            } catch (AttributeDoesNotExist ex) {
               // do nothing
            } catch (MultipleAttributesExist ex) {
               xResultData.logError("Artifact: " + XResultData.getHyperlink(artifact) + " Multiple resolution attributes exist");
            }
         }

         // Test for ats.State Completed;;;<num> or Cancelled;;;<num> and cleanup
         if (artifact instanceof StateMachineArtifact) {
            XStateDam stateDam = new XStateDam((StateMachineArtifact) artifact);
            for (SMAState state : stateDam.getStates()) {
               if (state.getName().equals(DefaultTeamState.Completed.name()) || state.getName().equals(
                     state.getName().equals(DefaultTeamState.Cancelled.name()))) {
                  if (state.getHoursSpent() != 0.0 || state.getPercentComplete() != 0) {
                     xResultData.logError("ats.State error for SMA: " + XResultData.getHyperlink(artifact) + " State: " + state.getName() + " Hours Spent: " + state.getHoursSpentStr() + " Percent: " + state.getPercentComplete());
                     if (fixAttributeValues) {
                        state.setHoursSpent(0);
                        state.setPercentComplete(0);
                        stateDam.setState(state);
                        xResultData.log(monitor, "Fixed");
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
                  state.getName().equals(DefaultTeamState.Cancelled.name()))) {
               if (state.getHoursSpent() != 0.0 || state.getPercentComplete() != 0) {
                  xResultData.logError("ats.CurrentState error for SMA: " + XResultData.getHyperlink(artifact) + " State: " + state.getName() + " Hours Spent: " + state.getHoursSpentStr() + " Percent: " + state.getPercentComplete());
                  if (fixAttributeValues) {
                     state.setHoursSpent(0);
                     state.setPercentComplete(0);
                     currentStateDam.setState(state);
                     xResultData.log(monitor, "Fixed");
                  }
               }
            }
         }
         if (artifact.isDirty()) {
            artifact.persistAttributes(transaction);
         }
      }
      transaction.execute();
   }

   private void testAtsActionsHaveTeamWorkflow() throws OseeCoreException {
      xResultData.log(monitor, "testAtsActionsHaveTeamWorkflow");
      for (Artifact artifact : artifacts) {
         if (artifact instanceof ActionArtifact) {
            if (((ActionArtifact) artifact).getTeamWorkFlowArtifacts().size() == 0) {
               xResultData.logError("Action " + XResultData.getHyperlink(artifact) + " has no Team Workflows\n");
            }
         }
      }
   }

   private void testAtsWorkflowsHaveAction() throws OseeCoreException {
      xResultData.log(monitor, "testAtsActionsHaveTeamWorkflow");
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TeamWorkFlowArtifact) {
            if (((TeamWorkFlowArtifact) artifact).getParentActionArtifact() == null) {
               xResultData.logError("Team " + XResultData.getHyperlink(artifact) + " has no parent Action\n");
            }
         }
      }
   }

   private void testAtsWorkflowsHaveZeroOrOneVersion() throws OseeCoreException {
      xResultData.log(monitor, "testAtsWorkflowsHaveZeroOrOneVersion");
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
            if (teamArt.getRelatedArtifacts(AtsRelation.TeamWorkflowTargetedForVersion_Version).size() > 1) {
               xResultData.logError("Team workflow " + XResultData.getHyperlink(teamArt) + " has " + teamArt.getRelatedArtifacts(
                     AtsRelation.TeamWorkflowTargetedForVersion_Version).size() + " versions");
            }
         }
      }

   }

   private void testTasksHaveParentWorkflow() throws OseeCoreException {
      xResultData.log(monitor, "testTasksHaveParentWorkflow");
      Set<Artifact> badTasks = new HashSet<Artifact>(30);
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TaskArtifact) {
            TaskArtifact taskArtifact = (TaskArtifact) artifact;
            if (taskArtifact.getRelatedArtifacts(AtsRelation.SmaToTask_Sma).size() != 1) {
               xResultData.logError("Task " + XResultData.getHyperlink(taskArtifact) + " has " + taskArtifact.getRelatedArtifacts(
                     AtsRelation.SmaToTask_Sma).size() + " parents.");
               badTasks.add(taskArtifact);
            }
         }
      }
      if (badTasks.size() > 0) {
         TaskEditor.open(new TaskEditorSimpleProvider("ValidateATSDatabase: Tasks have !=1 parent workflows.", badTasks));
      }
   }

   private void testReviewsHaveValidDefectAndRoleXml() throws OseeCoreException {
      xResultData.log(monitor, "testReviewsHaveValidDefectAndRoleXml");
      for (Artifact artifact : artifacts) {
         if (artifact instanceof ReviewSMArtifact) {
            ReviewSMArtifact reviewArtifact = (ReviewSMArtifact) artifact;
            if (reviewArtifact.getAttributes(ATSAttributes.REVIEW_DEFECT_ATTRIBUTE.getStoreName()).size() > 0 && reviewArtifact.getDefectManager().getDefectItems().size() == 0) {
               xResultData.logError("Review " + XResultData.getHyperlink(reviewArtifact) + " has defect attribute, but no defects (xml parsing error).");
            }
            if (reviewArtifact.getAttributes(ATSAttributes.ROLE_ATTRIBUTE.getStoreName()).size() > 0 && reviewArtifact.getUserRoleManager().getUserRoles().size() == 0) {
               xResultData.logError("Review " + XResultData.getHyperlink(reviewArtifact) + " has role attribute, but no roles (xml parsing error).");
            }
         }
      }
   }

   private void testReviewsHaveParentWorkflowOrActionableItems() throws OseeCoreException {
      xResultData.log(monitor, "testReviewsHaveParentWorkflowOrActionableItems");
      for (Artifact artifact : artifacts) {
         if (artifact instanceof ReviewSMArtifact) {
            ReviewSMArtifact reviewArtifact = (ReviewSMArtifact) artifact;
            if (reviewArtifact.getRelatedArtifacts(AtsRelation.TeamWorkflowToReview_Team).size() == 0 && reviewArtifact.getActionableItemsDam().getActionableItemGuids().size() == 0) {
               xResultData.logError("Review " + XResultData.getHyperlink(reviewArtifact) + " has 0 related parents and 0 actionable items.");
            }
         }
      }
   }

   private void testAtsLogs() throws OseeCoreException {
      xResultData.log(monitor, "testAtsLogs");
      for (Artifact art : artifacts) {
         if (art instanceof StateMachineArtifact) {
            StateMachineArtifact sma = (StateMachineArtifact) art;
            try {
               ATSLog log = sma.getSmaMgr().getLog();
               if (log.getOriginator() == null) {
                  try {
                     xResultData.logError(sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " originator == null");
                  } catch (Exception ex) {
                     xResultData.logError(sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " exception accessing originator: " + ex.getLocalizedMessage());
                  }
               }
               for (String stateName : Arrays.asList("Completed", "Cancelled")) {
                  if (sma.getSmaMgr().getStateMgr().getCurrentStateName().equals(stateName)) {
                     LogItem logItem = log.getStateEvent(LogType.StateEntered, stateName);
                     if (logItem == null) {
                        try {
                           xResultData.logError(sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " state \"" + stateName + "\" logItem == null");
                        } catch (Exception ex) {
                           xResultData.logError(sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " exception accessing logItem: " + ex.getLocalizedMessage());

                        }
                     }
                     if (logItem.getDate() == null) {
                        try {
                           xResultData.logError(sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " state \"" + stateName + "\" logItem.date == null");
                        } catch (Exception ex) {
                           xResultData.logError(sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " exception accessing logItem.date: " + ex.getLocalizedMessage());

                        }
                     }
                  }
               }
               // Generate html log which will exercise all the conversions
               log.getHtml();
               // Verify that all users are resolved
               for (LogItem logItem : sma.getSmaMgr().getLog().getLogItems()) {
                  if (logItem.getUser() == null) {
                     xResultData.logError(sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " user == null for userId \"" + logItem.getUserId() + "\"");
                  }
               }
            } catch (Exception ex) {
               xResultData.logError(sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " exception accessing AtsLog: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   private void testStateMachineAssignees() throws OseeCoreException {
      xResultData.log(monitor, "testStateMachineAssignees");
      User unAssignedUser = UserManager.getUser(SystemUser.UnAssigned);
      User oseeSystemUser = UserManager.getUser(SystemUser.OseeSystem);
      for (Artifact art : artifacts) {
         if (art instanceof StateMachineArtifact) {
            try {
               StateMachineArtifact sma = (StateMachineArtifact) art;
               SMAManager smaMgr = new SMAManager(sma);
               if ((smaMgr.isCompleted() || smaMgr.isCancelled()) && smaMgr.getStateMgr().getAssignees().size() > 0) {
                  xResultData.logError(sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " cancel/complete with attribute assignees");
                  if (fixAssignees) {
                     smaMgr.getStateMgr().clearAssignees();
                     smaMgr.getSma().persistAttributesAndRelations();
                     xResultData.log(monitor, "Fixed");
                  }
               }
               if (smaMgr.getStateMgr().getAssignees().size() > 1 && smaMgr.getStateMgr().getAssignees().contains(
                     unAssignedUser)) {
                  xResultData.logError(sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " is unassigned and assigned => " + Artifacts.toString(
                        "; ", smaMgr.getStateMgr().getAssignees()));
                  if (fixAssignees) {
                     smaMgr.getStateMgr().removeAssignee(unAssignedUser);
                     xResultData.log(monitor, "Fixed");
                  }
               }
               if (smaMgr.getStateMgr().getAssignees().contains(oseeSystemUser)) {
                  xResultData.logError(art.getHumanReadableId() + " is assigned to OseeSystem; invalid assignment - MANUAL FIX REQUIRED");
               }
               if ((!smaMgr.isCompleted() && !smaMgr.isCancelled()) && smaMgr.getStateMgr().getAssignees().size() == 0) {
                  xResultData.logError(sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " In Work without assignees");
               }
               if (art instanceof StateMachineArtifact) {
                  List<Artifact> relationAssigned =
                        art.getRelatedArtifacts(CoreRelationEnumeration.Users_User, Artifact.class);
                  if ((smaMgr.isCompleted() || smaMgr.isCancelled()) && relationAssigned.size() > 0) {
                     xResultData.logError(sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " cancel/complete with related assignees");
                     if (fixAssignees) {
                        try {
                           ((StateMachineArtifact) art).updateAssigneeRelations();
                           art.persistAttributesAndRelations();
                        } catch (OseeCoreException ex) {
                           OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                        }
                        xResultData.log(monitor, "Fixed");
                     }
                  } else if (smaMgr.getStateMgr().getAssignees().size() != relationAssigned.size()) {
                     // Make sure this isn't just an UnAssigned user issue (don't relate to unassigned user anymore)
                     if (!(smaMgr.getStateMgr().getAssignees().contains(UserManager.getUser(SystemUser.UnAssigned)) && relationAssigned.size() == 0)) {
                        xResultData.logError(sma.getArtifactTypeName() + " " + XResultData.getHyperlink(sma) + " attribute assignees doesn't match related assignees");
                        if (fixAssignees) {
                           try {
                              ((StateMachineArtifact) art).updateAssigneeRelations();
                              art.persistAttributesAndRelations();
                           } catch (OseeCoreException ex) {
                              OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                           }
                           xResultData.log(monitor, "Fixed");
                        }
                     }
                  }
               }
            } catch (OseeCoreException ex) {
               xResultData.logError("Exception testing assignees: " + ex.getLocalizedMessage());
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
