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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.TaskEditor;
import org.eclipse.osee.ats.editor.TaskEditorInput;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResolutionOptionRule;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class EditTasks extends AbstractBlam {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(final BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Edit Tasks", IProgressMonitor.UNKNOWN);

      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            try {
               boolean selected = false;
               StringBuffer sb = new StringBuffer();
               TeamDefinitionArtifact teamDef = getSelectedTeamDefinition();
               if (teamDef != null) {
                  sb.append("Team: " + teamDef + " - ");
                  selected = true;
               }
               VersionArtifact verArt = getSelectedVersionArtifact();
               if (verArt != null) {
                  sb.append("Version: " + verArt + " - ");
                  selected = true;
               }
               User user = variableMap.getUser("Assignee");
               if (user != null) {
                  sb.append("Assignee: " + user + " - ");
                  selected = true;
               }
               boolean includeCompleted = variableMap.getBoolean("Include Completed");
               if (includeCompleted) {
                  sb.append("Include Completed");
               }
               if (!selected) {
                  AWorkbench.popup("ERROR", "You must select at least one option");
                  return;
               }

               TaskSearchJob taskSearchJob = new TaskSearchJob(sb.toString(), teamDef, user, verArt, includeCompleted);
               taskSearchJob.setUser(true);
               taskSearchJob.setPriority(Job.LONG);
               taskSearchJob.schedule();
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });

      monitor.done();
   }

   private class TaskSearchJob extends Job {

      private final String title;
      private final TeamDefinitionArtifact teamDef;
      private final User user;
      private final VersionArtifact verArt;
      private final boolean includeCompleted;

      /**
       * @param name
       */
      public TaskSearchJob(String title, TeamDefinitionArtifact teamDef, User user, VersionArtifact verArt, boolean includeCompleted) {
         super(title);
         this.title = title;
         this.teamDef = teamDef;
         this.user = user;
         this.verArt = verArt;
         this.includeCompleted = includeCompleted;
      }

      /* (non-Javadoc)
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         try {
            Set<TaskArtifact> tasks = new HashSet<TaskArtifact>();
            List<Artifact> workflows = new ArrayList<Artifact>();

            if (verArt != null) {
               workflows.addAll(verArt.getRelatedArtifacts(AtsRelation.TeamWorkflowTargetedForVersion_Workflow));
            } else if (teamDef != null && teamDef.getTeamDefinitionHoldingVersions() != null) {
               for (VersionArtifact versionArt : teamDef.getTeamDefinitionHoldingVersions().getVersionsArtifacts()) {
                  workflows.addAll(versionArt.getRelatedArtifacts(AtsRelation.TeamWorkflowTargetedForVersion_Workflow));
               }
            }

            List<Artifact> teamDefWorkflows = new ArrayList<Artifact>();
            for (Artifact workflow : workflows) {
               if (teamDef.equals((((TeamWorkFlowArtifact) workflow).getTeamDefinition()))) {
                  teamDefWorkflows.add(workflow);
               }
            }

            // Bulk load tasks related to workflows
            Collection<Artifact> artifacts =
                  RelationManager.getRelatedArtifacts(teamDefWorkflows, 1, AtsRelation.SmaToTask_Task);

            // Apply the remaining criteria
            for (Artifact art : artifacts) {
               TaskArtifact taskArt = (TaskArtifact) art;
               // If include completed and canceled and task is such, check implementer list
               if (includeCompleted && taskArt.getSmaMgr().isCompleted() && taskArt.getImplementers().contains(user)) {
                  tasks.add(taskArt);
               }
               // If user is selected and not user is assigned, skip this task
               if (user != null && !taskArt.getSmaMgr().getStateMgr().getAssignees().contains(user)) {
                  continue;
               }

               tasks.add(taskArt);
            }

            // notify user if no tasks were found instead of kicking off empty task editor
            if (tasks.size() == 0) {
               return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, "No Tasks Match Search Criteria", null);
            }

            // kickoff task editor
            TeamWorkFlowArtifact team = tasks.iterator().next().getParentTeamWorkflow();
            SMAManager teamSmaMgr = new SMAManager(team);
            List<TaskResOptionDefinition> resOptions =
                  TaskResolutionOptionRule.getTaskResolutionOptions(teamSmaMgr.getWorkPageDefinitionByName(DefaultTeamState.Implement.name()));
            TaskEditorInput input = new TaskEditorInput(title, tasks, resOptions);
            TaskEditor.editArtifacts(input);
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         } finally {
            monitor.done();
         }
         return Status.OK_STATUS;
      }

   }

   private VersionArtifact getSelectedVersionArtifact() throws OseeCoreException, SQLException {
      String versionStr = versionCombo.get();
      if (versionStr == null || versionStr.equals("")) return null;
      TeamDefinitionArtifact teamDef = getSelectedTeamDefinition();
      if (teamDef != null) {
         TeamDefinitionArtifact teamDefHoldingVersions = teamDef.getTeamDefinitionHoldingVersions();
         if (teamDefHoldingVersions == null) return null;
         for (VersionArtifact versionArtifact : teamDefHoldingVersions.getVersionsArtifacts(VersionReleaseType.Both)) {
            if (versionArtifact.getDescriptiveName().equals(versionStr)) {
               return versionArtifact;
            }
         }
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   @Override
   public String getXWidgetsXml() {
      String widgetXml =
            "<xWidgets>" +
            //
            "<XWidget xwidgetType=\"XCombo()\" displayName=\"Team\" horizontalLabel=\"true\"/>" +
            //
            "<XWidget xwidgetType=\"XCombo()\" displayName=\"Version\" horizontalLabel=\"true\"/>" +
            //
            "<XWidget xwidgetType=\"XMembersCombo\" displayName=\"Assignee\" horizontalLabel=\"true\"/>" +
            //
            "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Completed\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>";
      widgetXml += "</xWidgets>";
      return widgetXml;
   }

   private XCombo teamCombo = null;
   private XCombo versionCombo = null;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#widgetCreated(org.eclipse.osee.framework.ui.skynet.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException, SQLException {
      super.widgetCreated(widget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (widget.getLabel().equals("Team")) {
         teamCombo = (XCombo) widget;
         teamCombo.setDataStrings(getTeams());
         teamCombo.getComboBox().setVisibleItemCount(25);
         teamCombo.addModifyListener(new ModifyListener() {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            @Override
            public void modifyText(ModifyEvent e) {
               if (versionCombo != null) {
                  try {
                     TeamDefinitionArtifact teamDefArt = getSelectedTeamDefinition();
                     if (teamDefArt == null) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     TeamDefinitionArtifact teamDefHoldingVersions = teamDefArt.getTeamDefinitionHoldingVersions();
                     if (teamDefHoldingVersions == null) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     Collection<String> names =
                           Artifacts.artNames(teamDefHoldingVersions.getVersionsArtifacts(VersionReleaseType.Both));
                     if (names.size() == 0) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     versionCombo.setDataStrings(names.toArray(new String[names.size()]));
                  } catch (Exception ex) {
                     OSEELog.logException(AtsPlugin.class, ex, true);
                  }
               }
            }
         });
      }
      if (widget.getLabel().equals("Version")) {
         versionCombo = (XCombo) widget;
         versionCombo.getComboBox().setVisibleItemCount(25);
         widget.getLabelWidget().setToolTipText("Select Team to populate Version list");
      }
   }

   private TeamDefinitionArtifact getSelectedTeamDefinition() throws OseeCoreException, SQLException {
      String selectedTeam = teamCombo.getComboBox().getText();
      Set<TeamDefinitionArtifact> teams = TeamDefinitionArtifact.getTeamDefinitions(Arrays.asList(selectedTeam));
      if (teams.size() > 0) {
         return teams.iterator().next();
      }
      return null;
   }

   private String[] getTeams() {
      try {
         Collection<String> names = Artifacts.artNames(TeamDefinitionArtifact.getTeamDefinitions(Active.Both));
         String[] namesSorted = names.toArray(new String[names.size()]);
         Arrays.sort(namesSorted);
         return namesSorted;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return Arrays.asList("unabled to acquire teams" + ex.getLocalizedMessage()).toArray(new String[1]);
      }
   }
}