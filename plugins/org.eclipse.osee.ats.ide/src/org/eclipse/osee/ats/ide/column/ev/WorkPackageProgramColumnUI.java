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

package org.eclipse.osee.ats.ide.column.ev;

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;

/**
 * @author Donald G. Dunne
 */
public class WorkPackageProgramColumnUI extends AbstractWorkPackageRelatedColumnUI {

   private static WorkPackageProgramColumnUI instance = new WorkPackageProgramColumnUI();

   public static WorkPackageProgramColumnUI getInstance() {
      return instance;
   }

   private WorkPackageProgramColumnUI() {
      super(AtsColumnTokens.WorkPackageProgramColumn, AtsColumnTokens.WorkPackageProgramColumn.getId(),
         "Work Package Program", 80, XViewerAlign.Left, false, SortDataType.String, true,
         "Provides Work Package Program from the selected Work Package related to the selected workflow.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WorkPackageProgramColumnUI copy() {
      WorkPackageProgramColumnUI newXCol = new WorkPackageProgramColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

}
