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
package org.eclipse.osee.framework.ui.skynet.util.filteredTree;

import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PatternFilter;

public abstract class OSEEFilteredTreeDialog<T> extends MessageDialog {

   protected Label statusLabel;
   private Button okButton;
   private OSEEFilteredTree treeViewer;
   private final PatternFilter patternFilter;
   private boolean checkTree = true;
   private boolean multiSelect = true;
   private T input;
   private T initialSelections;
   private final IContentProvider contentProvider;
   private final IBaseLabelProvider labelProvider;

   public OSEEFilteredTreeDialog(String dialogTitle, String dialogMessage, IBaseLabelProvider labelProvider, IContentProvider contentProvider, PatternFilter patternFilter) {
      super(Display.getCurrent().getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.NONE, new String[] {
            "OK", "Cancel"}, 0);
      this.contentProvider = contentProvider;
      this.labelProvider = labelProvider;
      this.patternFilter = patternFilter;
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   protected void createPreCustomArea(Composite parent) {
   }

   /**
    * Sets the input. Convenience method.
    * 
    * @param object the input.
    */
   public final void setInput(T input) {
      this.input = input;
      if (getTreeViewer() != null) {
         getTreeViewer().getViewer().setInput(input);
      }
   }

   public T getInput() {
      return input;
   }

   /**
    * Sets the initial selection. Convenience method.
    * 
    * @param object the initial selection.
    */
   public void setInitialSelections(T initialSelections) {
      this.initialSelections = initialSelections;
      if (getTreeViewer() != null) {
         updateInitialSelections(this.initialSelections);
      }
   }

   @SuppressWarnings("unchecked")
   private void updateInitialSelections(T object) {
      if (object != null) {
         ISelection selection;
         if (object instanceof List<?>) {
            selection = new StructuredSelection((List) object);
         } else {
            selection = new StructuredSelection(object);
         }
         getTreeViewer().getViewer().setSelection(selection, true);
      }
   }

   @Override
   protected Control createCustomArea(Composite parent) {

      statusLabel = new Label(parent, SWT.NONE);
      statusLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      updateStatusLabel();

      createPreCustomArea(parent);

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout());
      comp.setLayoutData(new GridData(GridData.FILL_BOTH));

      treeViewer =
            new OSEECheckedFilteredTree(
                  comp,
                  (multiSelect ? SWT.MULTI : SWT.SINGLE) | (isCheckTree() ? SWT.CHECK : SWT.NONE) | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER,
                  patternFilter);
      TreeViewer viewer = treeViewer.getViewer();
      viewer.setContentProvider(contentProvider);
      viewer.setLabelProvider(labelProvider);
      viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      treeViewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            updateStatusLabel();
         }
      });
      if (input != null) {
         treeViewer.getViewer().setInput(input);
      }
      updateInitialSelections(this.initialSelections);
      if (initialSelections != null) {
         updateInitialSelections(initialSelections);
      }
      return parent;
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
   public OSEEFilteredTree getTreeViewer() {
      return treeViewer;
   }

   /**
    * @return the isCheckTree
    */
   public boolean isCheckTree() {
      return checkTree;
   }

   /**
    * @param isCheckTree the isCheckTree to set
    */
   public void setCheckTree(boolean checkTree) {
      this.checkTree = checkTree;
   }

   /**
    * @return the isMultiSelect
    */
   public boolean isMultiSelect() {
      return multiSelect;
   }

   /**
    * @param isMultiSelect the isMultiSelect to set
    */
   public void setMultiSelect(boolean multiSelect) {
      this.multiSelect = multiSelect;
   }

}
