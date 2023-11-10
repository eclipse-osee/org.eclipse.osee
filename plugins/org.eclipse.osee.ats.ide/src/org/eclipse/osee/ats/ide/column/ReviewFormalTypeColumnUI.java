/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.column;

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttrTokenXColumn;

/**
 * @author Donald G. Dunne
 */
public class ReviewFormalTypeColumnUI extends XViewerAtsAttrTokenXColumn {

   public static ReviewFormalTypeColumnUI instance = new ReviewFormalTypeColumnUI();

   public static ReviewFormalTypeColumnUI getInstance() {
      return instance;
   }

   private ReviewFormalTypeColumnUI() {
      super(AtsAttributeTypes.ReviewFormalType, 22, XViewerAlign.Center, false, SortDataType.String, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ReviewFormalTypeColumnUI copy() {
      ReviewFormalTypeColumnUI newXCol = new ReviewFormalTypeColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

}
