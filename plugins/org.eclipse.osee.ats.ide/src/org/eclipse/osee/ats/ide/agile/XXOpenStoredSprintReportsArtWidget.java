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

import org.eclipse.osee.ats.api.agile.AgileReportType;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXButtonHyperWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXOpenStoredSprintReportsArtWidget extends XXButtonHyperWidget {

   public static WidgetId ID = WidgetIdAts.XXOpenStoredSprintReportsArtWidget;

   protected IAgileSprint sprint;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXOpenStoredSprintReportsArtWidget() {
      super(ID, "Open Snapshots of Stored Sprint Charts");
      setOseeImage(AtsImage.REPORT);
      setToolTip("Click to run Open Reports");
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   protected boolean handleSelection() {
      openExternally();
      return false;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public void openExternally() {
      boolean found = false;
      for (AgileReportType rpt : AgileReportType.values()) {
         ArtifactToken rptArt = AtsApiService.get().getRelationResolver().getChildNamedOrNull(sprint, rpt.name());
         if (rptArt != null) {
            found = true;
            RendererManager.open(AtsApiService.get().getQueryServiceIde().getArtifact(rptArt),
               PresentationType.PREVIEW);
         }
      }
      if (!found) {
         AWorkbench.popup("No Stored Reports Found");
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
