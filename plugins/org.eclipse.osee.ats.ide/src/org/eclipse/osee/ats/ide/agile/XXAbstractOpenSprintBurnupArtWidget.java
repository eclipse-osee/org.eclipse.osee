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

import java.net.URL;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXButtonHyperWidget;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author Donald G. Dunne
 */
public abstract class XXAbstractOpenSprintBurnupArtWidget extends XXButtonHyperWidget {

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;
   protected IAgileSprint sprint;
   private final String id;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXAbstractOpenSprintBurnupArtWidget(WidgetId widgetId, String name, String id) {
      super(widgetId, name);
      setOseeImage(AtsImage.REPORT);
      this.id = id;
      setToolTip("Click to run Open Report");
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   protected Artifact getAgileTeam() {
      return AtsApiService.get().getQueryServiceIde().getArtifact(
         AtsApiService.get().getRelationResolver().getRelatedOrNull(sprint,
            AtsRelationTypes.AgileTeamToSprint_AgileTeam));
   };

   public void openExternally() {
      String url = getUrl();
      Program.launch(url);
   }

   @SuppressWarnings("deprecation")
   public void openInternally() {
      IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
      try {
         IWebBrowser browser = browserSupport.createBrowser(id + sprint.getIdString());
         String url = getUrl();
         browser.openURL(new URL(url));
      } catch (Exception ex) {
         OseeLog.log(XXAbstractOpenSprintBurnupArtWidget.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   abstract String getUrl();

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
      if (artifact.isOfType(AtsArtifactTypes.AgileSprint)) {
         this.sprint = AtsApiService.get().getAgileService().getAgileSprint(artifact);
      }
   }

}
