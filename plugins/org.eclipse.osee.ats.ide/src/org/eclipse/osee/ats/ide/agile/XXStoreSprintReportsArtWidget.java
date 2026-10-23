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

package org.eclipse.osee.ats.ide.agile;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorerUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXButtonHyperWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXStoreSprintReportsArtWidget extends XXButtonHyperWidget {

   public static final WidgetId ID = WidgetIdAts.XXStoreSprintReportsArtWidget;

   protected IAgileSprint sprint;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXStoreSprintReportsArtWidget() {
      super(ID, "Store Snapshot of Sprint Reports");
      setOseeImage(AtsImage.REPORT);
      setToolTip("Click to Store and Open Snapshot of Sprint Reports");
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   protected boolean handleSelection() {
      storeAndOpen();
      return false;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   private void storeAndOpen() {
      if (MessageDialog.openConfirm(Displays.getActiveShell(), getLabel(),
         "This will generate all reports and store them in database.\n\nThis is important if Sprint contents change after " //
            + "Sprint is closed\nsuch as moving un-completed work to the next Sprint.\nSnapshot reports will retain the " //
            + "metrics at the point of storage.\n\nAre you sure?")) {
         try {
            ArtifactToken teamArt = AtsApiService.get().getRelationResolver().getRelatedOrNull(sprint,
               AtsRelationTypes.AgileTeamToSprint_AgileTeam);
            if (teamArt != null) {
               XResultData results = AtsApiService.get().getServerEndpoints().getAgileEndpoint().storeSprintReports(
                  teamArt.getId(), this.sprint.getId());
               if (results.isErrors()) {
                  AWorkbench.popup(getLabel() + " errors " + results.toString());
                  return;
               }
            }

            AtsApiService.get().getQueryServiceIde().getArtifact(sprint).reloadAttributesAndRelations();

            XXOpenStoredSprintReportsArtWidget stored = new XXOpenStoredSprintReportsArtWidget();
            stored.setArtifact(AtsApiService.get().getQueryServiceIde().getArtifact(sprint));
            stored.openExternally();

            ArtifactExplorerUtil.revealArtifact(AtsApiService.get().getQueryServiceIde().getArtifact(sprint));

            AWorkbench.popup("Reports opened in browser");

         } catch (Exception ex) {
            OseeLog.log(XXStoreSprintReportsArtWidget.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (getArtifact().isOfType(AtsArtifactTypes.AgileSprint)) {
         this.sprint = AtsApiService.get().getAgileService().getAgileSprint(artifact);
      }
   }

}
