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
public class BacklogColumnUI extends BaseGoalsColumn {

   private final WorkItemType goalType = WorkItemType.AgileBacklog;
   private final String persistString = "Set Backlogs";
   private final boolean isBacklogGoal = true;

   public static BacklogColumnUI instance = new BacklogColumnUI();

   public static BacklogColumnUI getInstance() {
      return instance;
   }

   private BacklogColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".backlog", "Backlog", 100, XViewerAlign.Left, false,
         SortDataType.String, true, "Backlog that this item belongs to.");
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
   public BacklogColumnUI copy() {
      BacklogColumnUI newXCol = new BacklogColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

}
