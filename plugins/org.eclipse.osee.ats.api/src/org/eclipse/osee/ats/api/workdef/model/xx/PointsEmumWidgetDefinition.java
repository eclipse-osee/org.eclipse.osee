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

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.framework.core.widget.XOption;

/**
 * The enum values are not provided by an EnumeratedAttribute because teams may have different values. They are provided
 * by default as Points.values() or can be overridden by providing thorough
 * WidgetDefBuilder.andPointsAttributeType(AtsAttributeTypes.PointsEnum, String...values);
 *
 * @author Donald G. Dunne
 */
public class PointsEmumWidgetDefinition extends AttrWidgetDef {

   public PointsEmumWidgetDefinition() {
      super("Points", AtsAttributeTypes.PointsEnum, WidgetIdAts.XXPointsEnumWidget);
      set(XOption.CLEAR);
      setOseeImage(AtsImage.POINTS);
   }

}
