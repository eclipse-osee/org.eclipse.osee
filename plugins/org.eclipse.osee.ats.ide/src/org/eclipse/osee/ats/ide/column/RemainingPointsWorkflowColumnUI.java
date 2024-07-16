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
public class RemainingPointsWorkflowColumnUI extends AbstractNumericTotalColumnUI {

   private static final String CALCULATION_STR = "Points - (Points * (Percent Complete from Workflow / 100))";
   private static RemainingPointsWorkflowColumnUI instance = new RemainingPointsWorkflowColumnUI();

   public static RemainingPointsWorkflowColumnUI getInstance() {
      return instance;
   }

   private RemainingPointsWorkflowColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".remainingPointsWorkflow", "Remaining Points - Workflow",
         "Points that remain to complete the changes based on percent complete set on workflow.", CALCULATION_STR,
         AtsAttributeTypes.Points);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public RemainingPointsWorkflowColumnUI copy() {
      RemainingPointsWorkflowColumnUI newXCol = new RemainingPointsWorkflowColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   protected int getPercentComplete(IAtsWorkItem workItem) {
      return AtsApiService.get().getQueryServiceIde().getArtifact(workItem).getSoleAttributeValue(
         AtsAttributeTypes.PercentComplete, 0);
   }

}
