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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeDialog extends ListDialog {

   ChangeType selected = null;

   public ChangeTypeDialog(Shell parent) {
      super(parent);
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new ChangeLabelProvider());
      setInput(ChangeType.values());
      setShellStyle(getShellStyle() | SWT.RESIZE);
      setTitle("Select Changet Type");
   }

   public ChangeType getSelection() {
      return (ChangeType) getResult()[0];
   }

   @Override
   protected Control createDialogArea(Composite container) {

      (new Label(container, SWT.NONE)).setText("     Select Change Type:");

      Control c = super.createDialogArea(container);
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 80;
      gd.widthHint = 300;
      getTableViewer().getTable().setLayoutData(gd);
      GridLayout layout = ALayout.getZeroMarginLayout();
      layout.marginWidth = 20;
      getTableViewer().getTable().getParent().setLayout(layout);
      if (selected != null) {
         ArrayList<Object> sel = new ArrayList<Object>();
         sel.add(selected);
         getTableViewer().setSelection(new StructuredSelection(sel.toArray(new Object[sel.size()])));
         getTableViewer().getTable().setFocus();
      }
      return c;
   }

   @Override
   protected void okPressed() {
      if (getTableViewer().getSelection().isEmpty()) {
         AWorkbench.popup("ERROR", "Must make selection.");
         return;
      }
      super.okPressed();
   }

   public class ChangeLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         ChangeType type = (ChangeType) arg0;
         return type.getImage();
      }

      public String getText(Object arg0) {
         ChangeType type = (ChangeType) arg0;
         return type.name();
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }

   }

   public void setSelected(ChangeType selected) {
      this.selected = selected;
   }

}
