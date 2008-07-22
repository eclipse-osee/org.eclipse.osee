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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.dialog.XViewerCustomizeDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * This manages the default table column definitions versus the user modified column data, sorter and filters.
 * 
 * @author Donald G. Dunne
 */
public class CustomizeManager {

   private IXViewerFactory xViewerFactory;
   private XViewer xViewer;
   private CustomizeData currentCustData;
   public static String CURRENT_LABEL = "-- Current Table View --";
   public static String TABLE_DEFAULT_LABEL = "-- Table Default --";
   // Added to keep filter, sorter from working till finished loading
   public boolean loading = true;

   public CustomizeManager(XViewer xViewer, IXViewerFactory xViewerFactory) {
      this.xViewer = xViewer;
      this.xViewerFactory = xViewerFactory;
      // Set customize to be user default, if selected, or table default
      currentCustData = xViewerFactory.getXViewerCustomizations(xViewer).getUserDefaultCustData();
      if (currentCustData == null) {
         currentCustData = getTableDefaultCustData();
      }
      xViewerFactory.getXViewerCustomMenu().init(xViewer);
   }

   /**
    * Clears out current columns, sorting and filtering and loads table customization
    */
   public void loadCustomization() {
      loadCustomization(currentCustData);
   }

   /**
    * Clears out current columns, sorting and filtering and loads table customization
    */
   public void loadCustomization(final CustomizeData newCustData) {
      SetCustomizationJob job = new SetCustomizationJob(xViewer, newCustData);
      if (doCustomizeInCurrentThread()) {
         job.run(null);
      } else {
         job.setUser(true);
         job.setPriority(Job.SHORT);
         job.schedule();
      }
   }

   public void addColumns() {
      for (final XViewerColumn xCol : currentCustData.getColumnData().getColumns()) {
         TreeColumn column = new TreeColumn(xViewer.getTree(), xCol.getAlign());
         column.setMoveable(true);
         column.setData(xCol);
         if (xCol.getToolTip().equals("")) {
            column.setToolTipText(xCol.getName());
         } else {
            column.setToolTipText(xCol.getToolTip());
         }
         column.setText(xCol.getName());
         if (xCol.isShow()) {
            column.setWidth(xCol.getWidth());
         } else {
            column.setWidth(0);
         }
         column.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               // Add sorter if doesn't exist
               if (xViewer.getSorter() == null) {
                  resetDefaultSorter();
               }
               if (xViewer.isCtrlKeyDown()) {
                  List<XViewerColumn> currSortCols = currentCustData.getSortingData().getSortXCols();
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
                  List<XViewerColumn> currSortCols = currentCustData.getSortingData().getSortXCols();
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
      (new XViewerCustomizeDialog(currentCustData, xViewer)).open();
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
      List<XViewerColumn> columns = new ArrayList<XViewerColumn>(15);
      for (Integer index : xViewer.getTree().getColumnOrder()) {
         TreeColumn treeCol = xViewer.getTree().getColumn(index);
         XViewerColumn xCol = (XViewerColumn) treeCol.getData();
         xCol.setWidth(treeCol.getWidth());
         xCol.setShow(treeCol.getWidth() > 0);
         columns.add(xCol);
      }
      custData.columnData.setColumns(columns);
      custData.sortingData.setFromXml(currentCustData.sortingData.getXml());
      custData.filterData.setFromXml(currentCustData.filterData.getXml());
      return custData;
   }

   public List<XViewerColumn> getCurrentTableColumns() {
      return currentCustData.getColumnData().getColumns();
   }

   /**
    * @return the defaultCustData
    */
   public CustomizeData getTableDefaultCustData() {
      CustomizeData custData = xViewer.getXViewerFactory().getDefaultTableCustomizeData(xViewer);
      if (custData.getName() == null || this.currentCustData.getName().equals("")) {
         custData.setName(TABLE_DEFAULT_LABEL);
      }
      custData.setNameSpace(xViewer.getViewerNamespace());
      return custData;
   }

   public boolean doCustomizeInCurrentThread() {
      return xViewer.doCustomizeInCurrentThread();
   }

   public String getSortingStr() {
      if (currentCustData.getSortingData().isSorting()) {
         return currentCustData.getSortingData().toString();
      }
      return "";
   }

   public boolean isCustomizationUserDefault(CustomizeData custData) {
      return xViewerFactory.getXViewerCustomizations(xViewer).isCustomizationUserDefault(custData);
   }

   public List<XViewerColumn> getSortXCols() {
      // return sort columns depending on default/customize
      return currentCustData.getSortingData().getSortXCols();
   }

   public boolean isLoading() {
      return loading;
   }

   public List<CustomizeData> getSavedCustDatas() {
      return xViewerFactory.getXViewerCustomizations(xViewer).getSavedCustDatas();
   }

   public void saveCustomization(CustomizeData custData) throws Exception {
      xViewerFactory.getXViewerCustomizations(xViewer).saveCustomization(custData);
   }

   /**
    * Set to newName or clear if newName == ""
    * 
    * @param xCol
    * @param newName
    */
   public void customizeColumnName(XViewerColumn xCol, String newName) {
      AWorkbench.popup("ERROR", "Not implemented yet");
   }

   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) {
      xViewerFactory.getXViewerCustomizations(xViewer).setUserDefaultCustData(newCustData, set);
   }

   public void deleteCustomization(CustomizeData custData) throws Exception {
      xViewerFactory.getXViewerCustomizations(xViewer).deleteCustomization(custData);

   }

   public boolean isSorting() {
      return currentCustData.getSortingData().isSorting();
   }

   public class SetCustomizationJob extends Job {

      private final CustomizeData newCustData;
      private final XViewer fXViewer;

      public SetCustomizationJob(final XViewer fXViewer, final CustomizeData newCustData) {
         super("Loading Customization " + newCustData.getName());
         this.fXViewer = fXViewer;
         this.newCustData = newCustData;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         Display.getDefault().syncExec(new Runnable() {
            public void run() {
               loading = true;
               if (fXViewer.getTree().isDisposed()) return;
               currentCustData = newCustData;
               if (currentCustData.getName() == null || currentCustData.getName().equals("")) {
                  currentCustData.setName(CURRENT_LABEL);
               }
               currentCustData.setNameSpace(fXViewer.getViewerNamespace());
               fXViewer.getTextFilterComp().setCustData(newCustData);
               if (currentCustData.getSortingData().isSorting())
                  fXViewer.resetDefaultSorter();
               else
                  fXViewer.setSorter(null);
               // Dispose all existing columns
               for (TreeColumn treeCol : fXViewer.getTree().getColumns())
                  treeCol.dispose();
               // Create new columns
               addColumns();
               fXViewer.updateStatusLabel();
               loading = false;
               fXViewer.refresh();
            }
         });
         if (monitor != null) monitor.done();
         return Status.OK_STATUS;
      }
   };

}
