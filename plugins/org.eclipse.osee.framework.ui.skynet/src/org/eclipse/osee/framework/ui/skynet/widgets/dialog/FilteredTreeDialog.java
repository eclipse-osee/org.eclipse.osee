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
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ToStringContainsPatternFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.PatternFilter;

public class FilteredTreeDialog extends MessageDialog {

   protected Label statusLabel;
   protected Button okButton;
   private FilteredTree treeViewer;
   private Object input;
   private final IContentProvider contentProvider;
   private final IBaseLabelProvider labelProvider;
   private Collection<? extends Object> initialSelections;
   private ViewerComparator viewerComparator;
   private boolean multiSelect = true;
   private PatternFilter patternFilter;
   List<Object> selected = new ArrayList<>();

   public FilteredTreeDialog(String dialogTitle, String dialogMessage, IContentProvider contentProvider, IBaseLabelProvider labelProvider) {
      this(dialogTitle, dialogMessage, contentProvider, labelProvider, null);
   }

   public FilteredTreeDialog(String dialogTitle, String dialogMessage, IContentProvider contentProvider, IBaseLabelProvider labelProvider, ViewerComparator viewerComparator) {
      super(Displays.getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.NONE,
         new String[] {"OK", "Cancel"}, 0);
      this.contentProvider = contentProvider;
      this.labelProvider = labelProvider;
      this.viewerComparator = viewerComparator;
      this.patternFilter = new ToStringContainsPatternFilter();
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   protected void createPreCustomArea(Composite parent) {
      // provided for subclass implementation
   }

   protected void createPostCustomArea(Composite parent) {
      // provided for subclass implementation
   }

   /**
    * Sets the input. Convenience method.
    *
    * @param object the input.
    */
   public void setInput(Object input) {
      this.input = input;
      if (treeViewer != null) {
         treeViewer.getViewer().setInput(input);
      }
   }

   /**
    * Sets the initial selection. Convenience method.
    *
    * @param object the initial selection.
    */
   public void setInitialSelections(Collection<? extends Object> initialSelections) {
      this.initialSelections = initialSelections;
      if (treeViewer != null) {
         IStructuredSelection selection = new StructuredSelection(initialSelections.toArray());
         treeViewer.getViewer().setSelection(selection);
      }
   }

   @SuppressWarnings("unchecked")
   public <T> Collection<T> getSelected() {
      return (Collection<T>) selected;
   }

   @SuppressWarnings("unchecked")
   public <T> T getSelectedFirst() {
      if (selected.size() > 0) {
         return (T) selected.iterator().next();
      }
      return null;
   }

   @Override
   protected Control createCustomArea(Composite parent) {

      statusLabel = new Label(parent, SWT.NONE);
      statusLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      createPreCustomArea(parent);

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout());
      comp.setLayoutData(new GridData(GridData.FILL_BOTH));

      treeViewer = new FilteredTree(comp,
         (multiSelect ? SWT.MULTI : SWT.NONE) | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter,
         true);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 500;
      treeViewer.getViewer().getTree().setLayoutData(gd);
      treeViewer.getViewer().setContentProvider(contentProvider);
      treeViewer.getViewer().setLabelProvider(labelProvider);
      treeViewer.getViewer().setAutoExpandLevel(0);
      treeViewer.setQuickSelectionMode(true);
      if (viewerComparator != null) {
         treeViewer.getViewer().setComparator(viewerComparator);
      }
      treeViewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            updateSelected();
            updateStatusLabel();
         }
      });
      final FilteredTreeDialog dialog = this;
      treeViewer.getViewer().addDoubleClickListener(new IDoubleClickListener() {

         @Override
         public void doubleClick(DoubleClickEvent event) {
            updateSelected();
            dialog.okPressed();
         }
      });
      treeViewer.getFilterControl().addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            Collection<TreeItem> visibleItems = getVisibleItems();
            if (visibleItems.size() == 1) {
               treeViewer.getViewer().setSelection(
                  new StructuredSelection(new Object[] {visibleItems.iterator().next().getData()}));
               getButton(IDialogConstants.OK_ID).setEnabled(true);
            }
         }
      });
      if (input != null) {
         treeViewer.getViewer().setInput(input);
      }
      if (initialSelections != null) {
         setInitialSelections(initialSelections);
      }
      updateStatusLabel();

      return parent;
   }

   public List<TreeItem> getVisibleItems() {
      List<TreeItem> toReturn = new ArrayList<>();
      getVisibleItems(toReturn, treeViewer.getViewer().getTree().getItems());
      return toReturn;
   }

   private void getVisibleItems(List<TreeItem> toReturn, TreeItem items[]) {
      for (TreeItem item : items) {
         toReturn.add(item);
         if (item.getExpanded()) {
            getVisibleItems(toReturn, item.getItems());
         }
      }
   }

   protected void updateSelected() {
      selected.clear();
      for (Object obj : ((StructuredSelection) treeViewer.getViewer().getSelection()).toArray()) {
         selected.add(obj);
      }
   }

   protected void updateStatusLabel() {
      Result result = isComplete();
      if (result.isFalse()) {
         statusLabel.setText(result.getText());
      } else {
         statusLabel.setText("");
      }
      statusLabel.getParent().layout();
      updateButtons();
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control c = super.createButtonBar(parent);
      okButton = getButton(0);
      okButton.setEnabled(false);
      return c;
   }

   protected Result isComplete() {
      return Result.TrueResult;
   }

   private void updateButtons() {
      if (okButton != null) {
         okButton.setEnabled(isComplete().isTrue());
      }
   }

   public void setMultiSelect(boolean multiSelect) {
      this.multiSelect = multiSelect;
   }

   public void setPatternFilter(PatternFilter patternFilter) {
      this.patternFilter = patternFilter;
   }

   public FilteredTree getTreeViewer() {
      return treeViewer;
   }

   public void setComparator(ViewerComparator viewerComparator) {
      this.viewerComparator = viewerComparator;
   }

}
