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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.dialog.XViewerCustomizeDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * This manages the default table column definitions versus the user modified column data, sorter and filters.
 * 
 * @author Donald G. Dunne
 */
public class CustomizeManager {

   private final IXViewerFactory xViewerFactory;
   private final XViewer xViewer;
   private CustomizeData currentCustData;
   public static String CURRENT_LABEL = "-- Current Table View --";
   public static String TABLE_DEFAULT_LABEL = "-- Table Default --";
   // Added to keep filter, sorter from working till finished loading
   public boolean loading = true;
   public static List<String> REMOVED_COLUMNS_TO_IGNORE = Arrays.asList("Metrics from Tasks");

   public CustomizeManager(XViewer xViewer, IXViewerFactory xViewerFactory) throws OseeCoreException {
      this.xViewer = xViewer;
      this.xViewerFactory = xViewerFactory;
      // Set customize to be user default, if selected, or table default
      CustomizeData userCustData = xViewerFactory.getXViewerCustomizations().getUserDefaultCustData();
      if (userCustData != null) {
         currentCustData = resolveLoadedCustomizeData(userCustData);
      } else {
         currentCustData = getTableDefaultCustData();
         currentCustData.setNameSpace(xViewerFactory.getNamespace());
      }
      xViewerFactory.getXViewerCustomMenu().init(xViewer);
   }

   private final Map<String, XViewerColumn> oldNameToColumnId = new HashMap<String, XViewerColumn>();

   /**
    * Since saved customize data is stored as xml, all the columns need to be resolved to the columns available from the
    * factory
    * 
    * @param loadedCustData
    * @return CustomizeData
    */
   public CustomizeData resolveLoadedCustomizeData(CustomizeData loadedCustData) {
      // Otherwise, have to resolve what was saved with what is valid for this table and available from the factory
      CustomizeData resolvedCustData = new CustomizeData();
      resolvedCustData.setName(loadedCustData.getName());
      resolvedCustData.setPersonal(loadedCustData.isPersonal());
      resolvedCustData.setGuid(loadedCustData.getGuid());
      resolvedCustData.setNameSpace(loadedCustData.getNameSpace());
      /* 
       * Need to resolve columns with what factory has which gets correct class/subclass of XViewerColumn and allows for removal of old and addition of new columns
       */
      List<XViewerColumn> resolvedColumns = new ArrayList<XViewerColumn>();
      for (XViewerColumn storedCol : loadedCustData.getColumnData().getColumns()) {
         XViewerColumn resolvedCol = xViewer.getXViewerFactory().getDefaultXViewerColumn(storedCol.getId());
         // Handle known stored values
         if (resolvedCol == null) {
            String name = storedCol.getName();
            if (name.equals("Impacted Items")) {
               resolvedCol = xViewer.getXViewerFactory().getDefaultXViewerColumn("ats.column.actionableItems");
            } else if (name.equals("State Percent")) {
               resolvedCol = xViewer.getXViewerFactory().getDefaultXViewerColumn("ats.column.statePercentComplete");
            }
         }
         // if not found, may have been stored without namespace; try to resolve for backward compatibility
         if (resolvedCol == null) {
            String name = storedCol.getName().replaceAll(" ", "");
            resolvedCol = oldNameToColumnId.get(name);
            // First try to match by .<oldname>
            if (resolvedCol == null) {
               for (XViewerColumn xCol : xViewer.getXViewerFactory().getDefaultTableCustomizeData().getColumnData().getColumns()) {
                  String colId = xCol.getId().toLowerCase();
                  String oldName = "." + name.toLowerCase();
                  if (colId.endsWith(oldName)) {
                     resolvedCol = xCol;
                     oldNameToColumnId.put(name, resolvedCol);
                     oldNameToColumnId.put(storedCol.getName(), resolvedCol);
                     break;
                  }
               }
            }
            // Then try to match by id endswith name 
            if (resolvedCol == null) {
               for (XViewerColumn xCol : xViewer.getXViewerFactory().getDefaultTableCustomizeData().getColumnData().getColumns()) {
                  if (xCol.getId().endsWith(name)) {
                     resolvedCol = xCol;
                     oldNameToColumnId.put(name, resolvedCol);
                     oldNameToColumnId.put(storedCol.getName(), resolvedCol);
                     break;
                  }
               }
            }
         }
         // Only handle columns that the factory supports and only resolve shown columns (rest will be loaded later)
         if (resolvedCol != null && resolvedCol.getWidth() > 0) {
            resolvedCol.setWidth(storedCol.getWidth());
            resolvedCol.setName(storedCol.getName());
            resolvedCol.setShow(storedCol.isShow());
            resolvedCol.setSortForward(storedCol.isSortForward());
            resolvedColumns.add(resolvedCol);
         }
         if (resolvedCol == null) {
            // Ignore known removed columns
            if (!REMOVED_COLUMNS_TO_IGNORE.contains(storedCol.getName())) {
               OSEELog.logWarning(
                     SkynetGuiPlugin.class,
                     "XViewer Conversion for saved Customization \"" + loadedCustData.getName() + "\" dropped unresolved column Name: \"" + storedCol.getName() + "\"  Id: \"" + storedCol.getId() + "\".  Delete customization and re-save to resolve.",
                     false);
            }
         }
      }
      /*
       * Add extra columns that were added to the table since storage of this custData
       */
      for (XViewerColumn extraCol : xViewer.getXViewerFactory().getDefaultTableCustomizeData().getColumnData().getColumns()) {
         if (!resolvedColumns.contains(extraCol)) {
            // Since column wasn't saved, don't show it
            extraCol.setShow(false);
            resolvedColumns.add(extraCol);
         }
      }
      resolvedCustData.getColumnData().setColumns(resolvedColumns);
      resolvedCustData.getFilterData().setFromXml(loadedCustData.getFilterData().getXml());
      resolvedCustData.getSortingData().setFromXml(loadedCustData.getSortingData().getXml());
      return resolvedCustData;
   }

   /**
    * Clears out current columns, sorting and filtering and loads table customization
    */
   public void loadCustomization() {
      loadCustomization(currentCustData);
   }

   public void resetDefaultSorter() {
      XViewerSorter sorter = xViewer.getXViewerFactory().createNewXSorter(xViewer);
      xViewer.setSorter(sorter);
   }

   public void clearSorter() {
      currentCustData.getSortingData().clearSorter();
      xViewer.setSorter(null);
      xViewer.updateStatusLabel();
   }

   public void handleTableCustomization() {
      (new XViewerCustomizeDialog(xViewer)).open();
   }

   /**
    * @return Returns the selectedCustomization.
    */
   public String getStatusLabelAddition() {
      if (currentCustData != null && currentCustData.getName() != null && !currentCustData.getName().equals(
            CURRENT_LABEL) && !currentCustData.getName().equals(TABLE_DEFAULT_LABEL) && currentCustData.getName() != null) return ("Custom: " + currentCustData.getName() + " - ");
      return "";
   }

   /**
    * @return the currentCustData
    */
   public CustomizeData generateCustDataFromTable() {
      CustomizeData custData = new CustomizeData();
      custData.setName(CustomizeManager.CURRENT_LABEL);
      custData.setNameSpace(xViewer.getXViewerFactory().getNamespace());
      List<XViewerColumn> columns = new ArrayList<XViewerColumn>(15);
      for (Integer index : xViewer.getTree().getColumnOrder()) {
         TreeColumn treeCol = xViewer.getTree().getColumn(index);
         XViewerColumn xCol = (XViewerColumn) treeCol.getData();
         xCol.setWidth(treeCol.getWidth());
         xCol.setShow(treeCol.getWidth() > 0);
         columns.add(xCol);
      }
      // Add all columns that are not visible
      for (XViewerColumn xCol : xViewer.getXViewerFactory().getDefaultTableCustomizeData().getColumnData().getColumns()) {
         if (!columns.contains(xCol)) {
            xCol.setShow(false);
            columns.add(xCol);
         }
      }
      custData.columnData.setColumns(columns);
      custData.sortingData.setFromXml(currentCustData.sortingData.getXml());
      custData.filterData.setFromXml(currentCustData.filterData.getXml());
      return custData;
   }

   public List<XViewerColumn> getCurrentTableColumns() {
      return currentCustData.getColumnData().getColumns();
   }

   public List<XViewerColumn> getCurrentTableColumnsInOrder() {
      List<XViewerColumn> columns = new ArrayList<XViewerColumn>(15);
      for (Integer index : xViewer.getTree().getColumnOrder()) {
         TreeColumn treeCol = xViewer.getTree().getColumn(index);
         XViewerColumn xCol = (XViewerColumn) treeCol.getData();
         columns.add(xCol);
      }
      return columns;
   }

   /**
    * Return index of XColumn to original column index on creation of table. Since table allows drag re-ordering of
    * columns, this index will provide the map back to the original column index. Used for label providers
    * getColumnText(object, index)
    * 
    * @return index
    */
   public Map<XViewerColumn, Integer> getCurrentTableColumnsIndex() {
      int[] index = xViewer.getTree().getColumnOrder();
      Map<XViewerColumn, Integer> xColToColumnIndex = new HashMap<XViewerColumn, Integer>(index.length);
      for (int x = 0; x < index.length; x++) {
         TreeColumn treeCol = xViewer.getTree().getColumn(index[x]);
         XViewerColumn xCol = (XViewerColumn) treeCol.getData();
         xColToColumnIndex.put(xCol, index[x]);
      }
      return xColToColumnIndex;
   }

   public int getColumnNumFromXViewerColumn(XViewerColumn xCol) {
      for (Integer index : xViewer.getTree().getColumnOrder()) {
         TreeColumn treeCol = xViewer.getTree().getColumn(index);
         XViewerColumn treeXCol = (XViewerColumn) treeCol.getData();
         if (xCol.equals(treeXCol)) return index;
      }
      return 0;
   }

   /**
    * @return the defaultCustData
    */
   public CustomizeData getTableDefaultCustData() {
      CustomizeData custData = xViewer.getXViewerFactory().getDefaultTableCustomizeData();
      if (custData.getName() == null || custData.getName().equals("")) {
         custData.setName(TABLE_DEFAULT_LABEL);
      }
      custData.setNameSpace(xViewer.getViewerNamespace());
      return custData;
   }

   public String getSortingStr() {
      if (currentCustData.getSortingData().isSorting()) {
         List<XViewerColumn> cols = getSortXCols();
         if (cols.size() == 0) return "";
         StringBuffer sb = new StringBuffer("Sort: ");
         for (XViewerColumn col : getSortXCols()) {
            if (col != null) {
               sb.append(col.getName());
               sb.append(col.isSortForward() ? " (FWD) , " : " (REV) , ");
            }
         }
         return sb.toString().replaceFirst(" , $", "");
      }
      return "";
   }

   public int getDefaultWidth(String id) {
      XViewerColumn xCol = xViewerFactory.getDefaultXViewerColumn(id);
      if (xCol == null)
         return 75;
      else
         return xCol.getWidth();
   }

   public boolean isCustomizationUserDefault(CustomizeData custData) {
      return xViewerFactory.getXViewerCustomizations().isCustomizationUserDefault(custData);
   }

   public List<XViewerColumn> getSortXCols() {
      // return sort columns depending on default/customize
      return currentCustData.getSortingData().getSortXCols(oldNameToColumnId);
   }

   public boolean isLoading() {
      return loading;
   }

   public List<CustomizeData> getSavedCustDatas() throws Exception {
      List<CustomizeData> custDatas = new ArrayList<CustomizeData>();
      for (CustomizeData savedCustData : xViewerFactory.getXViewerCustomizations().getSavedCustDatas()) {
         custDatas.add(resolveLoadedCustomizeData(savedCustData));
      }
      return custDatas;
   }

   public void saveCustomization(CustomizeData custData) throws Exception {
      xViewerFactory.getXViewerCustomizations().saveCustomization(custData);
   }

   /**
    * Set to newName or clear if newName == ""
    * 
    * @param xCol
    * @param newName
    */
   public void customizeColumnName(XViewerColumn xCol, String newName) {
      if (newName == "") {
         XViewerColumn defaultXCol = xViewerFactory.getDefaultXViewerColumn(xCol.getId());
         if (defaultXCol == null) {
            AWorkbench.popup("ERROR", "Column not defined.  Can't retrieve default name.");
            return;
         }
         xCol.setName(xCol.getName());
      } else {
         xCol.setName(newName);
      }
   }

   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) throws Exception {
      xViewerFactory.getXViewerCustomizations().setUserDefaultCustData(newCustData, set);
   }

   public void deleteCustomization(CustomizeData custData) throws Exception {
      xViewerFactory.getXViewerCustomizations().deleteCustomization(custData);

   }

   public boolean isSorting() {
      return currentCustData.getSortingData().isSorting();
   }

   /**
    * Clears out current columns, sorting and filtering and loads table customization
    */
   public void loadCustomization(final CustomizeData newCustData) {
      loading = true;
      if (xViewer.getTree().isDisposed()) return;
      currentCustData = newCustData;
      if (currentCustData.getName() == null || currentCustData.getName().equals("")) {
         currentCustData.setName(CURRENT_LABEL);
      }
      currentCustData.setNameSpace(xViewer.getViewerNamespace());
      xViewer.getTextFilterComp().setCustData(newCustData);
      if (currentCustData.getSortingData().isSorting())
         xViewer.resetDefaultSorter();
      else
         xViewer.setSorter(null);
      // Dispose all existing columns
      for (TreeColumn treeCol : xViewer.getTree().getColumns())
         treeCol.dispose();
      // Create new columns
      addColumns();
      xViewer.updateStatusLabel();
      if (xViewer.getLabelProvider() instanceof XViewerLabelProvider) {
         ((XViewerLabelProvider) xViewer.getLabelProvider()).clearXViewerColumnIndexCache();
      }
      loading = false;
   }

   public void addColumns() {
      for (final XViewerColumn xCol : currentCustData.getColumnData().getColumns()) {
         // Only add visible columns
         if (!xCol.isShow()) continue;
         xCol.setXViewer(xViewer);
         TreeColumn column = new TreeColumn(xViewer.getTree(), xCol.getAlign());
         column.setMoveable(true);
         column.setData(xCol);
         if (xCol.getToolTip() == null || xCol.getToolTip().equals("") || xCol.getToolTip().equals(xCol.getName())) {
            column.setToolTipText(xCol.getName() + "\n" + xCol.getId());
         } else {
            column.setToolTipText(xCol.getToolTip() + xCol.getName() + "\n" + xCol.getId());
         }
         column.setText(xCol.getName());
         column.setWidth(xCol.getWidth());
         column.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               // Add sorter if doesn't exist
               if (xViewer.getSorter() == null) {
                  resetDefaultSorter();
               }
               if (xViewer.isCtrlKeyDown()) {
                  List<XViewerColumn> currSortCols = currentCustData.getSortingData().getSortXCols(oldNameToColumnId);
                  if (currSortCols == null) {
                     currSortCols = new ArrayList<XViewerColumn>();
                     currSortCols.add(xCol);
                  } else {
                     // If already selected this item, reverse the sort
                     if (currSortCols.contains(xCol)) {
                        for (XViewerColumn currXCol : currSortCols)
                           if (currXCol.equals(xCol)) currXCol.reverseSort();
                     } else
                        currSortCols.add(xCol);
                  }
                  currentCustData.getSortingData().setSortXCols(currSortCols);
               } else {

                  List<XViewerColumn> cols = new ArrayList<XViewerColumn>();
                  cols.add(xCol);
                  // If sorter already has this column sorted, reverse the sort
                  List<XViewerColumn> currSortCols = currentCustData.getSortingData().getSortXCols(oldNameToColumnId);
                  if (currSortCols != null && currSortCols.size() == 1 && currSortCols.iterator().next().equals(xCol)) xCol.reverseSort();
                  // Set the newly sorted column
                  currentCustData.getSortingData().setSortXCols(cols);
               }
               xViewer.refresh();
               xViewer.updateStatusLabel();
            }
         });
      }
   }

}
