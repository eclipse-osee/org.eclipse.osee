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

import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ValidateAtsDatabase extends XNavigateItemAction {

   private final boolean fixAssignees = true;
   private final boolean fixAttributeValues = false;

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
            OSEELog.logException(AtsPlugin.class, ex, false);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   private Collection<Artifact> artifacts;
   private XResultData xResultData;

   private void runIt(IProgressMonitor monitor, XResultData xResultData) throws OseeCoreException {
      this.xResultData = xResultData;
      loadAtsBranchArtifacts();
      testAtsBranchAttributeValues();
      testAtsActionsHaveTeamWorkflow();
      testAtsWorkflowsHaveAction();
      testAtsWorkflowsHaveZeroOrOneVersion();
      testTasksHaveParentWorkflow();
      testReviewsHaveParentWorkflowOrActionableItems();
      testStateMachineAssignees();
      xResultData.log("Completed processing " + artifacts.size() + " artifacts.");
   }

   public void loadAtsBranchArtifacts() throws OseeCoreException {
      xResultData.log("testLoadAllCommonArtifacts - Started " + XDate.getDateNow(XDate.MMDDYYHHMM));
      artifacts = ArtifactQuery.getArtifactsFromBranch(AtsPlugin.getAtsBranch(), false);
      if (xResultData == null) {
         xResultData = new XResultData();
      }
      if (artifacts.size() == 0) {
         xResultData.logError("Artifact load returned 0 artifacts to check");
      }
      xResultData.log("testLoadAllCommonArtifacts - Completed " + XDate.getDateNow(XDate.MMDDYYHHMM));
   }

   public void testAtsBranchAttributeValues() throws OseeCoreException {
      xResultData.log("testAtsBranchAttributeValues");
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
      xResultData.log("testAtsActionsHaveTeamWorkflow");
      for (Artifact artifact : artifacts) {
         if (artifact instanceof ActionArtifact) {
            if (((ActionArtifact) artifact).getTeamWorkFlowArtifacts().size() == 0) {
               xResultData.logError("Action " + artifact.getHumanReadableId() + " has no Team Workflows\n");
            }
         }
      }
   }

   public void testAtsWorkflowsHaveAction() throws OseeCoreException {
      xResultData.log("testAtsActionsHaveTeamWorkflow");
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TeamWorkFlowArtifact) {
            if (((TeamWorkFlowArtifact) artifact).getParentActionArtifact() == null) {
               xResultData.logError("Team " + artifact.getHumanReadableId() + " has no parent Action\n");
            }
         }
      }
   }

   public void testAtsWorkflowsHaveZeroOrOneVersion() throws OseeCoreException {
      xResultData.log("testAtsWorkflowsHaveZeroOrOneVersion");
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
      xResultData.log("testTasksHaveParentWorkflow");
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TaskArtifact) {
            TaskArtifact taskArtifact = (TaskArtifact) artifact;
            if (taskArtifact.getRelatedArtifacts(AtsRelation.SmaToTask_Sma).size() != 1) {
               xResultData.logError("Task " + taskArtifact.getHumanReadableId() + " has " + taskArtifact.getRelatedArtifacts(
                     AtsRelation.SmaToTask_Sma).size() + " parents.");
            }
         }
      }
   }

   public void testReviewsHaveParentWorkflowOrActionableItems() throws OseeCoreException {
      xResultData.log("testReviewsHaveParentWorkflowOrActionableItems");
      for (Artifact artifact : artifacts) {
         if (artifact instanceof ReviewSMArtifact) {
            ReviewSMArtifact reviewArtifact = (ReviewSMArtifact) artifact;
            if (reviewArtifact.getRelatedArtifacts(AtsRelation.TeamWorkflowToReview_Team).size() == 0 && reviewArtifact.getActionableItemsDam().getActionableItemGuids().size() == 0) {
               xResultData.logError("Review " + reviewArtifact.getHumanReadableId() + " has 0 related parents and 0 actionable items.");
            }
         }
      }
   }

   public void testStateMachineAssignees() throws OseeCoreException {
      xResultData.log("testStateMachineAssignees");
      User unAssignedUser = SkynetAuthentication.getUser(UserEnum.UnAssigned);
      User noOneUser = SkynetAuthentication.getUser(UserEnum.NoOne);
      for (Artifact art : artifacts) {
         if (art instanceof StateMachineArtifact) {
            StateMachineArtifact sma = (StateMachineArtifact) art;
            SMAManager smaMgr = new SMAManager(sma);
            if ((smaMgr.isCompleted() || smaMgr.isCancelled()) && smaMgr.getStateMgr().getAssignees().size() > 0) {
               xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " cancel/complete with attribute assignees");
               if (fixAssignees) {
                  smaMgr.getStateMgr().clearAssignees();
                  smaMgr.getSma().persistAttributesAndRelations();
                  xResultData.log("Fixed");
               }
            }
            if (smaMgr.getStateMgr().getAssignees().size() > 1 && smaMgr.getStateMgr().getAssignees().contains(
                  unAssignedUser)) {
               xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " is unassigned and assigned => " + Artifacts.toString(
                     "; ", smaMgr.getStateMgr().getAssignees()));
               if (fixAssignees) {
                  smaMgr.getStateMgr().removeAssignee(unAssignedUser);
                  xResultData.log("Fixed");
               }
            }
            if (smaMgr.getStateMgr().getAssignees().contains(noOneUser)) {
               xResultData.logError(art.getHumanReadableId() + " is assigned to NoOne; invalid assignment - MANUAL FIX REQUIRED");
            }
            if ((!smaMgr.isCompleted() && !smaMgr.isCancelled()) && smaMgr.getStateMgr().getAssignees().size() == 0) {
               xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " In Work without assignees");
            }
            if (art instanceof StateMachineArtifact) {
               List<Artifact> assigned = art.getRelatedArtifacts(CoreRelationEnumeration.Users_User, Artifact.class);
               if ((smaMgr.isCompleted() || smaMgr.isCancelled()) && assigned.size() > 0) {
                  xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " cancel/complete with related assignees");
                  if (fixAssignees) {
                     try {
                        ((StateMachineArtifact) art).updateAssigneeRelations();
                        art.persistAttributesAndRelations();
                     } catch (OseeCoreException ex) {
                        OSEELog.logException(AtsPlugin.class, ex, false);
                     }
                     xResultData.log("Fixed");
                  }
               } else if (smaMgr.getStateMgr().getAssignees().size() != assigned.size()) {
                  xResultData.logError(sma.getArtifactTypeName() + " " + sma.getHumanReadableId() + " attribute assignees doesn't match related assignees");
                  if (fixAssignees) {
                     try {
                        ((StateMachineArtifact) art).updateAssigneeRelations();
                        art.persistAttributesAndRelations();
                     } catch (OseeCoreException ex) {
                        OSEELog.logException(AtsPlugin.class, ex, false);
                     }
                     xResultData.log("Fixed");
                  }
               }
            }
         }
      }
   }
}
