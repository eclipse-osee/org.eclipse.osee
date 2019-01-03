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

import java.util.Date;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.column.AtsColumnId;

/**
 * @author Donald G. Dunne
 */
public class ReleaseDateColumn extends AbstractWorkflowVersionDateColumn {

   public static ReleaseDateColumn instance = new ReleaseDateColumn();

   public static ReleaseDateColumn getInstance() {
      return instance;
   }

   private ReleaseDateColumn() {
      super(AtsColumnId.ReleaseDate.getId(), AtsAttributeTypes.ReleaseDate);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ReleaseDateColumn copy() {
      ReleaseDateColumn newXCol = new ReleaseDateColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      Date date = getDateFromWorkflow(getAttributeType(), element);

      if (date == null) {
         date = getDateFromTargetedVersion(getAttributeType(), element);
      }

      return date;
   }
}
