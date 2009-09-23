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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OpenWithMenuListener;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.menu.ArtifactTreeViewerGlobalMenuHelper;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenuPermissions;
import org.eclipse.osee.framework.ui.skynet.menu.IGlobalMenuHelper;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Donald G. Dunne
 */
public class GroupExplorer extends ViewPart implements IFrameworkTransactionEventListener, IActionable, IRebuildMenuListener {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.group.GroupExplorer";
   private GroupTreeViewer treeViewer;
   private Artifact rootArt;
   private GroupExplorerItem rootItem;
   private Collection<GroupExplorerItem> selected;
   private Object[] expanded = new Object[] {};
   private XBranchSelectWidget branchSelect;
   private Branch branch;
   private GroupExplorerDragAndDrop groupExpDnd;

   private NeedProjectMenuListener needProjectListener;
   private MenuItem openWithMenuItem;
   private MenuItem openMenuItem;
   IGlobalMenuHelper globalMenuHelper;

   public GroupExplorer() {
   }

   @Override
   public void createPartControl(Composite parent) {

      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {
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

      branchSelect = new XBranchSelectWidget("");
      branchSelect.setDisplayLabel(false);
      branchSelect.setBranch(branch);
      branchSelect.createWidgets(parent, 1);

      branchSelect.addListener(new Listener() {
         @Override
         public void handleEvent(Event event) {
            try {
               branch = branchSelect.getData();
               refresh();
               groupExpDnd.setBranch(branch);
            } catch (Exception ex) {
               OseeLog.log(getClass(), Level.SEVERE, ex);
            }
         }

      });

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

      globalMenuHelper = new ArtifactTreeViewerGlobalMenuHelper(treeViewer);
      OseeContributionItem.addTo(this, true);

      OseeEventManager.addListener(this);

      groupExpDnd = new GroupExplorerDragAndDrop(treeViewer, VIEW_ID, branch);

      getSite().setSelectionProvider(treeViewer);
      parent.layout();
      createActions();
      getViewSite().getActionBars().updateActionBars();
      setupPopupMenu();
      refresh();
   }

   @Override
   public void rebuildMenu() {
      setupPopupMenu();
   }

   public void setupPopupMenu() {

      Menu popupMenu = new Menu(treeViewer.getTree().getParent());
      needProjectListener = new NeedProjectMenuListener();
      popupMenu.addMenuListener(needProjectListener);

      createOpenMenuItem(popupMenu);
      createOpenWithMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);

      MenuItem item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText("&Remove from Group");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleRemoveFromGroup();
         }
      });

      item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText("&Delete Group");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleDeleteGroup();
         }
      });

      item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText("&New Group");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleNewGroup();
         }
      });

      new MenuItem(popupMenu, SWT.SEPARATOR);

      item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText("&Select All\tCtrl+A");
      item.addListener(SWT.Selection, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            treeViewer.getTree().selectAll();
         }
      });

      item = new MenuItem(popupMenu, SWT.PUSH);
      item.setText("Expand All\tCtrl+X");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            expandAll((IStructuredSelection) treeViewer.getSelection());
         }
      });

      treeViewer.getTree().setMenu(popupMenu);
   }

   private void createOpenMenuItem(Menu parentMenu) {
      openMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      openMenuItem.setText("&Open");

      ArtifactMenuListener listener = new ArtifactMenuListener();
      parentMenu.addMenuListener(listener);
      openMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent ev) {
            for (Artifact art : getSelectedArtifacts()) {
               RendererManager.openInJob(art, PresentationType.GENERALIZED_EDIT);
            }
         }
      });
   }

   private void createOpenWithMenuItem(Menu parentMenu) {
      openWithMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      openWithMenuItem.setText("&Open With");
      final Menu submenu = new Menu(openWithMenuItem);
      openWithMenuItem.setMenu(submenu);
      parentMenu.addMenuListener(new OpenWithMenuListener(submenu, treeViewer, this));
   }
   /**
    * @author Jeff C. Phillips
    */
   public class ArtifactMenuListener implements MenuListener {

      public void menuHidden(MenuEvent e) {
      }

      public void menuShown(MenuEvent e) {
         // Use this menu listener until all menu items can be moved to
         // GlobaMenu
         try {
            GlobalMenuPermissions permiss = new GlobalMenuPermissions(globalMenuHelper);
            openMenuItem.setEnabled(permiss.isReadPermission());
            openWithMenuItem.setEnabled(permiss.isReadPermission());
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }

      }
   }

   private void handleDoubleClick() {
      GroupExplorerItem item = getSelectedItem();
      if (item != null) {
         RendererManager.openInJob(item.getArtifact(), PresentationType.GENERALIZED_EDIT);
      }
   }

   protected void createActions() {
      Action refreshAction = new Action("Refresh", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            refresh();
         }
      };
      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.REFRESH));
      refreshAction.setToolTipText("Refresh");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(refreshAction);

      OseeAts.addBugToViewToolbar(this, this, SkynetGuiPlugin.getInstance(), VIEW_ID, "Group Explorer");
   }

   private void handleNewGroup() {
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Create New Group", null, "Enter Group Name",
                  MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
      if (ed.open() == 0) {
         try {
            UniversalGroup.addGroup(ed.getEntry(), branch);
            treeViewer.refresh();
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private void handleRemoveFromGroup() {
      if (getSelectedUniversalGroupItems().size() > 0) {
         AWorkbench.popup("ERROR", "Can't remove Group, use \"Delete Group\".");
         return;
      }
      final List<GroupExplorerItem> items = getSelectedItems();
      if (items.size() == 0) {
         AWorkbench.popup("ERROR", "No Items Selected");
         return;
      }
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Remove From Group",
            "Remove From Group - (Artifacts will not be deleted)\n\nAre you sure?")) {
         try {
            SkynetTransaction transaction = new SkynetTransaction(branch);
            for (GroupExplorerItem item : items) {
               item.getArtifact().deleteRelation(CoreRelationEnumeration.UNIVERSAL_GROUPING__GROUP,
                     item.getParentItem().getArtifact());
               item.getArtifact().persist(transaction);
            }
            transaction.execute();
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private void handleDeleteGroup() {
      final ArrayList<GroupExplorerItem> items = getSelectedUniversalGroupItems();
      if (items.size() == 0) {
         AWorkbench.popup("ERROR", "No groups selected.");
         return;
      }
      if (getSelectedItems().size() != items.size()) {
         AWorkbench.popup("ERROR", "Only select groups to be deleted.");
         return;
      }
      String names = "";
      for (GroupExplorerItem item : items) {
         if (item.isUniversalGroup()) {
            names += String.format("%s\n", item.getArtifact().getName());
         }
      }
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Delete Groups",
            "Delete Groups - (Contained Artifacts will not be deleted)\n\n" + names + "\nAre you sure?")) {
         try {
            SkynetTransaction transaction = new SkynetTransaction(branch);
            for (GroupExplorerItem item : items) {
               item.getArtifact().deleteAndPersist(transaction);
            }
            transaction.execute();
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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
      if (itemsIter.hasNext()) {
         return (GroupExplorerItem) itemsIter.next();
      }
      return null;
   }

   private Collection<Artifact> getSelectedArtifacts() {
      Set<Artifact> arts = new HashSet<Artifact>();
      for (GroupExplorerItem item : getSelectedItems()) {
         arts.add(item.getArtifact());
      }
      return arts;
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
      if (getSelectedItems().size() == 0) {
         return false;
      }
      for (GroupExplorerItem item : getSelectedItems()) {
         if (!item.isUniversalGroup()) {
            return false;
         }
      }
      return true;
   }

   private boolean isOnlyGroupItemsSelected() {
      return getSelectedUniversalGroupItems().size() == 0 && getSelectedItems().size() > 0;
   }

   private void expandAll(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         treeViewer.expandToLevel(iter.next(), TreeViewer.ALL_LEVELS);
      }
   }

   @Override
   public void setFocus() {
   }

   public void refresh() {
      //      System.out.println("GE: refresh");
      if (rootItem != null) {
         rootItem.dispose();
      }

      Artifact topArt = null;
      if (branch != null) {
         try {
            topArt = UniversalGroup.getTopUniversalGroupArtifact(branch);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      if (topArt == null) {
         rootArt = null;
         rootItem = null;
      } else {
         rootArt = topArt;
         rootItem = new GroupExplorerItem(treeViewer, rootArt, null, this);
         //         rootItem.getGroupItems();
      }

      if (treeViewer != null) {
         treeViewer.setInput(rootItem);
      }

   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   public String getActionDescription() {
      return "";
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (rootArt != null && transData.branchId != rootArt.getBranch().getBranchId()) {
         return;
      }
      try {
         Artifact topArt = UniversalGroup.getTopUniversalGroupArtifact(branch);
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
   private class NeedProjectMenuListener implements MenuListener {
      Collection<MenuItem> items;

      public NeedProjectMenuListener() {
         this.items = new LinkedList<MenuItem>();
      }

      public void add(MenuItem item) {
         items.add(item);
      }

      public void menuHidden(MenuEvent e) {
      }

      public void menuShown(MenuEvent e) {
         boolean valid = treeViewer.getInput() != null;
         for (MenuItem item : items) {
            if (!(item.getData() instanceof Exception)) {
               // Only modify
               // enabling if no
               // error is
               // associated
               item.setEnabled(valid);
            }
         }
      }
   }

   private static final String INPUT = "input";
   private static final String BRANCH_ID = "branchId";

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      memento = memento.createChild(INPUT);
      if (branch != null) {
         memento.putInteger(BRANCH_ID, branch.getBranchId());
      }
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      try {
         Integer branchId = null;

         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               branchId = memento.getInteger(BRANCH_ID);
               if (branchId != null) {
                  try {
                     branch = BranchManager.getBranch(branchId);
                     if (branch.getBranchState().isDeleted() || branch.getArchiveState().isArchived()) {
                        branch = null;
                     }
                  } catch (BranchDoesNotExist ex) {
                     branch = null;
                  }
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "Group Explorer exception on init", ex);
      }
   }

}