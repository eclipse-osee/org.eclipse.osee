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
package org.eclipse.osee.ats.ide.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IValidatingOperation;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workflow.util.DuplicateWorkflowAsIsOperation;
import org.eclipse.osee.ats.core.workflow.util.DuplicateWorkflowAtStartStateOperation;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowBlam extends AbstractBlam {

   private final static String TEAM_WORKFLOW = "Team Workflow (drop here)";
   private final static String DUPLICATE_WORKFLOW =
      "Duplicate Workflow - creates carbon copy with all fields and assignees intact.";
   private final static String CREATE_NEW_WORFLOW_IN_START_STATE =
      "Create new Workflow - creates new workflow in start state with current assignees.";
   private final static String DUPLICATE_TASKS = "Duplicate Tasks - only valid for Duplicate Workflow";
   private final static String DUPLICATE_METHOD = "Duplicate Method";
   private final static String TITLE = "New Title (blank for same title or for multiple workflows)";
   private Collection<? extends TeamWorkFlowArtifact> defaultTeamWorkflows;

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               List<Artifact> artifacts = variableMap.getArtifacts(TEAM_WORKFLOW);
               boolean duplicateTasks = variableMap.getBoolean(DUPLICATE_TASKS);
               boolean createNewWorkflow =
                  variableMap.getString(DUPLICATE_METHOD).equals(CREATE_NEW_WORFLOW_IN_START_STATE);
               boolean duplicateWorkflow = variableMap.getString(DUPLICATE_METHOD).equals(DUPLICATE_WORKFLOW);
               String title = variableMap.getString(TITLE);

               if (artifacts.isEmpty()) {
                  AWorkbench.popup("ERROR", "Must drag in Team Workflow to duplicate.");
                  return;
               }
               if (!createNewWorkflow && !duplicateWorkflow) {
                  AWorkbench.popup("ERROR", "Please select \"Duplicate Method\".");
                  return;
               }
               if (duplicateTasks && createNewWorkflow) {
                  AWorkbench.popup("ERROR", "Can not create workflow as new and duplicate tasks.");
                  return;
               }
               Artifact artifact = artifacts.iterator().next();
               if (!artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
                  AWorkbench.popup("ERROR", "Artifact MUST be Team Workflow");
                  return;
               }
               try {
                  Collection<TeamWorkFlowArtifact> teamArts = new ArrayList<TeamWorkFlowArtifact>();
                  for (Artifact art : artifacts) {
                     if (art instanceof TeamWorkFlowArtifact) {
                        teamArts.add((TeamWorkFlowArtifact) art);
                     }
                  }
                  //run from the BLAM editor directly, need to initalize default teamWFs
                  if (!teamArts.isEmpty()) {
                     if (defaultTeamWorkflows == null) {
                        setDefaultTeamWorkflows(teamArts);
                     }
                  }
                  if (createNewWorkflow) {
                     handleCreateNewWorkflow(teamArts, title);
                  } else {
                     handleCreateDuplicate(teamArts, duplicateTasks, title);
                  }
               } catch (Exception ex) {
                  log(ex);
               } finally {
                  AtsUtilClient.setEmailEnabled(true);
               }

            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         };
      });
   }

   private void handleCreateNewWorkflow(Collection<TeamWorkFlowArtifact> teamArts, String title) {
      AtsUser user = AtsClientService.get().getUserService().getCurrentUser();

      List<IAtsTeamWorkflow> teamWfs = new LinkedList<>();
      for (TeamWorkFlowArtifact teamArt : getDefaultTeamWorkflows()) {
         teamWfs.add(teamArt);
      }
      DuplicateWorkflowAtStartStateOperation op =
         new DuplicateWorkflowAtStartStateOperation(teamWfs, title, user, AtsClientService.get());
      IValidatingOperation operation = op;

      XResultData results = validateAndRun(operation);

      if (!results.isErrors()) {
         for (IAtsTeamWorkflow newTeamArt : op.getResults().values()) {
            WorkflowEditor.edit(newTeamArt);
         }
      }
   }

   private XResultData validateAndRun(IValidatingOperation operation) {
      XResultData results = operation.validate();

      if (results.isErrors()) {
         XResultDataUI.report(results, getName());
         return results;
      }

      results = operation.run();
      if (results.isErrors()) {
         XResultDataUI.report(results, getName());
         return results;
      }
      return results;
   }

   private void handleCreateDuplicate(Collection<TeamWorkFlowArtifact> teamArts, boolean duplicateTasks, String title) {
      AtsUser user = AtsClientService.get().getUserService().getCurrentUser();

      List<IAtsTeamWorkflow> teamWfs = new LinkedList<>();
      for (TeamWorkFlowArtifact teamArt : getDefaultTeamWorkflows()) {
         teamWfs.add(teamArt);
      }
      DuplicateWorkflowAsIsOperation op =
         new DuplicateWorkflowAsIsOperation(teamWfs, duplicateTasks, title, user, AtsClientService.get());
      IValidatingOperation operation = op;

      XResultData results = validateAndRun(operation);

      if (!results.isErrors()) {
         for (IAtsTeamWorkflow newTeamArt : op.getResults().values()) {
            WorkflowEditor.edit(newTeamArt);
         }
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(TEAM_WORKFLOW) && defaultTeamWorkflows != null) {
         XListDropViewer viewer = (XListDropViewer) xWidget;
         viewer.setInput(defaultTeamWorkflows);
      }
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + TEAM_WORKFLOW + "\" />" +
      //
         "<XWidget xwidgetType=\"XCombo(" + CREATE_NEW_WORFLOW_IN_START_STATE + "," + DUPLICATE_WORKFLOW + ")\" required=\"true\" displayName=\"" + DUPLICATE_METHOD + "\" horizontalLabel=\"true\" />" +
         //
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + DUPLICATE_TASKS + "\" horizontalLabel=\"true\" defaultValue=\"false\"/>" +
         //
         "<XWidget xwidgetType=\"XText\" displayName=\"" + TITLE + "\" horizontalLabel=\"true\" defaultValue=\"" + getDefaultTitle() + "\"/>" +
         //
         "</xWidgets>";
   }

   /**
    * Return "Copy of"-title if all titles of workflows are the same, else ""
    */
   private String getDefaultTitle() {
      if (defaultTeamWorkflows != null && defaultTeamWorkflows.size() == 1) {
         return AXml.textToXml("Copy of " + defaultTeamWorkflows.iterator().next().getName());
      } else {
         return "";
      }
   }

   @Override
   public String getDescriptionUsage() {
      return "Duplicate team workflow(s) as a carbon copy (all fields/states/assignees will be exactly as they are) or as new workflows in start state.";
   }

   /**
    * @return the defaultTeamWorkflows
    */
   public Collection<? extends TeamWorkFlowArtifact> getDefaultTeamWorkflows() {
      return defaultTeamWorkflows;
   }

   /**
    * @param defaultTeamWorkflows the defaultTeamWorkflows to set
    */
   public void setDefaultTeamWorkflows(Collection<? extends TeamWorkFlowArtifact> defaultTeamWorkflows) {
      this.defaultTeamWorkflows = defaultTeamWorkflows;
   }

   @Override
   public String getName() {
      return "Duplicate Workflow";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS");
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return java.util.Collections.singleton(CoreUserGroups.Everyone);
   }

}