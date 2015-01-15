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
package org.eclipse.osee.ats.goal;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.ats.world.search.GoalSearchItem;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
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
public class GoalSearchWorkflowSearchItem extends WorldEditorParameterSearchItem {

   private XHyperlabelTeamDefinitionSelection teamCombo = null;
   private XMembersCombo assigneeCombo;
   private XCheckBox includeCompletedCancelledCheckbox;

   public GoalSearchWorkflowSearchItem(String name) {
      super(name, AtsImage.GOAL);
   }

   public GoalSearchWorkflowSearchItem() {
      this("Goal Search");
   }

   public GoalSearchWorkflowSearchItem(GoalSearchWorkflowSearchItem goalWorkflowSearchItem) {
      super(goalWorkflowSearchItem, AtsImage.GOAL);
   }

   @Override
   public GoalSearchWorkflowSearchItem copy() {
      return new GoalSearchWorkflowSearchItem(this);
   }

   @Override
   public GoalSearchWorkflowSearchItem copyProvider() {
      return new GoalSearchWorkflowSearchItem(this);
   }

   @Override
   public String getParameterXWidgetXml() {
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
   public String getSelectedName(SearchType searchType) {
      StringBuffer sb = new StringBuffer();
      Collection<IAtsTeamDefinition> teamDefs = getSelectedTeamDefinitions();
      if (teamDefs.size() > 0) {
         sb.append(" - Teams: ");
         sb.append(org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", teamDefs));
      }
      if (getSelectedUser() != null) {
         sb.append(" - Assignee: ");
         sb.append(getSelectedUser());
      }
      if (isIncludeCompletedCancelledCheckbox()) {
         sb.append(" - Include Completed/Cancelled");
      }
      return Strings.truncate("Goals" + sb.toString(), WorldEditor.TITLE_MAX_LENGTH, true);
   }

   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
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

   public User getSelectedUser() {
      if (assigneeCombo == null || assigneeCombo.getUser() == null) {
         return null;
      }
      return assigneeCombo.getUser();
   }

   public void setSelectedUser(User user) {
      if (assigneeCombo != null) {
         assigneeCombo.set(user);
      }
   }

   public boolean isIncludeCompletedCancelledCheckbox() {
      if (includeCompletedCancelledCheckbox == null) {
         return false;
      }
      return includeCompletedCancelledCheckbox.isSelected();
   }

   public void includeCompletedCancelledCheckbox(boolean selected) {
      if (includeCompletedCancelledCheckbox != null) {
         includeCompletedCancelledCheckbox.set(selected);
      }
   }

   public Collection<IAtsTeamDefinition> getSelectedTeamDefinitions() {
      if (teamCombo == null) {
         return java.util.Collections.emptyList();
      }
      return teamCombo.getSelectedTeamDefintions();
   }

   public void setSelectedTeamDefinitions(Collection<IAtsTeamDefinition> selectedTeamDefs) {
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
         User user = getSelectedUser();
         boolean includeCompleted = isIncludeCompletedCancelledCheckbox();
         if (user != null && includeCompleted) {
            return new Result("Assignee and Include Completed are not compatible selections.");
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
   public IAtsVersion getTargetedVersionArtifact() {
      return null;
   }

   @Override
   public Callable<Collection<? extends Artifact>> createSearch() throws OseeCoreException {
      final GoalSearchItem searchItem =
         new GoalSearchItem("", this.getSelectedTeamDefinitions(), this.isIncludeCompletedCancelledCheckbox(),
            this.getSelectedUser());
      searchItem.performSearchGetResults(false);
      return new Callable<Collection<? extends Artifact>>() {

         @Override
         public Collection<? extends Artifact> call() throws Exception {
            return searchItem.performSearchGetResults(false);
         }
      };
   }

}
