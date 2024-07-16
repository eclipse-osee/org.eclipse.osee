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
public class WorkPackageIdColumnUI extends AbstractWorkPackageRelatedColumnUI {

   private static WorkPackageIdColumnUI instance = new WorkPackageIdColumnUI();

   public static WorkPackageIdColumnUI getInstance() {
      return instance;
   }

   private WorkPackageIdColumnUI() {
      super(AtsColumnTokens.WorkPackageIdColumn, AtsColumnTokens.WorkPackageIdColumn.getId(), "Work Package Id", 80,
         XViewerAlign.Left, false, SortDataType.String, true,
         "Provides Work Package Id from the selected Work Package related to the selected workflow.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WorkPackageIdColumnUI copy() {
      WorkPackageIdColumnUI newXCol = new WorkPackageIdColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

}
