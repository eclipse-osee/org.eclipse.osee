/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.api.workdef.model.xx;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.framework.core.widget.XOption;

/**
 * @author Donald G. Dunne
 */
public class PointsNumericWidgetDefinition extends AttrWidgetDef {

   public PointsNumericWidgetDefinition() {
      this("Points");
   }

   public PointsNumericWidgetDefinition(String name) {
      this(name, Collections.emptyList());
   }

   public PointsNumericWidgetDefinition(String name, List<String> pointsValues) {
      super(name, AtsAttributeTypes.PointsNumeric, WidgetIdAts.XXPointsNumericWidget);
      if (!pointsValues.isEmpty()) {
         getWidData().setSelectable(pointsValues);
      }
      set(XOption.CLEAR);
      setOseeImage(AtsImage.POINTS);
   }

   public LayoutItem andRequired() {
      set(XOption.REQUIRED);
      return this;
   }

}
