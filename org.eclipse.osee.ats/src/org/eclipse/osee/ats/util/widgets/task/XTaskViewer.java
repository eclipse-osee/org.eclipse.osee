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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.ats.util.SMAMetrics;
import org.eclipse.osee.ats.util.Import.TaskImportWizard;
import org.eclipse.osee.ats.world.WorldArtifactItem;
import org.eclipse.osee.ats.world.WorldCompletedFilter;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent.EventData;
import org.eclipse.osee.framework.skynet.core.relation.RelationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationPersistenceManager.Direction;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
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
public class XTaskViewer extends XWidget implements IEventReceiver, IActionable {

   private TaskXViewer xViewer;
   private ToolItem upItem, downItem, currentStateFilterItem;
   private MenuItem currentStateFilterMenuItem, filterCompletedMenuItem, selectionMetricsMenuItem;
   private TaskArtifact selected;
   private IXTaskViewer iXTaskViewer;
   private SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private TaskCurrentStateFilter currentStateFilter = null;
   private Label extraInfoLabel;
   private WorldCompletedFilter worldCompletedFilter = new WorldCompletedFilter();

   /**
    * @param label
    */
   public XTaskViewer(IXTaskViewer iXTaskViewer) {
      super(iXTaskViewer.getTabName());
      this.iXTaskViewer = iXTaskViewer;
      eventManager.register(RemoteTransactionEvent.class, this);
      eventManager.register(LocalTransactionEvent.class, this);
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

      createTaskActionBar(mainComp);

      try {
         xViewer =
               new TaskXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, iXTaskViewer.getEditor(),
                     iXTaskViewer.isUsingTaskResolutionOptions(), iXTaskViewer.getResOptions(), this);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
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

   public void createTaskActionBar(Composite parent) {

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
            updateCurrentStateFilter();
         }
      });

      if (iXTaskViewer.isTaskable()) {

         upItem = new ToolItem(toolBar, SWT.PUSH);
         upItem.setImage(AtsPlugin.getInstance().getImage("up.gif"));
         upItem.setToolTipText("Up; Disabled if sorted by Column");
         // DON Add ability to move tasks up/down
         // upItem.setEnabled(smaMgr.isTaskable());
         upItem.setEnabled(false);
         upItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
               handleMoveSelection(Direction.Back);
            }
         });

         downItem = new ToolItem(toolBar, SWT.PUSH);
         downItem.setImage(AtsPlugin.getInstance().getImage("down.gif"));
         downItem.setToolTipText("Down; Disabled if sorted by Column");
         // DON Add ability to move tasks up/down
         // downItem.setEnabled(smaMgr.isTaskable());
         downItem.setEnabled(false);
         downItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
               handleMoveSelection(Direction.Forward);
            }
         });

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
            loadTable();
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

   public void updateCurrentStateFilter() {
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
            updateCurrentStateFilter();
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

      if (iXTaskViewer.isTaskable()) {

         MenuItem item = new MenuItem(menu, SWT.PUSH);
         item.setText("Import Tasks via spreadsheet");
         item.setEnabled(iXTaskViewer.isTasksEditable() && iXTaskViewer.isTaskable());
         item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
               handleImportTasksViaSpreadsheet();
            }
         });

         item = new MenuItem(menu, SWT.PUSH);
         item.setText("Import Tasks via simple list");
         item.setEnabled(iXTaskViewer.isTasksEditable() && iXTaskViewer.isTaskable());
         item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
               handleImportTasksViaList();
            }
         });
      }

   }

   public void loadTable() {
      try {
         getXViewer().set(iXTaskViewer.getTaskArtifacts(""));
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }

      xViewer.refresh();
   }

   public void handleImportTasksViaList() {
      final EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Create Tasks", null,
                  "Enter task titles, one per line.\nNOTE: For more complex import use import via spreadsheet.",
                  MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
      ed.setFillVertically(true);
      if (ed.open() == 0) {
         try {
            AbstractSkynetTxTemplate txWrapper =
                  new AbstractSkynetTxTemplate(BranchPersistenceManager.getInstance().getAtsBranch()) {
                     @Override
                     protected void handleTxWork() throws Exception {
                        for (String str : ed.getEntry().split("\n")) {
                           str = str.replaceAll("\r", "");
                           if (!str.equals("")) {
                              TaskArtifact taskArt =
                                    iXTaskViewer.getParentSmaMgr().getTaskMgr().createNewTask(str, true);
                              taskArt.persist(true);
                           }
                        }
                     }
                  };
            txWrapper.execute();
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
      }
   }

   public void handleImportTasksViaSpreadsheet() {
      TaskImportWizard actionWizard = new TaskImportWizard();
      actionWizard.setHrid(iXTaskViewer.getParentSmaMgr().getSma().getHumanReadableId());
      WizardDialog dialog =
            new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), actionWizard);
      dialog.create();
      dialog.open();
   }

   public void handleDeleteTask() {
      final ArrayList<TaskArtifactItem> items = getSelectedTaskArtifactItems();
      if (items.size() == 0) {
         AWorkbench.popup("ERROR", "No Tasks Selected");
         return;
      }
      StringBuilder builder = new StringBuilder();
      for (TaskArtifactItem taskItem : items) {
         builder.append("\"" + taskItem.getTaskArtifact().getDescriptiveName() + "\"\n");
      }
      boolean delete =
            MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Delete Task",
                  "Are You Sure You Wish to Delete the Task(s):\n\n" + builder.toString());
      if (delete) {
         try {
            AbstractSkynetTxTemplate txWrapper =
                  new AbstractSkynetTxTemplate(BranchPersistenceManager.getInstance().getAtsBranch()) {
                     @Override
                     protected void handleTxWork() throws Exception {
                        // Done for concurrent modification purposes
                        ArrayList<TaskArtifactItem> delItems = new ArrayList<TaskArtifactItem>();
                        delItems.addAll(items);
                        for (TaskArtifactItem taskItem : delItems) {
                           SMAEditor.close(taskItem.getTaskArtifact(), false);
                           TaskArtifact taskArt = taskItem.getTaskArtifact();
                           taskArt.delete();
                        }
                        xViewer.removeItems(delItems);
                        xViewer.refresh();
                     }
                  };
            txWrapper.execute();
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
         } catch (SQLException ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
      }
      return taskArt;
   }

   public ArrayList<TaskArtifactItem> getSelectedTaskArtifactItems() {
      Iterator<?> i = ((IStructuredSelection) xViewer.getSelection()).iterator();
      ArrayList<TaskArtifactItem> items = new ArrayList<TaskArtifactItem>();
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof TaskArtifactItem) items.add((TaskArtifactItem) obj);
      }
      return items;
   }

   public void storeSelection() {
      // Store selected so can re-select after event re-draw
      if (xViewer.getSelectedTaskArtifactItems().size() > 0) selected =
            xViewer.getSelectedTaskArtifactItems().iterator().next().getTaskArtifact();
   }

   public void restoreSelection() {
      if (selected != null) {
         for (WorldArtifactItem item : xViewer.getRootSet()) {
            if (item.getArtifact().equals(selected)) {
               ArrayList<TaskArtifactItem> selected = new ArrayList<TaskArtifactItem>();
               selected.add((TaskArtifactItem) item);
               xViewer.setSelection(new StructuredSelection(selected.toArray(new Object[selected.size()])));
            }
         }
      }
   }

   public void handleMoveSelection(Direction dir) {
      storeSelection();
      if (xViewer.getSelectedTaskArtifactItems().size() != 1) {
         AWorkbench.popup("ERROR", "Must select single item to move");
         return;
      }
      TaskArtifact taskArt = xViewer.getSelectedTaskArtifactItems().iterator().next().getTaskArtifact();
      if (taskArt != null) {
         try {
            RelationPersistenceManager.getInstance().moveObjectB(iXTaskViewer.getParentSmaMgr().getSma(), taskArt,
                  RelationSide.SmaToTask_Task, dir);
            refresh();
         } catch (SQLException ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
      }
   }

   @Override
   public Control getControl() {
      return xViewer.getTree();
   }

   @Override
   public void dispose() {
      eventManager.unRegisterAll(this);
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
   public boolean isValid() {
      return false;
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
                  art.getCurrentStateName().replaceAll("(Task|State)", ""), smaMgr.getAssigneesWasIsStr(),
                  smaMgr.getSma().getWorldViewTotalPercentComplete() + "",
                  smaMgr.getSma().getWorldViewTotalHoursSpent() + "",
                  art.getSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName()),
                  art.getHumanReadableId()}));
         }
         html.append(AHTML.endBorderTable());
      } catch (SQLException ex) {
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

   public void onEvent(final Event event) {
      if (xViewer == null || xViewer.getTree() == null || xViewer.getTree().isDisposed()) return;

      if (event instanceof TransactionEvent) {
         if (iXTaskViewer.getParentSmaMgr() != null) {
            EventData ed = ((TransactionEvent) event).getEventData(iXTaskViewer.getParentSmaMgr().getSma());
            if (ed.isHasEvent() && ed.isRelChange())
               loadTable();
            else
               refresh();
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
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
         if (iXTaskViewer.getParentSmaMgr().getSma() == null) return;
         final Artifact[] artsToRelate = ((ArtifactData) e.data).getArtifacts();
         try {
            AbstractSkynetTxTemplate txWrapper =
                  new AbstractSkynetTxTemplate(BranchPersistenceManager.getInstance().getAtsBranch()) {
                     @Override
                     protected void handleTxWork() throws Exception {
                        for (Artifact art : artsToRelate) {
                           if (art instanceof TaskArtifact) {
                              TaskArtifact taskArt = (TaskArtifact) art;
                              if (taskArt.getParentSMA() != null) taskArt.unrelate(RelationSide.SmaToTask_Sma,
                                    taskArt.getParentSMA(), true);
                              taskArt.relate(RelationSide.SmaToTask_Sma, iXTaskViewer.getParentSmaMgr().getSma(), true);
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
