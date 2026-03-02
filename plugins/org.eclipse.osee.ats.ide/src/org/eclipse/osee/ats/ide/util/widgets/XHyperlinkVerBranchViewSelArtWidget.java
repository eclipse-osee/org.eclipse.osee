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
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractHyperlinkWithFilteredDialogWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.osgi.service.component.annotations.Component;

/**
 * Widget to allow selection of BranchView from a given ATS Version and store in the BranchView tuple
 *
 * @author Megumi Telles
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkVerBranchViewSelArtWidget extends XAbstractHyperlinkWithFilteredDialogWidget<ArtifactToken> {

   public static final WidgetId ID = WidgetIdAts.XHyperlinkVerBranchViewSelArtWidget;

   private Artifact versionArt;

   public XHyperlinkVerBranchViewSelArtWidget() {
      super(ID, "Branch View");
   }

   @Override
   protected boolean isSelectable() {
      if (AtsApiService.get().getUserService().isAtsAdmin()) {
         return true;
      }
      AWorkbench.popup("Must be ATS Admin to change");
      return false;
   }

   @Override
   public Collection<ArtifactToken> getSelectable() {
      IAtsVersion version = AtsApiService.get().getVersionService().getVersionById(versionArt);
      return AtsApiService.get().getBranchService().getBranchViews(version);
   }

   @Override
   public String getCurrentValue() {
      String result = Widgets.NOT_SET;
      if (versionArt != null) {
         IAtsVersion version = AtsApiService.get().getVersionService().getVersionById(versionArt);
         ArtifactId versionBranchViewId = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(versionArt,
            AtsAttributeTypes.VersionBranchView, ArtifactId.SENTINEL);
         BranchToken baselineBranch = AtsApiService.get().getBranchService().getBranch(version);
         ArtifactToken versionBranchView =
            AtsApiService.get().getQueryService().getArtifact(versionBranchViewId, baselineBranch);
         if (versionBranchView.isValid()) {
            result = versionBranchView.getName();
         }
      }
      return result;
   }

   @Override
   protected void handleSelectionPersist(ArtifactToken branchView) {
      IAtsVersion version = AtsApiService.get().getVersionService().getVersionById(versionArt);
      AtsApiService.get().getBranchService().setBranchView(version, branchView);
      versionArt.reloadAttributesAndRelations();
      ArtifactEvent artifactEvent = new ArtifactEvent(versionArt.getBranch());
      artifactEvent.addArtifact(versionArt);
      OseeEventManager.kickPersistEvent(XHyperlinkVerBranchViewSelArtWidget.class, artifactEvent);
   }

   @Override
   public boolean isEditable() {
      // Always make editable, but stop user if not admin when choose to change
      return true;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (getArtifact().isOfType(AtsArtifactTypes.Version)) {
         this.versionArt = artifact;
      } else {
         OseeLog.log(getClass(), Level.SEVERE,
            getClass().getSimpleName() + " - can only be used with ATS Version Artifact");
      }
      refresh();
   }

}
