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

import java.util.Collection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public abstract class OSEECheckedFilteredTreeDialog extends MessageDialog {

   protected Label statusLabel;
   protected Button okButton;
   private OSEECheckedFilteredTree treeViewer;
   private Object input;
   private final IContentProvider contentProvider;
   private final IBaseLabelProvider labelProvider;
   private Collection<? extends Object> initialSelections;
   private final ViewerSorter viewerSorter;
   private boolean showSelectButtons = false;

   public OSEECheckedFilteredTreeDialog(String dialogTitle, String dialogMessage, IContentProvider contentProvider, IBaseLabelProvider labelProvider, ViewerSorter viewerSorter) {
      super(new Shell(), dialogTitle, null, dialogMessage, MessageDialog.NONE, new String[] {"OK", "Cancel"}, 0);
      this.contentProvider = contentProvider;
      this.labelProvider = labelProvider;
      this.viewerSorter = viewerSorter;
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   public void setShowSelectButtons(boolean showSelectButtons) {
      this.showSelectButtons = showSelectButtons;
   }

   protected void createPreCustomArea(Composite parent) {
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

   /**
    * Sets the initial selection. Convenience method.
    * 
    * @param object the initial selection.
    */
   public void setInitialSelections(Collection<? extends Object> initialSelections) {
      this.initialSelections = initialSelections;
      if (treeViewer != null) {
         treeViewer.setInitalChecked(initialSelections);
      }
   }

   public Object[] getResult() {
      if (treeViewer == null) {
         return new Object[] {};
      }
      return treeViewer.getResult();
   }

   @Override
   protected Control createCustomArea(Composite parent) {

      statusLabel = new Label(parent, SWT.NONE);
      statusLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
      createPreCustomArea(parent);

      Composite aiComp = new Composite(parent, SWT.NONE);
      aiComp.setLayout(ALayout.getZeroMarginLayout());
      aiComp.setLayoutData(new GridData(GridData.FILL_BOTH));

      treeViewer =
         new OSEECheckedFilteredTree(aiComp,
            SWT.MULTI | SWT.CHECK | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 500;
      treeViewer.getViewer().getTree().setLayoutData(gd);
      treeViewer.getViewer().setContentProvider(contentProvider);
      treeViewer.getViewer().setLabelProvider(labelProvider);
      if (viewerSorter != null) {
         treeViewer.getViewer().setSorter(viewerSorter);
      }
      treeViewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            updateStatusLabel();
         }
      });
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
               getTreeViewer().setInitalChecked(((Collection<?>) getTreeViewer().getViewer().getInput()));
               updateStatusLabel();
            }
         });

         Button deSelectAllButton = new Button(comp, SWT.PUSH);
         deSelectAllButton.setText("De-Select All");
         deSelectAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               getTreeViewer().clearChecked();
               updateStatusLabel();
            }
         });
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
   public OSEECheckedFilteredTree getTreeViewer() {
      return treeViewer;
   }

}
