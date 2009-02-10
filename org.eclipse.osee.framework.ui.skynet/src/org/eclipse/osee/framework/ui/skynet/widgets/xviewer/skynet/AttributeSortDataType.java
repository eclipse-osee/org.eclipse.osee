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

import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;

/**
 * @author Donald G. Dunne
 */
public class AttributeSortDataType {

   public static SortDataType getSortDataType(AttributeType attributeType) {
      SortDataType sortType = SortDataType.String;
      if (attributeType.getBaseAttributeClass().equals(DateAttribute.class))
         sortType = SortDataType.Date;
      else if (attributeType.getBaseAttributeClass().equals(FloatingPointAttribute.class))
         sortType = SortDataType.Float;
      else if (attributeType.getBaseAttributeClass().equals(IntegerAttribute.class))
         sortType = SortDataType.Integer;
      else if (attributeType.getBaseAttributeClass().equals(BooleanAttribute.class)) sortType = SortDataType.Boolean;
      return sortType;
   }
}
