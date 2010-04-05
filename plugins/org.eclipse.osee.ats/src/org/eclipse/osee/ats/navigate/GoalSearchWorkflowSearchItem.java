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
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.ats.world.search.GoalSearchItem;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
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
public class GoalSearchWorkflowSearchItem extends WorldEditorParameterSearchItem {

   private XHyperlabelTeamDefinitionSelection teamCombo = null;
   private XMembersCombo assigneeCombo;
   private XCheckBox includeCompletedCancelledCheckbox;

   public GoalSearchWorkflowSearchItem(String name) throws OseeArgumentException {
      super(name, AtsImage.GOAL);
   }

   public GoalSearchWorkflowSearchItem() throws OseeArgumentException {
      this("Goal Search");
   }

   public GoalSearchWorkflowSearchItem(GoalSearchWorkflowSearchItem goalWorkflowSearchItem) throws OseeArgumentException {
      super(goalWorkflowSearchItem, AtsImage.GOAL);
   }

   @Override
   public GoalSearchWorkflowSearchItem copy() throws OseeArgumentException {
      return new GoalSearchWorkflowSearchItem(this);
   }

   public GoalSearchWorkflowSearchItem copyProvider() throws OseeArgumentException {
      return new GoalSearchWorkflowSearchItem(this);
   }

   @Override
   public String getParameterXWidgetXml() throws OseeCoreException {
      return "<xWidgets>" +
      //
      "<XWidget xwidgetType=\"XHyperlabelTeamDefinitionSelection\" displayName=\"Team Definitions(s)\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget xwidgetType=\"XMembersCombo\" displayName=\"Assignee\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Completed/Cancelled\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "</xWidgets>";
   }

   @Override
   public Collection<? extends Artifact> performSearchGetResults(SearchType searchType) throws OseeCoreException {
      return new GoalSearchItem("", getSelectedTeamDefinitions(), isIncludeCompletedCancelledCheckbox(),
            getSelectedUser()).performSearchGetResults(false);
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
      if (teamDefs.size() > 0) {
         sb.append(" - Teams: " + org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", teamDefs));
      }
      if (getSelectedUser() != null) {
         sb.append(" - Assignee: " + getSelectedUser());
      }
      if (isIncludeCompletedCancelledCheckbox()) {
         sb.append(" - Include Completed/Cancelled");
      }
      return Strings.truncate("Goals" + sb.toString(), WorldEditor.TITLE_MAX_LENGTH, true);
   }

   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      if (widget.getLabel().equals("Assignee")) {
         assigneeCombo = (XMembersCombo) widget;
      }
      if (widget.getLabel().equals("Include Completed/Cancelled")) {
         includeCompletedCancelledCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Team Definitions(s)")) {
         teamCombo = (XHyperlabelTeamDefinitionSelection) widget;
      }
   }

   private User getSelectedUser() {
      if (assigneeCombo == null) return null;
      return assigneeCombo.getUser();
   }

   public void setSelectedUser(User user) {
      if (assigneeCombo != null) assigneeCombo.set(user);
   }

   private boolean isIncludeCompletedCancelledCheckbox() {
      if (includeCompletedCancelledCheckbox == null) return false;
      return includeCompletedCancelledCheckbox.isSelected();
   }

   public void includeCompletedCancelledCheckbox(boolean selected) {
      if (includeCompletedCancelledCheckbox != null) includeCompletedCancelledCheckbox.set(selected);
   }

   public Collection<TeamDefinitionArtifact> getSelectedTeamDefinitions() throws OseeCoreException {
      if (teamCombo == null) return java.util.Collections.emptyList();
      return teamCombo.getSelectedTeamDefintions();
   }

   public void setSelectedTeamDefinitions(Collection<TeamDefinitionArtifact> selectedTeamDefs) {
      if (teamCombo != null) {
         teamCombo.setSelectedTeamDefs(selectedTeamDefs);
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
         boolean includeCompleted = isIncludeCompletedCancelledCheckbox();
         if (user != null && includeCompleted) {
            return new Result("Assignee and Include Completed are not compatible selections.");
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

   public VersionArtifact getTargetedVersionArtifact() throws OseeCoreException {
      return null;
   }

}
