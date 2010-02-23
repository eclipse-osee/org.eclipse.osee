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
package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class ChangePointDialog extends ListDialog {

   String selected = null;
   boolean clearSelected = false;

   public ChangePointDialog(Shell parent) {
      super(parent);
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new PointLabelProvider());
      try {
         setInput(AttributeTypeManager.getEnumerationValues(ATSAttributes.POINTS_ATTRIBUTE.getStoreName()));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      setShellStyle(getShellStyle() | SWT.RESIZE);
      setTitle("Select Point");
   }

   public String getSelection() {
      return (String) getResult()[0];
   }

   @Override
   protected Control createDialogArea(Composite container) {

      (new Label(container, SWT.NONE)).setText("     Select Point:");

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

      Button clearButton = new Button(getTableViewer().getTable().getParent(), SWT.PUSH);
      clearButton.setText("clear");
      clearButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            clearSelected = true;
            close();
         }
      });
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

   public class PointLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object arg0) {
         return arg0.toString();
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

   public void setSelected(String selected) {
      this.selected = selected;
   }

   public boolean isClearSelected() {
      return clearSelected;
   }

}
