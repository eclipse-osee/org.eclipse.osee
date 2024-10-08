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

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class RemainingPointsNumericWorkflowColumnUI extends AbstractNumericTotalColumnUI {

   private static final String CALCULATION_STR =
      "Points Numeric - (Points Numeric * (Percent Complete from Workflow / 100))";
   private static RemainingPointsNumericWorkflowColumnUI instance = new RemainingPointsNumericWorkflowColumnUI();

   public static RemainingPointsNumericWorkflowColumnUI getInstance() {
      return instance;
   }

   private RemainingPointsNumericWorkflowColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".remainingPointsNumericWorkflow",
         "Remaining Points Numeric - Workflow",
         "Points Numeric that remain to complete the changes based on percent complete set on workflow.",
         CALCULATION_STR, AtsAttributeTypes.PointsNumeric);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public RemainingPointsNumericWorkflowColumnUI copy() {
      RemainingPointsNumericWorkflowColumnUI newXCol = new RemainingPointsNumericWorkflowColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   protected int getPercentComplete(IAtsWorkItem workItem) {
      return AtsApiService.get().getQueryServiceIde().getArtifact(workItem).getSoleAttributeValue(
         AtsAttributeTypes.PercentComplete, 0);
   }

}
