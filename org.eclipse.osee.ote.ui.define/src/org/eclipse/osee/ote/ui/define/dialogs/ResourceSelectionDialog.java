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
package org.eclipse.osee.ote.ui.define.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

public class ResourceSelectionDialog extends CheckedTreeSelectionDialog {

   public ResourceSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
      super(parent, labelProvider, contentProvider);
   }

   protected Control createDialogArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
      layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
      layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
      layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(GridData.FILL_BOTH));
      applyDialogFont(composite);

      Label messageLabel = createMessageArea(composite);
      CheckboxTreeViewer treeViewer = createTreeViewer(composite);
      GridData data = new GridData(GridData.FILL_BOTH);
      data.widthHint = convertWidthInCharsToPixels(80);
      data.heightHint = convertHeightInCharsToPixels(16);
      Tree treeWidget = treeViewer.getTree();
      treeViewer.addCheckStateListener(new ICheckStateListener() {

         public void checkStateChanged(CheckStateChangedEvent event) {
            boolean wasChecked = event.getChecked();
            CheckboxTreeViewer viewer = getTreeViewer();
            TreeItem[] items = viewer.getTree().getItems();
            for (int i = 0; i < items.length; i++) {
               viewer.setSubtreeChecked(items[i], false);
            }
            event.getCheckable().setChecked(event.getElement(), wasChecked);
         }

      });
      treeWidget.setLayoutData(data);
      treeWidget.setFont(parent.getFont());
      messageLabel.setEnabled(true);
      treeWidget.setEnabled(true);
      return composite;
   }
};
