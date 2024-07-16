/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.search.filter;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

public class FilterTableCellModifier implements ICellModifier {

   private final FilterTableViewer filterTableViewer;

   public FilterTableCellModifier(FilterTableViewer filterTableViewer) {
      super();
      this.filterTableViewer = filterTableViewer;
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
    */
   @Override
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
   @Override
   public Object getValue(Object element, String property) {
      // Find the index of the column
      int columnIndex = filterTableViewer.getColumnNames().indexOf(property);

      switch (columnIndex) {
         case FilterTableViewer.DELETE_NUM:
            return Boolean.valueOf(false);
      }
      return "";
   }

   /**
    * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
    */
   @Override
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
