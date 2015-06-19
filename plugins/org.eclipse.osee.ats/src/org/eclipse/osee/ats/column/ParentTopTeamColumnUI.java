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
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.core.column.ParentTopTeamColumn;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class ParentTopTeamColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   private final static ParentTopTeamColumnUI instance = new ParentTopTeamColumnUI();

   public static ParentTopTeamColumnUI getInstance() {
      return instance;
   }

   private ParentTopTeamColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".topTeam", "Parent Top Team", 50, SWT.LEFT, true,
         SortDataType.String, false,
         "Top Team (if available) or parent Team that has been assigned to work this Action.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ParentTopTeamColumnUI copy() {
      ParentTopTeamColumnUI newXCol = new ParentTopTeamColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String result = "";
      if (element instanceof IAtsObject) {
         try {
            result = ParentTopTeamColumn.getColumnText((IAtsObject) element);
         } catch (OseeCoreException ex) {
            result = XViewerCells.getCellExceptionString(ex);
         }
      }
      return result;
   }

}
