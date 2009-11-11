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
package org.eclipse.osee.ote.ui.test.manager.pages.scriptTable;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.OseeData;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.ote.ui.markers.MarkerPlugin;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.configuration.ConfigFactory;
import org.eclipse.osee.ote.ui.test.manager.configuration.ILoadConfig;
import org.eclipse.osee.ote.ui.test.manager.configuration.ISaveConfig;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.models.OutputModelJob;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.xscript.XScriptTable;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.xscript.XScriptTableContentProvider;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.xscript.XScriptTableLabelProvider;
import org.eclipse.osee.ote.ui.test.manager.util.Dialogs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class ScriptTableViewer {

   private final ScriptTaskList taskList = new ScriptTaskList();
   private TestManagerEditor testManagerEditor = null;
   final int POPUP_NAVIGATOR = 2;
   final int POPUP_OUTPUT = 0;
   final int POPUP_SCRIPT = 1;
   final int POPUP_RESULTS = 3;
   private XScriptTable scriptTable;

   /**
    * @param parent
    */
   public ScriptTableViewer(Composite parent, TestManagerEditor editor) {
      this.testManagerEditor = editor;
      createWidget(parent);
   }

   public ScriptTableViewer(Composite parent, TestManagerEditor editor, List<ScriptTask> scriptList) {
      this(parent, editor);
      taskList.addTasks(scriptList);
   }

   public void addFile(String fullPath) {
      processDroppedFiles(new String[] {fullPath});
   }

   /*
    * Close the window and dispose of resources
    */
   public void close() {
      //      Shell shell = table.getShell();
      //      if (shell != null && !shell.isDisposed()) shell.dispose();
   }

   /**
    * Release resources
    */
   public void dispose() {
      scriptTable.dispose();
      // Tell the label provider to release its resources
      //      tableViewer.getLabelProvider().dispose();
   }

   /**
    * Return the column names in a collection
    * 
    * @return List containing column names
    */
   //   public List<String> getColumnNames() {
   //      return Arrays.asList(columnNames);
   //   }
   /**
    * Return the parent composite
    */
   //   public Control getControl() {
   //      return table.getParent();
   //   }
   /**
    * Get all tasks marked for run.
    * 
    * @return Vector of ScriptTask to run
    */
   public List<ScriptTask> getRunTasks() {
      return scriptTable.getVisibleSortedScriptTasksToRun();
   }

   /**
    * @return currently selected item
    */
   public ISelection getSelection() {
      return scriptTable.getSelection();
   }

   /**
    * Get string of scripts and run selections for storage
    * 
    * @return <script>-ISRUN-, <script>, <script>-ISRUN
    */
   public String getStorageString() {
      File file = OseeData.getFile("tm.xml");
      String configFile = testManagerEditor.loadValue(testManagerEditor.configFileName);
      ISaveConfig saveConfig =
            ConfigFactory.getInstance().getSaveConfigHandler(testManagerEditor.getPageManager().getScriptPage());
      try {
         saveConfig.saveConfig(file);
      } catch (Exception ex) {
         Dialogs.popupError("Error Loading File", String.format("Error loading file: [%s]\n%s", configFile,
               TestManagerPlugin.getStackMessages(ex)));
      }

      return "file:";
   }

   /**
    * Return the ExampleTaskList
    */
   public ScriptTaskList getTaskList() {
      return taskList;
   }

   public Vector<ScriptTask> getTasks() {
      return taskList.getTasks();
   }

   /**
    * @return Returns the testManagerEditor.
    */
   public TestManagerEditor getTestManagerEditor() {
      return testManagerEditor;
   }

   /**
    * Set taskList from input list of scripts and run selections
    * 
    * @param str <script>-ISRUN-, <script>, <script>-ISRUN
    */
   public void loadStorageString(String str) {
      OseeLog.log(TestManagerPlugin.class, Level.INFO, String.format("Storage String [%s]", str));
      if (str != null) {
         if (str.equals("file:")) {
            File configFile = OseeData.getFile("tm.xml");
            ILoadConfig loadConfig =
                  ConfigFactory.getInstance().getLoadConfigHandler(
                        this.testManagerEditor.getPageManager().getScriptPage());
            try {
               loadConfig.loadConfiguration(configFile);
            } catch (Exception ex) {
               Dialogs.popupError("Error Saving File", String.format("Error saving file: [%s]\n%s", configFile,
                     TestManagerPlugin.getStackMessages(ex)));
            }
         } else {
            String scripts[] = str.split(",");
            for (int i = 0; i < scripts.length; i++) {
               String script = scripts[i];
               if (!script.equals("")) {
                  boolean run = false;
                  if (script.endsWith("-ISRUN-")) {
                     run = true;
                     script = script.replaceFirst("-ISRUN-", "");
                  }
                  try {
                     ScriptTask task = new ScriptTask(script, testManagerEditor.getAlternateOutputDir());
                     task.setRun(run);
                     taskList.addTask(task);
                  } catch (Exception ex) {
                     OseeLog.log(TestManagerPlugin.class, Level.SEVERE, String.format(
                           "Unable to add file [%s] to script view.", script), ex);
                  }
               }
            }
         }
      }
      refresh();
   }

   public void loadTasksFromList(Vector<ScriptTask> tasks) {
      taskList.removeAllTasks();
      taskList.addTasks(tasks);
      refresh();
   }

   public void onConnectionChanged(boolean connected) {
      taskList.onConnected(connected);
      refresh();
   }

   public void refresh() {
      scriptTable.refresh();
   }

   public void refresh(ScriptTask task) {
      scriptTable.refresh(task);
   }

   public void removeSelectedTasks() {
      IStructuredSelection sel = (IStructuredSelection) scriptTable.getSelection();
      Iterator<?> iter = sel.iterator();
      while (iter.hasNext()) {
         ScriptTask task = (ScriptTask) iter.next();
         if (task != null) {
            taskList.removeTask(task);
         }
      }
      refresh();
   }

   /**
    * Calls setRun() on all of the tasks in the table with runState.
    * 
    * @param runState - the state to set all of the tasks's run value to
    */
   public void setAllTasksRun(boolean runState) {
      Iterator<ScriptTask> iter = taskList.getTasks().iterator();
      while (iter.hasNext()) {
         ScriptTask task = iter.next();
         task.setRun(runState);
         taskList.taskChanged(task);
      }
      refresh();
   }

   /**
    * Create a new shell, add the widgets, open the shell
    */
   private void createWidget(Composite parent) {
      scriptTable = new XScriptTable(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
      scriptTable.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      scriptTable.setContentProvider(new XScriptTableContentProvider());
      scriptTable.setLabelProvider(new XScriptTableLabelProvider(scriptTable));
      scriptTable.setInput(taskList);
      attachDragDropListener();
      attachKeyListeners();
      scriptTable.getMenuManager().addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            getPopupMenu();
         }
      });
   }

   private void attachDragDropListener() {
      final FileTransfer fileTransfer = FileTransfer.getInstance();
      final Transfer types[] = new Transfer[] {fileTransfer};
      // Add Drag/Drop to Table
      DropTargetListener scriptDropTargetListener = new DropTargetAdapter() {
         @Override
         public void drop(DropTargetEvent event) {
            if (fileTransfer.isSupportedType(event.currentDataType)) {
               processDroppedFiles((String[]) event.data);
            }
         }
      };
      // Setup drag/drop of files
      int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
      DropTarget scriptsTarget = new DropTarget(scriptTable.getTree(), operations);
      scriptsTarget.setTransfer(types);
      scriptsTarget.addDropListener(scriptDropTargetListener);
   }

   private void getPopupMenu() {

      MenuManager menuManager = scriptTable.getMenuManager();

      menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Action("Open Script") {
         @Override
         public void run() {
            handlePopupMenu(POPUP_SCRIPT);
         }
      });
      menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Action("Open Output File") {
         @Override
         public void run() {
            handlePopupMenu(POPUP_OUTPUT);
         }
      });
      menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Action("Refresh Output Results") {
         @Override
         public void run() {
            handlePopupMenu(POPUP_RESULTS);
         }
      });
      menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Action("Open in Package Explorer") {
         @Override
         public void run() {
            handlePopupMenu(POPUP_NAVIGATOR);
         }
      });
      menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());
      menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Action("Set Selected to Run",
            ImageManager.getImageDescriptor(FrameworkImage.CHECKBOX_ENABLED)) {
         @Override
         public void run() {
            IStructuredSelection sel = (IStructuredSelection) getSelection();
            Iterator<?> iter = sel.iterator();
            while (iter.hasNext()) {
               ScriptTask task = (ScriptTask) iter.next();
               task.setRun(true);
               taskList.taskChanged(task);
            }
            refresh();
         }
      });
      menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Action("Set Selected to Not Run",
            ImageManager.getImageDescriptor(FrameworkImage.CHECKBOX_DISABLED)) {
         @Override
         public void run() {
            IStructuredSelection sel = (IStructuredSelection) getSelection();
            Iterator<?> iter = sel.iterator();
            while (iter.hasNext()) {
               ScriptTask task = (ScriptTask) iter.next();
               task.setRun(false);
               taskList.taskChanged(task);
            }
            refresh();
         }
      });
      menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());
      menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Action("Select All to Run",
            ImageManager.getImageDescriptor(FrameworkImage.CHECKBOX_ENABLED)) {
         @Override
         public void run() {
            setAllTasksRun(true);
         }
      });
      menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Action("Deselect All to Run",
            ImageManager.getImageDescriptor(FrameworkImage.CHECKBOX_DISABLED)) {
         @Override
         public void run() {
            setAllTasksRun(false);
         }
      });
      menuManager.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());

      // item = new MenuItem(previewMenu, SWT.CASCADE);
      // item.setText("Load Test Output Markers");
      // item.addSelectionListener(new SelectionAdapter() {
      //
      // public void widgetSelected(SelectionEvent e) {
      // TableItem items[] = table.getSelection();
      // ScriptTask task = null;
      // if (items.length > 0) {
      // task = (ScriptTask) items[0].getData();
      // testManagerEditor.processOutFile(task);
      // }
      // AWorkbench.popupView(IPageLayout.ID_PROBLEM_VIEW);
      // }
      // });
      // item = new MenuItem(previewMenu, SWT.CASCADE);
      // item.setText("Remove Test Output Markers");
      // item.addSelectionListener(new SelectionAdapter() {
      //
      // public void widgetSelected(SelectionEvent e) {
      // TableItem items[] = table.getSelection();
      // ScriptTask task = null;
      // if (items.length > 0) {
      // task = (ScriptTask) items[0].getData();
      // MarkerSupport.deleteMarkersFromInputFile(task.getScriptModel().getIFile());
      // }
      // }
      // });
      //      
      // item = new MenuItem(previewMenu, SWT.CASCADE);
      // item.setText("Remove All Test Output Markers");
      // item.addSelectionListener(new SelectionAdapter() {
      //
      // public void widgetSelected(SelectionEvent e) {
      // MarkerSupport.deleteAllMarkers();
      // }
      // });

      // item = new MenuItem(previewMenu, SWT.SEPARATOR);

      //      return previewMenu;
   }

   private void handlePopupMenu(int type) {
      TreeSelection selection = (TreeSelection) scriptTable.getSelection();
      Object objs = selection.getFirstElement();
      if (objs instanceof ScriptTask) {
         ScriptTask task = (ScriptTask) objs;
         if (type == POPUP_SCRIPT) {
            task.getScriptModel().openEditor();
         } else if (type == POPUP_OUTPUT) {
            task.getScriptModel().getOutputModel().openEditor();
            refresh();
         } else if (type == POPUP_NAVIGATOR) {
            task.getScriptModel().openPackageExplorer();
         } else if (type == POPUP_RESULTS) {
            Iterator<?> it = selection.iterator();
            while (it.hasNext()) {
               ScriptTask currentTask = (ScriptTask) it.next();
               OutputModelJob.getSingleton().addTask(currentTask);
            }
         }
      }
   }

   private void processDroppedFiles(String files[]) {
      for (int i = 0; i < files.length; i++) {
         String toProcess = files[i];

         if (toProcess.endsWith(".xml")) {
            batchDropHandler(new File(toProcess));
         } else {
            if (toProcess.endsWith(".java") || toProcess.endsWith(".vxe") || !new File(toProcess).getName().matches(
                  ".*\\..*")) {
               ScriptTask newTask = new ScriptTask(files[i], testManagerEditor.getAlternateOutputDir());
               if (!taskList.contains(newTask)) {
                  //                  newTask.computeExists();
                  if (newTask.getScriptModel().getOutputModel() != null) {
                     if (newTask.getScriptModel().getOutputModel().getIFile() != null) {
                        MarkerPlugin.getDefault().addMarkers(newTask.getScriptModel().getOutputModel().getIFile());
                     }
                  }
                  taskList.addTask(newTask);
               }
            } else {
               Dialogs.popupError("Invalid Drop", "Only Java scripts and cppUnit executables can be dropped here.");
               OseeLog.log(TestManagerPlugin.class, Level.SEVERE, "not .java or cppExe file");
               return;
            }
         }
      }
      refresh();
      this.testManagerEditor.storeValue(testManagerEditor.scriptsQualName, getStorageString());
   }

   private void batchDropHandler(File batchFile) {
      try {
         ILoadConfig loadConfig =
               ConfigFactory.getInstance().getLoadConfigHandler(testManagerEditor.getPageManager().getScriptPage());
         loadConfig.loadConfiguration(batchFile);
      } catch (Exception ex) {
         Dialogs.popupError("Invalid Drop", String.format("Unable to read batch file\nFile [%s]\n%s",
               (batchFile != null ? batchFile.getAbsolutePath() : "NULL"), TestManagerPlugin.getStackMessages(ex)));
      }
   }

   /**
    * @return StructuredViewer
    */
   public StructuredViewer getXViewer() {
      return scriptTable;
   }

   private void attachKeyListeners() {
      scriptTable.getTree().addKeyListener(new KeyListener() {
         public void keyPressed(KeyEvent e) {
         }

         public void keyReleased(KeyEvent e) {
            // If they press enter, do the same as a double click
            if (e.character == SWT.DEL) {
               removeSelectedTasks();
               refresh();
            }
            if (e.character == 'r') {
               refresh();
            }
         }
      });
   }

   /**
    * @param files
    */
   public void addFiles(String[] files) {
      processDroppedFiles(files);
   }
}