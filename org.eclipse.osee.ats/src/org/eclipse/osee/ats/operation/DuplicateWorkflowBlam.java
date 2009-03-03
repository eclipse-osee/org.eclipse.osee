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
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.IAtsTeamWorkflow;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.world.IAtsWorldEditorMenuItem;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.BlamOperations;
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

   public static String TEAM_WORKFLOW = "Team Workflow (drop here)";
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

               if (artifacts.size() == 0) {
                  AWorkbench.popup("ERROR", "Must drag in Team Workflow to duplicate.");
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
                  handleCreateDuplicate(teamArts, duplicateTasks);
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                  return;
               } finally {
                  AtsPlugin.setEmailEnabled(true);
               }

               SMAEditor.editArtifact(artifact);
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         };
      });
   }

   private void handleCreateDuplicate(Collection<TeamWorkFlowArtifact> teamArts, boolean duplicateTasks) throws OseeCoreException {
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
         }
         // Notify all extension points that workflow is being duplicated in case they need to add, remove
         // attributes or relations
         for (IAtsTeamWorkflow teamExtension : TeamWorkflowExtensions.getInstance().getAtsTeamWorkflowExtensions()) {
            teamExtension.teamWorkflowDuplicating(teamArt, dupArt);
         }
      }
      transaction.execute();
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
      "</xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getDescriptionUsage()
    */
   @Override
   public String getDescriptionUsage() {
      return "Duplicate Team Workflow";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsWorldEditorMenuItem#run(org.eclipse.osee.ats.world.WorldEditor)
    */
   @Override
   public void run(WorldEditor worldEditor) throws OseeCoreException {
      if (worldEditor.getWorldComposite().getXViewer().getSelectedTeamWorkflowArtifacts().size() == 0) {
         AWorkbench.popup("ERROR", "Must select one or more team workflows to duplicate");
         return;
      }
      BlamOperation blamOperation = BlamOperations.getBlamOperation("DuplicateWorkflowBlam");
      ((DuplicateWorkflowBlam) blamOperation).setDefaultTeamWorkflows(worldEditor.getWorldComposite().getXViewer().getSelectedTeamWorkflowArtifacts());
      BlamEditor.edit(blamOperation);
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

}