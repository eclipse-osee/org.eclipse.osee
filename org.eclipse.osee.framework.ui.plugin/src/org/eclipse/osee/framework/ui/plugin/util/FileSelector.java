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

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Robert A. Fisher
 */
public class FileSelector extends Composite {

   private Label radSingleFile;
   private Text txtSingleFile;
   private Button btnSingleFile;

   /**
    * @param parent
    * @param style
    */
   public FileSelector(Composite parent, int style, String name, Listener listener) {
      super(parent, style);
      setLayout(new GridLayout());
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Group composite = new Group(this, style);
      composite.setText(name);
      GridLayout gd = new GridLayout();
      gd.numColumns = 3;
      composite.setLayout(gd);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      radSingleFile = new Label(composite, SWT.NONE);
      radSingleFile.setText("File:");
      txtSingleFile = new Text(composite, SWT.SINGLE | SWT.BORDER);
      txtSingleFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      txtSingleFile.addListener(SWT.Modify, listener);
      btnSingleFile = new Button(composite, SWT.PUSH);
      btnSingleFile.setText("&Browse...");
      btnSingleFile.addSelectionListener(new SelectionAdapter() {

         /* (non-Javadoc)
          * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
          */
         @Override
         public void widgetSelected(SelectionEvent e) {
            File file = selectFile();
            if (file != null && file.isFile()) txtSingleFile.setText(file.getPath());
         }

      });
   }

   public File getFile() {
      File file = new File(txtSingleFile.getText());
      return file;
   }

   public String getText() {
      return txtSingleFile.getText();
   }

   public void setText(String text) {
      txtSingleFile.setText(text);
   }

   public boolean validate(WizardDataTransferPage wizardPage) {
      if (getText().endsWith(".xml") && getFile().isFile()) return true;

      wizardPage.setErrorMessage("File is not a valid xml file");
      return false;
   }

   private File selectFile() {
      FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SINGLE);
      dialog.setFilterExtensions(new String[] {"*.xml"});
      dialog.setFilterPath(AWorkspace.getWorkspacePath());

      String path = dialog.open();

      if (path != null) {
         return new File(path);
      } else {
         return null;
      }
   }

}
