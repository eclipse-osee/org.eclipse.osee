/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.xx;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXPercentSTextWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXPercentWfeWidget extends XXPercentSTextWidget {

   public static final WidgetId ID = WidgetIdAts.XXPercentWfeWidget;
   private final static String LABEL_TOOL_TIP =
      "[Workflow Percent] | [Calculation: Sum of percent for workflow, reviews and tasks / # workflows, reviews and tasks]";
   final static String DIALOG_MESSAGE = "Enter Percent Complete (0 to 99)\n\n(use Transition to mark complete.)";

   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXPercentWfeWidget() {
      super(ID, "Percent Complete", "");
      setAttributeType(AtsAttributeTypes.PercentComplete);
      setToolTip(LABEL_TOOL_TIP);
      set(XOption.VAL_SINGLE_TEXT);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public String getDisplayValue() {
      if (xxWid.isWorkItem()) {
         int awaPercent = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(xxWid.getWorkItem(),
            AtsAttributeTypes.PercentComplete, 0);
         int totalPecent = AtsApiService.get().getWorkItemMetricsService().getPercentCompleteTotal(xxWid.getWorkItem());
         if (awaPercent != totalPecent) {
            return String.format("%d | %d", awaPercent, totalPecent);
         } else {
            return String.valueOf(awaPercent);
         }
      }
      return "";
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   @Override
   protected String getDialogMessage() {
      return DIALOG_MESSAGE;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
