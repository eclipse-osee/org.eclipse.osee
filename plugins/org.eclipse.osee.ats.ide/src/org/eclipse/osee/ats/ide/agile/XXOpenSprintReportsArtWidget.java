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

import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXButtonHyperWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXOpenSprintReportsArtWidget extends XXButtonHyperWidget {

   public static final WidgetId ID = WidgetIdAts.XXOpenSprintReportsArtWidget;

   protected IAgileSprint sprint;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXOpenSprintReportsArtWidget() {
      super(ID, "Open Current Sprint Reports");
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
      XXOpenSprintBurndownArtWidget down = new XXOpenSprintBurndownArtWidget();
      down.setArtifact(getArtifact());
      down.openExternally();

      XXOpenSprintBurnupArtWidget up = new XXOpenSprintBurnupArtWidget();
      up.setArtifact(getArtifact());
      up.openExternally();

      XXOpenSprintSummaryArtWidget sum = new XXOpenSprintSummaryArtWidget();
      sum.setArtifact(getArtifact());
      sum.openExternally();

      XXOpenSprintDataTableArtWidget data = new XXOpenSprintDataTableArtWidget();
      data.setArtifact(getArtifact());
      data.openExternally();
   }

   public void openInternally() {
      XXOpenSprintBurndownArtWidget down = new XXOpenSprintBurndownArtWidget();
      down.setArtifact(getArtifact());
      down.openInternally();

      XXOpenSprintBurnupArtWidget up = new XXOpenSprintBurnupArtWidget();
      up.setArtifact(getArtifact());
      up.openInternally();

      XXOpenSprintSummaryArtWidget sum = new XXOpenSprintSummaryArtWidget();
      sum.setArtifact(getArtifact());
      sum.openInternally();

      XXOpenSprintDataTableArtWidget data = new XXOpenSprintDataTableArtWidget();
      data.setArtifact(getArtifact());
      data.openInternally();
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
