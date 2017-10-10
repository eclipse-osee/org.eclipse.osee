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
package org.eclipse.osee.ote.ui.define.panels;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("deprecation")
public class ListSelectionPanel extends Composite {

   private TableViewer tableViewer;
   private final IBaseLabelProvider labelProvider;
   private final int width;
   private final int height;

   public ListSelectionPanel(Composite parent, int style, int width, int height, IBaseLabelProvider labelProvider) {
      super(parent, style);
      this.width = width;
      this.height = height;
      this.labelProvider = labelProvider;
      createControl(this);
   }

   private void createControl(Composite parent) {
      GridLayout layout = new GridLayout();
      layout.marginWidth = 0;
      layout.marginHeight = 0;

      parent.setLayout(layout);
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      tableViewer = new TableViewer(parent, getTableStyle());
      tableViewer.setContentProvider(new ArrayContentProvider());
      tableViewer.setLabelProvider(labelProvider);
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = width;
      gd.widthHint = height;
      Table table = tableViewer.getTable();
      table.setLayoutData(gd);
      table.setFont(parent.getFont());
   }

   private int getTableStyle() {
      return SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
   }

   public void addDoubleClickListener(IDoubleClickListener listener) {
      tableViewer.addDoubleClickListener(listener);
   }

   public void addSelectionChangedListener(ISelectionChangedListener listener) {
      tableViewer.addSelectionChangedListener(listener);
   }

   public void setInput(Object input) {
      tableViewer.setInput(input);
   }

   public void setSelection(int index) {
      tableViewer.getTable().select(index);
   }

   public void setSorter(ViewerSorter sorter) {
      tableViewer.setSorter(sorter);
   }

   public IStructuredSelection getSelection() {
      return (IStructuredSelection) tableViewer.getSelection();
   }

   public int indexOf(Object object) {
      int found = -1;
      TableItem[] items = tableViewer.getTable().getItems();
      for (int index = 0; index < items.length; index++) {
         TableItem item = items[index];
         if (item.getData().equals(object)) {
            found = index;
            break;
         }
      }
      return found;
   }
}
