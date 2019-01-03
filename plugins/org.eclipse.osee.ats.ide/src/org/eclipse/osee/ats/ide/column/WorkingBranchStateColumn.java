/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.column;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Donald G. Dunne
 */
public class WorkingBranchStateColumn extends AbstractBranchColumn {

   public static WorkingBranchStateColumn instance = new WorkingBranchStateColumn();

   public static WorkingBranchStateColumn getInstance() {
      return instance;
   }

   private WorkingBranchStateColumn() {
      super(".workBranchState", "Working Branch State");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WorkingBranchStateColumn copy() {
      WorkingBranchStateColumn newXCol = new WorkingBranchStateColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   String getColumnText(BranchId branch) {
      return BranchManager.getState(branch).getName();
   }

}
