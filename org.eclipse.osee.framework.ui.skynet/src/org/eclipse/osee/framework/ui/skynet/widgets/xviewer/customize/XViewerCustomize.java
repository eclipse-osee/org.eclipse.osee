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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.TreeViewerReport;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.HtmlDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class XViewerCustomize {

   protected XViewer xViewer;
   private CustomizeData currentCustData;
   public static String CURRENT_LABEL = "-- Current Table View --";
   public static String TABLE_DEFAULT_LABEL = "-- Table Default --";
   private IXViewerCustomizations xViewerCustomizations;
   private Clipboard clipboard = new Clipboard(null);

   protected Action clearAllSorting;
   protected Action tableProperties;
   protected Action viewTableReport;
   protected Action columnMultiEdit;
   protected Action copySelected;
   protected Action copySelectedCell;
   protected Action viewSelectedCell;

   /**
    * @param factory
    */
   public XViewerCustomize() {
      this(null);
   }

   public XViewerCustomize(IXViewerCustomizations xViewerCustomizations) {
      this.xViewerCustomizations = xViewerCustomizations;
   }

   public void init(XViewer xviewer) {
      this.xViewer = xviewer;
      if (xViewerCustomizations == null) {
         this.xViewerCustomizations = xViewer.getXViewerFactory().getXViewerCustomizations(xViewer);
      }
      setupActions();
      xViewer.getMenuManager().addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            setupMenu();
         }
      });
      xViewer.getTree().addKeyListener(new KeySelectedListener());
      xViewer.getTree().addDisposeListener(new DisposeListener() {
         public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e) {
            if (clipboard != null) clipboard.dispose();
         };
      });
   }

   /**
    * 
    */
   protected void setupMenu() {
      MenuManager mm = xViewer.getMenuManager();
      mm.add(new GroupMarker(XViewer.MENU_GROUP_PRE));
      mm.add(new Separator());
      mm.add(clearAllSorting);
      mm.add(tableProperties);
      mm.add(viewTableReport);
      if (xViewer.isColumnMultiEditEnabled()) mm.add(columnMultiEdit);
      mm.add(copySelected);
      mm.add(viewSelectedCell);
      mm.add(copySelectedCell);
      mm.add(new GroupMarker(XViewer.MENU_GROUP_POST));
   }

   protected void setupActions() {
      copySelected = new Action("Copy Selected Row(s)- Ctrl-C") {
         public void run() {
            performCopy();
         };
      };
      viewSelectedCell = new Action("View Selected Cell Data") {
         public void run() {
            performViewCell();
         };
      };
      copySelectedCell = new Action("Copy Selected Cell - Ctrl-Shift-C") {
         public void run() {
            performCopyCell();
         };
      };
      clearAllSorting = new Action("Clear All Sorting") {
         public void run() {
            xViewer.getCustomize().clearSorter();
         };
      };
      tableProperties = new Action("Table Customization") {
         public void run() {
            handleTableCustomization();
         }
      };
      viewTableReport = new Action("View Table Report") {
         public void run() {
            TreeViewerReport tvr = new TreeViewerReport(xViewer);
            ArrayList<Integer> ignoreCols = new ArrayList<Integer>();
            int columnNum = 0;
            for (XViewerColumn xCol : xViewer.getCustomize().getCurrentCustData().getColumnData().getColumns()) {
               columnNum++;
               if (!xCol.isShow()) {
                  ignoreCols.add(columnNum);
               }
            }
            tvr.setIgnoreColumns(ignoreCols);
            tvr.open();
         }
      };
      columnMultiEdit = new Action("Column Multi Edit") {
         public void run() {
            Set<TreeColumn> editableColumns = new HashSet<TreeColumn>();
            Collection<TreeItem> selectedTreeItems = Arrays.asList(xViewer.getTree().getSelection());
            for (TreeColumn treeCol : xViewer.getTree().getColumns())
               if (xViewer.isColumnMultiEditable(treeCol, selectedTreeItems)) editableColumns.add(treeCol);
            if (editableColumns.size() == 0) {
               AWorkbench.popup("ERROR", "No Columns Are Multi-Editable");
               return;
            }
            ListDialog ld = new ListDialog(Display.getCurrent().getActiveShell());
            ld.setMessage("Select Column to Edit");
            ld.setInput(editableColumns);
            ld.setLabelProvider(treeColumnLabelProvider);
            ld.setContentProvider(new ArrayContentProvider());
            ld.setTitle("Select Column to Edit");
            int result = ld.open();
            if (result != 0) return;
            xViewer.handleColumnMultiEdit((TreeColumn) ld.getResult()[0], selectedTreeItems);
         }
      };
   }

   private class KeySelectedListener implements KeyListener {
      public void keyPressed(KeyEvent e) {
         if (e.keyCode == 'c' && e.stateMask == (SWT.CONTROL | SWT.SHIFT)) {
            performCopyCell();
         } else if (e.keyCode == 'c' && e.stateMask == SWT.CONTROL) {
            performCopy();
         }
      }

      public void keyReleased(KeyEvent e) {
      }
   }

   private void performViewCell() {
      TreeColumn treeCol = xViewer.getRightClickSelectedColumn();
      TreeItem treeItem = xViewer.getRightClickSelectedItem();
      if (treeCol != null) {
         XViewerColumn xCol = (XViewerColumn) treeCol.getData();
         String data = ((XViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(treeItem.getData(), xCol);
         if (data != null && !data.equals("")) new HtmlDialog(treeCol.getText() + " Data", treeCol.getText() + " Data",
               data).open();
      }
   }

   private void performCopyCell() {
      Set<TreeColumn> visibleColumns = new HashSet<TreeColumn>();
      TreeItem[] items = xViewer.getTree().getSelection();
      if (items.length == 0) {
         AWorkbench.popup("ERROR", "No items to copy");
         return;
      }
      ArrayList<String> textTransferData = new ArrayList<String>();
      ITableLabelProvider labelProv = (ITableLabelProvider) xViewer.getLabelProvider();
      for (TreeColumn treeCol : xViewer.getTree().getColumns())
         if (treeCol.getWidth() > 0) visibleColumns.add(treeCol);
      if (visibleColumns.size() == 0) {
         AWorkbench.popup("ERROR", "No Columns Are Available");
         return;
      }
      ListDialog ld = new ListDialog(Display.getCurrent().getActiveShell()) {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.ui.dialogs.ListDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
          */
         @Override
         protected Control createDialogArea(Composite container) {
            Control control = super.createDialogArea(container);
            getTableViewer().setSorter(treeColumnSorter);
            return control;
         }
      };
      ld.setMessage("Select Column to Copy");
      ld.setInput(visibleColumns);
      ld.setLabelProvider(treeColumnLabelProvider);
      ld.setContentProvider(new ArrayContentProvider());
      ld.setTitle("Select Column to Copy");
      int result = ld.open();
      if (result != 0) return;
      TreeColumn treeCol = (TreeColumn) ld.getResult()[0];
      StringBuffer sb = new StringBuffer();
      for (TreeItem item : items) {
         for (int x = 0; x < xViewer.getTree().getColumnCount(); x++) {
            if (xViewer.getTree().getColumn(x).equals(treeCol)) {
               sb.append(labelProv.getColumnText(item.getData(), x) + "\n");
            }
         }
      }
      textTransferData.add(sb.toString());

      if (textTransferData.size() > 0) clipboard.setContents(
            new Object[] {org.eclipse.osee.framework.jdk.core.util.Collections.toString(textTransferData, null, ", ",
                  null)}, new Transfer[] {TextTransfer.getInstance()});
   }

   private void performCopy() {
      TreeItem[] items = xViewer.getTree().getSelection();
      if (items.length == 0) {
         AWorkbench.popup("ERROR", "No items to copy");
         return;
      }
      ArrayList<String> textTransferData = new ArrayList<String>();
      ITableLabelProvider labelProv = (ITableLabelProvider) xViewer.getLabelProvider();
      if (items != null && items.length > 0) {
         StringBuffer sb = new StringBuffer();
         for (TreeItem item : items) {
            List<String> strs = new ArrayList<String>();
            for (int x = 0; x < xViewer.getTree().getColumnCount(); x++) {
               if (xViewer.getTree().getColumn(x).getWidth() > 0) {
                  String data = labelProv.getColumnText(item.getData(), x);
                  if (data != null) strs.add(data);
               }
            }
            sb.append(org.eclipse.osee.framework.jdk.core.util.Collections.toString("\t", strs) + "\n");
         }
         textTransferData.add(sb.toString());

         if (textTransferData.size() > 0) clipboard.setContents(
               new Object[] {org.eclipse.osee.framework.jdk.core.util.Collections.toString(textTransferData, null,
                     ", ", null)}, new Transfer[] {TextTransfer.getInstance()});
      }
   }

   static LabelProvider treeColumnLabelProvider = new LabelProvider() {
      @Override
      public String getText(Object element) {
         if (element instanceof TreeColumn) {
            return ((TreeColumn) element).getText();
         }
         return "Unknown element type";
      }
   };

   static ViewerSorter treeColumnSorter = new ViewerSorter() {
      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer,
       *      java.lang.Object, java.lang.Object)
       */
      @SuppressWarnings("unchecked")
      @Override
      public int compare(Viewer viewer, Object e1, Object e2) {
         return getComparator().compare(((TreeColumn) e1).getText(), ((TreeColumn) e2).getText());
      }
   };

   public void setCustomization(final CustomizeData custData) {
      SetCustomizationJob job = new SetCustomizationJob(xViewer, custData);
      if (doCustomizeInCurrentThread()) {
         job.run(null);
      } else {
         job.setUser(true);
         job.setPriority(Job.SHORT);
         job.schedule();
      }
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
               if (fXViewer.getTree().isDisposed()) return;
               CustomizeData custData = new CustomizeData(newCustData.getXml(), fXViewer.getXViewerFactory());
               // Add any new columns that were added after this customization was saved
               custData.getColumnData().addMissingColumns(getTableDefaultCustData().getColumnData());
               fXViewer.getCustomize().currentCustData = custData;
               if (fXViewer.getCustomize().getCurrentCustData().getName() == null || fXViewer.getCustomize().getCurrentCustData().getName().equals(
                     "")) fXViewer.getCustomize().getCurrentCustData().setName(CURRENT_LABEL);
               fXViewer.getCustomize().getCurrentCustData().setNameSpace(fXViewer.getViewerNamespace());
               fXViewer.getTextFilterComp().setCustData(custData);
               if (fXViewer.getCustomize().getCurrentCustData().getSortingData().isSorting())
                  fXViewer.resetDefaultSorter();
               else
                  fXViewer.setSorter(null);
               // Dispose all existing columns
               for (TreeColumn treeCol : fXViewer.getTree().getColumns())
                  treeCol.dispose();
               // Create new columns
               fXViewer.addColumns();
               fXViewer.updateStatusLabel();
               fXViewer.refresh();
            }
         });
         if (monitor != null) monitor.done();
         return Status.OK_STATUS;
      }
   };

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
      if (currentCustData != null && !currentCustData.getName().equals(CURRENT_LABEL) && !currentCustData.getName().equals(
            TABLE_DEFAULT_LABEL) && currentCustData.getName() != null) return ("Custom: " + currentCustData.getName() + " - ");
      return "";
   }

   /**
    * @return the currentCustData
    */
   public CustomizeData getCurrentCustData() {
      return currentCustData;
   }

   /**
    * @return the defaultCustData
    */
   public CustomizeData getTableDefaultCustData() {
      CustomizeData custData = xViewer.getXViewerFactory().getDefaultTableCustomizeData(xViewer);
      if (custData.getName() == null || this.currentCustData.getName().equals("")) custData.setName(TABLE_DEFAULT_LABEL);
      custData.setNameSpace(xViewer.getViewerNamespace());
      return custData;
   }

   /**
    * Return the customize data that will be used to customize the table. First, check for a user selected default.
    * Second, check for a tableDefaultCustData set programatically.
    * 
    * @return the CustomizeData
    */
   public CustomizeData getDefaultCustData() {
      if (xViewerCustomizations.getUserDefaultCustData() != null) return xViewerCustomizations.getUserDefaultCustData();
      return this.getTableDefaultCustData();
   }

   /**
    * @return the xViewerCustomizations
    */
   public IXViewerCustomizations getXViewerCustomizations() {
      return xViewerCustomizations;
   }

   public boolean doCustomizeInCurrentThread() {
      return false;
   }

}
