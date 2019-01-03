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

/**
 * @author Donald G. Dunne
 */
public class DescriptionColumn extends XViewerAtsAttributeValueColumn {

   public static DescriptionColumn instance = new DescriptionColumn();

   public static DescriptionColumn getInstance() {
      return instance;
   }

   private DescriptionColumn() {
      super(AtsAttributeTypes.Description, 150, XViewerAlign.Left, false, SortDataType.String, true, "");
      setActionRollup(true);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public DescriptionColumn copy() {
      DescriptionColumn newXCol = new DescriptionColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean isMultiLineStringAttribute() {
      return true;
   }

}
