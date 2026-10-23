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

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.Points;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXPointsEnumWidget extends XXStringsWidget {

   public static final WidgetId ID = WidgetIdAts.XXPointsEnumWidget;

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXPointsEnumWidget() {
      super(ID, "Points");
      setAttributeType(AtsAttributeTypes.PointsEnum);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public Collection<String> getSelectable() {
      Collection<String> selectable = super.getSelectable();
      if (!selectable.isEmpty()) {
         return selectable;
      }
      if (xxWid.isWorkItem()) {
         List<String> pointEnums = xxWid.getWorkItem().getWorkDefinition().getPointsEnums();
         if (!pointEnums.isEmpty()) {
            return pointEnums;
         }
      }
      return Points.getValuesStr();
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
