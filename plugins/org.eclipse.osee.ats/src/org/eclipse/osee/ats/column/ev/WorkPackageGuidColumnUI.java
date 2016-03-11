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
package org.eclipse.osee.ats.column.ev;

import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class WorkPackageGuidColumnUI extends AbstractWorkPackageRelatedColumnUI {

   private static WorkPackageGuidColumnUI instance = new WorkPackageGuidColumnUI();

   public static WorkPackageGuidColumnUI getInstance() {
      return instance;
   }

   private WorkPackageGuidColumnUI() {
      super(AtsColumnId.WorkPackageGuid, WorldXViewerFactory.COLUMN_NAMESPACE + ".workPackageGuid", "Work Package Guid",
         80, SWT.LEFT, false, SortDataType.String, true,
         "Provides Work Package guid from the selected Work Package related to the selected workflow.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WorkPackageGuidColumnUI copy() {
      WorkPackageGuidColumnUI newXCol = new WorkPackageGuidColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

}
