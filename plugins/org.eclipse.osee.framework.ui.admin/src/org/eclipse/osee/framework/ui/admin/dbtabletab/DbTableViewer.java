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
package org.eclipse.osee.framework.ui.admin.dbtabletab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.ui.admin.AdminView;
import org.eclipse.osee.framework.ui.admin.dbtabletab.DbDescribe.Describe;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.cellEditor.UniversalCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class DbTableViewer {

   private final DbTableTab dbTab;

   // private Shell shell;
   private Table table;
   private TableViewer tableViewer;

   private List<Describe> describeList;
   private DbTaskList dbTaskList;
   private DbDescribe dbDescribe;
   private final DbItem dbItem;

   /**
    * @param parent
    * @throws OseeDataStoreException
    */
   public DbTableViewer(Composite parent, int numColumns, DbTableTab dbTab, DbItem dbItem) throws OseeDataStoreException {
      this.dbTab = dbTab;
      this.dbItem = dbItem;
      getTableDescription();
      this.addChildControls(parent, numColumns);
   }

   public void save() {
      for (DbModel dbModel : getTasks()) {
         if (dbModel.isNeedSave()) {
            dbItem.save(dbDescribe, dbModel);
         }
         dbModel.setNeedSave(false);
         dbModel.setColumnChanged(null);
      }
      refresh();
   }

   public void addRecord() {
      for (DbModel dbModel : getTasks()) {
         if (dbModel.isNeedSave()) {
            AWorkbench.popup("ERROR", "Save Table Before Adding Record");
            return;
         }
      }
      // Send in the first record as an example
      DbModel dbModel = dbItem.createNewRow(getTasks().get(0));
      dbTaskList.addTask(dbModel);
      refresh();
   }

   public void add(DbModel a) {
      dbTaskList.addTask(a);
   }

   public List<String> getColumnNames() {
      ArrayList<String> list = new ArrayList<String>();
      for (Describe d : describeList) {
         list.add(d.name);
      }
      return list;
   }

   public String[] getColumnNameArray() {
      return getColumnNames().toArray(new String[describeList.size()]);
   }

   /**
    * Run and wait for a close event
    * 
    * @param shell Instance of Shell
    */
   @SuppressWarnings("unused")
   private void run(Shell shell) {
      Display display = shell.getDisplay();
      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }
   }

   public void removeSelectedTasks() {
      IStructuredSelection sel = (IStructuredSelection) tableViewer.getSelection();
      Iterator<?> iter = sel.iterator();
      while (iter.hasNext()) {
         DbModel model = (DbModel) iter.next();
         if (model != null) {
            dbTaskList.removeTask(model);
         }
      }
   }

   /**
    * Release resources
    */
   public void dispose() {
      // Tell the label provider to release its resources
      tableViewer.getLabelProvider().dispose();
      tableViewer.getTable().dispose();
   }

   public void setSaveNeeded(boolean needed) {
      AdminView.setSaveNeeded(needed);
   }

   /**
    * Create a new shell, add the widgets, open the shell
    * 
    * @param composite - a Composite
    * @param numColumns - number of columns.
    */
   private void addChildControls(Composite composite, int numColumns) {
      // Create the table
      createTable(composite, numColumns);
      // Create and setup the TableViewer
      createTableViewer();
      tableViewer.setContentProvider(new ExampleContentProvider());
      tableViewer.setLabelProvider(new DbLabelProvider(getColumnNameArray()));
   }

   public Vector<DbModel> getTasks() {
      return dbTaskList.getTasks();
   }

   public void addDisposeListener(DisposeListener dl) {
      table.addDisposeListener(dl);
   }

   /**
    * Create the Table
    */
   private void createTable(Composite parent, int numColumns) {
      table =
            new Table(parent,
                  SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
      GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL);
      gridData.horizontalSpan = numColumns;
      table.setLayoutData(gridData);
      table.setLinesVisible(true);
      table.setHeaderVisible(true);

      int x = 0;
      for (Describe d : describeList) {
         TableColumn column = new TableColumn(table, SWT.LEFT, x);
         column.setText(d.name);
         int width = dbItem.getColumnWidth(d.name);
         if (width == 0) {
            column.setWidth(50);
         } else {
            column.setWidth(width);
         }
         final int fx = x;
         column.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               tableViewer.setSorter(new DbTableSorter(fx));
            }
         });
         x++;
      }

   }

   public void getTableDescription() throws OseeDataStoreException {
      dbDescribe = new DbDescribe(dbItem);
      describeList = dbDescribe.getDescription();
   }

   /**
    * Load table with administration items
    * 
    * @throws OseeDataStoreException
    */
   public void load() throws OseeDataStoreException {
      dbTaskList = dbDescribe.getDbTaskList(describeList);
      tableViewer.setInput(dbTaskList);
      refresh();
   }

   public void refresh() {
      tableViewer.refresh();
   }

   /**
    * Create the TableViewer
    */
   private void createTableViewer() {
      tableViewer = new TableViewer(table) {
      };
      tableViewer.setUseHashlookup(true);

      if (dbItem.isWriteAccess()) {
         String columnNames[] = new String[describeList.size()];
         int x = 0;
         for (Describe d : describeList) {
            columnNames[x++] = d.name;
         }
         tableViewer.setColumnProperties(columnNames);
         tableViewer.setCellModifier(new DbCellModifier(this, dbItem));

         // Create the cell editors; must be number of columns regardless
         // that we only use the first column as an editor
         CellEditor[] editors = new CellEditor[columnNames.length];
         x = 0;
         for (Describe d : describeList) {
            // ID Column of SITE_TOOLS is un-editable
            if (dbItem.isWriteable(d.name)) {
               editors[x] = new UniversalCellEditor(table);
            }
            x++;
         }

         // Assign the cell editors to the viewer
         tableViewer.setCellEditors(editors);
      }

      // Set the default sorter for the viewer
      tableViewer.setSorter(new DbTableSorter(0));

      table.addListener(SWT.MouseDoubleClick, new Listener() {
         public void handleEvent(Event event) {
            TableItem[] items = table.getSelection();
            if (items != null && items.length > 0) {
               TableItem item = items[0];
               for (int index = 0; index < table.getColumnCount(); index++) {
                  if (item.getBounds(index).contains(event.x, event.y)) {
                     Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                     CellTextDialog dialog =
                           new CellTextDialog(shell, getAdminView().getSelectedDbItem().getTableName(),
                                 table.getColumn(index).getText(), item.getText(index));
                     dialog.open();
                     break;
                  }
               }

            }
         }
      });
   }

   /*
    * Close the window and dispose of resources
    */
   public void close() {
      Shell shell = table.getShell();
      if (shell != null && !shell.isDisposed()) {
         shell.dispose();
      }
   }

   /**
    * InnerClass that acts as a proxy for the ExampleTaskList providing content for the Table. It implements the
    * ITaskListViewer interface since it must register changeListeners with the ExampleTaskList
    */
   class ExampleContentProvider implements IStructuredContentProvider, ITaskListViewer {

      public void inputChanged(Viewer v, Object oldInput, Object newInput) {
         if (newInput != null) {
            ((DbTaskList) newInput).addChangeListener(this);
         }
         if (oldInput != null) {
            ((DbTaskList) oldInput).removeChangeListener(this);
         }
      }

      public void dispose() {
         dbTaskList.removeChangeListener(this);
      }

      // Return the tasks as an array of Objects
      public Object[] getElements(Object parent) {
         return dbTaskList.getTasks().toArray();
      }

      public void addTask(DbModel task) {
         tableViewer.add(task);
      }

      public void removeTask(DbModel task) {
         tableViewer.remove(task);
      }

      public void updateTask(DbModel task) {
         tableViewer.update(task, null);
      }
   }

   /**
    * @return currently selected item
    */
   public ISelection getSelection() {
      return tableViewer.getSelection();
   }

   /**
    * Return the ExampleTaskList
    */
   public DbTaskList getTaskList() {
      return dbTaskList;
   }

   /**
    * Return the parent composite
    */
   public Control getControl() {
      return table.getParent();
   }

   public DbTableTab getAdminView() {
      return dbTab;
   }

   private static final class CellTextDialog extends MessageDialog {

      private final String dialogMessage;

      public CellTextDialog(Shell parentShell, String dialogTitle, String dialogMessage, String dialogText) {
         super(parentShell, dialogTitle, PlatformUI.getWorkbench().getSharedImages().getImage(
               ISharedImages.IMG_OBJS_INFO_TSK), dialogMessage, MessageDialog.INFORMATION,
               new String[] {IDialogConstants.OK_LABEL}, 0);
         this.dialogMessage = dialogText;
         this.setShellStyle(this.getShellStyle() | SWT.RESIZE);
      }

      @Override
      protected Control createCustomArea(Composite parent) {
         Composite composite = new Composite(parent, SWT.NONE);
         composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
         composite.setLayout(new GridLayout());
         composite.setFont(parent.getFont());

         Text text = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
         GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
         text.setLayoutData(data);
         text.setEditable(false);
         text.setText(dialogMessage);
         text.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
         text.setFont(parent.getFont());
         return composite;
      }
   }
}
