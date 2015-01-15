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
package org.eclipse.osee.ats.navigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.core.config.Versions;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.util.widgets.XStateSearchCombo;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.ats.world.search.UserWorldSearchItem;
import org.eclipse.osee.ats.world.search.UserWorldSearchItem.UserSearchOption;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class UserSearchWorkflowSearchItem extends WorldEditorParameterSearchItem {

   private XHyperlabelTeamDefinitionSelection teamCombo = null;
   private XCombo versionCombo = null;
   private XMembersCombo userCombo;
   private XCheckBox includeCancelledCheckbox;
   private XCheckBox includeCompletedCheckbox;
   private XCheckBox assigneeCheckbox;
   private XCheckBox favoriteCheckbox;
   private XCheckBox subscribedCheckbox;
   private XCheckBox originatorCheckbox;
   private XCheckBox reviewsCheckbox;
   private XCheckBox teamWorkflowsCheckbox;
   private XCheckBox tasksCheckbox;
   private XStateSearchCombo stateCombo = null;

   public UserSearchWorkflowSearchItem() {
      super("User Search", FrameworkImage.USER);
   }

   public UserSearchWorkflowSearchItem(UserSearchWorkflowSearchItem editTeamWorkflowSearchItem) {
      super(editTeamWorkflowSearchItem, FrameworkImage.USER);
   }

   @Override
   public UserSearchWorkflowSearchItem copy() {
      return new UserSearchWorkflowSearchItem(this);
   }

   @Override
   public UserSearchWorkflowSearchItem copyProvider() {
      return new UserSearchWorkflowSearchItem(this);
   }

   @Override
   public String getParameterXWidgetXml() {
      return "<xWidgets>" +
      //
      "<XWidget xwidgetType=\"XMembersCombo\" beginComposite=\"10\" displayName=\"User\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Assignee\" xwidgetType=\"XCheckBox\" defaultValue=\"true\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Originated\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Subscribed\"  xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Favorites\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Team Workflows\" beginComposite=\"6\" xwidgetType=\"XCheckBox\" defaultValue=\"true\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Reviews\" xwidgetType=\"XCheckBox\" defaultValue=\"true\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Tasks\" endComposite=\"true\" xwidgetType=\"XCheckBox\" defaultValue=\"true\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Version\" beginComposite=\"5\" xwidgetType=\"XCombo()\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"State\" xwidgetType=\"XStateSearchCombo\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Team Definitions(s)\" beginComposite=\"2\" endComposite=\"true\" xwidgetType=\"XHyperlabelTeamDefinitionSelection\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Completed\" beginComposite=\"4\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Cancelled\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "</xWidgets>";
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      Collection<IAtsTeamDefinition> teamDefs = getSelectedTeamDefinitions();
      if (teamDefs.size() > 0) {
         sb.append(" - Teams: ");
         sb.append(org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", teamDefs));
      }
      if (getSelectedVersionArtifact() != null) {
         sb.append(" - Version: ");
         sb.append(getSelectedVersionArtifact());
      }
      if (getSelectedUser() != null) {
         sb.append(" - User: ");
         sb.append(getSelectedUser());
      }
      if (isIncludeCancelledCheckbox()) {
         sb.append(" - Include Cancelled");
      }
      if (isIncludeCompletedCheckbox()) {
         sb.append(" - Include Completed");
      }
      if (isAssigneeCheckbox()) {
         sb.append(" - Assignee");
      }
      if (getSelectedState() != null) {
         sb.append(" - State: ");
      }
      if (isOriginatedCheckbox()) {
         sb.append(" - Originated");
      }
      if (isFavoritesCheckbox()) {
         sb.append(" - Favorites");
      }
      if (isSubscribedCheckbox()) {
         sb.append(" - Subscribed");
      }
      if (isReviewsCheckbox()) {
         sb.append(" - Reviews");
      }
      if (isTeamWorkflowsCheckbox()) {
         sb.append(" - Team Workflows");
      }
      if (isTasksCheckbox()) {
         sb.append(" - Tasks");
      }
      return Strings.truncate("User Search" + sb.toString(), WorldEditor.TITLE_MAX_LENGTH, true);
   }

   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      if (widget.getLabel().equals("User")) {
         userCombo = (XMembersCombo) widget;
      }
      if (widget.getLabel().equals("Include Completed")) {
         includeCompletedCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Include Cancelled")) {
         includeCancelledCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Assignee")) {
         assigneeCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Favorites")) {
         favoriteCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Subscribed")) {
         subscribedCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Originated")) {
         originatorCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Include Team Workflows")) {
         teamWorkflowsCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Include Reviews")) {
         reviewsCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Include Tasks")) {
         tasksCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Version")) {
         versionCombo = (XCombo) widget;
         versionCombo.getComboBox().setVisibleItemCount(25);
         widget.setToolTip("Select Team to populate Version list");
      }
      if (widget.getLabel().equals("State")) {
         stateCombo = (XStateSearchCombo) widget;
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
                     Collection<IAtsTeamDefinition> teamDefArts = getSelectedTeamDefinitions();
                     if (teamDefArts.isEmpty()) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     IAtsTeamDefinition teamDefHoldingVersions =
                        teamDefArts.iterator().next().getTeamDefinitionHoldingVersions();
                     if (teamDefHoldingVersions == null) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     Collection<String> names =
                        Versions.getNames(teamDefHoldingVersions.getVersions(VersionReleaseType.Both,
                           VersionLockedType.Both));
                     if (names.isEmpty()) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     List<String> namesList = new ArrayList<String>(names);
                     java.util.Collections.sort(namesList);
                     versionCombo.setDataStrings(namesList.toArray(new String[namesList.size()]));
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            }
         });
      }
   }

   public IAtsUser getSelectedUser() throws OseeCoreException {
      if (userCombo == null || userCombo.getUser() == null) {
         return null;
      }
      return AtsClientService.get().getUserServiceClient().getUserFromOseeUser(userCombo.getUser());
   }

   public void setSelectedUser(IAtsUser user) throws OseeCoreException {
      if (userCombo != null) {
         userCombo.set(AtsClientService.get().getUserServiceClient().getOseeUser(user));
      }
   }

   public String getSelectedState() {
      if (stateCombo == null) {
         return null;
      }
      return stateCombo.getSelectedState();
   }

   public void setSelectedState(String stateName) {
      if (stateCombo == null) {
         return;
      }
      List<Object> states = new ArrayList<Object>();
      states.add("Implement");
      stateCombo.setSelected(states);
   }

   public void setSelected(UserSearchOption userSearchOption, boolean set) throws OseeStateException {
      if (userSearchOption == UserSearchOption.Assignee) {
         assigneeCheckbox.set(set);
      } else if (userSearchOption == UserSearchOption.Favorites) {
         favoriteCheckbox.set(set);
      } else if (userSearchOption == UserSearchOption.Subscribed) {
         subscribedCheckbox.set(set);
      } else if (userSearchOption == UserSearchOption.Originator) {
         originatorCheckbox.set(set);
      } else if (userSearchOption == UserSearchOption.IncludeCancelled) {
         includeCancelledCheckbox.set(set);
      } else if (userSearchOption == UserSearchOption.IncludeCompleted) {
         includeCompletedCheckbox.set(set);
      } else if (userSearchOption == UserSearchOption.IncludeReviews) {
         reviewsCheckbox.set(set);
      } else if (userSearchOption == UserSearchOption.IncludeTeamWorkflows) {
         teamWorkflowsCheckbox.set(set);
      } else if (userSearchOption == UserSearchOption.IncludeTasks) {
         tasksCheckbox.set(set);
      } else {
         throw new OseeStateException("Unhandled checkbox [%s]", userSearchOption);
      }
   }

   public boolean isIncludeCompletedCheckbox() {
      if (includeCompletedCheckbox == null) {
         return false;
      }
      return includeCompletedCheckbox.isSelected();
   }

   public boolean isIncludeCancelledCheckbox() {
      if (includeCancelledCheckbox == null) {
         return false;
      }
      return includeCancelledCheckbox.isSelected();
   }

   public boolean isAssigneeCheckbox() {
      if (assigneeCheckbox == null) {
         return false;
      }
      return assigneeCheckbox.isSelected();
   }

   public boolean isFavoritesCheckbox() {
      if (favoriteCheckbox == null) {
         return false;
      }
      return favoriteCheckbox.isSelected();
   }

   public boolean isOriginatedCheckbox() {
      if (originatorCheckbox == null) {
         return false;
      }
      return originatorCheckbox.isSelected();
   }

   public boolean isSubscribedCheckbox() {
      if (subscribedCheckbox == null) {
         return false;
      }
      return subscribedCheckbox.isSelected();
   }

   public boolean isTeamWorkflowsCheckbox() {
      if (teamWorkflowsCheckbox == null) {
         return false;
      }
      return teamWorkflowsCheckbox.isSelected();
   }

   public boolean isReviewsCheckbox() {
      if (reviewsCheckbox == null) {
         return false;
      }
      return reviewsCheckbox.isSelected();
   }

   public boolean isTasksCheckbox() {
      if (tasksCheckbox == null) {
         return false;
      }
      return tasksCheckbox.isSelected();
   }

   public void includeCompletedCheckbox(boolean selected) {
      if (includeCompletedCheckbox != null) {
         includeCompletedCheckbox.set(selected);
      }
   }

   public void includeCancelledCheckbox(boolean selected) {
      if (includeCancelledCheckbox != null) {
         includeCancelledCheckbox.set(selected);
      }
   }

   public IAtsVersion getSelectedVersionArtifact() throws OseeCoreException {
      if (versionCombo == null) {
         return null;
      }
      String versionStr = versionCombo.get();
      if (!Strings.isValid(versionStr)) {
         return null;
      }
      Collection<IAtsTeamDefinition> teamDefs = getSelectedTeamDefinitions();
      if (teamDefs.size() > 0) {
         IAtsTeamDefinition teamDefHoldingVersions = teamDefs.iterator().next().getTeamDefinitionHoldingVersions();
         if (teamDefHoldingVersions == null) {
            return null;
         }
         for (IAtsVersion versionArtifact : teamDefHoldingVersions.getVersions(VersionReleaseType.Both,
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

   public Collection<IAtsTeamDefinition> getSelectedTeamDefinitions() {
      if (teamCombo == null) {
         return java.util.Collections.emptyList();
      }
      return teamCombo.getSelectedTeamDefintions();
   }

   public void setSelectedTeamDefinitions(Set<IAtsTeamDefinition> selectedTeamDefs) {
      if (teamCombo != null) {
         teamCombo.setSelectedTeamDefs(selectedTeamDefs);
         teamCombo.notifyXModifiedListeners();
      }
   }

   @Override
   public void createXWidgetLayoutData(XWidgetRendererItem layoutData, XWidget widget, FormToolkit toolkit, Artifact art, XModifiedListener modListener, boolean isEditable) {
      // do nothing
   }

   @Override
   public void widgetCreating(XWidget widget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      // do nothing
   }

   @Override
   public Result isParameterSelectionValid() {
      try {
         IAtsUser user = getSelectedUser();
         if (user == null) {
            return new Result("You must select at User.");
         }
         if (!isAssigneeCheckbox() && !isSubscribedCheckbox() && !isOriginatedCheckbox() && !isFavoritesCheckbox()) {
            return new Result("You must select one or more of Assigneed, Originated, Subscribed or Favorites");
         }
         if (!isTeamWorkflowsCheckbox() && !isReviewsCheckbox() && !isTasksCheckbox()) {
            return new Result("You must select one or more of Team Workflows, Reviews or Tasks");
         }
         return Result.TrueResult;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Result("Exception: " + ex.getLocalizedMessage());
      }
   }

   @Override
   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      return null;
   }

   @Override
   public IAtsVersion getTargetedVersionArtifact() throws OseeCoreException {
      if (versionCombo == null) {
         return null;
      }
      return getSelectedVersionArtifact();
   }

   @Override
   public Callable<Collection<? extends Artifact>> createSearch() throws OseeCoreException {
      UserSearchWorkflowSearchItem params = this;
      IAtsVersion version = params.getSelectedVersionArtifact();
      final UserWorldSearchItem searchItem =
         new UserWorldSearchItem(
            params.getSelectedUser(),
            params.getSelectedTeamDefinitions(),
            (version != null ? Collections.singleton(version) : null),
            params.getSelectedState(),
            //
            (params.isAssigneeCheckbox() ? UserSearchOption.Assignee : UserSearchOption.None),
            (params.isFavoritesCheckbox() ? UserSearchOption.Favorites : UserSearchOption.None),
            (params.isOriginatedCheckbox() ? UserSearchOption.Originator : UserSearchOption.None),
            (params.isSubscribedCheckbox() ? UserSearchOption.Subscribed : UserSearchOption.None),
            (params.isReviewsCheckbox() ? UserSearchOption.IncludeReviews : UserSearchOption.None),
            (params.isTeamWorkflowsCheckbox() ? UserSearchOption.IncludeTeamWorkflows : UserSearchOption.None),
            (params.isTasksCheckbox() ? UserSearchOption.IncludeTasks : UserSearchOption.None),
            (params.isIncludeCancelledCheckbox() ? UserSearchOption.IncludeCancelled : UserSearchOption.None),
            (params.isIncludeCompletedCheckbox() ? UserSearchOption.IncludeCompleted : UserSearchOption.None));
      return new Callable<Collection<? extends Artifact>>() {

         @Override
         public Collection<? extends Artifact> call() throws Exception {
            return searchItem.performSearch();
         }
      };
   }

}
