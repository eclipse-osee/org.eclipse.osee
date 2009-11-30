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
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.OpenNewAtsTaskEditorAction.IOpenNewAtsTaskEditorHandler;
import org.eclipse.osee.ats.actions.OpenNewAtsTaskEditorSelected.IOpenNewAtsTaskEditorSelectedHandler;
import org.eclipse.osee.ats.actions.TaskAddAction.ITaskAddActionHandler;
import org.eclipse.osee.ats.actions.TaskDeleteAction.ITaskDeleteActionHandler;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.config.AtsBulkLoadCache;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.ats.world.WorldContentProvider;
import org.eclipse.osee.ats.world.WorldLabelProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class TaskComposite extends Composite implements IOpenNewAtsTaskEditorSelectedHandler, ITaskDeleteActionHandler, ITaskAddActionHandler, IOpenNewAtsTaskEditorHandler, IRefreshActionHandler {

   private TaskXViewer taskXViewer;
   private final IXTaskViewer iXTaskViewer;
   protected Label showReleaseMetricsLabel;

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

      try {

         showReleaseMetricsLabel = new Label(this, SWT.NONE);
         showReleaseMetricsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         taskXViewer =
               new TaskXViewer(this, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, iXTaskViewer.getEditor(), this);
         taskXViewer.setTasksEditable(iXTaskViewer.isTasksEditable());
         taskXViewer.setAddDeleteTaskEnabled(iXTaskViewer.isTaskable());
         taskXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

         taskXViewer.setContentProvider(new WorldContentProvider(taskXViewer));
         taskXViewer.setLabelProvider(new WorldLabelProvider(taskXViewer));

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
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public IXTaskViewer getIXTaskViewer() {
      return iXTaskViewer;
   }

   public void add(Collection<TaskArtifact> taskArts) {
      if (getTaskXViewer().getInput() == null) {
         getTaskXViewer().setInput(Collections.singleton(taskArts));
      } else {
         ((Collection) getTaskXViewer().getInput()).addAll(taskArts);
      }
      taskXViewer.refresh();
      taskXViewer.getTree().setFocus();
   }

   public void loadTable() throws OseeCoreException {
      getTaskXViewer().setInput(iXTaskViewer.getTaskArtifacts(""));
      taskXViewer.refresh();
      taskXViewer.getTree().setFocus();
   }

   public void handleDeleteTask() {
      final ArrayList<TaskArtifact> items = getSelectedTaskArtifactItems();
      if (items.size() == 0) {
         AWorkbench.popup("ERROR", "No Tasks Selected");
         return;
      }
      StringBuilder builder = new StringBuilder();
      if (items.size() > 15) {
         builder.append("Are you sure you wish to delete " + items.size() + " Tasks?\n\n");
      } else {
         builder.append("Are you sure you wish to delete ");
         if (items.size() == 1)
            builder.append("this Task?\n\n");
         else
            builder.append("these Tasks?\n\n");
         for (TaskArtifact taskItem : items) {
            builder.append("\"" + taskItem.getName() + "\"\n");
         }

         builder.append("\n\nNote: Workflow will be saved.");

      }
      boolean delete =
            MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Delete Task",
                  builder.toString());
      if (delete) {
         try {
            SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Delete Tasks");
            // Done for concurrent modification purposes
            ArrayList<TaskArtifact> delItems = new ArrayList<TaskArtifact>();
            delItems.addAll(items);
            for (TaskArtifact taskArt : delItems) {
               SMAEditor.close(Collections.singleton(taskArt), false);
               taskArt.deleteAndPersist(transaction);
            }
            transaction.execute();
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }

         //         iXTaskViewer.handleRefreshAction();
      }
   }

   public TaskArtifact handleNewTask() {
      TaskArtifact taskArt = null;
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Create New Task", null,
                  "Enter Task Title/Description", MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
      if (ed.open() == 0) {
         try {
            taskArt = iXTaskViewer.getParentSmaMgr().getTaskMgr().createNewTask(ed.getEntry());
            iXTaskViewer.getEditor().onDirtied();
            add(Collections.singleton(taskArt));
            taskXViewer.getTree().setFocus();
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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
            html.append(AHTML.addRowMultiColumnTable(new String[] {art.getName(),
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
            SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Drop Add Tasks");
            for (Artifact art : artsToRelate) {
               if (art instanceof TaskArtifact) {
                  TaskArtifact taskArt = (TaskArtifact) art;
                  // task dropped on same sma as current parent; do nothing
                  if (taskArt.getParentSMA().equals(iXTaskViewer.getParentSmaMgr().getSma())) {
                     return;
                  }
                  if (taskArt.getParentSMA() != null) {
                     taskArt.deleteRelation(AtsRelationTypes.SmaToTask_Sma, taskArt.getParentSMA());
                  }
                  taskArt.addRelation(AtsRelationTypes.SmaToTask_Sma, iXTaskViewer.getParentSmaMgr().getSma());
                  taskArt.persist(transaction);
               }
            }
            transaction.execute();
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   public String getActionDescription() {
      return null;
   }

   @Override
   public void refreshActionHandler() {
      try {
         if (iXTaskViewer.isRefreshActionHandled()) {
            iXTaskViewer.handleRefreshAction();
         } else {
            loadTable();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public CustomizeData getCustomizeDataCopy() throws OseeCoreException {
      return taskXViewer.getCustomizeMgr().generateCustDataFromTable();
   }

   @Override
   public ITaskEditorProvider getTaskEditorProviderCopy() throws OseeCoreException {
      return ((TaskEditorInput) ((TaskEditor) iXTaskViewer.getEditor()).getEditorInput()).getItaskEditorProvider().copyProvider();
   }

   @Override
   public ArrayList<? extends Artifact> getSelectedArtifacts() throws OseeCoreException {
      return getSelectedTaskArtifactItems();
   }

   @Override
   public void taskAddActionHandler() {
      handleNewTask();
   }

   @Override
   public void taskDeleteActionHandler() {
      handleDeleteTask();
   }
}
