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
package org.eclipse.osee.ats.review;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionManager;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.XHyperlabelActionableItemSelection;
import org.eclipse.osee.ats.util.widgets.XReviewStateSearchCombo;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
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
public class ReviewSearchWorkflowSearchItem extends WorldEditorParameterSearchItem {

   protected XHyperlabelActionableItemSelection aiCombo = null;
   protected XCombo versionCombo = null, reviewFormalCombo = null, reviewTypeCombo = null;
   protected XMembersCombo assigneeCombo;
   protected XCheckBox includeCompletedCheckbox, includeCancelledCheckbox;
   private XReviewStateSearchCombo stateCombo = null;

   public ReviewSearchWorkflowSearchItem(String name) {
      super(name, AtsImage.REVIEW_SEARCH);
   }

   public ReviewSearchWorkflowSearchItem() {
      this("Review Search");
   }

   public ReviewSearchWorkflowSearchItem(ReviewSearchWorkflowSearchItem peerSearchItem) {
      super(peerSearchItem, AtsImage.REVIEW_SEARCH);
   }

   @Override
   public ReviewSearchWorkflowSearchItem copy() {
      return new ReviewSearchWorkflowSearchItem(this);
   }

   @Override
   public ReviewSearchWorkflowSearchItem copyProvider() {
      return new ReviewSearchWorkflowSearchItem(this);
   }

   @SuppressWarnings("unused")
   @Override
   public String getParameterXWidgetXml() throws OseeCoreException {
      return "<xWidgets>" +
      //
      "<XWidget displayName=\"Actionable Item(s)\" xwidgetType=\"XHyperlabelActionableItemSelection\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Version\" xwidgetType=\"XCombo()\" beginComposite=\"6\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Review Type\" xwidgetType=\"XCombo(PeerToPeer,Decision)\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Review Formal Type\" xwidgetType=\"XCombo(InFormal,Formal)\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Assignee\" beginComposite=\"4\" xwidgetType=\"XMembersCombo\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"State\" xwidgetType=\"XReviewStateSearchCombo\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Completed\" beginComposite=\"6\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget displayName=\"Include Cancelled\" xwidgetType=\"XCheckBox\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "</xWidgets>";
   }

   @Override
   public Collection<? extends Artifact> performSearchGetResults(SearchType searchType) throws OseeCoreException {
      Collection<Artifact> artifacts =
         new ReviewWorldSearchItem("", getSelectedAIs(), isIncludeCompletedCheckbox(), isIncludeCancelledCheckbox(),
            false, getSelectedVersionArtifact(), getSelectedUser(), getSelectedReviewFormalType(),
            getSelectedReviewType(), getSelectedState()).performSearchGetResults(false);
      return artifacts;
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      Collection<ActionableItemArtifact> teamDefs = getSelectedAIs();
      if (teamDefs.size() > 0) {
         sb.append(" - AIs: ");
         sb.append(org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", teamDefs));
      }
      if (getSelectedVersionArtifact() != null) {
         sb.append(" - Version: ");
         sb.append(getSelectedVersionArtifact());
      }
      ReviewFormalType reviewFormalType = getSelectedReviewFormalType();
      if (reviewFormalType != null) {
         sb.append(" - Review Formal Type: ");
         sb.append(reviewFormalType);
      }
      ReviewType reviewType = getSelectedReviewType();
      if (reviewType != null) {
         sb.append(" - Review Type: ");
         sb.append(reviewType);
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
      if (widget.getLabel().equals("State")) {
         stateCombo = (XReviewStateSearchCombo) widget;
         stateCombo.getComboViewer().getCombo().setVisibleItemCount(25);
         widget.setToolTip("Select State of Task");
      }
      if (widget.getLabel().equals("Version")) {
         versionCombo = (XCombo) widget;
         versionCombo.getComboBox().setVisibleItemCount(25);
         widget.setToolTip("Select Team to populate Version list");
      }
      if (widget.getLabel().equals("Review Formal Type")) {
         reviewFormalCombo = (XCombo) widget;
         reviewFormalCombo.getComboBox().setVisibleItemCount(25);
      }
      if (widget.getLabel().equals("Review Type")) {
         reviewTypeCombo = (XCombo) widget;
         reviewTypeCombo.getComboBox().setVisibleItemCount(25);
      }
      if (widget.getLabel().equals("Actionable Item(s)")) {
         aiCombo = (XHyperlabelActionableItemSelection) widget;
         aiCombo.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget) {
               if (versionCombo != null) {
                  try {
                     Collection<ActionableItemArtifact> aiArts = getSelectedAIs();
                     if (aiArts.isEmpty()) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     Set<VersionArtifact> versions = getSelectableVersionArtifacts();
                     if (versions.isEmpty()) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     Collection<String> names = Artifacts.artNames(versions);
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

   protected ReviewType getSelectedReviewType() {
      if (reviewTypeCombo == null) {
         return null;
      }
      try {
         return ReviewType.valueOf(reviewTypeCombo.get());
      } catch (IllegalArgumentException ex) {
         return null;
      }
   }

   protected ReviewFormalType getSelectedReviewFormalType() {
      if (reviewFormalCombo == null) {
         return null;
      }
      try {
         return ReviewFormalType.valueOf(reviewFormalCombo.get());
      } catch (IllegalArgumentException ex) {
         return null;
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

   private String getSelectedState() {
      if (stateCombo == null) {
         return null;
      }
      return stateCombo.getSelectedState();
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

   protected Artifact getSelectedVersionArtifact() throws OseeCoreException {
      if (versionCombo == null) {
         return null;
      }
      String versionStr = versionCombo.get();
      if (!Strings.isValid(versionStr)) {
         return null;
      }

      for (Artifact versionArtifact : getSelectableVersionArtifacts()) {
         if (versionArtifact.getName().equals(versionStr)) {
            return versionArtifact;
         }
      }
      return null;
   }

   protected Set<VersionArtifact> getSelectableVersionArtifacts() throws OseeCoreException {
      Collection<ActionableItemArtifact> aias = getSelectedAIs();
      Set<VersionArtifact> versions = new HashSet<VersionArtifact>();
      if (!aias.isEmpty()) {
         for (TeamDefinitionArtifact teamDef : TeamDefinitionManager.getImpactedTeamDefs(aias)) {
            TeamDefinitionArtifact teamDefHoldingVersions = teamDef.getTeamDefinitionHoldingVersions();
            if (teamDefHoldingVersions != null) {
               versions.addAll(teamDefHoldingVersions.getVersionsArtifacts());
            }
         }
      }
      return versions;
   }

   public void setVersion(String versionStr) {
      if (versionCombo != null && versionCombo.getInDataStrings() != null && versionCombo.getInDataStrings().length > 0) {
         versionCombo.set(versionStr);
      }
   }

   public Collection<ActionableItemArtifact> getSelectedAIs() {
      if (aiCombo == null) {
         return java.util.Collections.emptyList();
      }
      return aiCombo.getSelectedTeamDefintions();
   }

   public void setSelectedTeamDefinitions(Collection<ActionableItemArtifact> selectedAis) {
      if (aiCombo != null) {
         aiCombo.setSelectedAIs(selectedAis);
         aiCombo.notifyXModifiedListeners();
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
         Collection<ActionableItemArtifact> ais = getSelectedAIs();
         if (ais.size() > 0) {
            selected = true;
         }
         Artifact verArt = getSelectedVersionArtifact();
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
         if (user != null && includeCompleted && verArt == null && ais.isEmpty()) {
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
   public Artifact getTargetedVersionArtifact() throws OseeCoreException {
      if (versionCombo == null) {
         return null;
      }
      return getSelectedVersionArtifact();
   }

}
