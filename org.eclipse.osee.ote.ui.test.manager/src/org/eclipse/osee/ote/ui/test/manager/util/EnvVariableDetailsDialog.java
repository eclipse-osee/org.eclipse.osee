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
package org.eclipse.osee.ote.ui.test.manager.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Roberto E. Escobar
 */
public class EnvVariableDetailsDialog extends MessageDialog {

   private Button cancelButton;
   private String dialogMessage;

   private Button okButton;

   private Button importButton;

   private String selection;
   private boolean selectionOk;
   private StyledText textArea;


   public EnvVariableDetailsDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex,
         String oldValue) {
      super(parentShell, dialogTitle, dialogTitleImage, null, dialogImageType, dialogButtonLabels, defaultIndex);

      this.selectionOk = false;
      this.selection = oldValue;
      this.dialogMessage = dialogMessage;
   }

   public String getSelection() {
      return selection;
   }

   public boolean isValid() {
      return selectionOk;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control c = super.createButtonBar(parent);
      okButton = getButton(0);
      cancelButton = getButton(1);

      okButton.addSelectionListener(new SelectionListener() {

         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         public void widgetSelected(SelectionEvent e) {
            selectionOk = true;

         }
      });

      cancelButton.addSelectionListener(new SelectionListener() {

         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         public void widgetSelected(SelectionEvent e) {
            selectionOk = false;
         }
      });
      return c;
   }

   @Override
   protected Control createCustomArea(Composite parent) {
      // super.createCustomArea(parent);

      GridData d = new GridData(GridData.FILL);

      Group setValueGroup = new Group(parent, SWT.NONE);
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      setValueGroup.setLayout(gridLayout);
      setValueGroup.setText(dialogMessage);

      Composite topLevelComposite = new Composite(setValueGroup, SWT.NONE);
      gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      topLevelComposite.setLayout(gridLayout);
      topLevelComposite.setLayoutData(d);

      importButton = new Button(setValueGroup, SWT.NONE);
      importButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
      importButton.setText("Import");
      importButton.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            FileDialog dialog = new FileDialog(EnvVariableDetailsDialog.this.getShell(), SWT.OPEN);

            String result = dialog.open();
            if (result != null && !result.equals("")) {
               String importedData;
               try {
                  importedData = Lib.fileToString(new File(result));
                  textArea.setText(textArea.getText() + importedData);
                  selection = textArea.getText();
               }
               catch (IOException ex) {
                  OseeLog.log(TestManagerPlugin.class, Level.SEVERE, ex.getMessage(), ex);
               }
            }
         }
      });

      textArea = new StyledText(setValueGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
      GridData gd = new GridData();
      gd.grabExcessHorizontalSpace = true;
      gd.horizontalAlignment = GridData.FILL;
      gd.horizontalSpan = 1;
      gd.heightHint = 200;
      gd.widthHint = 400;
      textArea.setLayoutData(gd);

      textArea.setText((selection != null ? selection : ""));

      textArea.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            selection = textArea.getText();
         }
      });

      return parent;
   }

}
