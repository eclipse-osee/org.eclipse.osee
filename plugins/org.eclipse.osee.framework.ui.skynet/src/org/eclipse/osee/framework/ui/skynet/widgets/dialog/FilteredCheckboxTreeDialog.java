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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTree.FilterableCheckboxTreeViewer;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ToStringContainsPatternFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PatternFilter;

public class FilteredCheckboxTreeDialog<T> extends MessageDialog {

   protected Label statusLabel;
   protected Button okButton;
   private FilteredCheckboxTree treeViewer;
   private Object input;
   private IContentProvider contentProvider;
   private final IBaseLabelProvider labelProvider;
   private Collection<T> initialSelections;
   private final ViewerComparator viewerComparator;
   private boolean showSelectButtons = false;
   private boolean expandChecked = false;
   private boolean multiSelect = true;
   private PatternFilter patternFilter;
   protected Collection<T> selectables = new ArrayList<>();

   public FilteredCheckboxTreeDialog(String dialogTitle, String dialogMessage, IContentProvider contentProvider, IBaseLabelProvider labelProvider) {
      this(dialogTitle, dialogMessage, contentProvider, labelProvider, null);
   }

   public FilteredCheckboxTreeDialog(String dialogTitle, String dialogMessage, IContentProvider contentProvider, IBaseLabelProvider labelProvider, ViewerComparator viewerSorter) {
      super(Displays.getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.NONE,
         new String[] {"OK", "Cancel"}, 0);
      this.contentProvider = contentProvider;
      this.labelProvider = labelProvider;
      this.viewerComparator = viewerSorter;
      this.patternFilter = new ToStringContainsPatternFilter();
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   public FilteredCheckboxTreeDialog(String dialogTitle, String dialogMessage, Collection<T> selectables, IContentProvider contentProvider, IBaseLabelProvider labelProvider, ViewerComparator viewerSorter) {
      this(dialogTitle, dialogMessage, contentProvider, labelProvider, viewerSorter);
      this.selectables = selectables;
   }

   public void setShowSelectButtons(boolean showSelectButtons) {
      this.showSelectButtons = showSelectButtons;
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
   public void setInitialSelections(Collection<T> initialSelections) {
      this.initialSelections = initialSelections;
      if (treeViewer != null) {
         getCheckboxTreeViewer().setCheckedElements(initialSelections.toArray());
      }
   }

   protected FilterableCheckboxTreeViewer getCheckboxTreeViewer() {
      return treeViewer.getCheckboxTreeViewer();
   }

   public Object[] getResult() {
      if (treeViewer == null) {
         return new Object[] {};
      }
      return getCheckboxTreeViewer().getCheckedElements();
   }

   @Override
   protected Control createCustomArea(Composite parent) {

      statusLabel = new Label(parent, SWT.NONE);
      statusLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      createPreCustomArea(parent);

      Composite aiComp = new Composite(parent, SWT.NONE);
      aiComp.setLayout(ALayout.getZeroMarginLayout());
      aiComp.setLayoutData(new GridData(GridData.FILL_BOTH));

      treeViewer = new FilteredCheckboxTree(aiComp,
         SWT.CHECK | (multiSelect ? SWT.MULTI : SWT.NONE) | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER,
         patternFilter);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 500;
      treeViewer.getViewer().getTree().setLayoutData(gd);
      treeViewer.getViewer().setContentProvider(contentProvider);
      treeViewer.getViewer().setLabelProvider(labelProvider);
      treeViewer.getViewer().setAutoExpandLevel(0);
      if (viewerComparator != null) {
         treeViewer.getViewer().setComparator(viewerComparator);
      }
      treeViewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            updateStatusLabel();
         }
      });

      if (selectables != null && !selectables.isEmpty()) {
         setInput(selectables);
         for (T selectable : selectables) {
            treeViewer.getCheckboxTreeViewer().getOrCreateItem(selectable);
         }
      }

      if (input != null) {
         treeViewer.getViewer().setInput(input);
      }
      if (initialSelections != null) {
         treeViewer.setInitalChecked(initialSelections);
      }
      updateStatusLabel();

      if (showSelectButtons) {
         Composite comp = new Composite(parent, SWT.NONE);
         comp.setLayout(new GridLayout(2, false));
         comp.setLayoutData(new GridData());

         Button selectAllButton = new Button(comp, SWT.PUSH);
         selectAllButton.setText("Select All");
         selectAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               treeViewer.setCheckAll(true);
               updateStatusLabel();
            }
         });

         Button deSelectAllButton = new Button(comp, SWT.PUSH);
         deSelectAllButton.setText("De-Select All");
         deSelectAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               treeViewer.setCheckAll(false);
               updateStatusLabel();
            }
         });
      }
      if (expandChecked) {
         getCheckboxTreeViewer().expandChecked();
      }

      createPostCustomArea(parent);

      return parent;
   }

   public void addCheckStateListener(ICheckStateListener listener) {
      getCheckboxTreeViewer().addCheckStateListener(listener);
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

   /**
    * @return the treeViewer
    */
   public FilteredCheckboxTree getTreeViewer() {
      return treeViewer;
   }

   public void setExpandChecked(boolean expandChecked) {
      this.expandChecked = expandChecked;
   }

   public void setMultiSelect(boolean multiSelect) {
      this.multiSelect = multiSelect;
   }

   public void setPatternFilter(PatternFilter patternFilter) {
      this.patternFilter = patternFilter;
   }

   @SuppressWarnings("unchecked")
   public Collection<T> getChecked() {
      List<T> checked = new ArrayList<>();
      for (Object obj : getResult()) {
         checked.add((T) obj);
      }
      return checked;
   }

   public Collection<T> getInitialSelections() {
      return initialSelections;
   }

   public void setContentProvider(IContentProvider contentProvider) {
      this.contentProvider = contentProvider;
   }

}
