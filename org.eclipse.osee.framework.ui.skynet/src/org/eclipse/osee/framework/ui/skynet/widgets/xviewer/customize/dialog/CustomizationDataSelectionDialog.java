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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.dialog;

import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeDataLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class CustomizationDataSelectionDialog extends ListDialog {

   private XText custText;
   private String enteredName;
   private boolean saveGlobal = false;
   private XCheckBox saveGlobalCheck;
   private CustomizeData selectedCustData = null;

   public CustomizationDataSelectionDialog(XViewer xViewer, List<CustomizeData> custDatas) {
      this(Display.getCurrent().getActiveShell(), xViewer, custDatas);
   }

   public CustomizationDataSelectionDialog(Shell parent, XViewer xViewer, List<CustomizeData> custDatas) {
      super(Display.getCurrent().getActiveShell());
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new CustomizeDataLabelProvider(xViewer));
      setInput(custDatas);
      setShellStyle(getShellStyle() | SWT.RESIZE);
      setTitle("Save Customization");
      setMessage("Enter name or select customization.");
   }

   @Override
   protected void okPressed() {
      if (custText.get().equals("") && getSelectedCustData() == null) {
         AWorkbench.popup("ERROR", "Must select customization or enter new customization name.");
         return;
      }
      super.okPressed();
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Control c = super.createDialogArea(container);

      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      comp.setLayout(new GridLayout(2, true));

      custText = new XText("Enter New Customization Name");
      custText.createWidgets(comp, 1);
      custText.setFocus();
      custText.addModifyListener(new ModifyListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
          */
         public void modifyText(ModifyEvent e) {
            enteredName = custText.get();
         }
      });

      if (OseeAts.isAtsAdmin()) {
         comp = new Composite(container, SWT.NONE);
         comp.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
         comp.setLayout(new GridLayout(2, false));

         saveGlobalCheck = new XCheckBox("Save Global");
         saveGlobalCheck.createWidgets(comp, 1);
         saveGlobalCheck.addSelectionListener(new SelectionAdapter() {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
               saveGlobal = saveGlobalCheck.get();
            }
         });
      }

      getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
          */
         public void selectionChanged(SelectionChangedEvent event) {
            selectedCustData = getSelectedCustomizeData();
            if (saveGlobalCheck != null) {
               saveGlobalCheck.set(!selectedCustData.isPersonal());
               saveGlobal = !selectedCustData.isPersonal();
            }
         }
      });
      return c;
   }

   private CustomizeData getSelectedCustomizeData() {
      IStructuredSelection selection = (IStructuredSelection) getTableViewer().getSelection();
      if (selection.size() == 0) return null;
      Iterator<?> i = selection.iterator();
      return (CustomizeData) i.next();
   }

   /**
    * @return the selectedCustData
    */
   public CustomizeData getSelectedCustData() {
      return selectedCustData;
   }

   public String getEnteredName() {
      return enteredName;
   }

   public boolean isSaveGlobal() {
      return saveGlobal;
   }

   public void setSaveGlobal(boolean saveGlobal) {
      this.saveGlobal = saveGlobal;
   }

}
