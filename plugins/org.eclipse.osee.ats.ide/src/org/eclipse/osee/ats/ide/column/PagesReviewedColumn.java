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
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class PagesReviewedColumn extends XViewerAtsAttributeValueColumn {

   public static PagesReviewedColumn instance = new PagesReviewedColumn();

   public static PagesReviewedColumn getInstance() {
      return instance;
   }

   private PagesReviewedColumn() {
      super(AtsAttributeTypes.PagesReviewed, WorldXViewerFactory.COLUMN_NAMESPACE + ".pagesReviewed",
         AtsAttributeTypes.PagesReviewed.getUnqualifiedName(), 40, XViewerAlign.Center, false, SortDataType.Integer,
         true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PagesReviewedColumn copy() {
      PagesReviewedColumn newXCol = new PagesReviewedColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
