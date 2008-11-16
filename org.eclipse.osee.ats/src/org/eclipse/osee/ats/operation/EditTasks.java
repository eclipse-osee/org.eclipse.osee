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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.editor.ITaskEditorProvider;
import org.eclipse.osee.ats.editor.TaskEditor;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.world.search.TeamWorldNewSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldNewSearchItem.ReleasedOption;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelGroupSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class EditTasks extends AbstractBlam {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Search / Edit Tasks", IProgressMonitor.UNKNOWN);

      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            try {
               boolean selected = false;
               StringBuffer sb = new StringBuffer();
               Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
               if (teamDefs.size() > 0) {
                  sb.append("Team Definitions(s): " + org.eclipse.osee.framework.jdk.core.util.Collections.toString(
                        ",", teamDefs) + " - ");
                  selected = true;
               }
               VersionArtifact verArt = getSelectedVersionArtifact();
               if (verArt != null) {
                  sb.append("Version: " + verArt + " - ");
                  selected = true;
               }
               Collection<Artifact> groups = getSelectedGroups();
               if (groups.size() > 0) {
                  sb.append("Groups: " + org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", groups) + " - ");
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
                  AWorkbench.popup("ERROR", "You must select at least Team, Version or Assignee.");
                  return;
               }
               if (groups.size() > 0 && (verArt != null || teamDefs.size() > 0)) {
                  AWorkbench.popup("ERROR", "Group selection not valid with Team and Version.");
                  return;
               }
               if (user != null && includeCompleted && verArt == null && teamDefs.size() == 0) {
                  AWorkbench.popup("ERROR", "You must select at least Team or Version with Include Completed.");
                  return;
               }
               TaskEditor.open(new EditTasksProvider(sb.toString(), teamDefs, groups, user, verArt, includeCompleted));
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });
      monitor.done();
   }

   public class EditTasksProvider implements ITaskEditorProvider {

      private final String title;
      private final User user;
      private final VersionArtifact verArt;
      private final boolean includeCompleted;
      private final Collection<TeamDefinitionArtifact> teamDefs;
      private final Collection<Artifact> groups;

      public EditTasksProvider(String title, Collection<TeamDefinitionArtifact> teamDefs, Collection<Artifact> groups, User user, VersionArtifact verArt, boolean includeCompleted) {
         this.title = title;
         this.teamDefs = teamDefs;
         this.groups = groups;
         this.user = user;
         this.verArt = verArt;
         this.includeCompleted = includeCompleted;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTableLoadOptions()
       */
      @Override
      public Collection<TableLoadOption> getTableLoadOptions() throws OseeCoreException {
         return Collections.emptyList();
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorLabel(org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType)
       */
      @Override
      public String getTaskEditorLabel(SearchType searchType) throws OseeCoreException {
         return title;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorTaskArtifacts()
       */
      @Override
      public Collection<? extends Artifact> getTaskEditorTaskArtifacts() throws OseeCoreException {
         List<Artifact> workflows = new ArrayList<Artifact>();

         // If only user selected, handle that case separately
         if (verArt == null && teamDefs.size() == 0 && user != null) {
            return handleOnlyUserSelected();
         }

         if (user != null && includeCompleted && verArt == null && teamDefs.size() == 0) {
            // This case is unsupported  and should be filtered out prior to this point
            throw new OseeArgumentException("Unsupported User and Include Completed selected.");
         }

         if (groups.size() > 0 && (verArt != null || teamDefs.size() > 0)) {
            // This case is unsupported  and should be filtered out prior to this point
            throw new OseeArgumentException("Unsupported Groups selection with Version or Team(s).");
         }

         // If version specified, get workflows from targeted relation
         if (verArt != null) {
            for (Artifact art : verArt.getRelatedArtifacts(AtsRelation.TeamWorkflowTargetedForVersion_Workflow)) {
               if (teamDefs.size() == 0) {
                  workflows.add(art);
               }
               // Filter by team def if specified
               else if (teamDefs.contains((((TeamWorkFlowArtifact) art).getTeamDefinition()))) {
                  workflows.add(art);
               }
            }
         }
         // Else, get workflows from teamdefs
         else if (teamDefs.size() > 0) {
            TeamWorldNewSearchItem teamWorldSearchItem =
                  new TeamWorldNewSearchItem("", teamDefs, true, false, false, null, null, ReleasedOption.UnReleased);
            workflows.addAll(teamWorldSearchItem.performSearchGetResults(false, SearchType.Search));
         } else if (groups.size() > 0) {
            Set<TaskArtifact> taskArts = new HashSet<TaskArtifact>();
            for (Artifact groupArt : groups) {
               for (Artifact art : groupArt.getRelatedArtifacts(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS)) {
                  if (art instanceof TaskArtifact) {
                     taskArts.add((TaskArtifact) art);
                  } else if (art instanceof StateMachineArtifact) {
                     taskArts.addAll(((StateMachineArtifact) art).getSmaMgr().getTaskMgr().getTaskArtifacts());
                  }
               }
            }
            return filterByCompletedAndSelectedUser(taskArts);
         }

         // Bulk load tasks related to workflows
         Collection<Artifact> artifacts = RelationManager.getRelatedArtifacts(workflows, 1, AtsRelation.SmaToTask_Task);

         // Apply the remaining criteria
         return filterByCompletedAndSelectedUser(artifacts);
      }

      private Collection<TaskArtifact> handleOnlyUserSelected() throws OseeCoreException {
         return filterByCompletedAndSelectedUser(getUserAssignedTaskArtifacts());
      }

      private Collection<TaskArtifact> getUserAssignedTaskArtifacts() throws OseeCoreException {
         Set<TaskArtifact> tasks = new HashSet<TaskArtifact>();
         for (Artifact art : user.getRelatedArtifacts(CoreRelationEnumeration.Users_Artifact)) {
            if (art instanceof TaskArtifact) {
               tasks.add((TaskArtifact) art);
            }
         }
         return tasks;
      }

      private Collection<TaskArtifact> filterByCompletedAndSelectedUser(Collection<? extends Artifact> artifacts) throws OseeCoreException {
         Set<TaskArtifact> tasks = new HashSet<TaskArtifact>();
         for (Artifact art : artifacts) {
            TaskArtifact taskArt = (TaskArtifact) art;
            // If not include completed and task is such, skip this task
            if (!includeCompleted && taskArt.getSmaMgr().isCompleted()) {
               continue;
            }
            // If include completed and task is such and user not implementer, skip this task
            if (includeCompleted && taskArt.getSmaMgr().isCompleted() && user != null && taskArt.getImplementers().contains(
                  user)) {
               tasks.add(taskArt);
               continue;
            }
            // If user is selected and not user is assigned, skip this task
            else if (user != null && !taskArt.getSmaMgr().getStateMgr().getAssignees().contains(user)) {
               continue;
            }
            tasks.add(taskArt);
         }
         return tasks;
      }
   }

   private VersionArtifact getSelectedVersionArtifact() throws OseeCoreException {
      String versionStr = versionCombo.get();
      if (versionStr == null || versionStr.equals("")) return null;
      Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
      if (teamDefs.size() > 0) {
         TeamDefinitionArtifact teamDefHoldingVersions = teamDefs.iterator().next().getTeamDefinitionHoldingVersions();
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
            "<XWidget xwidgetType=\"XHyperlabelTeamDefinitionSelection\" displayName=\"Team Definitions(s)\" horizontalLabel=\"true\"/>" +
            //
            "<XWidget xwidgetType=\"XCombo()\" displayName=\"Version\" horizontalLabel=\"true\"/>" +
            //
            "<XWidget xwidgetType=\"XHyperlabelGroupSelection\" displayName=\"Group(s)\" horizontalLabel=\"true\"/>" +
            //
            "<XWidget xwidgetType=\"XMembersCombo\" displayName=\"Assignee\" horizontalLabel=\"true\"/>" +
            //
            "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Completed\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>";
      widgetXml += "</xWidgets>";
      return widgetXml;
   }

   private XHyperlabelTeamDefinitionSelection teamCombo = null;
   private XHyperlabelGroupSelection groupWidget = null;
   private XCombo versionCombo = null;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#widgetCreated(org.eclipse.osee.framework.ui.skynet.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreated(widget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (widget.getLabel().equals("Group(s)")) {
         groupWidget = (XHyperlabelGroupSelection) widget;
      }
      if (widget.getLabel().equals("Team Definitions(s)")) {
         teamCombo = (XHyperlabelTeamDefinitionSelection) widget;
         teamCombo.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget) {
               if (versionCombo != null) {
                  try {
                     Collection<TeamDefinitionArtifact> teamDefArts = getSelectedTeamDefinitions();
                     if (teamDefArts.size() == 0) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     TeamDefinitionArtifact teamDefHoldingVersions =
                           teamDefArts.iterator().next().getTeamDefinitionHoldingVersions();
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

   private Collection<TeamDefinitionArtifact> getSelectedTeamDefinitions() throws OseeCoreException {
      return teamCombo.getSelectedTeamDefintions();
   }

   private Collection<Artifact> getSelectedGroups() throws OseeCoreException {
      return groupWidget.getSelectedGroups();
   }

}