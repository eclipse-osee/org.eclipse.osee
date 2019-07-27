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