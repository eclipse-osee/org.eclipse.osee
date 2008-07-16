/*
 * Created on Jul 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet;

import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;

/**
 * @author Donald G. Dunne
 */
public class XViewerAttributeSortDataType {

   public static SortDataType get(AttributeType attributeType) {
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
