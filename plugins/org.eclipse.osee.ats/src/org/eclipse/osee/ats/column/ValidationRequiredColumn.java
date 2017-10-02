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

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class ValidationRequiredColumn extends XViewerAtsAttributeValueColumn {

   public static ValidationRequiredColumn instance = new ValidationRequiredColumn();

   public static ValidationRequiredColumn getInstance() {
      return instance;
   }

   private ValidationRequiredColumn() {
      super(AtsAttributeTypes.ValidationRequired, WorldXViewerFactory.COLUMN_NAMESPACE + ".validationRequired",
         AtsAttributeTypes.ValidationRequired.getUnqualifiedName(), 80, XViewerAlign.Left, false, SortDataType.String,
         false, "If set, Originator will be asked to perform a review to\nensure changes are as expected.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ValidationRequiredColumn copy() {
      ValidationRequiredColumn newXCol = new ValidationRequiredColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
