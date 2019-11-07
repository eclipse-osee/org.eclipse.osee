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
package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class ListSelectionDialogNoSave extends MessageDialog {

   private List selections;
   private int selectionIndex;
   private final Object[] choose;

   public ListSelectionDialogNoSave(Object[] choose, Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
         defaultIndex);
      this.choose = choose;
   }

   @Override
   protected Control createCustomArea(Composite parent) {
      selections = new List(parent, SWT.SINGLE | SWT.BORDER);
      for (int i = 0; i < choose.length; i++) {
         selections.add(choose[i].toString());
      }

      selections.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {

         @Override
         public void mouseDoubleClick(MouseEvent e) {
            super.mouseDoubleClick(e);
            selectionIndex = selections.getSelectionIndex();
            setReturnCode(OK);
            close();
         }

      });

      selections.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            selectionIndex = selections.getSelectionIndex();
         }

      });

      selections.select(0);
      return parent;
   }

   public int getSelection() {
      return selectionIndex;
   }
}
