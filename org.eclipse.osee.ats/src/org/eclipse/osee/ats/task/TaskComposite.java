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

package org.eclipse.osee.ats.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.config.AtsBulkLoadCache;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.operation.ImportTasksFromSimpleList;
import org.eclipse.osee.ats.operation.ImportTasksFromSpreadsheet;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.ats.util.SMAMetrics;
import org.eclipse.osee.ats.world.WorldCompletedFilter;
import org.eclipse.osee.ats.world.WorldContentProvider;
import org.eclipse.osee.ats.world.WorldLabelProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.BlamOperations;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class TaskComposite extends Composite implements IActionable {

   private TaskXViewer taskXViewer;
   private MenuItem filterCompletedMenuItem, selectionMetricsMenuItem;
   private final IXTaskViewer iXTaskViewer;
   private Label extraInfoLabel;
   private final WorldCompletedFilter worldCompletedFilter = new WorldCompletedFilter();

   /**
    * @param label
    * @throws Exception
    */
   public TaskComposite(IXTaskViewer iXTaskViewer, Composite parent, int style) throws OseeCoreException {
      this(iXTaskViewer, parent, style, null);
   }

   public TaskComposite(IXTaskViewer iXTaskViewer, Composite parent, int style, ToolBar toolBar) throws OseeCoreException {
      super(parent, style);
      this.iXTaskViewer = iXTaskViewer;
      AtsBulkLoadCache.run(false);

      setLayout(ALayout.getZeroMarginLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      if (!DbConnectionExceptionComposite.dbConnectionIsOk(this)) {
         return;
      }

      populateToolBar(toolBar);

      try {

         extraInfoLabel = new Label(this, SWT.NONE);
         extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         taskXViewer =
               new TaskXViewer(this, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, iXTaskViewer.getEditor(), this);
         taskXViewer.setTasksEditable(iXTaskViewer.isTasksEditable());
         taskXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

         taskXViewer.setContentProvider(new WorldContentProvider(taskXViewer));
         taskXViewer.setLabelProvider(new WorldLabelProvider(taskXViewer));
         taskXViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
               try {
                  updateExtraInfoLine();
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
         });
         taskXViewer.getTree().addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent event) {
            }

            public void keyReleased(KeyEvent event) {
               try {
                  if ((event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
                     if (event.keyCode == 'a') {
                        taskXViewer.getTree().setSelection(taskXViewer.getTree().getItems());
                        updateExtraInfoLine();
                     } else if (event.keyCode == 'x') {
                        if (selectionMetricsMenuItem != null) {
                           selectionMetricsMenuItem.setSelection(!selectionMetricsMenuItem.getSelection());
                           updateExtraInfoLine();
                        }
                     } else if (event.keyCode == 'f') {
                        if (filterCompletedMenuItem != null) {
                           filterCompletedMenuItem.setSelection(!filterCompletedMenuItem.getSelection());
                           handleFilterAction();
                        }
                     }
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
         });

         Tree tree = taskXViewer.getTree();
         GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
         gridData.heightHint = 100;
         gridData.widthHint = 100;
         tree.setLayoutData(gridData);
         tree.setHeaderVisible(true);
         tree.setLinesVisible(true);

         setupDragAndDropSupport();
         parent.layout();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /**
    * @return the iXTaskViewer
    */
   public IXTaskViewer getIXTaskViewer() {
      return iXTaskViewer;
   }

   public void handleFilterAction() {
      if (filterCompletedMenuItem.getSelection()) {
         taskXViewer.addFilter(worldCompletedFilter);
      } else {
         taskXViewer.removeFilter(worldCompletedFilter);
      }
      updateExtendedStatusString();
   }

   public void updateExtendedStatusString() {
      String str = "";
      if (filterCompletedMenuItem != null && filterCompletedMenuItem.getSelection()) {
         str += "[Complete/Cancel Filter]";
      }
      taskXViewer.setExtendedStatusString(str);
      taskXViewer.refresh();
   }

   private void populateToolBar(ToolBar toolBar) throws OseeCoreException {
      ToolItem item = null;

      if (iXTaskViewer.isTaskable()) {

         item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(AtsPlugin.getInstance().getImage("newTask.gif"));
         item.setToolTipText("New Task");
         item.setEnabled(iXTaskViewer.isTasksEditable() && iXTaskViewer.isTaskable());
         item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               handleNewTask();
            }
         });

         item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(AtsPlugin.getInstance().getImage("redRemove.gif"));
         item.setToolTipText("Delete Task");
         item.setEnabled(iXTaskViewer.isTasksEditable() && iXTaskViewer.isTaskable());
         item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               handleDeleteTask();
            }
         });

      }

      if (iXTaskViewer.getEditor() != null && (iXTaskViewer.getEditor() instanceof TaskEditor)) {
         item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(AtsPlugin.getInstance().getImage("task.gif"));
         item.setToolTipText("Open New ATS Task Editor");
         item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               try {
                  ITaskEditorProvider provider =
                        ((TaskEditorInput) ((TaskEditor) iXTaskViewer.getEditor()).getEditorInput()).getItaskEditorProvider().copyProvider();
                  provider.setCustomizeData(taskXViewer.getCustomizeMgr().generateCustDataFromTable());
                  provider.setTableLoadOptions(TableLoadOption.NoUI);
                  TaskEditor.open(provider);
               } catch (OseeCoreException ex) {
                  OSEELog.logException(AtsPlugin.class, ex, true);
               }
            }
         });
      }

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(AtsPlugin.getInstance().getImage("taskSelected.gif"));
      item.setToolTipText("Open Selected in ATS Task Editor");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (taskXViewer.getSelectedArtifacts().size() == 0) {
               AWorkbench.popup("ERROR", "Select items to open");
               return;
            }
            try {
               TaskEditor.open(new TaskEditorSimpleProvider("Tasks", taskXViewer.getSelectedArtifacts(),
                     taskXViewer.getCustomizeMgr().generateCustDataFromTable()));
            } catch (OseeCoreException ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(AtsPlugin.getInstance().getImage("refresh.gif"));
      item.setToolTipText("Refresh Tasks");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               if (iXTaskViewer.isRefreshActionHandled()) {
                  iXTaskViewer.handleRefreshAction();
               } else {
                  loadTable();
               }
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("customize.gif"));
      item.setToolTipText("Customize Table");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            taskXViewer.getCustomizeMgr().handleTableCustomization();
         }
      });

      OseeAts.addButtonToEditorToolBar(this, AtsPlugin.getInstance(), toolBar, SMAEditor.EDITOR_ID, "ATS Task Tab");
      createTaskActionBarPulldown(toolBar, toolBar.getParent());

   }

   public void updateExtraInfoLine() throws OseeCoreException {
      if (selectionMetricsMenuItem != null && selectionMetricsMenuItem.getSelection())
         extraInfoLabel.setText(SMAMetrics.getEstRemainMetrics(getTaskXViewer().getSelectedSMAArtifacts(), null,
               getTaskXViewer().getSelectedSMAArtifacts().iterator().next().getManHrsPerDayPreference()));
      else
         extraInfoLabel.setText("");
      extraInfoLabel.getParent().layout();
   }

   public void createTaskActionBarPulldown(final ToolBar toolBar, Composite composite) {
      final ToolItem dropDown = new ToolItem(toolBar, SWT.PUSH);
      dropDown.setImage(AtsPlugin.getInstance().getImage("downTriangle.gif"));
      final Menu menu = new Menu(composite);

      dropDown.addListener(SWT.Selection, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            Rectangle rect = dropDown.getBounds();
            Point pt = new Point(rect.x, rect.y + rect.height);
            pt = toolBar.toDisplay(pt);
            menu.setLocation(pt.x, pt.y);
            menu.setVisible(true);
         }
      });

      selectionMetricsMenuItem = new MenuItem(menu, SWT.CHECK);
      selectionMetricsMenuItem.setText("Show Release Metrics by Selection - Ctrl-X");
      selectionMetricsMenuItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               updateExtraInfoLine();
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });

      filterCompletedMenuItem = new MenuItem(menu, SWT.CHECK);
      filterCompletedMenuItem.setText("Filter Out Completed/Cancelled - Ctrl-F");
      filterCompletedMenuItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleFilterAction();
         }
      });

      try {
         if (iXTaskViewer.isTaskable()) {
            new MenuItem(menu, SWT.SEPARATOR);

            MenuItem item = new MenuItem(menu, SWT.PUSH);
            item.setText("Import Tasks via spreadsheet");
            item.setEnabled(iXTaskViewer.isTasksEditable() && iXTaskViewer.isTaskable());
            item.addSelectionListener(new SelectionAdapter() {
               @Override
               public void widgetSelected(SelectionEvent e) {
                  try {
                     handleImportTasksViaSpreadsheet();
                  } catch (Exception ex) {
                     OSEELog.logException(AtsPlugin.class, ex, true);
                  }
               }
            });

            item = new MenuItem(menu, SWT.PUSH);
            item.setText("Import Tasks via simple list");
            item.setEnabled(iXTaskViewer.isTasksEditable() && iXTaskViewer.isTaskable());
            item.addSelectionListener(new SelectionAdapter() {
               @Override
               public void widgetSelected(SelectionEvent e) {
                  try {
                     handleImportTasksViaList();
                  } catch (Exception ex) {
                     OSEELog.logException(AtsPlugin.class, ex, true);
                  }
               }
            });
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }

   }

   public void loadTable() throws OseeCoreException {
      getTaskXViewer().set(iXTaskViewer.getTaskArtifacts(""));
      taskXViewer.refresh();
   }

   public void handleImportTasksViaList() throws OseeCoreException {
      BlamOperation blamOperation = BlamOperations.getBlamOperation("ImportTasksFromSimpleList");
      ((ImportTasksFromSimpleList) blamOperation).setTaskableStateMachineArtifact((TaskableStateMachineArtifact) iXTaskViewer.getParentSmaMgr().getSma());
      BlamEditor.edit(blamOperation);
      loadTable();
   }

   public void handleImportTasksViaSpreadsheet() throws OseeCoreException {
      BlamOperation blamOperation = BlamOperations.getBlamOperation("ImportTasksFromSpreadsheet");
      ((ImportTasksFromSpreadsheet) blamOperation).setTaskableStateMachineArtifact((TaskableStateMachineArtifact) iXTaskViewer.getParentSmaMgr().getSma());
      BlamEditor.edit(blamOperation);
      loadTable();
   }

   public void handleDeleteTask() {
      final ArrayList<TaskArtifact> items = getSelectedTaskArtifactItems();
      if (items.size() == 0) {
         AWorkbench.popup("ERROR", "No Tasks Selected");
         return;
      }
      StringBuilder builder = new StringBuilder();
      if (items.size() > 15) {
         builder.append("Are You Sure You Wish to Delete " + items.size() + " Tasks (NOTE: workflow will be saved)");
      } else {
         builder.append("Are You Sure You Wish to Delete the Task(s) (NOTE: workflow will be saved):\n\n");
         for (TaskArtifact taskItem : items) {
            builder.append("\"" + taskItem.getDescriptiveName() + "\"\n");
         }
      }
      boolean delete =
            MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Delete Task",
                  builder.toString());
      if (delete) {
         try {
            SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
            // Done for concurrent modification purposes
            ArrayList<TaskArtifact> delItems = new ArrayList<TaskArtifact>();
            delItems.addAll(items);
            for (TaskArtifact taskArt : delItems) {
               SMAEditor.close(taskArt, false);
               taskArt.delete(transaction);
            }
            transaction.execute();
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
      }
   }

   public TaskArtifact handleNewTask() {
      TaskArtifact taskArt = null;
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Create New Task", null,
                  "Enter Task Title/Description", MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
      if (ed.open() == 0) {
         try {
            taskArt = iXTaskViewer.getParentSmaMgr().getTaskMgr().createNewTask(ed.getEntry(), false);
            iXTaskViewer.getEditor().onDirtied();
            taskXViewer.add(taskArt);
            taskXViewer.getTree().setFocus();
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
      }
      return taskArt;
   }

   public ArrayList<TaskArtifact> getSelectedTaskArtifactItems() {
      Iterator<?> i = ((IStructuredSelection) taskXViewer.getSelection()).iterator();
      ArrayList<TaskArtifact> items = new ArrayList<TaskArtifact>();
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof TaskArtifact) items.add((TaskArtifact) obj);
      }
      return items;
   }

   public String toHTML(String labelFont) {
      if (getTaskXViewer().getTree().getItemCount() == 0) return "";
      StringBuffer html = new StringBuffer();
      try {
         html.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "Tasks"));
         html.append(AHTML.startBorderTable(100, Overview.normalColor, ""));
         html.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Title", "State", "POC", "%", "Hrs",
               "Resolution", "ID"}));
         for (TaskArtifact art : iXTaskViewer.getTaskArtifacts("")) {
            SMAManager smaMgr = new SMAManager(art);
            html.append(AHTML.addRowMultiColumnTable(new String[] {art.getDescriptiveName(),
                  art.getSmaMgr().getStateMgr().getCurrentStateName().replaceAll("(Task|State)", ""),
                  smaMgr.getSma().getWorldViewActivePoc(), smaMgr.getSma().getPercentCompleteSMATotal() + "",
                  smaMgr.getSma().getHoursSpentSMATotal() + "",
                  art.getSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(), ""),
                  art.getHumanReadableId()}));
         }
         html.append(AHTML.endBorderTable());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "Task Exception - " + ex.getLocalizedMessage();
      }
      return html.toString();
   }

   /**
    * @return Returns the xViewer.
    */
   public TaskXViewer getTaskXViewer() {
      return taskXViewer;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return taskXViewer.getInput();
   }

   private void setupDragAndDropSupport() {
      DragSource source = new DragSource(taskXViewer.getTree(), DND.DROP_COPY);
      source.setTransfer(new Transfer[] {ArtifactTransfer.getInstance()});
      source.addDragListener(new DragSourceListener() {

         public void dragFinished(DragSourceEvent event) {
         }

         public void dragSetData(DragSourceEvent event) {
            Collection<TaskArtifact> arts = taskXViewer.getSelectedTaskArtifacts();
            if (arts.size() > 0) {
               event.data = new ArtifactData(arts.toArray(new Artifact[arts.size()]), "", SMAEditor.EDITOR_ID);
            }
         }

         public void dragStart(DragSourceEvent event) {
         }
      });

      DropTarget target = new DropTarget(taskXViewer.getTree(), DND.DROP_COPY);
      target.setTransfer(new Transfer[] {FileTransfer.getInstance(), TextTransfer.getInstance(),
            ArtifactTransfer.getInstance()});
      target.addDropListener(new DropTargetAdapter() {

         @Override
         public void drop(DropTargetEvent event) {
            performDrop(event);
         }

         @Override
         public void dragOver(DropTargetEvent event) {
            event.detail = DND.DROP_COPY;
         }

         @Override
         public void dropAccept(DropTargetEvent event) {
         }
      });
   }

   private void performDrop(DropTargetEvent e) {
      if (e.data instanceof ArtifactData) {
         try {
            if (iXTaskViewer.getParentSmaMgr() == null) return;
            if (iXTaskViewer.getParentSmaMgr().getSma() == null) return;
            final Artifact[] artsToRelate = ((ArtifactData) e.data).getArtifacts();
            SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
            for (Artifact art : artsToRelate) {
               if (art instanceof TaskArtifact) {
                  TaskArtifact taskArt = (TaskArtifact) art;
                  // task dropped on same sma as current parent; do nothing
                  if (taskArt.getParentSMA().equals(iXTaskViewer.getParentSmaMgr().getSma())) {
                     return;
                  }
                  if (taskArt.getParentSMA() != null) {
                     taskArt.deleteRelation(AtsRelation.SmaToTask_Sma, taskArt.getParentSMA());
                  }
                  taskArt.addRelation(AtsRelation.SmaToTask_Sma, iXTaskViewer.getParentSmaMgr().getSma());
                  taskArt.persistRelations(transaction);
               }
            }
            transaction.execute();
         } catch (Exception ex) {
            OSEELog.logException(SkynetActivator.class, ex, true);
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ats.IActionable#getActionDescription()
    */
   public String getActionDescription() {
      return null;
   }
}
