/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.search.filter;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

public class FilterTableCellModifier implements ICellModifier {

   private FilterTableViewer filterTableViewer;

   public FilterTableCellModifier(FilterTableViewer filterTableViewer) {
      super();
      this.filterTableViewer = filterTableViewer;
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
    */
   public boolean canModify(Object element, String property) {
      // Find the index of the column
      int columnIndex = filterTableViewer.getColumnNames().indexOf(property);

      switch (columnIndex) {
         case FilterTableViewer.DELETE_NUM:
            return true;
      }
      return true;
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
    */
   public Object getValue(Object element, String property) {
      // Find the index of the column
      int columnIndex = filterTableViewer.getColumnNames().indexOf(property);

      switch (columnIndex) {
         case FilterTableViewer.DELETE_NUM:
            return new Boolean(false);
      }
      return "";
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
    */
   public void modify(Object element, String property, Object value) {

      // Find the index of the column
      int columnIndex = filterTableViewer.getColumnNames().indexOf(property);

      TableItem item = (TableItem) element;
      FilterModel model = (FilterModel) item.getData();

      switch (columnIndex) {
         case FilterTableViewer.DELETE_NUM:
            filterTableViewer.removeFilter(model);
            break;
         default:
      }
      filterTableViewer.getFilterList().filterChanged(model);
      filterTableViewer.refresh();
   }
}
