/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Donald G. Dunne
 */
public class WorkingBranchIdColumn extends AbstractBranchColumn {

   public static WorkingBranchIdColumn instance = new WorkingBranchIdColumn();

   public static WorkingBranchIdColumn getInstance() {
      return instance;
   }

   private WorkingBranchIdColumn() {
      super(".workBranchId", "Working Branch Id");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WorkingBranchIdColumn copy() {
      WorkingBranchIdColumn newXCol = new WorkingBranchIdColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   String getColumnText(BranchId branch) {
      return branch.getIdString();
   }

}
