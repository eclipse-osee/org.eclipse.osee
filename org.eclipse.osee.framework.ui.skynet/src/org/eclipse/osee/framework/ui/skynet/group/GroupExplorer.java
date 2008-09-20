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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Donald G. Dunne
 */
public class GroupExplorer extends ViewPart implements IBranchEventListener, IFrameworkTransactionEventListener, IActionable {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.group.GroupExplorer";
   private TreeViewer treeViewer;
   private Artifact rootArt;
   private GroupExplorerItem rootItem;
   private static GroupExplorerItem selected;
   private boolean isCtrlPressed = false;

   public GroupExplorer() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createPartControl(Composite parent) {

      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

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

      SkynetContributionItem.addTo(this, true);

      OseeEventManager.addListener(this);

      new GroupExplorerDragAndDrop(treeViewer.getTree(), VIEW_ID);

      getSite().setSelectionProvider(treeViewer);
      parent.layout();
      createActions();
      refresh();
   }

   private void handleDoubleClick() {
      GroupExplorerItem item = getSelectedItem();
      if (item != null) {
         RendererManager.getInstance().editInJob(item.getArtifact());
      }
   }

   protected void createActions() {
      Action refreshAction = new Action("Refresh", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            refresh();
         }
      };
      refreshAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("refresh.gif"));
      refreshAction.setToolTipText("Refresh");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
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
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleRemoveFromGroup();
         }
      });

      item = new MenuItem(previewMenu, SWT.PUSH);
      item.setText("&Delete Group");
      item.setEnabled(isOnlyGroupsSelected());
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleDeleteGroup();
         }
      });

      item = new MenuItem(previewMenu, SWT.PUSH);
      item.setText("&New Group");
      item.setEnabled(true);
      item.addSelectionListener(new SelectionAdapter() {
         @Override
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
         @Override
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
            UniversalGroup.addGroup(ed.getEntry(), BranchPersistenceManager.getDefaultBranch());
            treeViewer.refresh();
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   }

   private void handleRemoveFromGroup() {
      final List<GroupExplorerItem> items = getSelectedUniversalGroupItems();
      if (items.size() == 0) {
         AWorkbench.popup("ERROR", "No Items Selected");
         return;
      }
      String names = "";
      for (GroupExplorerItem item : items)
         if (item.isUniversalGroup()) names += String.format("%s\n", item.getArtifact().getDescriptiveName());
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Remove From Group",
            "Remove From Group - (Artifacts will not be deleted)\n\n" + names + "\nAre you sure?")) {
         AbstractSkynetTxTemplate unrelateTx =
               new AbstractSkynetTxTemplate(BranchPersistenceManager.getDefaultBranch()) {
                  @Override
                  protected void handleTxWork() throws OseeCoreException, SQLException {
                     for (GroupExplorerItem item : items) {
                        item.getArtifact().deleteRelation(CoreRelationEnumeration.UNIVERSAL_GROUPING__GROUP,
                              item.getParentItem().getArtifact());
                        item.getArtifact().persistRelations();
                     }
                  }
               };

         try {
            unrelateTx.execute();
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   }

   private void handleDeleteGroup() {
      final ArrayList<GroupExplorerItem> items = getSelectedUniversalGroupItems();
      if (items.size() == 0) {
         AWorkbench.popup("ERROR", "No Groups Selected");
         return;
      }
      String names = "";
      for (GroupExplorerItem item : items)
         if (item.isUniversalGroup()) names += String.format("%s\n", item.getArtifact().getDescriptiveName());
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Delete Groups",
            "Delete Groups - (Contained Artifacts will not be deleted)\n\n" + names + "\nAre you sure?")) {

         AbstractSkynetTxTemplate deleteUniversalGroupTx =
               new AbstractSkynetTxTemplate(BranchPersistenceManager.getDefaultBranch()) {
                  @Override
                  protected void handleTxWork() throws OseeCoreException, SQLException {
                     for (GroupExplorerItem item : items) {
                        item.getArtifact().delete();
                     }
                  }
               };

         try {
            deleteUniversalGroupTx.execute();
            refresh();
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   }

   public void storeSelection() {
      // Store selected so can re-select after event re-draw
      selected = getSelectedItem();
   }

   public void restoreSelection() {
      if (selected != null && rootItem != null) {
         GroupExplorerItem selItem = rootItem.getItem(selected.getArtifact());
         ArrayList<GroupExplorerItem> selected = new ArrayList<GroupExplorerItem>();
         selected.add(selItem);
         treeViewer.setSelection(new StructuredSelection(selected.toArray(new Object[selected.size()])));
      }
   }

   public GroupExplorerItem getSelectedItem() {
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      Iterator<?> itemsIter = selection.iterator();
      if (itemsIter.hasNext()) return (GroupExplorerItem) itemsIter.next();
      return null;
   }

   private ArrayList<GroupExplorerItem> getSelectedUniversalGroupItems() {
      ArrayList<GroupExplorerItem> arts = new ArrayList<GroupExplorerItem>();
      Iterator<?> i = ((IStructuredSelection) treeViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof GroupExplorerItem) {
            arts.add((GroupExplorerItem) obj);
         }
      }
      return arts;
   }

   private boolean isOnlyGroupsSelected() {
      if (getSelectedUniversalGroupItems().size() == 0) return false;
      for (GroupExplorerItem item : getSelectedUniversalGroupItems()) {
         if (!item.isUniversalGroup()) return false;
      }
      return true;
   }

   private boolean isOnlyArtifactsSelected() {
      if (getSelectedUniversalGroupItems().size() == 0) return false;
      for (GroupExplorerItem item : getSelectedUniversalGroupItems()) {
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
   @Override
   public void setFocus() {
   }

   private class keySelectedListener implements KeyListener {
      public void keyPressed(KeyEvent e) {
         isCtrlPressed = (e.keyCode == SWT.CONTROL);
      }

      public void keyReleased(KeyEvent e) {
         if (e.keyCode == 'a' && e.stateMask == SWT.CONTROL) {
            treeViewer.getTree().selectAll();
         }
         if (e.keyCode == 'x' && e.stateMask == SWT.CONTROL) {
            expandAll((IStructuredSelection) treeViewer.getSelection());
         }
         isCtrlPressed = !(e.keyCode == SWT.CONTROL);
      }
   }

   public void refresh() {
      if (rootItem != null) {
         rootItem.dispose();
      }

      Artifact topArt = null;
      try {
         topArt = UniversalGroup.getTopUniversalGroupArtifact(BranchPersistenceManager.getDefaultBranch());
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
      if (topArt == null) {
         rootArt = null;
         rootItem = null;
      } else {
         rootArt = topArt;
         rootItem = new GroupExplorerItem(treeViewer, rootArt, null, this);
         rootItem.getGroupItems();
      }

      if (treeViewer != null) treeViewer.setInput(rootItem);

      restoreSelection();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.WorkbenchPart#dispose()
    */
   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   public String getActionDescription() {
      return "";
   }

   private class GroupExplorerDragAndDrop extends SkynetDragAndDrop {
      boolean isFeedbackAfter = false;

      public GroupExplorerDragAndDrop(Tree tree, String viewId) {
         super(tree, viewId);
      }

      @Override
      public Artifact[] getArtifacts() {
         IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
         Iterator<?> i = selection.iterator();
         List<Artifact> artifacts = new ArrayList<Artifact>();
         while (i.hasNext()) {
            Object object = i.next();
            if ((object instanceof GroupExplorerItem) && !((GroupExplorerItem) object).isUniversalGroup()) artifacts.add(((GroupExplorerItem) object).getArtifact());
         }
         return artifacts.toArray(new Artifact[artifacts.size()]);
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         Tree tree = treeViewer.getTree();
         TreeItem dragOverTreeItem = tree.getItem(treeViewer.getTree().toControl(event.x, event.y));

         event.feedback = DND.FEEDBACK_EXPAND;
         event.detail = DND.DROP_NONE;

         // Set as COPY if drag item over group (copy versus move will be determined on drop
         if (dragOverTreeItem != null && ((GroupExplorerItem) dragOverTreeItem.getData()).isUniversalGroup()) {
            event.detail = DND.DROP_COPY;
            tree.setInsertMark(null, false);
         }
         // Handle re-ordering within same group
         else if (dragOverTreeItem != null && !((GroupExplorerItem) dragOverTreeItem.getData()).isUniversalGroup()) {
            GroupExplorerItem dragOverGroupItem = (GroupExplorerItem) dragOverTreeItem.getData();
            IStructuredSelection selectedItem = (IStructuredSelection) treeViewer.getSelection();
            Object obj = selectedItem.getFirstElement();
            if (obj instanceof GroupExplorerItem) {
               GroupExplorerItem droppingGroupItem = (GroupExplorerItem) obj;

               // the group to move must belong to the same group as the member to insert before/after
               if ((dragOverGroupItem.getParentItem()).equals(droppingGroupItem.getParentItem())) {
                  if (isFeedbackAfter) {
                     event.feedback = DND.FEEDBACK_INSERT_AFTER;
                  } else {
                     event.feedback = DND.FEEDBACK_INSERT_BEFORE;
                  }
                  event.detail = DND.DROP_MOVE;
               }
            } else {
               if (isFeedbackAfter) {
                  event.feedback = DND.FEEDBACK_INSERT_AFTER;
               } else {
                  event.feedback = DND.FEEDBACK_INSERT_BEFORE;
               }
               event.detail = DND.DROP_COPY;
            }
         } else {
            tree.setInsertMark(null, false);
         }
      }

      @Override
      public void operationChanged(DropTargetEvent event) {
         if (!isCtrlPressed(event)) {
            isFeedbackAfter = false;
         }
      }

      private boolean isCtrlPressed(DropTargetEvent event) {
         boolean ctrPressed = (event.detail == 1);

         if (ctrPressed) {
            isFeedbackAfter = true;
         }
         return ctrPressed;
      }

      @Override
      public void performDrop(DropTargetEvent event) {
         try {
            TreeItem dragOverTreeITem = treeViewer.getTree().getItem(treeViewer.getTree().toControl(event.x, event.y));

            // This should always be true as all items are Group Explorer Items
            if (dragOverTreeITem.getData() instanceof GroupExplorerItem) {
               final GroupExplorerItem dragOverExplorerItem = (GroupExplorerItem) dragOverTreeITem.getData();

               // Drag item dropped ON universal group item 
               if (dragOverExplorerItem.isUniversalGroup()) {

                  // Drag item came from inside Group Explorer
                  if (event.data instanceof ArtifactData) {
                     // If event originated outside, it's a copy event;
                     // OR if event is inside and ctrl is down, this is a copy; add items to group
                     if (!((ArtifactData) event.data).getSource().equals(VIEW_ID) || (((ArtifactData) event.data).getSource().equals(
                           VIEW_ID) && isCtrlPressed)) {
                        copyArtifactsToGroup(event, dragOverExplorerItem);
                     }
                     // Else this is a move
                     else {
                        IStructuredSelection selectedItem = (IStructuredSelection) treeViewer.getSelection();
                        Iterator<?> iterator = selectedItem.iterator();
                        final Set<Artifact> insertArts = new HashSet<Artifact>();
                        while (iterator.hasNext()) {
                           Object obj = iterator.next();
                           if (obj instanceof GroupExplorerItem) {
                              insertArts.add(((GroupExplorerItem) obj).getArtifact());
                           }
                        }
                        GroupExplorerItem parentUnivGroupItem =
                              ((GroupExplorerItem) selectedItem.getFirstElement()).getParentItem();
                        final Artifact parentArtifact = parentUnivGroupItem.getArtifact();
                        final Artifact targetArtifact = dragOverExplorerItem.getArtifact();

                        AbstractSkynetTxTemplate relateArtifactTx =
                              new AbstractSkynetTxTemplate(BranchPersistenceManager.getDefaultBranch()) {

                                 @Override
                                 protected void handleTxWork() throws OseeCoreException, SQLException {
                                    for (Artifact artifact : insertArts) {
                                       // Remove item from old group
                                       parentArtifact.deleteRelation(
                                             CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS, artifact);
                                       // Add items to new group
                                       targetArtifact.addRelation(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS,
                                             artifact);
                                    }
                                    parentArtifact.persistAttributesAndRelations();
                                    targetArtifact.persistAttributesAndRelations();
                                 }
                              };

                        try {
                           relateArtifactTx.execute();
                        } catch (Exception ex) {
                           OSEELog.logException(SkynetGuiPlugin.class, ex, true);
                        }
                     }
                  }
               }
               // Drag item dropped before or after group member
               else if (!dragOverExplorerItem.isUniversalGroup()) {

                  if (event.data instanceof ArtifactData) {

                     GroupExplorerItem parentUnivGroupItem = null;
                     Artifact[] artifactsToInsert = null;
                     // Drag item came from inside Group Explorer
                     if (((ArtifactData) event.data).getSource().equals(VIEW_ID)) {
                        IStructuredSelection selectedItem = (IStructuredSelection) treeViewer.getSelection();
                        Iterator<?> iterator = selectedItem.iterator();
                        Set<Artifact> insertArts = new HashSet<Artifact>();
                        while (iterator.hasNext()) {
                           Object obj = iterator.next();
                           if (obj instanceof GroupExplorerItem) {
                              insertArts.add(((GroupExplorerItem) obj).getArtifact());
                           }
                        }
                        parentUnivGroupItem = ((GroupExplorerItem) selectedItem.getFirstElement()).getParentItem();
                        artifactsToInsert = insertArts.toArray(new Artifact[insertArts.size()]);

                        Artifact parentArtifact = parentUnivGroupItem.getArtifact();
                        Artifact targetArtifact = dragOverExplorerItem.getArtifact();

                        for (Artifact art : insertArts) {
                           parentArtifact.setRelationOrder(targetArtifact, isFeedbackAfter,
                                 CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS, art);
                           targetArtifact = art;
                        }
                        parentArtifact.persistRelations();
                     }
                     // Drag item came from outside Group Explorer
                     else {
                        List<Artifact> insertArts = Arrays.asList(((ArtifactData) event.data).getArtifacts());
                        parentUnivGroupItem = dragOverExplorerItem.getParentItem();
                        artifactsToInsert = insertArts.toArray(new Artifact[insertArts.size()]);

                        Artifact parentArtifact = parentUnivGroupItem.getArtifact();
                        Artifact targetArtifact = dragOverExplorerItem.getArtifact();

                        for (Artifact art : insertArts) {
                           parentArtifact.addRelation(targetArtifact, isFeedbackAfter,
                                 CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS, art, "");
                           targetArtifact = art;
                        }
                        parentArtifact.persistRelations();
                     }
                  }
               }
               treeViewer.refresh(dragOverExplorerItem);
            }

            isFeedbackAfter = false;
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }

      public void copyArtifactsToGroup(DropTargetEvent event, final GroupExplorerItem dragOverExplorerItem) {
         // Items dropped on Group; simply add items to group
         final Artifact[] artsToRelate = ((ArtifactData) event.data).getArtifacts();
         boolean alreadyRelated = true;
         for (Artifact artifact : artsToRelate) {
            if (!dragOverExplorerItem.contains(artifact)) {
               alreadyRelated = false;
               break;
            }
         }
         if (alreadyRelated) {
            AWorkbench.popup("ERROR", "Artifact(s) already related.");
            return;
         }
         AbstractSkynetTxTemplate relateArtifactTx =
               new AbstractSkynetTxTemplate(BranchPersistenceManager.getDefaultBranch()) {

                  @Override
                  protected void handleTxWork() throws OseeCoreException, SQLException {
                     for (Artifact art : artsToRelate) {
                        if (!dragOverExplorerItem.contains(art)) {
                           dragOverExplorerItem.getArtifact().addRelation(
                                 CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS, art);
                        }
                     }
                     dragOverExplorerItem.getArtifact().persistRelations();
                  }
               };

         try {
            relateArtifactTx.execute();
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }

      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      if (branchModType == BranchEventType.DefaultBranchChanged) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               refresh();
            }
         });
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) {
      if (rootArt != null && transData.branchId != rootArt.getBranch().getBranchId()) return;
      try {
         Artifact topArt = UniversalGroup.getTopUniversalGroupArtifact(BranchPersistenceManager.getDefaultBranch());
         if (topArt != null) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  refresh();
               }
            });
            return;
         }
         if (transData.isHasEvent(topArt)) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  refresh();
               }
            });
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

}