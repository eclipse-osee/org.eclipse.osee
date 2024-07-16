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
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorerUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XButton;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class XStoreSprintReportsButton extends XButton implements ArtifactWidget {

   protected IAgileSprint sprint;
   private final boolean editable = false;
   public static final String WIDGET_ID = XStoreSprintReportsButton.class.getSimpleName();

   public XStoreSprintReportsButton() {
      super("Store Snapshot of Sprint Reports");
      setImage(ImageManager.getImage(AtsImage.REPORT));
      setToolTip("Click to Store and Open Snapshot of Sprint Reports");
      addXModifiedListener(listener);
   }

   @Override
   public Artifact getArtifact() {
      return AtsApiService.get().getQueryServiceIde().getArtifact(sprint);
   }

   XModifiedListener listener = new XModifiedListener() {
      @Override
      public void widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget widget) {
         try {
            storeAndOpen();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }

   };

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

            XOpenStoredSprintReportsButton stored = new XOpenStoredSprintReportsButton();
            stored.setArtifact(AtsApiService.get().getQueryServiceIde().getArtifact(sprint));
            stored.openExternally();

            ArtifactExplorerUtil.revealArtifact(AtsApiService.get().getQueryServiceIde().getArtifact(sprint));

            AWorkbench.popup("Reports opened in browser");

         } catch (Exception ex) {
            OseeLog.log(XStoreSprintReportsButton.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.AgileSprint)) {
         this.sprint = (IAgileSprint) artifact;
      }
   }

   @Override
   public boolean isEditable() {
      return editable;
   }

}
