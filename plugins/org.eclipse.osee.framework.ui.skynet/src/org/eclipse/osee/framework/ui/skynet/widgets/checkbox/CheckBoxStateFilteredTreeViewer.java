/*********************************************************************
 * Copyright (c) 2018 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.checkbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.osee.framework.ui.skynet.util.IsEnabled;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTree;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * Tree Viewer with ability to disable items from being checked. Override the Label Provider to provide own images. Run
 * CheckBoxStateTreeViewerExample to see example.
 *
 * @author Donald G. Dunne
 */
public class CheckBoxStateFilteredTreeViewer<T> extends FilteredTree implements ICheckBoxStateTreeViewer {

   private final List<ICheckBoxStateTreeListener> listeners = new ArrayList<>();
   private final Set<T> checked = new HashSet<>();
   private final Set<T> disabled = new HashSet<>();
   private IsEnabled enabledChecker = null;
   protected boolean treeExpandCollapseEvent;

   public CheckBoxStateFilteredTreeViewer(Composite parent, int style) {
      super(parent, style, new PatternFilter(), true);
      treeViewer.setLabelProvider(new CheckBoxStateTreeLabelProvider(this));
      treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

         @SuppressWarnings("unchecked")
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            /**
             * Expanding the tree may kick a selection change event when one didn't really happen. Ignore any tree
             * expand/collapse events.
             */
            if (treeExpandCollapseEvent) {
               treeExpandCollapseEvent = false;
               return;
            }
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            Object selected = selection.getFirstElement();
            if (isEnabled(selected)) {
               setChecked((T) selected, !isChecked(selected));
               for (ICheckBoxStateTreeListener listener : listeners) {
                  listener.checkStateNodesChanged();
               }
               treeViewer.refresh(selected);
            }
         }
      });
      treeViewer.addTreeListener(new ITreeViewerListener() {

         @Override
         public void treeExpanded(TreeExpansionEvent event) {
            treeExpandCollapseEvent = true;
         }

         @Override
         public void treeCollapsed(TreeExpansionEvent event) {
            treeExpandCollapseEvent = true;
         }
      });
   }

   @Override
   public boolean isEnabled(Object obj) {
      if (enabledChecker != null) {
         return enabledChecker.isEnabled(obj);
      }
      return !this.disabled.contains(obj);
   }

   @Override
   public boolean isChecked(Object obj) {
      return this.checked.contains(obj);
   }

   public void setChecked(T obj, boolean checked) {
      if (checked) {
         this.checked.add(obj);
      } else {
         this.checked.remove(obj);
      }
      treeViewer.refresh();
      for (ICheckBoxStateTreeListener listener : listeners) {
         listener.checkStateChanged(obj);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public void setEnabled(Object obj, boolean enabled) {
      if (enabled) {
         this.disabled.remove(obj);
      } else {
         this.disabled.add((T) obj);
      }
      treeViewer.refresh();
   }

   public void addCheckListener(ICheckBoxStateTreeListener listener) {
      listeners.add(listener);
   }

   public void deSelectAll() {
      this.checked.clear();
      for (ICheckBoxStateTreeListener listener : listeners) {
         listener.checkStateNodesChanged();
      }
      treeViewer.refresh();
   }

   public Collection<T> getChecked() {
      return this.checked;
   }

   public void setChecked(Collection<T> checked) {
      this.checked.addAll(checked);
      treeViewer.refresh();
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

   public IsEnabled getEnabledChecker() {
      return enabledChecker;
   }

   public void setEnabledChecker(IsEnabled enabledChecker) {
      this.enabledChecker = enabledChecker;
   }

   public void expandOneLevel() {
      for (TreeItem item : treeViewer.getTree().getItems()) {
         item.setExpanded(true);
      }
   }

   public void clearChecked() {
      this.checked.clear();
   }
}
