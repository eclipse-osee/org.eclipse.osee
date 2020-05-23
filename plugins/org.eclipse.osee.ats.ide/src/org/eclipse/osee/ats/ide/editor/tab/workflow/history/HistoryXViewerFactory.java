/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.history;

import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.ide.editor.tab.workflow.history.column.AuthorColumn;
import org.eclipse.osee.ats.ide.editor.tab.workflow.history.column.DateColumn;
import org.eclipse.osee.ats.ide.editor.tab.workflow.history.column.EventColumn;
import org.eclipse.osee.ats.ide.editor.tab.workflow.history.column.TransactionColumn;

/**
 * @author Donald G. Dunne
 * @author Jeff C. Phillips
 */
public class HistoryXViewerFactory extends XViewerFactory {

   public HistoryXViewerFactory() {
      super("ats.history");
      registerColumns(TransactionColumn.getInstance(), EventColumn.getInstance(), DateColumn.getInstance(),
         AuthorColumn.getInstance());
   }

   @Override
   public boolean isAdmin() {
      return false;
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData customizeData = super.getDefaultTableCustomizeData();
      for (XViewerColumn xCol : customizeData.getColumnData().getColumns()) {
         if (xCol.getId().equals(TransactionColumn.getInstance().getId())) {
            xCol.setSortForward(false);
         }
      }
      customizeData.getSortingData().setSortingNames(TransactionColumn.getInstance().getId());
      return customizeData;
   }

}