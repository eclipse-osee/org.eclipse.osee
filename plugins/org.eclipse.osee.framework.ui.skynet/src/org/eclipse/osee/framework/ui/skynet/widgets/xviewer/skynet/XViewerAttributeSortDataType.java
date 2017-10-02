/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet;

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.LongAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.OutlineNumberAttribute;

/**
 * @author Donald G. Dunne
 */
public class XViewerAttributeSortDataType {

   public static SortDataType get(AttributeTypeId attributeType) {
      SortDataType sortType = SortDataType.String;
      if (AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeType)) {
         sortType = SortDataType.Date;
      } else if (AttributeTypeManager.isBaseTypeCompatible(FloatingPointAttribute.class, attributeType)) {
         sortType = SortDataType.Float;
      } else if (AttributeTypeManager.isBaseTypeCompatible(IntegerAttribute.class, attributeType)) {
         sortType = SortDataType.Integer;
      } else if (AttributeTypeManager.isBaseTypeCompatible(LongAttribute.class, attributeType)) {
         sortType = SortDataType.Long;
      } else if (AttributeTypeManager.isBaseTypeCompatible(BooleanAttribute.class, attributeType)) {
         sortType = SortDataType.Boolean;
      } else if (AttributeTypeManager.isBaseTypeCompatible(OutlineNumberAttribute.class, attributeType)) {
         sortType = SortDataType.Paragraph_Number;
      }
      return sortType;
   }
}
