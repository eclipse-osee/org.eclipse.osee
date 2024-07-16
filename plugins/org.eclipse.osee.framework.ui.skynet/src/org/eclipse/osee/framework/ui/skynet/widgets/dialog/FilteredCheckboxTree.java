/*******************************************************************************
 * Copyright (c) 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Ian Bull <irbull@cs.uvic.ca> - bug 207064
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * A FilteredChecboxTree. This tree stores all the tree elements internally, and keeps the check state in sync. This
 * way, even if an element is filtered, the caller can get and set the checked state. The internal representation is
 * additive. That is, elements are never removed from the internal representation. This is OK since the PDE launch
 * Dialog never changes the elements once the view is opened. If any other tree is based on this code, they may want to
 * address this issue. This is not public because it was customized for the Launch Dialog.
 */
@SuppressWarnings("deprecation")
public class FilteredCheckboxTree extends FilteredTree {

   private WorkbenchJob refreshJob;

   /**
    * The FilteredCheckboxTree Constructor.
    *
    * @param parent The parent composite where this Tree will be placed.
    * @param treeStyle Tree styles
    * @param filter The pattern filter that will be used to filter elements
    */
   public FilteredCheckboxTree(Composite parent, int treeStyle, PatternFilter filter) {
      super(parent, treeStyle, filter, true);
   }

   public FilteredCheckboxTree(Composite parent, int treeStyle) {
      super(parent, treeStyle, new PatternFilter(), true);
   }

   @Override
   protected Composite createFilterControls(Composite parent) {
      Color background = getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout(4, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      comp.setBackground(background);

      super.createFilterControls(comp);

      Label expandAll = new Label(comp, SWT.PUSH);
      expandAll.setBackground(background);
      expandAll.setImage(ImageManager.getImage(FrameworkImage.EXPAND_ALL));
      expandAll.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
         @Override
         public void mouseUp(MouseEvent e) {
            super.mouseUp(e);
            treeViewer.expandAll();
         }
      });

      Label collapseAll = new Label(comp, SWT.PUSH);
      collapseAll.setBackground(background);
      collapseAll.setImage(ImageManager.getImage(FrameworkImage.COLLAPSE_ALL));
      collapseAll.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
         @Override
         public void mouseUp(MouseEvent e) {
            super.mouseUp(e);
            treeViewer.collapseAll();
         }
      });

      filterText.setFocus();
      return comp;
   }

   public FilterableCheckboxTreeViewer getCheckboxTreeViewer() {
      return (FilterableCheckboxTreeViewer) getViewer();
   }

   public void setCheckAll(boolean checked) {
      for (TreeItem item : getViewer().getTree().getItems()) {
         getCheckboxTreeViewer().setSubtreeChecked(item.getData(), checked);
      }
   }

   public void setInitalChecked(Collection<? extends Object> checked) {
      getCheckboxTreeViewer().setCheckedElements(checked.toArray());
   }

   public void clearChecked() {
      setCheckAll(false);
   }

   @SuppressWarnings("unchecked")
   public <T> Collection<T> getChecked() {
      List<T> checked = new ArrayList<>();
      for (Object obj : getCheckboxTreeViewer().getCheckedElements()) {
         checked.add((T) obj);
      }
      return checked;
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.ui.dialogs.FilteredTree#doCreateTreeViewer(org.eclipse.swt.widgets.Composite, int)
    */
   @Override
   protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
      return new FilterableCheckboxTreeViewer(parent, style);
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.ui.dialogs.FilteredTree#doCreateRefreshJob()
    */
   @Override
   protected WorkbenchJob doCreateRefreshJob() {
      // Since refresh job is private, we have to get a handle to it
      // when it is created, and store it locally.
      //
      // See: 218903: [Viewers] support extensibility of the refresh job in FilteredTree
      // https://bugs.eclipse.org/bugs/show_bug.cgi?id=218903
      WorkbenchJob job = super.doCreateRefreshJob();
      refreshJob = job;
      return job;
   }

   /**
    * Resets the filter and returns when the refresh is complete
    */
   public void resetFilter() {
      // Set the next to the initial Text, stop any outstanding jobs
      // and call the refresh job to run synchronously.
      Text filterText = getFilterControl();
      if (filterText != null) {
         filterText.setText(this.initialText);
      }
      refreshJob.cancel();
      refreshJob.runInUIThread(new NullProgressMonitor());
   }

   /**
    * Get the number of pixels the tree viewer is from the top of the filtered checkbox tree viewer. This is useful if
    * you wish to align buttons with the tree.
    *
    * @return the offset of the Tree from the top of the container
    */
   int getTreeLocationOffset() {
      GridLayout layout = (GridLayout) getLayout();
      int space = layout.horizontalSpacing + layout.marginTop + ((GridData) getLayoutData()).verticalIndent + 1;
      Text filterText = getFilterControl();
      if (filterText != null) {
         space += filterText.getSize().y;
      }
      return space;
   }

   /**
    * Classes which implement this interface deal with notifications from the filtered checkbox tree viewer. The
    * notifications are fired before a refresh happens.
    */
   interface PreRefreshNotifier {
      public void preRefresh(FilterableCheckboxTreeViewer viewer, boolean filtered);
   }

   /**
    * A CheckboxTreeViewer that maintains an internal representation of all the nodes.
    */
   public class FilterableCheckboxTreeViewer extends CheckboxTreeViewer {
      static final String NONE = "none"; //$NON-NLS-1$
      static final String CHECKED = "checked"; //$NON-NLS-1$
      static final String GREYED = "greyed"; //$NON-NLS-1$
      static final String CHECKED_GREYED = "checked_greyed"; //$NON-NLS-1$

      /**
       * The internal node for the FilterableCheckboxTreeViewer
       */
      class FilteredCheckboxTreeItem {
         Object data; // Data element
         String state; // Checked State
         List<FilteredCheckboxTreeItem> children = new ArrayList<>();

         public FilteredCheckboxTreeItem(Object data, String state, Map<Object, FilteredCheckboxTreeItem> itemCache, FilteredCheckboxTreeItem parent) {
            this.data = data;
            this.state = state;
            itemCache.put(data, this);
            if (parent != null) {
               parent.children.add(this);
            }
         }
      }

      /* A cache of all the nodes */
      Map<Object, FilteredCheckboxTreeItem> itemCache = new HashMap<>();
      /* The preRefresh Listeners */
      List<PreRefreshNotifier> refreshingListeners = new ArrayList<>();

      @Override
      protected void unmapAllElements() {
         itemCache = new HashMap<>();
         super.unmapAllElements();
      }

      /**
       * FilterableCheckboxTreeViewer constructor. This creates the tree part of the filtered tree.
       */
      public FilterableCheckboxTreeViewer(Composite parent, int style) {
         super(parent, style);
         addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
               FilteredCheckboxTreeItem item = getOrCreateItem(event.getElement());
               if (item != null) {
                  item.state = event.getChecked() ? CHECKED : NONE;
               }
            }
         });
      }

      public FilteredCheckboxTreeItem getOrCreateItem(Object data) {
         FilteredCheckboxTreeItem item = itemCache.get(data);
         if (item == null) {
            item = new FilteredCheckboxTreeItem(data, NONE, itemCache, null);
            itemCache.put(data, item);
         }
         return item;
      }

      /**
       * Allows clients to listen to the tree refresh.
       */
      public void addPreRefreshNotifier(PreRefreshNotifier notifier) {
         if (refreshingListeners.contains(notifier)) {
            return;
         }
         refreshingListeners.add(notifier);
      }

      /*
       * (non-Javadoc)
       * @see org.eclipse.jface.viewers.CheckboxTreeViewer#getChecked(java.lang.Object)
       */
      @Override
      public boolean getChecked(Object element) {
         Widget testFindItem = getViewer().testFindItem(element);
         if (testFindItem == null) {
            if (itemCache.containsKey(element)) {
               FilteredCheckboxTreeItem item = itemCache.get(element);
               if (item.state.equals(CHECKED)) {
                  return true;
               }
               if (item.state.equals(CHECKED_GREYED)) {
                  return true;
               }
               if (item.state.equals(GREYED)) {
                  return true;
               } else if (item.state.equals(NONE)) {
                  return false;
               }
            }
         }
         return super.getChecked(element);
      }

      public Object[] getCheckedChildren(Object element) {
         FilteredCheckboxTreeItem item = itemCache.get(element);
         List<Object> checkedChildren = new ArrayList<>();
         if (item != null) {
            List<FilteredCheckboxTreeItem> children = item.children;
            Iterator<FilteredCheckboxTreeItem> iterator = children.iterator();
            while (iterator.hasNext()) {
               FilteredCheckboxTreeItem child = iterator.next();
               if (child.state == CHECKED) {
                  checkedChildren.add(child.data);
               }
            }
         }
         return checkedChildren.toArray();
      }

      /*
       * (non-Javadoc)
       * @see org.eclipse.jface.viewers.CheckboxTreeViewer#getCheckedElements()
       */
      @Override
      public Object[] getCheckedElements() {
         Iterator<FilteredCheckboxTreeItem> iterator = itemCache.values().iterator();
         List<Object> checkedElements = new LinkedList<>();
         while (iterator.hasNext()) {
            getCheckedElementsRecurse(iterator.next(), checkedElements);
         }
         return checkedElements.toArray();
      }

      private void getCheckedElementsRecurse(FilteredCheckboxTreeItem item, List<Object> checkedElements) {
         Widget testFindItem = getViewer().testFindItem(item.data);
         if (testFindItem == null) {
            if (item.state.equals(CHECKED) || item.state.equals(CHECKED_GREYED) || item.state.equals(GREYED)) {
               checkedElements.add(item.data);
            }
         } else {
            if (((TreeItem) testFindItem).getChecked()) {
               checkedElements.add(testFindItem.getData());
            }
         }
      }

      /*
       * (non-Javadoc)
       * @see org.eclipse.jface.viewers.CheckboxTreeViewer#setChecked(java.lang.Object, boolean)
       */
      @Override
      public boolean setChecked(Object element, boolean state) {
         if (itemCache.containsKey(element)) {
            FilteredCheckboxTreeItem item = itemCache.get(element);
            item.state = state ? CHECKED : NONE;
         }
         return super.setChecked(element, state);
      }

      /*
       * (non-Javadoc)
       * @see org.eclipse.jface.viewers.CheckboxTreeViewer#setCheckedElements(java.lang.Object[])
       */
      @Override
      public void setCheckedElements(Object[] elements) {
         Set<Object> s = new HashSet<>(itemCache.keySet());
         s.removeAll(new HashSet<>(Arrays.asList(elements)));
         for (int i = 0; i < elements.length; i++) {
            Object element = elements[i];
            FilteredCheckboxTreeItem item = itemCache.get(element);
            if (item != null) {
               item.state = CHECKED;
            } else {
               item = new FilteredCheckboxTreeItem(element, CHECKED, itemCache, null);
               itemCache.put(element, item);
            }
         }
         for (Iterator<Object> iterator = s.iterator(); iterator.hasNext();) {
            Object object = iterator.next();
            FilteredCheckboxTreeItem item = itemCache.get(object);
            if (item != null) {
               item.state = NONE;
            }
         }
         super.setCheckedElements(elements);
      }

      /*
       * (non-Javadoc)
       * @see org.eclipse.jface.viewers.CheckboxTreeViewer#setSubtreeChecked(java.lang.Object, boolean)
       */
      @Override
      public boolean setSubtreeChecked(Object element, boolean state) {
         String newState = state ? CHECKED : NONE;
         TreeItem item = (TreeItem) testFindItem(element);
         FilteredCheckboxTreeItem filteredCheckboxTreeItem = itemCache.get(element);
         if (item != null && filteredCheckboxTreeItem != null) {
            filteredCheckboxTreeItem.state = newState;
            TreeItem[] items = item.getItems();
            for (int i = 0; i < items.length; i++) {
               item = items[i];
               if (item != null) {
                  filteredCheckboxTreeItem = itemCache.get(item.getData());
                  if (filteredCheckboxTreeItem != null) {
                     filteredCheckboxTreeItem.state = newState;
                  }
               }
            }
         }
         return super.setSubtreeChecked(element, state);
      }

      /*
       * public boolean setSubtreeChecked(Object element, boolean state) { String newState = state ? CHECKED : NONE;
       * FilteredCheckboxTreeItem filteredCheckboxTreeItem = (FilteredCheckboxTreeItem) itemCache.get(element); if
       * (filteredCheckboxTreeItem != null) { filteredCheckboxTreeItem.state = newState; List children =
       * filteredCheckboxTreeItem.children; for (Iterator iterator = children.iterator(); iterator.hasNext();) {
       * FilteredCheckboxTreeItem child = (FilteredCheckboxTreeItem) iterator.next(); child.state = newState; } } return
       * super.setSubtreeChecked(element, state); }
       */

      /*
       * (non-Javadoc)
       * @see org.eclipse.jface.viewers.CheckboxTreeViewer#preservingSelection(java.lang.Runnable)
       */
      @Override
      protected void preservingSelection(Runnable updateCode) {
         super.preservingSelection(updateCode);

         // Re-apply the checked state
         ArrayList<TreeItem> allTreeItems = getAllTreeItems(treeViewer.getTree().getItems());
         for (Iterator<TreeItem> iterator = allTreeItems.iterator(); iterator.hasNext();) {
            TreeItem item = iterator.next();
            doApplyCheckedState(item, item.getData());
         }
      }

      /*
       * (non-Javadoc)
       * @see org.eclipse.jface.viewers.AbstractTreeViewer#internalRefresh(java.lang.Object, boolean)
       */
      @Override
      protected void internalRefresh(Object element, boolean updateLabels) {
         String text = FilteredCheckboxTree.this.getFilterString();
         boolean initial = initialText != null && initialText.equals(text);
         if (text != null) {
            boolean filtered = text.length() > 0 && !initial;

            // Notify anybody who is listening for the refresh
            for (Iterator<PreRefreshNotifier> iterator = refreshingListeners.iterator(); iterator.hasNext();) {
               PreRefreshNotifier notifier = iterator.next();
               notifier.preRefresh(FilterableCheckboxTreeViewer.this, filtered);
            }
         }
         saveCheckedState();
         super.internalRefresh(element, updateLabels);
      }

      public void expandChecked() {
         TreeItem[] items = treeViewer.getTree().getItems();
         expandChecked(items);
      }

      private void expandChecked(TreeItem[] items) {
         for (int i = 0; i < items.length; i++) {
            TreeItem item = items[i];
            if (item.getData() != null) {
               if (item.getChecked()) {
                  expandParents(item);
               }
            }
            expandChecked(item.getItems());
         }
      }

      public void expandParents(TreeItem item) {
         if (item.getParentItem() != null) {
            TreeItem parent = item.getParentItem();
            if (!parent.getExpanded()) {
               parent.setExpanded(true);
            }
            expandParents(parent);
         }
      }

      /*
       * Set the checked state
       */
      private void doApplyCheckedState(Item item, Object element) {
         // update the item first
         super.doUpdateItem(item, element);

         // Update the checked state
         TreeItem treeItem = (TreeItem) item;
         if (itemCache.containsKey(element)) {
            String state = itemCache.get(element).state;
            if (state.equals(CHECKED_GREYED)) {
               treeItem.setGrayed(true);
               treeItem.setChecked(true);
            } else if (state.equals(CHECKED)) {
               treeItem.setChecked(true);
               treeItem.setGrayed(false);
            } else if (state.equals(GREYED)) {
               treeItem.setGrayed(true);
               treeItem.setChecked(false);
            } else {
               treeItem.setGrayed(false);
               treeItem.setChecked(false);
            }
         }
      }

      /*
       * A helper method to get all the items in the tree
       */
      private ArrayList<TreeItem> getAllTreeItems(TreeItem[] roots) {
         ArrayList<TreeItem> list = new ArrayList<>();
         for (int i = 0; i < roots.length; i++) {
            TreeItem item = roots[i];
            list.add(item);
            list.addAll(getAllTreeItems(item.getItems()));
         }
         return list;
      }

      /**
       * Saves the checked state of all the elements in the tree
       */
      private void saveCheckedState() {
         TreeItem[] items = treeViewer.getTree().getItems();
         for (int i = 0; i < items.length; i++) {
            TreeItem item = items[i];
            if (item.getData() != null) {
               if (!itemCache.containsKey(item.getData())) {
                  new FilteredCheckboxTreeItem(item.getData(), getItemState(item), itemCache, null);
               }
               FilteredCheckboxTreeItem filteredCheckboxTreeItem = itemCache.get(item.getData());
               filteredCheckboxTreeItem.state = getItemState(item);
               saveCheckedState(filteredCheckboxTreeItem, item);
            }
         }
      }

      /**
       * Saves the checked state of an item and all its children
       */
      private void saveCheckedState(FilteredCheckboxTreeItem parent, TreeItem parentItem) {
         TreeItem[] items = parentItem.getItems();
         for (int i = 0; i < items.length; i++) {
            TreeItem item = items[i];
            if (item.getData() != null) {
               if (!itemCache.containsKey(item.getData())) {
                  new FilteredCheckboxTreeItem(item.getData(), getItemState(item), itemCache, parent);
               }
               FilteredCheckboxTreeItem filteredCheckboxTreeItem = itemCache.get(item.getData());
               filteredCheckboxTreeItem.state = getItemState(item);
               saveCheckedState(filteredCheckboxTreeItem, item);
            }
         }
      }

      /**
       * Computes the checked state from a tree item
       */
      private String getItemState(TreeItem item) {
         if (item.getChecked() && item.getGrayed()) {
            return CHECKED_GREYED;
         } else if (item.getChecked()) {
            return CHECKED;
         } else if (item.getGrayed()) {
            return GREYED;
         } else {
            return NONE;
         }
      }

   } // end of FilterableCheckboxTreeViewer

   @Override
   public void setEnabled(boolean enabled) {
      if ((filterText.getStyle() & SWT.ICON_CANCEL) == 0) { // filter uses FilteredTree new look, not native
         int filterColor = enabled ? SWT.COLOR_LIST_BACKGROUND : SWT.COLOR_WIDGET_BACKGROUND;
         filterComposite.setBackground(getDisplay().getSystemColor(filterColor));
      }
      super.setEnabled(enabled);
      treeViewer.getTree().setEnabled(enabled);

   }

   public void setSorter(ViewerSorter sorter) {
      getViewer().setSorter(sorter);
   }
}