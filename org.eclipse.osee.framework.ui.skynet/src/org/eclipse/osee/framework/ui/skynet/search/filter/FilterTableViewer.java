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

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.ui.skynet.search.ArtifactSearchPage;
import org.eclipse.osee.framework.ui.skynet.search.ui.IFilterListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class FilterTableViewer {

   private Table table;
   private TableViewer tableViewer;

   private FilterModelList filterList;

   private String[] columnNames = new String[] {"Remove", "Filter", "Type", "Value"};
   private static int[] columnWidths = new int[] {75, 100, 100, 100, 200};

   public static final int DELETE_NUM = 0;
   public static final int SEARCH_NUM = 1;
   public static final int TYPE_NUM = 2;
   public static final int VALUE_NUM = 3;

   /**
    * @param table -
    */
   public FilterTableViewer(Table table) {
      this.table = table;

      this.createControl();

      tableViewer.setContentProvider(new FilterContentProvider());
      tableViewer.setLabelProvider(new FilterModelLabelProvider());
      tableViewer.setInput(filterList);
   }

   public void addItem(ISearchPrimitive searchPrimitive, String search, String type, String value) {
      FilterModel model = new FilterModel(searchPrimitive, search, type, value);
      filterList.addFilter(model, true);
   }

   private void createControl() {
      filterList = new FilterModelList();
      createColumns();
      createTableViewer();
   }

   /**
    * Create the TableViewer
    */
   private void createTableViewer() {

      tableViewer = new TableViewer(table);
      tableViewer.setUseHashlookup(true);
      tableViewer.setColumnProperties(columnNames);

      CellEditor[] validEditors = new CellEditor[columnNames.length];
      validEditors[DELETE_NUM] = new CheckboxCellEditor(table, SWT.NONE);

      // Assign the cell editors to the viewer
      tableViewer.setCellEditors(validEditors);
      // Assign the cell modifier to the viewer
      tableViewer.setCellModifier(new FilterTableCellModifier(this));
   }

   /**
    * Create the Columns
    */
   private void createColumns() {
      table.setLinesVisible(true);
      table.setHeaderVisible(true);

      TableColumn column = new TableColumn(table, SWT.LEFT, DELETE_NUM);
      column.setText(columnNames[DELETE_NUM]);
      column.setWidth(columnWidths[DELETE_NUM]);

      column = new TableColumn(table, SWT.LEFT, SEARCH_NUM);
      column.setText(columnNames[SEARCH_NUM]);
      column.setWidth(columnWidths[SEARCH_NUM]);

      column = new TableColumn(table, SWT.LEFT, TYPE_NUM);
      column.setText(columnNames[TYPE_NUM]);
      column.setWidth(columnWidths[TYPE_NUM]);

      column = new TableColumn(table, SWT.LEFT, VALUE_NUM);
      column.setText(columnNames[VALUE_NUM]);
      column.setWidth(columnWidths[VALUE_NUM]);
   }

   public List<String> getColumnNames() {
      return Arrays.asList(columnNames);
   }

   public FilterModelList getFilterList() {
      return this.filterList;
   }

   public void refresh() {
      tableViewer.refresh();
      ArtifactSearchPage.updateOKStatus();
   }

   /**
    * @return Returns the table.
    */
   public Table getTable() {
      return table;
   }

   public void removeFilter(FilterModel filter) {
      filterList.removeFilter(filter);
   }

   public int getCount() {
      return filterList.getFilters().size();
   }
   /**
    * InnerClass that acts as a proxy for the FilterModelList providing content for the Table. It implements the
    * IFilterListViewer interface since it must register changeListeners with the FilterModelList
    */
   class FilterContentProvider implements IStructuredContentProvider, IFilterListViewer {

      public void inputChanged(Viewer v, Object oldInput, Object newInput) {
         if (newInput != null) filterList.addChangeListener(this);
         if (oldInput != null) filterList.removeChangeListener(this);
      }

      public void dispose() {
         filterList.removeChangeListener(this);
      }

      // Return the tasks as an array of Objects
      public Object[] getElements(Object parent) {
         return filterList.getFilters().toArray();
      }

      public void addFilter(FilterModel filter) {
         tableViewer.add(filter);
      }

      public void removeFilter(FilterModel filter) {
         tableViewer.remove(filter);
      }

      public void updateFilter(FilterModel filter) {
         tableViewer.update(filter, null);
      }
   }
}
