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

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.world.WorldXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class RemainingPointsNumericTotalColumn extends AbstractNumericTotalColumn {

   private static final String CALCULATION_STR =
      "Points Numeric - (Points Numeric * (Percent Complete from Workflow Rollup (Team Wf, Tasks and Reviews) / 100))";
   private static RemainingPointsNumericTotalColumn instance = new RemainingPointsNumericTotalColumn();

   public static RemainingPointsNumericTotalColumn getInstance() {
      return instance;
   }

   private RemainingPointsNumericTotalColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".remainingPointsNumericTotal", "Remaining Points Numeric - Total",
         "Points Numeric that remain to complete the changes based on percent complete from workflow rollup.",
         CALCULATION_STR, AtsAttributeTypes.PointsNumeric);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public RemainingPointsNumericTotalColumn copy() {
      RemainingPointsNumericTotalColumn newXCol = new RemainingPointsNumericTotalColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   protected int getPercentComplete(IAtsWorkItem workItem)  {
      return PercentCompleteTotalUtil.getPercentCompleteTotal(workItem, AtsClientService.get());
   }

}
