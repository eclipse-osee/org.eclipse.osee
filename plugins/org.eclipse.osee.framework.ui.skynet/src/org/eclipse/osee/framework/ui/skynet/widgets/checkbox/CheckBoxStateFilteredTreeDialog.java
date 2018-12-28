/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.checkbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.util.Result;
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

public class CheckBoxStateFilteredTreeDialog<T> extends MessageDialog {

   protected Label statusLabel;
   protected Button okButton;
   private CheckBoxStateFilteredTreeViewer<T> treeViewer;
   private Object input;
   private IContentProvider contentProvider;
   private final CheckBoxStateTreeLabelProvider labelProvider;
   private Collection<T> initialSelections;
   private final ViewerComparator viewerComparator;
   private boolean showSelectButtons = false;
   private boolean expandChecked = false;
   private PatternFilter patternFilter;
   private Collection<T> selectables;

   public CheckBoxStateFilteredTreeDialog(String dialogTitle, String dialogMessage, IContentProvider contentProvider, CheckBoxStateTreeLabelProvider labelProvider) {
      this(dialogTitle, dialogMessage, contentProvider, labelProvider, null);
   }

   public CheckBoxStateFilteredTreeDialog(String dialogTitle, String dialogMessage, IContentProvider contentProvider, CheckBoxStateTreeLabelProvider labelProvider, ViewerComparator viewerSorter) {
      super(Displays.getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.NONE,
         new String[] {"OK", "Cancel"}, 0);
      this.contentProvider = contentProvider;
      this.labelProvider = labelProvider;
      this.viewerComparator = viewerSorter;
      if (patternFilter == null) {
         this.patternFilter = new ToStringContainsPatternFilter();
      } else {
         patternFilter = new PatternFilter();
      }
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   public CheckBoxStateFilteredTreeDialog(String dialogTitle, String dialogMessage, Collection<T> selectables, IContentProvider contentProvider, CheckBoxStateTreeLabelProvider labelProvider, ViewerComparator viewerSorter) {
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
   public final void setInput(Object input) {
      this.input = input;
      if (treeViewer != null) {
         treeViewer.getViewer().setInput(input);
      }
   }

   public void setInitialSelections(Collection<T> initialSelections) {
      this.initialSelections = initialSelections;
      if (treeViewer != null) {
         getCheckboxTreeViewer().setChecked(initialSelections);
      }
   }

   protected CheckBoxStateFilteredTreeViewer<T> getCheckboxTreeViewer() {
      return treeViewer;
   }

   public Collection<T> getResult() {
      if (treeViewer == null) {
         return Collections.emptyList();
      }
      return treeViewer.getChecked();
   }

   @Override
   protected Control createCustomArea(Composite parent) {

      statusLabel = new Label(parent, SWT.NONE);
      statusLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      createPreCustomArea(parent);

      Composite comp = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(1, true);
      layout.marginWidth = 0;
      comp.setLayout(layout);
      comp.setLayoutData(new GridData(GridData.FILL_BOTH));

      treeViewer =
         new CheckBoxStateFilteredTreeViewer<>(comp, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 500;
      treeViewer.getViewer().getTree().setLayoutData(gd);
      treeViewer.getViewer().setContentProvider(contentProvider);
      labelProvider.setTreeViewer(treeViewer);
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
      }

      if (input != null) {
         treeViewer.getViewer().setInput(input);
      }
      if (initialSelections != null) {
         treeViewer.setChecked(initialSelections);
         treeViewer.getViewer().refresh();
      }
      updateStatusLabel();

      if (showSelectButtons) {
         Composite buttonComp = new Composite(parent, SWT.NONE);
         buttonComp.setLayout(new GridLayout(2, false));
         buttonComp.setLayoutData(new GridData());

         Button selectAllButton = new Button(buttonComp, SWT.PUSH);
         selectAllButton.setText("Select All");
         selectAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               treeViewer.setChecked(getAllItems());
               updateStatusLabel();
            }
         });

         Button deSelectAllButton = new Button(buttonComp, SWT.PUSH);
         deSelectAllButton.setText("De-Select All");
         deSelectAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               treeViewer.deSelectAll();
               updateStatusLabel();
            }
         });
      }
      if (expandChecked) {
         treeViewer.expandChecked();
      }

      createPostCustomArea(parent);

      return parent;
   }

   protected Collection<T> getAllItems() {
      return null;
   }

   public void addCheckStateListener(ICheckBoxStateTreeListener listener) {
      getCheckboxTreeViewer().addCheckListener(listener);
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

   public void setExpandChecked(boolean expandChecked) {
      this.expandChecked = expandChecked;
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

   public CheckBoxStateFilteredTreeViewer<T> getTreeViewer() {
      return treeViewer;
   }

}
