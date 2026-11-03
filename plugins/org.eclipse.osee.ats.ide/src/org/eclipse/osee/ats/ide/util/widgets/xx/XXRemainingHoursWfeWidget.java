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

import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.column.RemainingHoursColumnUI;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXLabelValueWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXRemainingHoursWfeWidget extends XXLabelValueWidget {

   public static final WidgetId ID = WidgetIdAts.XXRemainingHoursWfeWidget;

   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXRemainingHoursWfeWidget() {
      super(ID, "Remaining Hours", "");
      setToolTip(RemainingHoursColumnUI.getInstance().getDescription());
      remove(XOption.VAL_SINGLE_SEL);
      set(XOption.VAL_NOT_APPLICABLE);
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

   @Override
   protected void initializeSelected() {
      if (xxArt.isArtifact() && getAttributeType().isValid()) {
         String value = "";
         if (xxWid.isWorkItem()) {
            Result result = RemainingHoursColumnUI.isRemainingHoursValid(xxWid.getWorkItem());
            if (result.isFalse()) {
               value = result.getText();
            } else {
               value = String.valueOf(
                  AtsUtil.doubleToI18nString(RemainingHoursColumnUI.getRemainingHours(xxWid.getWorkItem())));
            }
         }
         if (Strings.isValid(value)) {
            setSelected(value, false);
         }
      }
   }

}
