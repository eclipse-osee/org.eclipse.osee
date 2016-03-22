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
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;

/**
 * @author Donald G. Dunne
 */
public class TeamColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static TeamColumn instance = new TeamColumn();

   public static TeamColumn getInstance() {
      return instance;
   }

   private TeamColumn() {
      super(AtsColumnId.Team.getId(), "Team", 50, XViewerAlign.Left, true, SortDataType.String, false,
         "Team that has been assigned to work this Action.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TeamColumn copy() {
      TeamColumn newXCol = new TeamColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String result = "";
      if (element instanceof IAtsObject) {
         result =
            AtsClientService.get().getColumnService().getColumn(AtsColumnId.Team).getColumnText((IAtsObject) element);
      }
      return result;
   }
}
