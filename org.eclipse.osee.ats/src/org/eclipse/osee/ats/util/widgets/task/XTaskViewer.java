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

package org.eclipse.osee.ats.util.widgets.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.operation.ImportTasksFromSimpleList;
import org.eclipse.osee.ats.operation.ImportTasksFromSpreadsheet;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.ats.util.SMAMetrics;
import org.eclipse.osee.ats.world.WorldCompletedFilter;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.blam.BlamOperations;
import org.eclipse.osee.framework.ui.skynet.blam.WorkflowEditor;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
public class XTaskViewer extends XWidget implements IActionable {

   private TaskXViewer xViewer;
   private ToolItem currentStateFilterItem;
   private MenuItem currentStateFilterMenuItem, filterCompletedMenuItem, selectionMetricsMenuItem;
   private IXTaskViewer iXTaskViewer;
   private TaskCurrentStateFilter currentStateFilter = null;
   private Label extraInfoLabel;
   private WorldCompletedFilter worldCompletedFilter = new WorldCompletedFilter();

   /**
    * @param label
    * @throws Exception
    */
   public XTaskViewer(IXTaskViewer iXTaskViewer) throws OseeCoreException, SQLException {
      super(iXTaskViewer.getTabName());
      this.iXTaskViewer = iXTaskViewer;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#createWidgets(org.eclipse.swt.widgets.Composite,
    *      int)
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {

      Composite mainComp = new Composite(parent, SWT.BORDER);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
      mainComp.setLayout(ALayout.getZeroMarginLayout());
      if (toolkit != null) toolkit.paintBordersFor(mainComp);

      try {
         createTaskActionBar(mainComp);

         xViewer =
               new TaskXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, iXTaskViewer.getEditor(),
                     iXTaskViewer.getResOptions(), this);
         xViewer.setTasksEditable(iXTaskViewer.isTasksEditable());
         xViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

         xViewer.setContentProvider(new TaskContentProvider(xViewer));
         xViewer.setLabelProvider(new TaskLabelProvider(xViewer));
         xViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
               updateExtraInfoLine();
            }
         });
         xViewer.getTree().addKeyListener(new KeyListener() {
            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
             */
            public void keyPressed(KeyEvent event) {
               // if CTRL key is already pressed
               if ((event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
                  if (event.keyCode == 'a') {
                     xViewer.getTree().setSelection(xViewer.getTree().getItems());
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
               // System.out.println("keypressed " + event.keyCode);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
             */
            public void keyReleased(KeyEvent event) {
            }
         });

         if (toolkit != null) toolkit.adapt(xViewer.getStatusLabel(), false, false);

         Tree tree = xViewer.getTree();
         GridData gridData = new GridData(GridData.FILL_BOTH);
         gridData.heightHint = 100;
         tree.setLayout(ALayout.getZeroMarginLayout());
         tree.setLayoutData(gridData);
         tree.setHeaderVisible(true);
         tree.setLinesVisible(true);
         if (toolkit != null) toolkit.adapt(tree);
         updateCurrentStateFilter();
         setupDragAndDropSupport();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   public void handleFilterAction() {
      if (filterCompletedMenuItem.getSelection()) {
         xViewer.addFilter(worldCompletedFilter);
      } else {
         xViewer.removeFilter(worldCompletedFilter);
      }
      updateExtendedStatusString();
   }

   public void updateExtendedStatusString() {
      String str = "";
      if (filterCompletedMenuItem != null && filterCompletedMenuItem.getSelection()) {
         str += " Complete/Cancel FILTERED - ";
      }
      if (currentStateFilterItem != null && currentStateFilterItem.getSelection()) {
         str += " Curr State FILTERED - ";
      }
      xViewer.setExtendedStatusString(str);
      xViewer.refresh();
   }

   public void createTaskActionBar(Composite parent) throws IllegalArgumentException, Exception {

      // Button composite for state transitions, etc
      Composite bComp = new Composite(parent, SWT.NONE);
      // bComp.setBackground(mainSComp.getDisplay().getSystemColor(SWT.COLOR_CYAN));
      bComp.setLayout(new GridLayout(2, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite leftComp = new Composite(bComp, SWT.NONE);
      leftComp.setLayout(new GridLayout());
      leftComp.setLayoutData(new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL));

      extraInfoLabel = new Label(leftComp, SWT.NONE);
      extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite rightComp = new Composite(bComp, SWT.NONE);
      rightComp.setLayout(new GridLayout());
      rightComp.setLayoutData(new GridData(GridData.END));

      ToolBar toolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);
      ToolItem item = null;

      currentStateFilterItem = new ToolItem(toolBar, SWT.CHECK);
      currentStateFilterItem.setImage(AtsPlugin.getInstance().getImage("currentState.gif"));
      currentStateFilterItem.setToolTipText("Filter by Current State.");
      currentStateFilterItem.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            try {
               updateCurrentStateFilter();
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });

      if (iXTaskViewer.isTaskable()) {

         item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(AtsPlugin.getInstance().getImage("newTask.gif"));
         item.setToolTipText("New Task");
         item.setEnabled(iXTaskViewer.isTasksEditable() && iXTaskViewer.isTaskable());
         item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
               handleNewTask();
            }
         });

         item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(AtsPlugin.getInstance().getImage("redRemove.gif"));
         item.setToolTipText("Delete Task");
         item.setEnabled(iXTaskViewer.isTasksEditable() && iXTaskViewer.isTaskable());
         item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
               handleDeleteTask();
            }
         });

      }
      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(AtsPlugin.getInstance().getImage("refresh.gif"));
      item.setToolTipText("Refresh Tasks");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            try {
               loadTable();
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("customize.gif"));
      item.setToolTipText("Customize Table");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            xViewer.getCustomize().handleTableCustomization();
         }
      });

      OseeAts.addButtonToEditorToolBar(this, AtsPlugin.getInstance(), toolBar, SMAEditor.EDITOR_ID, "ATS Task Tab");
      createTaskActionBarPulldown(toolBar, rightComp);

   }

   public void updateExtraInfoLine() {
      if (selectionMetricsMenuItem != null && selectionMetricsMenuItem.getSelection())
         extraInfoLabel.setText(SMAMetrics.getEstRemainMetrics(getXViewer().getSelectedSMAArtifacts()));
      else
         extraInfoLabel.setText("");
      extraInfoLabel.getParent().layout();
   }

   public void updateCurrentStateFilter() throws OseeCoreException, SQLException {
      if (currentStateFilterItem != null && currentStateFilterItem.getSelection()) {
         currentStateFilter = new TaskCurrentStateFilter(iXTaskViewer.getCurrentStateName());
         getXViewer().addFilter(currentStateFilter);
      } else {
         if (currentStateFilter != null) getXViewer().removeFilter(currentStateFilter);
         currentStateFilter = null;
      }
      if (currentStateFilterMenuItem != null) currentStateFilterMenuItem.setSelection(currentStateFilterItem.getSelection());
      updateExtendedStatusString();
   }

   public void createTaskActionBarPulldown(final ToolBar toolBar, Composite composite) {
      final ToolItem dropDown = new ToolItem(toolBar, SWT.DROP_DOWN);
      final Menu menu = new Menu(composite);

      dropDown.addListener(SWT.Selection, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            if (event.detail == SWT.ARROW) {
               Rectangle rect = dropDown.getBounds();
               Point pt = new Point(rect.x, rect.y + rect.height);
               pt = toolBar.toDisplay(pt);
               menu.setLocation(pt.x, pt.y);
               menu.setVisible(true);
            }
         }
      });

      currentStateFilterMenuItem = new MenuItem(menu, SWT.CHECK);
      currentStateFilterMenuItem.setText("Filter by Current State");
      currentStateFilterMenuItem.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            currentStateFilterItem.setSelection(!currentStateFilterItem.getSelection());
            try {
               updateCurrentStateFilter();
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });

      selectionMetricsMenuItem = new MenuItem(menu, SWT.CHECK);
      selectionMetricsMenuItem.setText("Show Release Metrics by Selection - Ctrl-X");
      selectionMetricsMenuItem.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            updateExtraInfoLine();
         }
      });

      filterCompletedMenuItem = new MenuItem(menu, SWT.CHECK);
      filterCompletedMenuItem.setText("Filter Out Completed/Cancelled - Ctrl-F");
      filterCompletedMenuItem.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            handleFilterAction();
         }
      });

      try {
         if (iXTaskViewer.isTaskable()) {

            MenuItem item = new MenuItem(menu, SWT.PUSH);
            item.setText("Import Tasks via spreadsheet");
            item.setEnabled(iXTaskViewer.isTasksEditable() && iXTaskViewer.isTaskable());
            item.addSelectionListener(new SelectionAdapter() {
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

   public void loadTable() throws OseeCoreException, SQLException {
      try {
         getXViewer().set(iXTaskViewer.getTaskArtifacts(""));
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }

      xViewer.refresh();
   }

   public void handleImportTasksViaList() throws OseeCoreException, SQLException {
      BlamOperation blamOperation = BlamOperations.getBlamOperation("ImportTasksFromSimpleList");
      ((ImportTasksFromSimpleList) blamOperation).setTaskableStateMachineArtifact((TaskableStateMachineArtifact) iXTaskViewer.getParentSmaMgr().getSma());
      WorkflowEditor.edit(blamOperation);
      loadTable();
   }

   public void handleImportTasksViaSpreadsheet() throws OseeCoreException, SQLException {
      BlamOperation blamOperation = BlamOperations.getBlamOperation("ImportTasksFromSpreadsheet");
      ((ImportTasksFromSpreadsheet) blamOperation).setTaskableStateMachineArtifact((TaskableStateMachineArtifact) iXTaskViewer.getParentSmaMgr().getSma());
      WorkflowEditor.edit(blamOperation);
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
         builder.append("Are You Sure You Wish to Delete " + items.size() + " Tasks");
      } else {
         builder.append("Are You Sure You Wish to Delete the Task(s):\n\n");
         for (TaskArtifact taskItem : items) {
            builder.append("\"" + taskItem.getDescriptiveName() + "\"\n");
         }
      }
      boolean delete =
            MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Delete Task",
                  builder.toString());
      if (delete) {
         try {
            AbstractSkynetTxTemplate txWrapper = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
               @Override
               protected void handleTxWork() throws OseeCoreException, SQLException {
                  // Done for concurrent modification purposes
                  ArrayList<TaskArtifact> delItems = new ArrayList<TaskArtifact>();
                  delItems.addAll(items);
                  for (TaskArtifact taskArt : delItems) {
                     SMAEditor.close(taskArt, false);
                     taskArt.delete();
                  }
               }
            };
            txWrapper.execute();
            if (!xViewer.getTree().isDisposed()) {
               xViewer.remove(items);
               xViewer.refresh();
            }
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
            xViewer.add(taskArt);
            xViewer.getTree().setFocus();
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
      }
      return taskArt;
   }

   public ArrayList<TaskArtifact> getSelectedTaskArtifactItems() {
      Iterator<?> i = ((IStructuredSelection) xViewer.getSelection()).iterator();
      ArrayList<TaskArtifact> items = new ArrayList<TaskArtifact>();
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof TaskArtifact) items.add((TaskArtifact) obj);
      }
      return items;
   }

   @Override
   public Control getControl() {
      return xViewer.getTree();
   }

   @Override
   public void dispose() {
      xViewer.dispose();
   }

   @Override
   public void setFocus() {
      xViewer.getTree().setFocus();
   }

   public void refresh() {
      xViewer.refresh();
   }

   @Override
   public Result isValid() {
      return Result.TrueResult;
   }

   @Override
   public void setXmlData(String str) {
   }

   @Override
   public String getXmlData() {
      return null;
   }

   @Override
   public String toHTML(String labelFont) {
      if (getXViewer().getTree().getItemCount() == 0) return "";
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
                  smaMgr.getAssigneesWasIsStr(), smaMgr.getSma().getPercentCompleteSMATotal() + "",
                  smaMgr.getSma().getHoursSpentSMATotal() + "",
                  art.getSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(), ""),
                  art.getHumanReadableId()}));
         }
         html.append(AHTML.endBorderTable());
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return "Task Exception - " + ex.getLocalizedMessage();
      }
      return html.toString();
   }

   @Override
   public String getReportData() {
      return null;
   }

   /**
    * @return Returns the xViewer.
    */
   public TaskXViewer getXViewer() {
      return xViewer;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return xViewer.getInput();
   }

   private void setupDragAndDropSupport() {
      DragSource source = new DragSource(xViewer.getTree(), DND.DROP_COPY);
      source.setTransfer(new Transfer[] {ArtifactTransfer.getInstance()});
      source.addDragListener(new DragSourceListener() {

         public void dragFinished(DragSourceEvent event) {
            refresh();
         }

         public void dragSetData(DragSourceEvent event) {
            Collection<TaskArtifact> arts = xViewer.getSelectedTaskArtifacts();
            if (arts.size() > 0) {
               event.data = new ArtifactData(arts.toArray(new Artifact[arts.size()]), "", SMAEditor.EDITOR_ID);
            }
         }

         public void dragStart(DragSourceEvent event) {
         }
      });

      DropTarget target = new DropTarget(xViewer.getTree(), DND.DROP_COPY);
      target.setTransfer(new Transfer[] {FileTransfer.getInstance(), TextTransfer.getInstance(),
            ArtifactTransfer.getInstance()});
      target.addDropListener(new DropTargetAdapter() {

         public void drop(DropTargetEvent event) {
            performDrop(event);
         }

         public void dragOver(DropTargetEvent event) {
            event.detail = DND.DROP_COPY;
         }

         public void dropAccept(DropTargetEvent event) {
         }
      });
   }

   private void performDrop(DropTargetEvent e) {
      if (e.data instanceof ArtifactData) {
         try {
            if (iXTaskViewer.getParentSmaMgr().getSma() == null) return;
            final Artifact[] artsToRelate = ((ArtifactData) e.data).getArtifacts();
            AbstractSkynetTxTemplate txWrapper = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
               @Override
               protected void handleTxWork() throws OseeCoreException, SQLException {
                  for (Artifact art : artsToRelate) {
                     if (art instanceof TaskArtifact) {
                        TaskArtifact taskArt = (TaskArtifact) art;
                        if (taskArt.getParentSMA() != null) {
                           taskArt.deleteRelation(AtsRelation.SmaToTask_Sma, taskArt.getParentSMA());
                        }
                        taskArt.addRelation(AtsRelation.SmaToTask_Sma, iXTaskViewer.getParentSmaMgr().getSma());
                        taskArt.persistRelations();
                     }
                  }
               }
            };
            txWrapper.execute();
         } catch (Exception ex) {
            OSEELog.logException(SkynetActivator.class, ex, true);
         }
      }
      refresh();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ats.IActionable#getActionDescription()
    */
   public String getActionDescription() {
      return null;
   }
}
