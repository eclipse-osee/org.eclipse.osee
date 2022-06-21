/*********************************************************************
 * Copyright (c) 2022 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.reqif.dialogs;

import java.io.File;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Export ReqIf Dialog
 *
 * @author Manjunath Sangappa
 */
public class ExportReqIfDialog extends TitleAreaDialog {

   private Combo fileText;
   private Combo fileTypeCombo;
   private Button browseButton;
   private String sDirName = "";
   private String sFileName = "";

   /**
    * @param parentShell
    */
   public ExportReqIfDialog(final Shell parentShell) {
      super(parentShell);

   }


   @Override
   protected Control createContents(final Composite parent) {
      Control contents = super.createContents(parent);
      setTitle("Export Requirments into REQIF format");
      parent.getShell().setText("Export Requirments");
      getButton(IDialogConstants.OK_ID).setEnabled(false);
      return contents;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Control createDialogArea(final Composite parent) {
      Composite fileSelectionArea = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(3, false);
      fileSelectionArea.setLayout(layout);
      GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
      fileSelectionArea.setLayoutData(layoutData);

      Label label1 = new Label(fileSelectionArea, SWT.NULL);
      label1.setText("Name");

      this.fileText = new Combo(fileSelectionArea, SWT.NULL);
      GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
      this.fileText.setLayoutData(gridData1);

      // Set Listener
      this.fileText.addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(final ModifyEvent e) {
            validateCombo();
         }
      });

      Label dummyLabel = new Label(fileSelectionArea, SWT.NULL);
      dummyLabel.setText("");

      Label label2 = new Label(fileSelectionArea, SWT.NULL);
      label2.setText("To Directory:");

      this.fileTypeCombo = new Combo(fileSelectionArea, SWT.NONE);
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.widthHint = 250;
      this.fileTypeCombo.setLayoutData(gridData);

      // Set Listener
      this.fileTypeCombo.addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(final ModifyEvent e) {
            validateCombo();
         }
      });

      this.browseButton = new Button(fileSelectionArea, SWT.PUSH);
      this.browseButton.setText("Browse");
      this.browseButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(final SelectionEvent e) {
            DirectoryDialog dlg = new DirectoryDialog(Displays.getActiveShell());
            dlg.setText("Select the export directory");
            dlg.setMessage("Select the export directory");
            String dir = dlg.open();
            if (dir != null) {
               ExportReqIfDialog.this.fileTypeCombo.setText(dir);
            }
            validateCombo();
         }
      });
      return fileSelectionArea;
   }


   /**
    * Validate options provided in Combo.
    */
   private void validateCombo() {
      boolean isPageComplete = true;
      String fileNameString = this.fileText.getText();
      if ((fileNameString == null) || (fileNameString.length() == 0)) {
         setMessage("Please enter a fileName");
         isPageComplete = false;
         if (validateString(fileNameString)) {
            setMessage("Please enter a valid fileName");
            isPageComplete = false;
         }
         else {
            isPageComplete = true;
         }
      }
      else if (isPageComplete) {
         String selectedFile = this.fileTypeCombo.getText();
         if ((selectedFile != null) && !"".equals(selectedFile)) {


            File f = new File(selectedFile);
            if (f.exists() && f.isDirectory()) {
               setMessage(null);
            }
            else {
               setMessage("Directory does not exist");
               isPageComplete = false;
            }
         }
         else {
            setMessage("Select the export directory");
            isPageComplete = false;
         }
      }
      if (isPageComplete) {
         setMessage("");
      }
      getButton(IDialogConstants.OK_ID).setEnabled(isPageComplete);
   }

   /**
    * @param selectedPrj
    */
   private boolean validateString(final String selectedPrj) {
      char[] charArray = selectedPrj.toCharArray();
      boolean isValid = Character.isJavaIdentifierStart(charArray[0]);
      if (isValid) {
         for (int i = 1; i < charArray.length; i++) {
            if (!Character.isJavaIdentifierPart(charArray[i])) {
               return false;
            }
         }
      }
      return isValid;
   }

   /**
    * @return the sDirName
    */
   public String getDirName() {
      return this.sDirName;
   }

   /**
    * @return the sFileName
    */
   public String getFileName() {
      return this.sFileName;
   }


   @Override
   protected void createButtonsForButtonBar(final Composite parent) {
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
      createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
   }

   @Override
   protected void okPressed() {
      saveInput();
      super.okPressed();
   }

   private void saveInput() {
      this.sFileName = this.fileText.getText();
      this.sDirName = this.fileTypeCombo.getText();
   }

}