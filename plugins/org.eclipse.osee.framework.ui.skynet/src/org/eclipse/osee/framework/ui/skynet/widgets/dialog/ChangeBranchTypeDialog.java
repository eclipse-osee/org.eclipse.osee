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
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
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
 * @author Megumi Telles
 */
public class ChangeBranchTypeDialog extends ListDialog {

   BranchType selected = null;

   public ChangeBranchTypeDialog(Shell parent) {
      super(parent);
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new ChangeLabelProvider());
      setInput(BranchType.values());
      setShellStyle(getShellStyle() | SWT.RESIZE);
      setTitle("Select Branch Type");
   }

   public BranchType getSelection() {
      return (BranchType) getResult()[0];
   }

   @Override
   protected Control createDialogArea(Composite container) {

      new Label(container, SWT.NONE).setText("     Select Branch Type:");

      Control c = super.createDialogArea(container);
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 80;
      gd.widthHint = 300;
      getTableViewer().getTable().setLayoutData(gd);
      GridLayout layout = ALayout.getZeroMarginLayout();
      layout.marginWidth = 20;
      getTableViewer().getTable().getParent().setLayout(layout);
      if (selected != null) {
         ArrayList<Object> sel = new ArrayList<>();
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

      @Override
      public Image getImage(Object arg0) {
         return null;
      }

      @Override
      public String getText(Object arg0) {
         BranchType type = (BranchType) arg0;
         return type.getName();
      }

      @Override
      public void addListener(ILabelProviderListener arg0) {
         // do nothing
      }

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      @Override
      public void removeListener(ILabelProviderListener arg0) {
         // do nothing
      }

   }

   public void setSelected(BranchType selected) {
      this.selected = selected;
   }

}
