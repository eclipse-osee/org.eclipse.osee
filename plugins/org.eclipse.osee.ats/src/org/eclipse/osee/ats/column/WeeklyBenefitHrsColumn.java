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
