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
public class WorkingBranchArchivedColumn extends AbstractBranchColumn {

   public static WorkingBranchArchivedColumn instance = new WorkingBranchArchivedColumn();

   public static WorkingBranchArchivedColumn getInstance() {
      return instance;
   }

   private WorkingBranchArchivedColumn() {
      super(".workBranchArchived", "Working Branch Archived");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WorkingBranchArchivedColumn copy() {
      WorkingBranchArchivedColumn newXCol = new WorkingBranchArchivedColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   String getColumnText(BranchId branch) {
      return BranchManager.getArchivedStr(branch);
   }

}
