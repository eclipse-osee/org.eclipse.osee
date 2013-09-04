/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.ui.skynet.util.StringNameSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTreeContentProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author Angel Avila
 */
public class MultipleBranchSelectionDialog extends SelectionDialog {
   private CheckboxTreeViewer treeViewer;
   private final Set<Branch> checkedItems = new HashSet<Branch>();
   private final Set<Branch> selectable;

   public MultipleBranchSelectionDialog(Set<Branch> selectable, String title, String message) {
      super(Displays.getActiveShell());
      this.selectable = selectable;
      setTitle(title);
      setMessage(message);
   }

   @Override
   protected void createButtonsForButtonBar(Composite parent) {
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
      createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
      Button selectButton = createButton(parent, IDialogConstants.SELECT_ALL_ID, "Select All", false);
      Button deselectButton = createButton(parent, IDialogConstants.DESELECT_ALL_ID, "Deselect All", false);
      SelectionListener listener = new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            for (Branch branch : selectable) {
               checkedItems.add(branch);
               treeViewer.setChecked(branch, true);
            }
         }
      };
      selectButton.addSelectionListener(listener);

      SelectionListener listenerDeselect = new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            for (Branch branch : selectable) {
               checkedItems.clear();
               treeViewer.setChecked(branch, false);
            }
         }
      };

      deselectButton.addSelectionListener(listenerDeselect);
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 300;
      gd.widthHint = 400;
      comp.setLayoutData(gd);

      Label label = new Label(comp, SWT.NONE);
      gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = 2;
      label.setLayoutData(gd);

      treeViewer = new CheckboxTreeViewer(comp, SWT.MULTI | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      treeViewer.setContentProvider(new ArtifactTreeContentProvider());
      treeViewer.setSorter(new StringNameSorter());

      treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            checkedItems.clear();
            for (Object obj : treeViewer.getCheckedElements()) {
               checkedItems.add((Branch) obj);
            }
         };
      });
      treeViewer.setInput(selectable);
      return container;
   }

   public Collection<Branch> getSelectedBranches() {
      return checkedItems;
   }
}
