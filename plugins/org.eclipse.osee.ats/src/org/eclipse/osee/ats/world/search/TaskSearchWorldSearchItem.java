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
package org.eclipse.osee.ats.world.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.artifact.AbstractTaskableArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionLockedType;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.task.ITaskEditorProvider;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskEditorParameterSearchItem;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.util.widgets.XStateCombo;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem.ReleasedOption;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelGroupSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class TaskSearchWorldSearchItem extends TaskEditorParameterSearchItem {

   private XMembersCombo assigneeCombo;
   private XCheckBox includeCompletedCheckbox;
   private XCheckBox includeCancelledCheckbox;
   private XHyperlabelTeamDefinitionSelection teamCombo = null;
   private XHyperlabelGroupSelection groupWidget = null;
   private XCombo versionCombo = null;
   private XStateCombo stateCombo = null;

   public TaskSearchWorldSearchItem(WorldSearchItem worldSearchItem) {
      super(worldSearchItem);
   }

   public TaskSearchWorldSearchItem() {
      super("Task Search");
   }

   @Override
   public String getParameterXWidgetXml() {
      return "<xWidgets>" +
      //
      "<XWidget displayName=\"Team Definitions(s)\" xwidgetType=\"XHyperlabelTeamDefinitionSelection\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Version\" beginComposite=\"6\" xwidgetType=\"XCombo()\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Assignee\" xwidgetType=\"XMembersCombo\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"State\" xwidgetType=\"XStateCombo\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Group(s)\" beginComposite=\"2\" xwidgetType=\"XHyperlabelGroupSelection\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Completed\" beginComposite=\"4\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Cancelled\" xwidgetType=\"XCheckBox\"  defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "</xWidgets>";
   }

   @Override
   public Collection<? extends Artifact> getTaskEditorTaskArtifacts() throws OseeCoreException {
      List<Artifact> workflows = new ArrayList<Artifact>();
      Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
      VersionArtifact verArt = getSelectedVersionArtifact();
      Collection<Artifact> groups = getSelectedGroups();
      User user = getSelectedUser();

      // If user selected, handle that case separately cause it's faster to start with assigned
      if (user != null) {
         Set<TaskArtifact> userTaskArts = getUserAssignedTaskArtifacts();
         if (isIncludeCompletedCheckbox() || isIncludeCancelledCheckbox()) {
            // If include cancelled or completed, need to perform extra search
            // Note: Don't need to do this for Originator, Subscribed or Favorites, cause it does completed canceled in it's own searches
            userTaskArts.addAll(Collections.castMatching(TaskArtifact.class,
               ArtifactQuery.getArtifactListFromTypeAndAttribute(AtsArtifactTypes.Task, AtsAttributeTypes.State,
                  "%<" + user.getUserId() + ">%", AtsUtil.getAtsBranch())));
         }
         Set<TaskArtifact> removeTaskArts = new HashSet<TaskArtifact>();
         for (TaskArtifact taskArt : userTaskArts) {
            if (verArt != null && !verArt.equals(taskArt.getParentTeamWorkflow().getTargetedVersion())) {
               removeTaskArts.add(taskArt);
            }
            if (!teamDefs.isEmpty() && !teamDefs.contains(taskArt.getParentTeamWorkflow().getTeamDefinition())) {
               removeTaskArts.add(taskArt);
            }
         }
         userTaskArts.removeAll(removeTaskArts);
         return filterByCompletedAndStateAndSelectedUser(userTaskArts);

      } // If version specified, get workflows from targeted relation
      if (verArt != null) {
         for (Artifact art : verArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow)) {
            if (teamDefs.isEmpty()) {
               workflows.add(art);
            }
            // Filter by team def if specified
            else if (teamDefs.contains(((TeamWorkFlowArtifact) art).getTeamDefinition())) {
               workflows.add(art);
            }
         }
      }
      // Else, get workflows from teamdefs
      else if (teamDefs.size() > 0) {
         TeamWorldSearchItem teamWorldSearchItem =
            new TeamWorldSearchItem("", teamDefs, true, true, false, false, null, null, ReleasedOption.Both, null);
         workflows.addAll(teamWorldSearchItem.performSearchGetResults(false, SearchType.Search));
      } else if (groups.size() > 0) {
         Set<TaskArtifact> taskArts = new HashSet<TaskArtifact>();
         for (Artifact groupArt : groups) {
            for (Artifact art : groupArt.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Members)) {
               if (art instanceof TaskArtifact) {
                  taskArts.add((TaskArtifact) art);
               } else if (art instanceof AbstractTaskableArtifact) {
                  taskArts.addAll(((AbstractTaskableArtifact) art).getTaskArtifacts());
               }
            }
         }
         return filterByCompletedAndStateAndSelectedUser(taskArts);
      }

      // Bulk load tasks related to workflows
      Collection<Artifact> artifacts =
         RelationManager.getRelatedArtifacts(workflows, 1, AtsRelationTypes.SmaToTask_Task);

      // Apply the remaining criteria
      return filterByCompletedAndStateAndSelectedUser(artifacts);
   }

   private Set<TaskArtifact> getUserAssignedTaskArtifacts() throws OseeCoreException {
      Set<TaskArtifact> tasks = new HashSet<TaskArtifact>();
      for (Artifact art : AtsUtil.getAssigned(getSelectedUser(), TaskArtifact.class)) {
         tasks.add((TaskArtifact) art);
      }
      return tasks;
   }

   private Collection<TaskArtifact> filterByCompletedAndStateAndSelectedUser(Collection<? extends Artifact> artifacts) throws OseeCoreException {
      Set<TaskArtifact> tasks = new HashSet<TaskArtifact>();
      String selectedState = getSelectedState();
      boolean isSelectedStateValid = Strings.isValid(selectedState);
      for (Artifact art : artifacts) {
         TaskArtifact taskArt = (TaskArtifact) art;
         if (isSelectedStateValid) {
            if (!taskArt.getCurrentStateName().equals(selectedState)) {
               continue;
            }
         }
         // If not include completed and task is such, skip this task
         if (!isIncludeCompletedCheckbox() && taskArt.isCompleted()) {
            continue;
         }
         if (!isIncludeCancelledCheckbox() && taskArt.isCancelled()) {
            continue;
         }
         boolean isIncludeCompletedAndCompleted = isIncludeCompletedCheckbox() && taskArt.isCompleted();
         boolean isIncludeCancelledAndCancelled = isIncludeCancelledCheckbox() && taskArt.isCancelled();
         // If include completed and task is such and user not implementer, skip this task
         if ((isIncludeCompletedAndCompleted || isIncludeCancelledAndCancelled) && getSelectedUser() != null && taskArt.getImplementers().contains(
            getSelectedUser())) {
            tasks.add(taskArt);
            continue;
         }
         // If user is selected and not user is assigned, skip this task
         else if (getSelectedUser() != null && !taskArt.getStateMgr().getAssignees().contains(getSelectedUser())) {
            continue;
         }
         tasks.add(taskArt);
      }
      return tasks;
   }

   @Override
   public Result isParameterSelectionValid() throws OseeCoreException {
      if (getSelectedUser() != null && isIncludeCompletedCheckbox() && isIncludeCancelledCheckbox() && getSelectedVersionArtifact() == null && getSelectedTeamDefinitions().isEmpty()) {
         // This case is unsupported  and should be filtered out prior to this point
         throw new OseeArgumentException("Unsupported User and Include Completed/Cancelled selected.");
      }

      // If only user selected, handle that case separately
      if (getSelectedVersionArtifact() == null && getSelectedTeamDefinitions().isEmpty() && getSelectedUser() != null) {
         return Result.TrueResult;
      }

      if (getSelectedGroups().size() > 0 && (getSelectedVersionArtifact() != null || getSelectedTeamDefinitions().size() > 0)) {
         // This case is unsupported  and should be filtered out prior to this point
         throw new OseeArgumentException("Unsupported Groups selection with Version or Team(s).");
      }
      return Result.TrueResult;
   }

   @Override
   public Collection<TableLoadOption> getTableLoadOptions() {
      return null;
   }

   @Override
   public String getTaskEditorLabel(SearchType searchType) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
      if (teamDefs.size() > 0) {
         sb.append(" - Teams: ");
         sb.append(org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", teamDefs));
      }
      if (getSelectedVersionArtifact() != null) {
         sb.append(" - Version: ");
         sb.append(getSelectedVersionArtifact());
      }
      if (getSelectedGroups().size() > 0) {
         sb.append(" - Groups: ");
         sb.append(Collections.toString(",", getSelectedGroups()));
      }
      if (getSelectedUser() != null) {
         sb.append(" - Assignee: ");
         sb.append(getSelectedUser());
      }
      if (getSelectedState() != null) {
         sb.append(" - State: ");
         sb.append(getSelectedState());
      }
      if (isIncludeCompletedCheckbox() && isIncludeCancelledCheckbox()) {
         sb.append(" - Include Completed/Cancelled");
      }
      if (isIncludeCompletedCheckbox()) {
         sb.append(" - Include Completed");
      }
      if (isIncludeCancelledCheckbox()) {
         sb.append(" - Include Cancelled");
      }
      return Strings.truncate("Tasks" + sb.toString(), TaskEditor.TITLE_MAX_LENGTH, true);
   }

   @Override
   public void createXWidgetLayoutData(DynamicXWidgetLayoutData layoutData, XWidget widget, FormToolkit toolkit, Artifact art, XModifiedListener modListener, boolean isEditable) {
      // do nothing
   }

   @Override
   public void widgetCreating(XWidget widget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      // do nothing
   }

   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      if (widget.getLabel().equals("Group(s)")) {
         groupWidget = (XHyperlabelGroupSelection) widget;
      }
      if (widget.getLabel().equals("Assignee")) {
         assigneeCombo = (XMembersCombo) widget;
      }
      if (widget.getLabel().equals("Include Completed")) {
         includeCompletedCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Include Cancelled")) {
         includeCancelledCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Version")) {
         versionCombo = (XCombo) widget;
         versionCombo.getComboBox().setVisibleItemCount(25);
         widget.setToolTip("Select Team to populate Version list");
      }
      if (widget.getLabel().equals("State")) {
         stateCombo = (XStateCombo) widget;
         stateCombo.getComboViewer().getCombo().setVisibleItemCount(25);
         widget.setToolTip("Select State of Task");
      }
      if (widget.getLabel().equals("Team Definitions(s)")) {
         teamCombo = (XHyperlabelTeamDefinitionSelection) widget;
         teamCombo.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget) {
               if (versionCombo != null) {
                  try {
                     Collection<TeamDefinitionArtifact> teamDefArts = getSelectedTeamDefinitions();
                     if (teamDefArts.isEmpty()) {
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
                        Artifacts.artNames(teamDefHoldingVersions.getVersionsArtifacts(VersionReleaseType.Both,
                           VersionLockedType.Both));
                     if (names.isEmpty()) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     versionCombo.setDataStrings(names.toArray(new String[names.size()]));
                  } catch (Exception ex) {
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            }
         });
      }
   }

   private User getSelectedUser() {
      if (assigneeCombo == null) {
         return null;
      }
      return assigneeCombo.getUser();
   }

   public void setSelectedUser(User user) {
      if (assigneeCombo != null) {
         assigneeCombo.set(user);
      }
   }

   private boolean isIncludeCancelledCheckbox() {
      if (includeCancelledCheckbox == null) {
         return false;
      }
      return includeCancelledCheckbox.isSelected();
   }

   public void setIncludeCancelledCheckbox(boolean selected) {
      if (includeCancelledCheckbox != null) {
         includeCancelledCheckbox.set(selected);
      }
   }

   private boolean isIncludeCompletedCheckbox() {
      if (includeCompletedCheckbox == null) {
         return false;
      }
      return includeCompletedCheckbox.isSelected();
   }

   public void setIncludeCompletedCheckbox(boolean selected) {
      if (includeCompletedCheckbox != null) {
         includeCompletedCheckbox.set(selected);
      }
   }

   private String getSelectedState() {
      if (stateCombo == null) {
         return null;
      }
      return stateCombo.getSelectedState();
   }

   private VersionArtifact getSelectedVersionArtifact() throws OseeCoreException {
      if (versionCombo == null) {
         return null;
      }
      String versionStr = versionCombo.get();
      if (!Strings.isValid(versionStr)) {
         return null;
      }
      Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
      if (teamDefs.size() > 0) {
         TeamDefinitionArtifact teamDefHoldingVersions = teamDefs.iterator().next().getTeamDefinitionHoldingVersions();
         if (teamDefHoldingVersions == null) {
            return null;
         }
         for (VersionArtifact versionArtifact : teamDefHoldingVersions.getVersionsArtifacts(VersionReleaseType.Both,
            VersionLockedType.Both)) {
            if (versionArtifact.getName().equals(versionStr)) {
               return versionArtifact;
            }
         }
      }
      return null;
   }

   public void setVersion(String versionStr) {
      if (versionCombo != null && versionCombo.getInDataStrings() != null && versionCombo.getInDataStrings().length > 0) {
         versionCombo.set(versionStr);
      }
   }

   private Collection<TeamDefinitionArtifact> getSelectedTeamDefinitions() {
      return teamCombo.getSelectedTeamDefintions();
   }

   public void setSelectedTeamDefinitions(Collection<TeamDefinitionArtifact> selectedTeamDefs) {
      if (teamCombo != null) {
         teamCombo.setSelectedTeamDefs(selectedTeamDefs);
         teamCombo.notifyXModifiedListeners();
      }
   }

   private Collection<Artifact> getSelectedGroups() {
      return groupWidget.getSelectedGroups();
   }

   public void setSelectedGroups(Set<Artifact> selectedUsers) {
      if (groupWidget != null) {
         groupWidget.setSelectedGroups(selectedUsers);
      }
   }

   public void handleSelectedGroupsClear() {
      if (groupWidget != null) {
         groupWidget.handleClear();
      }
   }

   @Override
   public TaskSearchWorldSearchItem copy() {
      return new TaskSearchWorldSearchItem(this);
   }

   @Override
   public ITaskEditorProvider copyProvider() {
      return null;
   }

   @Override
   public void setCustomizeData(CustomizeData customizeData) {
      // do nothing
   }

   @Override
   public void setTableLoadOptions(TableLoadOption... tableLoadOptions) {
      // do nothing
   }

}
