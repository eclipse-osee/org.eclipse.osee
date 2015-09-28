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
import java.util.concurrent.Callable;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.review.ReviewFormalType;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.config.Versions;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.XHyperlabelActionableItemSelection;
import org.eclipse.osee.ats.util.widgets.XReviewStateSearchCombo;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      Collection<IAtsActionableItem> teamDefs = getSelectedAIs();
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

   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
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
                     Collection<IAtsActionableItem> aiArts = getSelectedAIs();
                     if (aiArts.isEmpty()) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     Set<IAtsVersion> versions = getSelectableVersionArtifacts();
                     if (versions.isEmpty()) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     Collection<String> names = Versions.getNames(versions);
                     if (names.isEmpty()) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     versionCombo.setDataStrings(names.toArray(new String[names.size()]));
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            }
         });
      }
   }

   public ReviewType getSelectedReviewType() {
      if (reviewTypeCombo == null) {
         return null;
      }
      try {
         return ReviewType.valueOf(reviewTypeCombo.get());
      } catch (IllegalArgumentException ex) {
         return null;
      }
   }

   public ReviewFormalType getSelectedReviewFormalType() {
      if (reviewFormalCombo == null) {
         return null;
      }
      try {
         return ReviewFormalType.valueOf(reviewFormalCombo.get());
      } catch (IllegalArgumentException ex) {
         return null;
      }
   }

   public IAtsUser getSelectedUser() throws OseeCoreException {
      if (assigneeCombo == null || assigneeCombo.getUser() == null) {
         return null;
      }
      return AtsClientService.get().getUserServiceClient().getUserFromOseeUser(assigneeCombo.getUser());
   }

   public void setSelectedUser(IAtsUser user) throws OseeCoreException {
      if (assigneeCombo != null) {
         assigneeCombo.set(AtsClientService.get().getUserServiceClient().getOseeUser(user));
      }
   }

   public String getSelectedState() {
      if (stateCombo == null) {
         return null;
      }
      return stateCombo.getSelectedState();
   }

   public boolean isIncludeCancelledCheckbox() {
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

   public boolean isIncludeCompletedCheckbox() {
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

   public IAtsVersion getSelectedVersionArtifact() throws OseeCoreException {
      if (versionCombo == null) {
         return null;
      }
      String versionStr = versionCombo.get();
      if (!Strings.isValid(versionStr)) {
         return null;
      }

      for (IAtsVersion versionArtifact : getSelectableVersionArtifacts()) {
         if (versionArtifact.getName().equals(versionStr)) {
            return versionArtifact;
         }
      }
      return null;
   }

   protected Set<IAtsVersion> getSelectableVersionArtifacts() throws OseeCoreException {
      Collection<IAtsActionableItem> aias = getSelectedAIs();
      Set<IAtsVersion> versions = new HashSet<>();
      if (!aias.isEmpty()) {
         for (IAtsTeamDefinition teamDef : TeamDefinitions.getImpactedTeamDefs(aias)) {
            IAtsTeamDefinition teamDefHoldingVersions = teamDef.getTeamDefinitionHoldingVersions();
            if (teamDefHoldingVersions != null) {
               versions.addAll(teamDefHoldingVersions.getVersions());
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

   public Collection<IAtsActionableItem> getSelectedAIs() {
      if (aiCombo == null) {
         return java.util.Collections.emptyList();
      }
      return aiCombo.getSelectedActionableItems();
   }

   public void setSelectedTeamDefinitions(Collection<IAtsActionableItem> selectedAis) {
      if (aiCombo != null) {
         aiCombo.setSelectedAIs(selectedAis);
         aiCombo.notifyXModifiedListeners();
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
   public Result isParameterSelectionValid() throws OseeCoreException {
      try {
         boolean selected = false;
         Collection<IAtsActionableItem> ais = getSelectedAIs();
         if (ais.size() > 0) {
            selected = true;
         }
         IAtsVersion verArt = getSelectedVersionArtifact();
         if (verArt != null) {
            selected = true;
         }
         IAtsUser user = getSelectedUser();
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
      ReviewSearchWorkflowSearchItem params = this;
      final ReviewWorldSearchItem searchItem =
         new ReviewWorldSearchItem("", params.getSelectedAIs(), params.isIncludeCompletedCheckbox(),
            params.isIncludeCancelledCheckbox(), false, params.getSelectedVersionArtifact(), params.getSelectedUser(),
            params.getSelectedReviewFormalType(), params.getSelectedReviewType(), params.getSelectedState());

      return new Callable<Collection<? extends Artifact>>() {

         @Override
         public Collection<? extends Artifact> call() throws Exception {
            return searchItem.performSearchGetResults(false);
         }
      };
   }

}
