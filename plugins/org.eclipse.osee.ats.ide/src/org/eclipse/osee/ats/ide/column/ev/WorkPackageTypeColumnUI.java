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
public class WorkPackageTypeColumnUI extends AbstractWorkPackageRelatedColumnUI {

   private static WorkPackageTypeColumnUI instance = new WorkPackageTypeColumnUI();

   public static WorkPackageTypeColumnUI getInstance() {
      return instance;
   }

   private WorkPackageTypeColumnUI() {
      super(AtsColumnTokens.WorkPackageTypeColumn, AtsColumnTokens.WorkPackageTypeColumn.getId(), "Work Package Type", 40,
         XViewerAlign.Left, false, SortDataType.String, true,
         "Provides Work Package Type from the selected Work Package related to the selected workflow.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WorkPackageTypeColumnUI copy() {
      WorkPackageTypeColumnUI newXCol = new WorkPackageTypeColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

}
