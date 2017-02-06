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
package org.eclipse.osee.ats.column;

import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Donald G. Dunne
 */
public class WorkingBranchUuidColumn extends AbstractBranchColumn {

   public static WorkingBranchUuidColumn instance = new WorkingBranchUuidColumn();

   public static WorkingBranchUuidColumn getInstance() {
      return instance;
   }

   private WorkingBranchUuidColumn() {
      super(".workBranchUuid", "Working Branch Uuid");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WorkingBranchUuidColumn copy() {
      WorkingBranchUuidColumn newXCol = new WorkingBranchUuidColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   String getColumnText(Branch branch) {
      return branch.getIdString();
   }

}
