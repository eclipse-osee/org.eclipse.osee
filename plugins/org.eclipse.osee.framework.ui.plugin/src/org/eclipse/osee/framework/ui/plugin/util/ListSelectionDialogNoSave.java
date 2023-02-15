/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class ListSelectionDialogNoSave extends MessageDialog {

   private ListViewer selectionList;
   private final java.util.List<Object> options;
   private Object selected;
   private final IBaseLabelProvider labelProvider;

   public ListSelectionDialogNoSave(java.util.List<Object> options, Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      this(options, parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
         defaultIndex, new StringLabelProvider());
   }

   public ListSelectionDialogNoSave(java.util.List<Object> options, Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex, IBaseLabelProvider labelProvider) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
         defaultIndex);
      this.options = options;
      this.labelProvider = labelProvider;
   }

   @Override
   protected Control createCustomArea(Composite parent) {

      Composite comp = new Composite(parent, SWT.None);
      comp.setLayout(ALayout.getZeroMarginLayout());
      GridData gd = new GridData(SWT.CENTER, SWT.NONE, true, false);
      comp.setLayoutData(gd);

      selectionList = new ListViewer(comp);
      selectionList.setLabelProvider(labelProvider);
      selectionList.setContentProvider(new ArrayContentProvider());
      //   TBD   selectionList.getList().setSingleSelection??
      selectionList.setInput(options);
      selectionList.getList().addMouseListener(new org.eclipse.swt.events.MouseAdapter() {

         @Override
         public void mouseDoubleClick(MouseEvent e) {
            super.mouseDoubleClick(e);
            selected = selectionList.getStructuredSelection().getFirstElement();
            setReturnCode(OK);
            close();
         }

      });

      selectionList.getList().addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            selected = selectionList.getStructuredSelection().getFirstElement();
         }

      });

      selectionList.getList().select(0);
      selected = options.iterator().next();
      return parent;
   }

   public Object getSelected() {
      return selected;
   }

}
