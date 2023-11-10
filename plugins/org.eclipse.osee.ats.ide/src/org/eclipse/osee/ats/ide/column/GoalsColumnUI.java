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
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;

/**
 * @author Donald G. Dunne
 * @author David W Miller
 */
public class GoalsColumnUI extends BaseGoalsColumnUI {

   private final WorkItemType goalType = WorkItemType.Goal;
   private final String persistString = "Set Goals";
   private final boolean isBacklogGoal = false;

   public static GoalsColumnUI instance = new GoalsColumnUI();

   public static GoalsColumnUI getInstance() {
      return instance;
   }

   private GoalsColumnUI() {
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
   public GoalsColumnUI copy() {
      GoalsColumnUI newXCol = new GoalsColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

}
