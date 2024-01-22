/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.ide.column.signby;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;

/**
 * @author Donald G. Dunne
 */
public class ReviewedByDateColumnUI extends AbstractSignByDateColumnUI {

   public static ReviewedByDateColumnUI instance = new ReviewedByDateColumnUI();

   public static ReviewedByDateColumnUI getInstance() {
      return instance;
   }

   public ReviewedByDateColumnUI() {
      super(AtsAttributeTypes.ReviewedByDate, AtsAttributeTypes.ReviewedBy);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public AbstractSignByAndDateColumnUI copy() {
      ReviewedByDateColumnUI newXCol = new ReviewedByDateColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

}
