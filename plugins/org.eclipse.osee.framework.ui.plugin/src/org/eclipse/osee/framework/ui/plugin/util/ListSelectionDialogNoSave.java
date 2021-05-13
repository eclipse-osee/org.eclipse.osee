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
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class ListSelectionDialogNoSave extends MessageDialog {

   private List selectionList;
   private final java.util.List<Object> options;
   private int selectionIndex;

   public ListSelectionDialogNoSave(java.util.List<Object> options, Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
         defaultIndex);
      this.options = options;
   }

   @Override
   protected Control createCustomArea(Composite parent) {

      Composite comp = new Composite(parent, SWT.None);
      comp.setLayout(ALayout.getZeroMarginLayout());
      GridData gd = new GridData(SWT.CENTER, SWT.NONE, true, false);
      comp.setLayoutData(gd);

      selectionList = new List(comp, SWT.SINGLE | SWT.BORDER);
      for (Object option : options) {
         selectionList.add(option.toString());
      }

      selectionList.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {

         @Override
         public void mouseDoubleClick(MouseEvent e) {
            super.mouseDoubleClick(e);
            selectionIndex = selectionList.getSelectionIndex();
            setReturnCode(OK);
            close();
         }

      });

      selectionList.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            selectionIndex = selectionList.getSelectionIndex();
         }

      });

      selectionList.select(0);
      return parent;
   }

   public Object getSelected() {
      return options.get(getSelectionIndex());
   }

   public int getSelectionIndex() {
      return selectionIndex;
   }
}
