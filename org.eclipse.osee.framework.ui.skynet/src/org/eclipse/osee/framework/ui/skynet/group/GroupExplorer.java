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

package org.eclipse.osee.framework.ui.skynet.group;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.DefaultBranchChangedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.event.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteBranchEvent;
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
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.DefineHttpServerRequest;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.branch.BranchLabelProvider;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.StatusLineContributionItem;

/**
 * @author Donald G. Dunne
 */
public class GroupExplorer extends ViewPart implements IEventReceiver, IActionable {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(GroupExplorer.class);
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.group.GroupExplorer";
   private static final String ROOT_GUID = "group.explorer.last.root_guid";
   private TreeViewer treeViewer;
   private Artifact rootArt;
   private UniversalGroupItem rootItem;
   private SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private static final BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();
   private static UniversalGroupItem selected;
   private StatusLineContributionItem branchStatusItem;

   private Branch branch;

   public GroupExplorer() {
      branchStatusItem = new StatusLineContributionItem("skynet.branch", true, 30);
      branchStatusItem.setImage(SkynetGuiPlugin.getInstance().getImage("branch.gif"));
      branchStatusItem.setToolTipText("The branch that the artifacts in the explorer are from.");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
    */
   public void createPartControl(Composite parent) {

      try {
         ConnectionHandler.getConnection();
      } catch (Exception ex) {
         (new Label(parent, SWT.NONE)).setText("  DB Connection Unavailable");
         return;
      }

      GridData gridData = new GridData();
      gridData.verticalAlignment = GridData.FILL;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      gridData.grabExcessHorizontalSpace = true;

      GridLayout gridLayout = new GridLayout(1, false);
      gridData.heightHint = 1000;
      gridData.widthHint = 1000;

      parent.setLayout(gridLayout);
      parent.setLayoutData(gridData);

      if (!ConnectionHandler.isConnected()) {
         (new Label(parent, SWT.NONE)).setText("DB Connection Unavailable");
         return;
      }

      getViewSite().getActionBars().getStatusLineManager().add(branchStatusItem);

      treeViewer = new TreeViewer(parent);
      treeViewer.setContentProvider(new GroupContentProvider(this));
      treeViewer.setLabelProvider(new GroupLabelProvider());
      treeViewer.setUseHashlookup(true);
      treeViewer.getTree().addListener(SWT.MouseDoubleClick, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            handleDoubleClick();
         }
      });
      treeViewer.getControl().setLayoutData(gridData);
      treeViewer.getTree().addKeyListener(new keySelectedListener());
      treeViewer.getTree().addListener(SWT.MouseUp, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            if (event.button == 3) getPopupMenu().setVisible(true);
         }
      });
      treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
          */
         public void selectionChanged(SelectionChangedEvent event) {
            updateStatusLabel();
         }
      });

      eventManager.register(LocalTransactionEvent.class, this);
      eventManager.register(RemoteTransactionEvent.class, this);
      eventManager.register(LocalBranchEvent.class, this);
      eventManager.register(RemoteBranchEvent.class, this);
      eventManager.register(DefaultBranchChangedEvent.class, this);

      getSite().setSelectionProvider(treeViewer);
      addExploreSelection();
      setupDragAndDropSupport();
      parent.layout();

      createActions();
   }

   private void handleDoubleClick() {
      UniversalGroupItem item = getSelectedItem();
      if (item != null) {
         RendererManager.getInstance().editInJob(item.getArtifact());
      }
   }

   protected void createActions() {

      Action refreshAction = new Action("Refresh", Action.AS_PUSH_BUTTON) {

         public void run() {
            refresh();
         }
      };
      refreshAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("refresh.gif"));
      refreshAction.setToolTipText("Refresh");

      Action upAction = new Action("Up", Action.AS_PUSH_BUTTON) {

         public void run() {
            handleMoveSelection(Direction.Back);
         }
      };
      upAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("up.gif"));
      upAction.setToolTipText("Up");

      Action downAction = new Action("Down", Action.AS_PUSH_BUTTON) {

         public void run() {
            handleMoveSelection(Direction.Forward);
         }
      };
      downAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("down.gif"));
      downAction.setToolTipText("Down");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(upAction);
      toolbarManager.add(downAction);
      toolbarManager.add(refreshAction);

      // IMenuManager manager = getViewSite().getActionBars().getMenuManager();

      OseeAts.addBugToViewToolbar(this, this, SkynetGuiPlugin.getInstance(), VIEW_ID, "Group Explorer");
   }

   private Menu getPopupMenu() {

      Menu previewMenu = new Menu(treeViewer.getTree().getParent());

      MenuItem item = new MenuItem(previewMenu, SWT.PUSH);
      item.setText("&Remove from Group");
      item.setEnabled(isOnlyArtifactsSelected());
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            handleRemoveFromGroup();
         }
      });

      item = new MenuItem(previewMenu, SWT.PUSH);
      item.setText("&Delete Group");
      item.setEnabled(isOnlyGroupsSelected());
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            handleDeleteGroup();
         }
      });

      item = new MenuItem(previewMenu, SWT.PUSH);
      item.setText("&New Group");
      item.setEnabled(true);
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            handleNewGroup();
         }
      });

      new MenuItem(previewMenu, SWT.SEPARATOR);

      item = new MenuItem(previewMenu, SWT.PUSH);
      item.setText("&Select All\tCtrl+A");
      item.addListener(SWT.Selection, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            treeViewer.getTree().selectAll();
         }
      });

      item = new MenuItem(previewMenu, SWT.PUSH);
      item.setText("Expand All\tCtrl+X");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            expandAll((IStructuredSelection) treeViewer.getSelection());
         }
      });
      return previewMenu;
   }

   private void handleNewGroup() {
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Create New Group", null, "Enter Group Name",
                  MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
      if (ed.open() == 0) {
         try {
            UniversalGroup.addGroup(ed.getEntry());
         } catch (IllegalArgumentException ex) {
            AWorkbench.popup("ERROR", "Error creating group\n\n" + ex.getLocalizedMessage());
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            return;
         }
         treeViewer.refresh();
      }
   }

   private void handleRemoveFromGroup() {
      final List<UniversalGroupItem> items = getSelectedUniversalGroupItems();
      if (items.size() == 0) {
         AWorkbench.popup("ERROR", "No Items Selected");
         return;
      }
      String names = "";
      for (UniversalGroupItem item : items)
         if (item.isUniversalGroup()) names += String.format("%s\n", item.getArtifact().getDescriptiveName());
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Remove From Group",
            "Remove From Group - (Artifacts will not be deleted)\n\n" + names + "\nAre you sure?")) {
         AbstractSkynetTxTemplate unrelateTx = new AbstractSkynetTxTemplate(branch) {
            @Override
            protected void handleTxWork() throws Exception {
               for (UniversalGroupItem item : items) {
                  item.getArtifact().unrelate(RelationSide.UNIVERSAL_GROUPING__GROUP,
                        item.getParentItem().getArtifact(), true);
               }
            }
         };

         try {
            unrelateTx.execute();
         } catch (Exception ex) {
            OSEELog.logException(getClass(), ex, true);
         }
      }
   }

   private void handleDeleteGroup() {
      final ArrayList<UniversalGroupItem> items = getSelectedUniversalGroupItems();
      if (items.size() == 0) {
         AWorkbench.popup("ERROR", "No Groups Selected");
         return;
      }
      String names = "";
      for (UniversalGroupItem item : items)
         if (item.isUniversalGroup()) names += String.format("%s\n", item.getArtifact().getDescriptiveName());
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Delete Groups",
            "Delete Groups - (Contained Artifacts will not be deleted)\n\n" + names + "\nAre you sure?")) {

         AbstractSkynetTxTemplate deleteUniversalGroupTx = new AbstractSkynetTxTemplate(branch) {
            @Override
            protected void handleTxWork() throws Exception {
               for (UniversalGroupItem item : items) {
                  item.getArtifact().delete();
               }
            }
         };

         try {
            deleteUniversalGroupTx.execute();
            refresh();
         } catch (Exception ex) {
            OSEELog.logException(getClass(), ex, true);
         }
      }
   }

   public void storeSelection() {
      // Store selected so can re-select after event re-draw
      selected = getSelectedItem();
   }

   public void restoreSelection() {
      if (selected != null) {
         UniversalGroupItem selItem = rootItem.getItem(selected.getArtifact());
         ArrayList<UniversalGroupItem> selected = new ArrayList<UniversalGroupItem>();
         selected.add(selItem);
         treeViewer.setSelection(new StructuredSelection(selected.toArray(new Object[selected.size()])));
      }
   }

   public void handleMoveSelection(Direction dir) {
      storeSelection();
      UniversalGroupItem selItem = getSelectedItem();
      if (selItem != null) {
         try {
            RelationPersistenceManager.getInstance().moveObjectB(selItem.getParentItem().getArtifact(),
                  selItem.getArtifact(), RelationSide.UNIVERSAL_GROUPING__MEMBERS, dir);
         } catch (SQLException ex) {
            OSEELog.logException(SkynetActivator.class, ex, true);
         }
      }
   }

   public UniversalGroupItem getSelectedItem() {
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      Iterator<?> itemsIter = selection.iterator();
      if (itemsIter.hasNext()) return (UniversalGroupItem) itemsIter.next();
      return null;
   }

   private void setupDragAndDropSupport() {
      DragSource source = new DragSource(treeViewer.getTree(), DND.DROP_COPY);
      source.setTransfer(new Transfer[] {ArtifactTransfer.getInstance()});
      source.addDragListener(new DragSourceListener() {

         public void dragFinished(DragSourceEvent event) {
         }

         public void dragSetData(DragSourceEvent event) {
            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            Iterator<?> i = selection.iterator();

            String item = "work";

            if (i.hasNext()) {
               Artifact artifact = (Artifact) i.next();
               item = DefineHttpServerRequest.getInstance().getUrl(artifact);
            }
            Object[] objects = selection.toArray();
            Artifact[] artifacts = new Artifact[objects.length];

            for (int index = 0; index < objects.length; index++)
               artifacts[index] = (Artifact) objects[index];

            event.data = new ArtifactData(artifacts, item, VIEW_ID);
         }

         public void dragStart(DragSourceEvent event) {
         }
      });

      DropTarget target = new DropTarget(treeViewer.getTree(), DND.DROP_COPY);
      target.setTransfer(new Transfer[] {FileTransfer.getInstance(), TextTransfer.getInstance(),
            ArtifactTransfer.getInstance()});
      target.addDropListener(new DropTargetAdapter() {

         public void drop(DropTargetEvent event) {
            performDrop(event);
         }

         public void dragOver(DropTargetEvent event) {
            TreeItem selected = treeViewer.getTree().getItem(treeViewer.getTree().toControl(event.x, event.y));

            if (selected != null && selected.getData() instanceof UniversalGroupItem) {
               if (((UniversalGroupItem) selected.getData()).isUniversalGroup()) event.detail = DND.DROP_COPY;
            } else
               event.detail = DND.DROP_NONE;
         }

         public void dropAccept(DropTargetEvent event) {
         }
      });
   }

   private void performDrop(DropTargetEvent e) {
      TreeItem selected = treeViewer.getTree().getItem(treeViewer.getTree().toControl(e.x, e.y));

      if (selected.getData() instanceof UniversalGroupItem) {
         final UniversalGroupItem item = (UniversalGroupItem) selected.getData();
         if (!item.isUniversalGroup()) return;

         if (e.data instanceof ArtifactData) {
            final Artifact[] artsToRelate = ((ArtifactData) e.data).getArtifacts();
            if (artsToRelate.length == 1 && item.contains(artsToRelate[0])) {
               AWorkbench.popup("ERROR", "Artifact already related.");
               return;
            }
            AbstractSkynetTxTemplate relateArtifactTx = new AbstractSkynetTxTemplate(branch) {

               @Override
               protected void handleTxWork() throws Exception {
                  for (Artifact art : artsToRelate) {
                     if (!item.contains(art)) {
                        item.getArtifact().relate(RelationSide.UNIVERSAL_GROUPING__MEMBERS, art, true);
                     }
                  }
               }
            };

            try {
               relateArtifactTx.execute();
            } catch (Exception ex) {
               OSEELog.logException(SkynetActivator.class, ex, true);
            }
         }
         treeViewer.refresh(item);
      }
   }

   private ArrayList<UniversalGroupItem> getSelectedUniversalGroupItems() {
      ArrayList<UniversalGroupItem> arts = new ArrayList<UniversalGroupItem>();
      Iterator<?> i = ((IStructuredSelection) treeViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof UniversalGroupItem) {
            arts.add((UniversalGroupItem) obj);
         }
      }
      return arts;
   }

   private boolean isOnlyGroupsSelected() {
      if (getSelectedUniversalGroupItems().size() == 0) return false;
      for (UniversalGroupItem item : getSelectedUniversalGroupItems()) {
         if (!item.isUniversalGroup()) return false;
      }
      return true;
   }

   private boolean isOnlyArtifactsSelected() {
      if (getSelectedUniversalGroupItems().size() == 0) return false;
      for (UniversalGroupItem item : getSelectedUniversalGroupItems()) {
         if (item.isUniversalGroup()) return false;
      }
      return true;
   }

   private void expandAll(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         treeViewer.expandToLevel(iter.next(), TreeViewer.ALL_LEVELS);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchPart#setFocus()
    */
   public void setFocus() {
   }

   public void explore(Artifact artifact) throws CoreException, IllegalArgumentException, SQLException {
      if (rootItem != null) {
         rootItem.dispose();
      }
      branch = artifact.getBranch();
      rootArt = artifact;
      rootItem = new UniversalGroupItem(treeViewer, rootArt, null, this);
      rootItem.getGroupItems();

      setPartName("Group Explorer");

      if (treeViewer != null) treeViewer.setInput(rootItem);

      restoreSelection();
   }

   /**
    * Add the selection from the define explorer
    */
   private void addExploreSelection() {
      if (rootArt != null) {
         try {
            refresh();
         } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
      }
   }

   public void onEvent(final Event event) {
      try {
         if (event instanceof TransactionEvent) {
            EventData ed =
                  ((TransactionEvent) event).getEventData(UniversalGroup.getTopUniversalGroupArtifact(branchPersistenceManager.getDefaultBranch()));
            if (ed.isRelChange()) {
               refresh();
            }
         } else if (event instanceof DefaultBranchChangedEvent) {
            refresh();
         } else if (event instanceof BranchEvent) {
            refresh();
         } else
            SkynetGuiPlugin.getLogger().log(Level.SEVERE, "Unexpected event => " + event);
      } catch (SQLException ex) {
         SkynetGuiPlugin.getLogger().log(Level.SEVERE, "Can't get group root artifact", ex);
      }
   }

   public boolean runOnEventInDisplayThread() {
      return true;
   }

   private class keySelectedListener implements KeyListener {
      public void keyPressed(KeyEvent e) {
         if (e.keyCode == 'a' && e.stateMask == SWT.CONTROL) {
            treeViewer.getTree().selectAll();
         }
         if (e.keyCode == 'x' && e.stateMask == SWT.CONTROL) {
            expandAll((IStructuredSelection) treeViewer.getSelection());
         }
      }

      public void keyReleased(KeyEvent e) {
      }
   }

   public void refresh() {
      try {
         explore(UniversalGroup.getTopUniversalGroupArtifact(branchPersistenceManager.getDefaultBranch()));
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      updateStatusLabel();
   }

   private void updateStatusLabel() {
      if (treeViewer != null && !treeViewer.getTree().isDisposed()) {
         Artifact root = ((UniversalGroupItem) treeViewer.getInput()).getArtifact();
         if (root != null && root.getPersistenceMemo() != null) {
            Branch branch = root.getPersistenceMemo().getTransactionId().getBranch();
            branchStatusItem.setText(branch.getDisplayName());
            branchStatusItem.setImage(BranchLabelProvider.getBranchImage(branch));
         } else {
            branchStatusItem.setText("");
            branchStatusItem.setImage(SkynetGuiPlugin.getInstance().getImage("branch.gif"));
         }
      }
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      if (memento != null) {
         try {
            Artifact previousArtifact =
                  ArtifactPersistenceManager.getInstance().getArtifact(memento.getString(ROOT_GUID),
                        BranchPersistenceManager.getInstance().getCommonBranch());
            if (previousArtifact != null) {
               explore(previousArtifact);
               return;
            }
         } catch (Exception ex) {
            logger.log(Level.SEVERE, "Falling back to the root artifact: " + ex.getLocalizedMessage(), ex);
         }
      }
      refresh();
   }

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      if (rootArt != null) {
         memento.putString(ROOT_GUID, rootArt.getGuid());
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.WorkbenchPart#dispose()
    */
   @Override
   public void dispose() {
      super.dispose();
      SkynetEventManager.getInstance().unRegisterAll(this);
   }

   public String getActionDescription() {
      return "";
   }

}
