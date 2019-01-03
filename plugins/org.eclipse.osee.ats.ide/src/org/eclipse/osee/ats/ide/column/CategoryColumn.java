/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.column;

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class CategoryColumn extends XViewerAtsAttributeValueColumn {

   public static CategoryColumn category1 = new CategoryColumn(AtsAttributeTypes.Category1);
   public static CategoryColumn category2 = new CategoryColumn(AtsAttributeTypes.Category2);
   public static CategoryColumn category3 = new CategoryColumn(AtsAttributeTypes.Category3);

   public static CategoryColumn getCategory1Instance() {
      return category1;
   }

   public static CategoryColumn getCategory2Instance() {
      return category2;
   }

   public static CategoryColumn getCategory3Instance() {
      return category3;
   }

   public CategoryColumn(AttributeTypeToken attributeType) {
      super(attributeType, 80, XViewerAlign.Left, false, SortDataType.String, true, "");
      setActionRollup(true);
   }

   private CategoryColumn() {
      super();
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public CategoryColumn copy() {
      CategoryColumn newXCol = new CategoryColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
