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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Donald G. Dunne
 */
public class GroupExplorer extends ViewPart implements IBranchEventListener, IFrameworkTransactionEventListener, IActionable {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.group.GroupExplorer";
   private GroupTreeViewer treeViewer;
   private Artifact rootArt;
   private GroupExplorerItem rootItem;
   private Collection<GroupExplorerItem> selected;
   private Object[] expanded = new Object[] {};

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

      treeViewer = new GroupTreeViewer(this, parent);
      treeViewer.setContentProvider(new GroupContentProvider(this));
      treeViewer.setLabelProvider(new GroupLabelProvider());
      treeViewer.setUseHashlookup(true);
      treeViewer.getTree().addListener(SWT.MouseDoubleClick, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            handleDoubleClick();
         }
      });
      treeViewer.getControl().setLayoutData(gridData);
      treeViewer.getTree().addListener(SWT.MouseUp, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            if (event.button == 3) getPopupMenu().setVisible(true);
         }
      });

      OseeContributionItem.addTo(this, true);

      OseeEventManager.addListener(this);

      new GroupExplorerDragAndDrop(treeViewer, VIEW_ID);

      getSite().setSelectionProvider(treeViewer);
      parent.layout();
      createActions();
      refresh();
   }

   private void handleDoubleClick() {
      GroupExplorerItem item = getSelectedItem();
      if (item != null) {
         try {
            RendererManager.editInJob(item.getArtifact());
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
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
      item.setEnabled(isOnlyGroupItemsSelected());
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
            UniversalGroup.addGroup(ed.getEntry(), BranchManager.getDefaultBranch());
            treeViewer.refresh();
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   }

   private void handleRemoveFromGroup() {
      final List<GroupExplorerItem> items = getSelectedItems();
      if (items.size() == 0) {
         AWorkbench.popup("ERROR", "No Items Selected");
         return;
      }
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Remove From Group",
            "Remove From Group - (Artifacts will not be deleted)\n\nAre you sure?")) {
         try {
            SkynetTransaction transaction = new SkynetTransaction(BranchManager.getDefaultBranch());
            for (GroupExplorerItem item : items) {
               item.getArtifact().deleteRelation(CoreRelationEnumeration.UNIVERSAL_GROUPING__GROUP,
                     item.getParentItem().getArtifact());
               item.getArtifact().persistRelations(transaction);
            }
            transaction.execute();
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
         try {
            SkynetTransaction transaction = new SkynetTransaction(BranchManager.getDefaultBranch());
            for (GroupExplorerItem item : items) {
               item.getArtifact().delete(transaction);
            }
            transaction.execute();
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   }

   public void storeExpandedAndSelection() {
      //      System.out.println("GE: storeExpandedAndSelection");
      // Store selected so can re-select after event re-draw
      selected = getSelectedItems();
      if (treeViewer != null && !treeViewer.getTree().isDisposed()) {
         expanded = treeViewer.getExpandedElements();
      }
   }

   public void restoreExpandedAndSelection() {
      //      System.out.println("GE: restoreExpandedAndSelection");
      if (expanded != null && expanded.length > 0 && rootArt != null) {
         treeViewer.setExpandedElements(expanded);
      }
      if (selected != null && selected.size() > 0 && rootArt != null) {
         treeViewer.setSelection(new StructuredSelection(selected.toArray(new Object[selected.size()])));
      }
   }

   public GroupExplorerItem getSelectedItem() {
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      Iterator<?> itemsIter = selection.iterator();
      if (itemsIter.hasNext()) return (GroupExplorerItem) itemsIter.next();
      return null;
   }

   private ArrayList<GroupExplorerItem> getSelectedItems() {
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

   private ArrayList<GroupExplorerItem> getSelectedUniversalGroupItems() {
      ArrayList<GroupExplorerItem> arts = new ArrayList<GroupExplorerItem>();
      Iterator<?> i = ((IStructuredSelection) treeViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof GroupExplorerItem && ((GroupExplorerItem) obj).isUniversalGroup()) {
            arts.add((GroupExplorerItem) obj);
         }
      }
      return arts;
   }

   private boolean isOnlyGroupsSelected() {
      if (getSelectedItems().size() == 0) return false;
      for (GroupExplorerItem item : getSelectedItems()) {
         if (!item.isUniversalGroup()) return false;
      }
      return true;
   }

   private boolean isOnlyGroupItemsSelected() {
      if (getSelectedItems().size() == 0) return false;
      for (GroupExplorerItem item : getSelectedItems()) {
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

   public void refresh() {
      //      System.out.println("GE: refresh");
      if (rootItem != null) {
         rootItem.dispose();
      }

      Artifact topArt = null;
      try {
         topArt = UniversalGroup.getTopUniversalGroupArtifact(BranchManager.getDefaultBranch());
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
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
               restoreExpandedAndSelection();
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
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (rootArt != null && transData.branchId != rootArt.getBranch().getBranchId()) return;
      try {
         Artifact topArt = UniversalGroup.getTopUniversalGroupArtifact(BranchManager.getDefaultBranch());
         if (topArt != null) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  storeExpandedAndSelection();
                  refresh();
                  restoreExpandedAndSelection();
               }
            });
            return;
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

}