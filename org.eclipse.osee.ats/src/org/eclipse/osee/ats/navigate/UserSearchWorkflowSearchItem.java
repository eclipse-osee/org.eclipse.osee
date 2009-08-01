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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.ats.world.search.UserWorldSearchItem;
import org.eclipse.osee.ats.world.search.UserWorldSearchItem.UserSearchOption;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class UserSearchWorkflowSearchItem extends WorldEditorParameterSearchItem {

   private XHyperlabelTeamDefinitionSelection teamCombo = null;
   private XCombo versionCombo = null;
   private XMembersCombo assigneeCombo;
   private XCheckBox includeCancelledCheckbox;
   private XCheckBox includeCompletedCheckbox;
   private XCheckBox favoriteCheckbox;
   private XCheckBox subscribedCheckbox;
   private XCheckBox originatorCheckbox;
   private XCheckBox reviewsCheckbox;
   private XCheckBox teamWorkflowsCheckbox;
   private XCheckBox tasksCheckbox;

   public UserSearchWorkflowSearchItem() throws OseeArgumentException {
      super("User Search", FrameworkImage.USER);
   }

   public UserSearchWorkflowSearchItem(UserSearchWorkflowSearchItem editTeamWorkflowSearchItem) throws OseeArgumentException {
      super(editTeamWorkflowSearchItem, FrameworkImage.USER);
   }

   @Override
   public UserSearchWorkflowSearchItem copy() throws OseeArgumentException {
      return new UserSearchWorkflowSearchItem(this);
   }

   public UserSearchWorkflowSearchItem copyProvider() throws OseeArgumentException {
      return new UserSearchWorkflowSearchItem(this);
   }

   @Override
   public String getParameterXWidgetXml() throws OseeCoreException {
      return "<xWidgets>" +
      //
      "<XWidget xwidgetType=\"XMembersCombo\" beginComposite=\"14\" displayName=\"Assignee\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Originated\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Subscribed\"  xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Favorites\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Team Workflows\" xwidgetType=\"XCheckBox\" defaultValue=\"true\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Reviews\" xwidgetType=\"XCheckBox\" defaultValue=\"true\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Tasks\" endComposite=\"true\" xwidgetType=\"XCheckBox\" defaultValue=\"true\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Version\" xwidgetType=\"XCombo()\" beginComposite=\"3\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Team Definitions(s)\" endComposite=\"true\" xwidgetType=\"XHyperlabelTeamDefinitionSelection\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Completed\" beginComposite=\"4\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Cancelled\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "</xWidgets>";
   }

   @Override
   public Collection<? extends Artifact> performSearchGetResults(SearchType searchType) throws OseeCoreException {
      return new UserWorldSearchItem(
            getSelectedUser(),
            getSelectedTeamDefinitions(),
            (getSelectedVersionArtifact() != null ? Collections.singleton(getSelectedVersionArtifact()) : null),
            //
            (isFavoritesCheckbox() ? UserSearchOption.Favorites : UserSearchOption.None),
            (isOriginatedCheckbox() ? UserSearchOption.Originator : UserSearchOption.None),
            (isSubscribedCheckbox() ? UserSearchOption.Subscribed : UserSearchOption.None),
            (isReviewsCheckbox() ? UserSearchOption.IncludeReviews : UserSearchOption.None),
            (isTeamWorkflowsCheckbox() ? UserSearchOption.IncludeTeamWorkflows : UserSearchOption.None),
            (isTasksCheckbox() ? UserSearchOption.IncludeTasks : UserSearchOption.None),
            (isIncludeCancelledCheckbox() ? UserSearchOption.IncludeCancelled : UserSearchOption.None),
            (isIncludeCompletedCheckbox() ? UserSearchOption.IncludeCompleted : UserSearchOption.None)

      //
      ).performSearch();
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
      if (teamDefs.size() > 0) {
         sb.append(" - Teams: " + org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", teamDefs));
      }
      if (getSelectedVersionArtifact() != null) {
         sb.append(" - Version: " + getSelectedVersionArtifact());
      }
      if (getSelectedUser() != null) {
         sb.append(" - Assignee: " + getSelectedUser());
      }
      if (isIncludeCancelledCheckbox()) {
         sb.append(" - Include Cancelled");
      }
      if (isIncludeCompletedCheckbox()) {
         sb.append(" - Include Completed");
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
      return "User Search" + sb.toString();
   }

   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      if (widget.getLabel().equals("Assignee")) {
         assigneeCombo = (XMembersCombo) widget;
      }
      if (widget.getLabel().equals("Include Completed")) {
         includeCompletedCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Include Cancelled")) {
         includeCancelledCheckbox = (XCheckBox) widget;
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
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            }
         });
      }
   }

   private User getSelectedUser() {
      if (assigneeCombo == null) return null;
      return assigneeCombo.getUser();
   }

   public void setSelectedUser(User user) {
      if (assigneeCombo != null) assigneeCombo.set(user);
   }

   public void setSelected(UserSearchOption userSearchOption, boolean set) {
      if (userSearchOption == UserSearchOption.Favorites) {
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
         throw new IllegalStateException(String.format("Unhandled checkbox [%s]", userSearchOption));
      }
   }

   private boolean isIncludeCompletedCheckbox() {
      if (includeCompletedCheckbox == null) return false;
      return includeCompletedCheckbox.isSelected();
   }

   private boolean isIncludeCancelledCheckbox() {
      if (includeCancelledCheckbox == null) return false;
      return includeCancelledCheckbox.isSelected();
   }

   private boolean isFavoritesCheckbox() {
      if (favoriteCheckbox == null) return false;
      return favoriteCheckbox.isSelected();
   }

   private boolean isOriginatedCheckbox() {
      if (originatorCheckbox == null) return false;
      return originatorCheckbox.isSelected();
   }

   private boolean isSubscribedCheckbox() {
      if (subscribedCheckbox == null) return false;
      return subscribedCheckbox.isSelected();
   }

   private boolean isTeamWorkflowsCheckbox() {
      if (teamWorkflowsCheckbox == null) return false;
      return teamWorkflowsCheckbox.isSelected();
   }

   private boolean isReviewsCheckbox() {
      if (reviewsCheckbox == null) return false;
      return reviewsCheckbox.isSelected();
   }

   private boolean isTasksCheckbox() {
      if (tasksCheckbox == null) return false;
      return tasksCheckbox.isSelected();
   }

   public void includeCompletedCheckbox(boolean selected) {
      if (includeCompletedCheckbox != null) includeCompletedCheckbox.set(selected);
   }

   public void includeCancelledCheckbox(boolean selected) {
      if (includeCancelledCheckbox != null) includeCancelledCheckbox.set(selected);
   }

   private VersionArtifact getSelectedVersionArtifact() throws OseeCoreException {
      if (versionCombo == null) return null;
      String versionStr = versionCombo.get();
      if (versionStr == null || versionStr.equals("")) return null;
      Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
      if (teamDefs.size() > 0) {
         TeamDefinitionArtifact teamDefHoldingVersions = teamDefs.iterator().next().getTeamDefinitionHoldingVersions();
         if (teamDefHoldingVersions == null) return null;
         for (VersionArtifact versionArtifact : teamDefHoldingVersions.getVersionsArtifacts(VersionReleaseType.Both)) {
            if (versionArtifact.getName().equals(versionStr)) {
               return versionArtifact;
            }
         }
      }
      return null;
   }

   public void setVersion(String versionStr) {
      if (versionCombo != null && versionCombo.getInDataStrings() != null) {
         // should check if the version combo was populated
         if (versionCombo.getInDataStrings().length > 0) {
            versionCombo.set(versionStr);
         }
      }
   }

   public Collection<TeamDefinitionArtifact> getSelectedTeamDefinitions() throws OseeCoreException {
      if (teamCombo == null) return java.util.Collections.emptyList();
      return teamCombo.getSelectedTeamDefintions();
   }

   public void setSelectedTeamDefinitions(Set<TeamDefinitionArtifact> selectedUsers) {
      if (teamCombo != null) {
         teamCombo.setSelectedTeamDefs(selectedUsers);
         teamCombo.notifyXModifiedListeners();
      }
   }

   @Override
   public void createXWidgetLayoutData(DynamicXWidgetLayoutData layoutData, XWidget widget, FormToolkit toolkit, Artifact art, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
   }

   @Override
   public void widgetCreating(XWidget widget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
   }

   @Override
   public Result isParameterSelectionValid() throws OseeCoreException {
      try {
         User user = getSelectedUser();
         if (user == null) {
            return new Result("You must select at Assignee.");
         }
         if (!isTeamWorkflowsCheckbox() && !isReviewsCheckbox() && !isTasksCheckbox()) {
            return new Result("You must select one or more of Team Workflows, Reviews or Tasks");
         }
         return Result.TrueResult;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Result("Exception: " + ex.getLocalizedMessage());
      }
   }

   @Override
   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      return null;
   }

   @Override
   public VersionArtifact getTargetedVersionArtifact() throws OseeCoreException {
      if (versionCombo == null) return null;
      return getSelectedVersionArtifact();
   }

}
