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
package org.eclipse.osee.ats.operation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.IAtsTeamWorkflow;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.ActionArtifact.CreateTeamOption;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.world.IAtsWorldEditorMenuItem;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowBlam extends AbstractBlam implements IAtsWorldEditorMenuItem {

   private static String TEAM_WORKFLOW = "Team Workflow (drop here)";
   private static String CREATE_NEW_ACTION =
         "Create New Action - Creates new action in start state with current assignees.";
   private Collection<? extends TaskableStateMachineArtifact> defaultTeamWorkflows;

   public DuplicateWorkflowBlam() throws IOException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               List<Artifact> artifacts = variableMap.getArtifacts(TEAM_WORKFLOW);
               boolean duplicateTasks = variableMap.getBoolean("Duplicate Tasks");
               boolean createNewWorkflow = variableMap.getBoolean(CREATE_NEW_ACTION);

               if (artifacts.size() == 0) {
                  AWorkbench.popup("ERROR", "Must drag in Team Workflow to duplicate.");
                  return;
               }
               if (duplicateTasks && createNewWorkflow) {
                  AWorkbench.popup("ERROR", "Can not create workflow as new and duplicate tasks.");
                  return;
               }
               Artifact artifact = artifacts.iterator().next();
               if (!(artifact instanceof TeamWorkFlowArtifact)) {
                  AWorkbench.popup("ERROR", "Artifact MUST be Team Workflow");
                  return;
               }
               try {
                  AtsPlugin.setEmailEnabled(false);
                  Collection<TeamWorkFlowArtifact> teamArts = Collections.castAll(artifacts);
                  if (createNewWorkflow) {
                     handleCreateNewWorkflow(teamArts);
                  } else {
                     handleCreateDuplicate(teamArts, duplicateTasks);
                  }
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                  return;
               } finally {
                  AtsPlugin.setEmailEnabled(true);
               }

            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         };
      });
   }

   private void handleCreateNewWorkflow(Collection<TeamWorkFlowArtifact> teamArts) throws OseeCoreException {
      Set<TeamWorkFlowArtifact> newTeamArts = new HashSet<TeamWorkFlowArtifact>();
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      for (TeamWorkFlowArtifact teamArt : teamArts) {
         Collection<User> assignees = teamArt.getSmaMgr().getStateMgr().getAssignees();
         if (!assignees.contains(UserManager.getUser())) {
            assignees.add(UserManager.getUser());
         }
         TeamWorkFlowArtifact newTeamArt =
               teamArt.getParentActionArtifact().createTeamWorkflow(teamArt.getTeamDefinition(),
                     teamArt.getActionableItemsDam().getActionableItems(), assignees, transaction,
                     CreateTeamOption.Duplicate_If_Exists);
         newTeamArt.persistAttributesAndRelations(transaction);
         newTeamArts.add(newTeamArt);
      }
      transaction.execute();
      for (TeamWorkFlowArtifact newTeamArt : newTeamArts) {
         SMAEditor.editArtifact(newTeamArt);
      }
   }

   private void handleCreateDuplicate(Collection<TeamWorkFlowArtifact> teamArts, boolean duplicateTasks) throws OseeCoreException {
      Set<TeamWorkFlowArtifact> newTeamArts = new HashSet<TeamWorkFlowArtifact>();
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      for (TeamWorkFlowArtifact teamArt : teamArts) {
         TeamWorkFlowArtifact dupArt = (TeamWorkFlowArtifact) teamArt.duplicate(AtsPlugin.getAtsBranch());
         dupArt.addRelation(AtsRelation.ActionToWorkflow_Action, teamArt.getParentActionArtifact());
         dupArt.getSmaMgr().getLog().addLog(LogType.Note, null,
               "Workflow duplicated from " + teamArt.getHumanReadableId());
         if (duplicateTasks) {
            for (TaskArtifact taskArt : teamArt.getSmaMgr().getTaskMgr().getTaskArtifacts()) {
               TaskArtifact dupTaskArt = (TaskArtifact) taskArt.duplicate(AtsPlugin.getAtsBranch());
               dupTaskArt.getSmaMgr().getLog().addLog(LogType.Note, null,
                     "Task duplicated from " + taskArt.getHumanReadableId());
               dupArt.addRelation(AtsRelation.SmaToTask_Task, dupTaskArt);
               dupArt.persistAttributes(transaction);
            }
            dupArt.persistAttributesAndRelations(transaction);
            newTeamArts.add(dupArt);
         }
         // Notify all extension points that workflow is being duplicated in case they need to add, remove
         // attributes or relations
         for (IAtsTeamWorkflow teamExtension : TeamWorkflowExtensions.getInstance().getAtsTeamWorkflowExtensions()) {
            teamExtension.teamWorkflowDuplicating(teamArt, dupArt);
         }
      }
      transaction.execute();
      for (TeamWorkFlowArtifact newTeamArt : newTeamArts) {
         SMAEditor.editArtifact(newTeamArt);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#widgetCreated(org.eclipse.osee.framework.ui.skynet.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(TEAM_WORKFLOW) && defaultTeamWorkflows != null) {
         XListDropViewer viewer = (XListDropViewer) xWidget;
         viewer.setInput(defaultTeamWorkflows);
      }
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + TEAM_WORKFLOW + "\" />" +
      //
      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Duplicate Tasks\" defaultValue=\"true\"/>" +
      //
      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + CREATE_NEW_ACTION + "\" defaultValue=\"false\"/>" +
      //
      "</xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getDescriptionUsage()
    */
   @Override
   public String getDescriptionUsage() {
      return "Duplicates a team workflow in the exact state as it currently is with tasks in their exact state.  " +
      //
      "All history will be duplicated.  \"Create New Workflow\" option will create the workflow as an initial workflow as if a new action were created except" +
      //
      " the new workflow will be under the same Action.  " +
      //
      "\"Create New Workflow\" is not compatible with \"Duplicate Tasks\".";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsWorldEditorMenuItem#run(org.eclipse.osee.ats.world.WorldEditor)
    */
   @Override
   public void runMenuItem(WorldEditor worldEditor) throws OseeCoreException {
      if (worldEditor.getWorldComposite().getXViewer().getSelectedTeamWorkflowArtifacts().size() == 0) {
         AWorkbench.popup("ERROR", "Must select one or more team workflows to duplicate");
         return;
      }
      try {
         BlamOperation blamOperation = new DuplicateWorkflowBlam();
         ((DuplicateWorkflowBlam) blamOperation).setDefaultTeamWorkflows(worldEditor.getWorldComposite().getXViewer().getSelectedTeamWorkflowArtifacts());
         BlamEditor.edit(blamOperation);
      } catch (IOException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * @return the defaultTeamWorkflows
    */
   public Collection<? extends TaskableStateMachineArtifact> getDefaultTeamWorkflows() {
      return defaultTeamWorkflows;
   }

   /**
    * @param defaultTeamWorkflows the defaultTeamWorkflows to set
    */
   public void setDefaultTeamWorkflows(Collection<? extends TaskableStateMachineArtifact> defaultTeamWorkflows) {
      this.defaultTeamWorkflows = defaultTeamWorkflows;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getName()
    */
   @Override
   public String getMenuItemName() {
      return "Duplicate Team Workflow";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getName()
    */
   @Override
   public String getName() {
      return "Duplicate Workflow";
   }

   public Collection<String> getCategories() {
      return Arrays.asList("ATS");
   }
}