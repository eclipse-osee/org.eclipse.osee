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
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;

/**
 * @author Donald G. Dunne
 * @author David W Miller
 */
public class GoalsColumn extends BaseGoalsColumn {

   private final WorkItemType goalType = WorkItemType.Goal;
   private final String persistString = "Set Goals";
   private final boolean isBacklogGoal = false;

   public static GoalsColumn instance = new GoalsColumn();

   public static GoalsColumn getInstance() {
      return instance;
   }

   private GoalsColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".goals", "Goals", 100, XViewerAlign.Left, false,
         SortDataType.String, true, "Goals");
   }

   @Override
   protected WorkItemType getWorkItemType() {
      return goalType;
   }

   @Override
   protected String getPersistString() {
      return persistString;
   }

   @Override
   protected boolean isBacklogGoal() {
      return isBacklogGoal;
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public GoalsColumn copy() {
      GoalsColumn newXCol = new GoalsColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
