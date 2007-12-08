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
package org.eclipse.osee.framework.ui.skynet.util;

import java.sql.SQLException;
import java.util.ArrayList;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class BranchSelectionDialog extends ListDialog {

   Branch selected = null;

   public BranchSelectionDialog(Shell parent) {
      super(parent);
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new BranchLabelProvider());
      try {
         setInput(BranchPersistenceManager.getInstance().getBranches());
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
      setShellStyle(getShellStyle() | SWT.RESIZE);
      setTitle("Select Branch");
      setMessage("Select Branch");
   }

   public Branch getSelection() {
      return (Branch) getResult()[0];
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Control c = super.createDialogArea(container);
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

   public class BranchLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object arg0) {
         Branch type = (Branch) arg0;
         return type.getBranchName();
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

   public void setSelected(Branch selected) {
      this.selected = selected;
   }

}
