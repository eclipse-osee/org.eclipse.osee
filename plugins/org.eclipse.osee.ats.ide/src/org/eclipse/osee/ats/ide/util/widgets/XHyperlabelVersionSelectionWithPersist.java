/*********************************************************************
 * Copyright (c) 2017 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * Single version selection dialog that persists upon selection. Clear and Close button shown by default to un-select.
 *
 * @author Megumi Telles
 * @author Donald G. Dunne
 */
public abstract class XHyperlabelVersionSelectionWithPersist extends XHyperlinkLabelValueSelection implements ArtifactWidget {

   public static final String WIDGET_ID = XHyperlabelVersionSelectionWithPersist.class.getSimpleName();
   Collection<IAtsVersion> selectableVersions;
   VersionListDialog dialog = null;
   private Artifact artifact;
   private final RelationTypeSide relType;
   protected AtsApi atsApi;
   protected boolean removeAllAllowed = true;

   public XHyperlabelVersionSelectionWithPersist(String label, RelationTypeSide relType) {
      super(label);
      this.relType = relType;
      atsApi = AtsApiService.get();
   }

   public Version getSelectedVersion() {
      ArtifactToken related = atsApi.getRelationResolver().getRelatedOrSentinel(artifact, relType);
      if (related.isValid()) {
         atsApi.getVersionService().createVersion(related);
      }
      return null;
   }

   @Override
   public Object getData() {
      return getSelectedVersion();
   }

   @Override
   public String getCurrentValue() {
      ArtifactToken related = atsApi.getRelationResolver().getRelatedOrSentinel(artifact, relType);
      if (related.isValid()) {
         return related.getName();
      }
      return "Not Set";
   }

   public boolean handleClear() {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Update " + relType.getName());
      changes.unrelateAll(getArtifact(), relType);
      changes.executeIfNeeded();
      notifyXModifiedListeners();
      return true;
   }

   @Override
   public boolean handleSelection() {
      try {
         if (selectableVersions == null) {
            dialog = new VersionListDialog("Select Version", "Select Version", getSelectableVersions());
         } else {
            dialog = new VersionListDialog("Select Version", "Select Version", selectableVersions);
         }
         dialog.setRemoveAllAllowed(removeAllAllowed);
         int result = dialog.open();
         if (result == 0) {
            IAtsChangeSet changes = AtsApiService.get().createChangeSet("Update " + relType.getName());
            changes.setRelation(getArtifact(), relType, dialog.getSelectedFirst());
            TransactionToken transaction = changes.executeIfNeeded();
            if (transaction.isValid()) {
               notifyXModifiedListeners();
            }
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   private Collection<IAtsVersion> getSelectableVersions() {
      if (artifact instanceof IAtsTeamWorkflow) {
         IAtsTeamDefinition teamDefHoldingVersion = atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(
            ((IAtsTeamWorkflow) artifact).getTeamDefinition());
         if (teamDefHoldingVersion != null) {
            return atsApi.getTeamDefinitionService().getVersions(teamDefHoldingVersion);
         }
      }
      return java.util.Collections.emptyList();
   }

   public void setVersions(Collection<IAtsVersion> versions) {
      this.selectableVersions = versions;
      if (dialog != null) {
         dialog.setInput(versions);
      }
   }

   @Override
   public boolean isEmpty() {
      return getSelectedVersion() != null;
   }

   public void setEnableHyperLink() {
      if (Widgets.isAccessible(labelHyperlink)) {
         labelHyperlink.setEnabled(true);
      }
   }

   public void setDisableHyperLink() {
      if (Widgets.isAccessible(labelHyperlink)) {
         labelHyperlink.setEnabled(false);
      }
   }

   @Override
   public void setArtifact(Artifact art) {
      if (art instanceof TeamWorkFlowArtifact) {
         this.artifact = art;
      }
   }

   @Override
   public Artifact getArtifact() {
      return this.artifact;
   }

   @Override
   public void revert() {
      //
   }

   @Override
   public void saveToArtifact() {
      //
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   public boolean isRemoveAllAllowed() {
      return removeAllAllowed;
   }

   public void setRemoveAllAllowed(boolean removeAllAllowed) {
      this.removeAllAllowed = removeAllAllowed;
   }

}
