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
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class WeeklyBenefitHrsColumn extends XViewerAtsAttributeValueColumn {

   public static WeeklyBenefitHrsColumn instance = new WeeklyBenefitHrsColumn();

   public static WeeklyBenefitHrsColumn getInstance() {
      return instance;
   }

   private WeeklyBenefitHrsColumn() {
      super(AtsAttributeTypes.WeeklyBenefit, WorldXViewerFactory.COLUMN_NAMESPACE + ".weeklyBenefitHrs",
         AtsAttributeTypes.WeeklyBenefit.getUnqualifiedName(), 40, XViewerAlign.Center, false, SortDataType.Float, true,
         "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WeeklyBenefitHrsColumn copy() {
      WeeklyBenefitHrsColumn newXCol = new WeeklyBenefitHrsColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
