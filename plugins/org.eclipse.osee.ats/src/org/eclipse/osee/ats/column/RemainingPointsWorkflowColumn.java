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
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class RemainingPointsWorkflowColumn extends AbstractNumericTotalColumn {

   private static final String CALCULATION_STR = "Points - (Points * (Percent Complete from Workflow / 100))";
   private static RemainingPointsWorkflowColumn instance = new RemainingPointsWorkflowColumn();

   public static RemainingPointsWorkflowColumn getInstance() {
      return instance;
   }

   private RemainingPointsWorkflowColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".remainingPointsWorkflow", "Remaining Points - Workflow",
         "Points that remain to complete the changes based on percent complete set on workflow.", CALCULATION_STR,
         AtsAttributeTypes.Points);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public RemainingPointsWorkflowColumn copy() {
      RemainingPointsWorkflowColumn newXCol = new RemainingPointsWorkflowColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   protected int getPercentComplete(IAtsWorkItem workItem)  {
      return ((Artifact) workItem.getStoreObject()).getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0);
   }

}
