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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSLog;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskEditorSimpleProvider;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ValidateAtsDatabase extends XNavigateItemAction {

   private final boolean fixAssignees = true;
   private final boolean fixAttributeValues = true;

   /**
    * @param parent
    */
   public ValidateAtsDatabase(XNavigateItem parent) {
      super(parent, "Validate ATS Database");
   }

   public ValidateAtsDatabase() {
      this(null);
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

   private void runIt(IProgressMonitor monitor, XResultData xResultData) throws OseeCoreException {
      this.monitor = monitor;
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      this.xResultData = xResultData;
      monitor.beginTask(getName(), 10);
      loadAtsBranchArtifacts();
      monitor.worked(1);
      testAtsBranchAttributeValues();
      monitor.worked(1);
      testAtsActionsHaveTeamWorkflow();
      monitor.worked(1);
      testAtsWorkflowsHaveAction();
      monitor.worked(1);
      testAtsWorkflowsHaveZeroOrOneVersion();
      monitor.worked(1);
      testTasksHaveParentWorkflow();
      monitor.worked(1);
      testReviewsHaveParentWorkflowOrActionableItems();
      monitor.worked(1);
      testTeamWorkflows();
      monitor.worked(1);
      testStateMachineAssignees();
      monitor.worked(1);
      testAtsLogs();
      monitor.worked(1);
      this.xResultData.reportSevereLoggingMonitor(monitorLog);
      xResultData.log(monitor, "Completed processing " + artifacts.size() + " artifacts.");
   }

   public void testTeamWorkflows() throws OseeCoreException {
      xResultData.log(monitor, "testTeamWorkflows");
      for (Artifact art : artifacts) {
         if (art instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;
            try {
               if (teamArt.getActionableItemsDam().getActionableItems().size() == 0) {
                  xResultData.logError("TeamWorkflow " + teamArt.getHumanReadableId() + " has 0 ActionableItems");
               }
               if (teamArt.getTeamDefinition() == null) {
                  xResultData.logError("TeamWorkflow " + teamArt.getHumanReadableId() + " has no TeamDefinition");
               }
            } catch (Exception ex) {
               xResultData.logError(teamArt.getArtifactTypeName() + " " + teamArt.getHumanReadableId() + " exception testing testTeamWorkflows: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   public void loadAtsBranchArtifacts() throws OseeCoreException {
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

   public void testAtsBranchAttributeValues() throws OseeCoreException {
      xResultData.log(monitor, "testAtsBranchAttributeValues");
      for (Artifact artifact : artifacts) {
         for (Attribute<?> attr : artifact.getAttributes(false)) {
            if (attr.getValue() == null) {
               xResultData.logError("Artifact: " + artifact.getHumanReadableId() + " Types: " + artifact.getArtifactTypeName() + " - Null Attribute: " + attr.getNameValueDescription());
               if (fixAttributeValues) {
                  attr.delete();
               }
            }
         }
         if (artifact.isDirty()) artifact.persistAttributes();
      }
   }

   public void testAtsActionsHaveTeamWorkflow() throws OseeCoreException {
      xResultData.log(monitor, "testAtsActionsHaveTeamWorkflow");
      for (Artifact artifact : artifacts) {
         if (artifact instanceof ActionArtifact) {
            if (((ActionArtifact) artifact).getTeamWorkFlowArtifacts().size() == 0) {
               xResultData.logError("Action " + artifact.getHumanReadableId() + " has no Team Workflows\n");
            }
         }
      }
   }

   public void testAtsWorkflowsHaveAction() throws OseeCoreException {
      xResultData.log(monitor, "testAtsActionsHaveTeamWorkflow");
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TeamWorkFlowArtifact) {
            if (((TeamWorkFlowArtifact) artifact).getParentActionArtifact() == null) {
               xResultData.logError("Team " + artifact.getHumanReadableId() + " has no parent Action\n");
            }
         }
      }
   }

   public void testAtsWorkflowsHaveZeroOrOneVersion() throws OseeCoreException {
      xResultData.log(monitor, "testAtsWorkflowsHaveZeroOrOneVersion");
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
            if (teamArt.getRelatedArtifacts(AtsRelation.TeamWorkflowTargetedForVersion_Version).size() > 1) {
               xResultData.logError("Team workflow " + teamArt.getHumanReadableId() + " has " + teamArt.getRelatedArtifacts(
                     AtsRelation.TeamWorkflowTargetedForVersion_Version).size() + " versions");
            }
         }
      }

   }

   public void testTasksHaveParentWorkflow() throws OseeCoreException {
      xResultData.log(monitor, "testTasksHaveParentWorkflow");
      Set<Artifact> badTasks = new HashSet<Artifact>(30);
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TaskArtifact) {
            TaskArtifact taskArtifact = (TaskArtifact) artifact;
            if (taskArtifact.getRelatedArtifacts(AtsRelation.SmaToTask_Sma).size() != 1) {
               xResultData.logError("Task " + taskArtifact.getHumanReadableId() + " has " + taskArtifact.getRelatedArtifacts(
                     AtsRelation.SmaToTask_Sma).size() + " parents.");
               badTasks.add(taskArtifact);
            }
         }
      }
      if (badTasks.size() > 0) {
         TaskEditor.open(new TaskEditorSimpleProvider("ValidateATSDatabase: Tasks have !=1 parent workflows.", badTasks));
      }
   }

   public void testReviewsHaveParentWorkflowOrActionableItems() throws OseeCoreException {
      xResultData.log(monitor, "testReviewsHaveParentWorkflowOrActionableItems");
      for (Artifact artifact : artifacts) {
         if (artifact instanceof ReviewSMArtifact) {
            ReviewSMArtifact reviewArtifact = (ReviewSMArtifact) artifact;
            if (reviewArtifact.getRelatedArtifacts(AtsRelation.TeamWorkflowToReview_Team).size() == 0 && reviewArtifact.getActionableItemsDam().getActionableItemGuids().size() == 0) {
               xResultData.logError("Review " + reviewArtifact.getHumanReadableId() + " has 0 related parents and 0 actionable items.");
            }
         }
      }
   }

   public void testAtsLogs() throws OseeCoreException {
      xResultData.log(monitor, "testAtsLogs");
      for (Artifact art : artifacts) {
         if (art instanceof StateMachineArtifact) {
            StateMachineArtifact sma = (StateMachineArtifact) art;
            try {
               ATSLog log = sma.getSmaMgr().getLog();
               if (log.getOriginator() == null) {
                  try {
                     xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " originator == null");
                  } catch (Exception ex) {
                     xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " exception accessing originator: " + ex.getLocalizedMessage());
                  }
               }
               for (String stateName : Arrays.asList("Completed", "Cancelled")) {
                  if (sma.getSmaMgr().getStateMgr().getCurrentStateName().equals(stateName)) {
                     LogItem logItem = log.getStateEvent(LogType.StateEntered, stateName);
                     if (logItem == null) {
                        try {
                           xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " state \"" + stateName + "\" logItem == null");
                        } catch (Exception ex) {
                           xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " exception accessing logItem: " + ex.getLocalizedMessage());

                        }
                     }
                     if (logItem.getDate() == null) {
                        try {
                           xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " state \"" + stateName + "\" logItem.date == null");
                        } catch (Exception ex) {
                           xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " exception accessing logItem.date: " + ex.getLocalizedMessage());

                        }
                     }
                  }
               }
               // Generate html log which will exercise all the conversions
               log.getHtml();
               // Verify that all users are resolved
               for (LogItem logItem : sma.getSmaMgr().getLog().getLogItems()) {
                  if (logItem.getUser() == null) {
                     xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " user == null for userId \"" + logItem.getUserId() + "\"");
                  }
               }
            } catch (Exception ex) {
               xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " exception accessing AtsLog: " + ex.getLocalizedMessage());
            }
         }
      }
   }

   public void testStateMachineAssignees() throws OseeCoreException {
      xResultData.log(monitor, "testStateMachineAssignees");
      User unAssignedUser = UserManager.getUser(SystemUser.UnAssigned);
      User oseeSystemUser = UserManager.getUser(SystemUser.OseeSystem);
      for (Artifact art : artifacts) {
         if (art instanceof StateMachineArtifact) {
            try {
               StateMachineArtifact sma = (StateMachineArtifact) art;
               SMAManager smaMgr = new SMAManager(sma);
               if ((smaMgr.isCompleted() || smaMgr.isCancelled()) && smaMgr.getStateMgr().getAssignees().size() > 0) {
                  xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " cancel/complete with attribute assignees");
                  if (fixAssignees) {
                     smaMgr.getStateMgr().clearAssignees();
                     smaMgr.getSma().persistAttributesAndRelations();
                     xResultData.log(monitor, "Fixed");
                  }
               }
               if (smaMgr.getStateMgr().getAssignees().size() > 1 && smaMgr.getStateMgr().getAssignees().contains(
                     unAssignedUser)) {
                  xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " is unassigned and assigned => " + Artifacts.toString(
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
                  xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " In Work without assignees");
               }
               if (art instanceof StateMachineArtifact) {
                  List<Artifact> relationAssigned =
                        art.getRelatedArtifacts(CoreRelationEnumeration.Users_User, Artifact.class);
                  if ((smaMgr.isCompleted() || smaMgr.isCancelled()) && relationAssigned.size() > 0) {
                     xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " cancel/complete with related assignees");
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
                        xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " attribute assignees doesn't match related assignees");
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
}
