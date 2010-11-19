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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.AbstractTaskableArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem.ReleasedOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;
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
public class TeamWorkflowSearchWorkflowSearchItem extends WorldEditorParameterSearchItem {

   protected XHyperlabelTeamDefinitionSelection teamCombo = null;
   protected XCombo releasedCombo = null;
   protected XCombo versionCombo = null;
   protected XMembersCombo assigneeCombo;
   protected XCheckBox includeCompletedCheckbox;
   protected XCheckBox includeCancelledCheckbox;
   protected XCheckBox showFlatCheckbox;

   public TeamWorkflowSearchWorkflowSearchItem(String name) {
      super(name, AtsImage.TEAM_WORKFLOW);
   }

   public TeamWorkflowSearchWorkflowSearchItem() {
      this("Team Workflow Search");
   }

   public TeamWorkflowSearchWorkflowSearchItem(TeamWorkflowSearchWorkflowSearchItem editTeamWorkflowSearchItem) {
      super(editTeamWorkflowSearchItem, AtsImage.TEAM_WORKFLOW);
   }

   @Override
   public TeamWorkflowSearchWorkflowSearchItem copy() {
      return new TeamWorkflowSearchWorkflowSearchItem(this);
   }

   @Override
   public TeamWorkflowSearchWorkflowSearchItem copyProvider() {
      return new TeamWorkflowSearchWorkflowSearchItem(this);
   }

   @SuppressWarnings("unused")
   @Override
   public String getParameterXWidgetXml() throws OseeCoreException {
      return "<xWidgets>" +
      //
      "<XWidget displayName=\"Team Definitions(s)\" xwidgetType=\"XHyperlabelTeamDefinitionSelection\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Version\" xwidgetType=\"XCombo()\" beginComposite=\"6\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Released\" xwidgetType=\"XCombo(Both,Released,UnReleased)\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Assignee\" xwidgetType=\"XMembersCombo\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Completed\" beginComposite=\"6\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Cancelled\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Show Flat\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\" toolTip=\"Show Tasks/Reviews flattened instead of hierarchcial\"/>" +
      //
      "</xWidgets>";
   }

   @Override
   public Collection<? extends Artifact> performSearchGetResults(SearchType searchType) throws OseeCoreException {
      Collection<Artifact> artifacts =
         new TeamWorldSearchItem("", getSelectedTeamDefinitions(), isIncludeCompletedCheckbox(),
            isIncludeCancelledCheckbox(), false, false, getSelectedVersionArtifact(), getSelectedUser(),
            getSelectedReleased()).performSearchGetResults(false);
      return filterShowFlat(artifacts);
   }

   protected Collection<? extends Artifact> filterShowFlat(Collection<Artifact> artifacts) throws OseeCoreException {
      if (!isShowFlatCheckbox()) {
         return artifacts;
      }
      Set<Artifact> results = new HashSet<Artifact>(artifacts);
      for (Artifact artifact : artifacts) {
         if (artifact instanceof AbstractTaskableArtifact) {
            results.addAll(((AbstractTaskableArtifact) artifact).getTaskArtifacts());
         }
         if (artifact instanceof TeamWorkFlowArtifact) {
            results.addAll(ReviewManager.getReviews((TeamWorkFlowArtifact) artifact));
         }
      }
      return results;
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
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
      ReleasedOption releaseOption = getSelectedReleased();
      if (releaseOption != null && releaseOption != ReleasedOption.Both) {
         sb.append(" - ReleasedOption: ");
         sb.append(releaseOption);
      }
      if (getSelectedUser() != null) {
         sb.append(" - Assignee: ");
         sb.append(getSelectedUser());
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
      return Strings.truncate(getName() + sb.toString(), WorldEditor.TITLE_MAX_LENGTH, true);
   }

   @SuppressWarnings("unused")
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
      if (widget.getLabel().equals("Show Flat")) {
         showFlatCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Version")) {
         versionCombo = (XCombo) widget;
         versionCombo.getComboBox().setVisibleItemCount(25);
         widget.setToolTip("Select Team to populate Version list");
      }
      if (widget.getLabel().equals("Released")) {
         releasedCombo = (XCombo) widget;
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
                     Collection<String> names = Artifacts.artNames(teamDefHoldingVersions.getVersionsArtifacts());
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

   protected User getSelectedUser() {
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

   protected boolean isIncludeCancelledCheckbox() {
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

   protected boolean isIncludeCompletedCheckbox() {
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

   protected boolean isShowFlatCheckbox() {
      if (showFlatCheckbox == null) {
         return false;
      }
      return showFlatCheckbox.isSelected();
   }

   public void includeShowFlatCheckbox(boolean selected) {
      if (showFlatCheckbox != null) {
         showFlatCheckbox.set(selected);
      }
   }

   protected VersionArtifact getSelectedVersionArtifact() throws OseeCoreException {
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
         for (VersionArtifact versionArtifact : teamDefHoldingVersions.getVersionsArtifacts()) {
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

   public Collection<TeamDefinitionArtifact> getSelectedTeamDefinitions() {
      if (teamCombo == null) {
         return java.util.Collections.emptyList();
      }
      return teamCombo.getSelectedTeamDefintions();
   }

   public void setSelectedTeamDefinitions(Collection<TeamDefinitionArtifact> selectedTeamDefs) {
      if (teamCombo != null) {
         teamCombo.setSelectedTeamDefs(selectedTeamDefs);
         teamCombo.notifyXModifiedListeners();
      }
   }

   protected ReleasedOption getSelectedReleased() {
      if (releasedCombo == null || !Strings.isValid(releasedCombo.get())) {
         return ReleasedOption.Both;
      }
      return ReleasedOption.valueOf(releasedCombo.get());
   }

   public void setSelectedReleased(ReleasedOption option) {
      if (releasedCombo != null) {
         releasedCombo.set(option.toString());
      }
   }

   @Override
   public void createXWidgetLayoutData(DynamicXWidgetLayoutData layoutData, XWidget widget, FormToolkit toolkit, Artifact art, XModifiedListener modListener, boolean isEditable) {
      // do nothing
   }

   @Override
   public void widgetCreating(XWidget widget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      // do nothing
   }

   @SuppressWarnings("unused")
   @Override
   public Result isParameterSelectionValid() throws OseeCoreException {
      try {
         boolean selected = false;
         Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
         if (teamDefs.size() > 0) {
            selected = true;
         }
         VersionArtifact verArt = getSelectedVersionArtifact();
         if (verArt != null) {
            selected = true;
         }
         User user = getSelectedUser();
         if (user != null) {
            selected = true;
         }
         boolean includeCompleted = isIncludeCompletedCheckbox() || isIncludeCancelledCheckbox();
         if (!selected) {
            return new Result("You must select at least Team, Version or Assignee.");
         }
         if (user != null && includeCompleted) {
            return new Result("Assignee and Include Completed are not compatible selections.");
         }
         if (user != null && includeCompleted && verArt == null && teamDefs.isEmpty()) {
            return new Result("You must select at least Team or Version with Include Completed.");
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
      if (versionCombo == null) {
         return null;
      }
      return getSelectedVersionArtifact();
   }

}
