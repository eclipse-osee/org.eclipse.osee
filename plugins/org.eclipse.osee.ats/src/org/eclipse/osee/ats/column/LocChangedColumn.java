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
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.core.client.type.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.swt.SWT;

public class LocChangedColumn extends XViewerAtsAttributeValueColumn {

   public static LocChangedColumn instance = new LocChangedColumn();

   public static LocChangedColumn getInstance() {
      return instance;
   }

   private LocChangedColumn() {
      super(AtsAttributeTypes.LocChanged, WorldXViewerFactory.COLUMN_NAMESPACE + ".locChanged",
         AtsAttributeTypes.LocChanged.getUnqualifiedName(), 40, SWT.CENTER, false, SortDataType.Integer, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public LocChangedColumn copy() {
      LocChangedColumn newXCol = new LocChangedColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
