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
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Robert A. Fisher
 */
public class DirectoryOrFileSelector extends Composite implements Listener {

   private Button radDirectory;
   private Button radSingleFile;
   private Text txtDirectory;
   private Text txtSingleFile;
   private Button btnDirectory;
   private Button btnSingleFile;

   /**
    * @param parent
    * @param style
    */
   public DirectoryOrFileSelector(Composite parent, int style, String name, Listener listener) {
      super(parent, style);
      GridLayout gdMain = new GridLayout();
      gdMain.marginHeight = 0;
      gdMain.marginWidth = 0;
      setLayout(gdMain);
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Group composite = new Group(this, style);
      composite.setText(name);
      GridLayout gd = new GridLayout();
      gd.numColumns = 3;
      composite.setLayout(gd);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      radDirectory = new Button(composite, SWT.RADIO);
      radDirectory.setText("Directory:");
      radDirectory.addListener(SWT.Selection, this);
      radDirectory.addListener(SWT.Selection, listener);
      txtDirectory = new Text(composite, SWT.SINGLE | SWT.BORDER);
      txtDirectory.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      txtDirectory.addListener(SWT.Modify, this);
      txtDirectory.addListener(SWT.Modify, listener);
      btnDirectory = new Button(composite, SWT.PUSH);
      btnDirectory.setText("&Browse...");
      btnDirectory.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            File directory = selectDirectory();
            if (directory != null && directory.isDirectory())
               txtDirectory.setText(directory.getPath());
         }

      });

      radSingleFile = new Button(composite, SWT.RADIO);
      radSingleFile.setText("File:");
      radSingleFile.addListener(SWT.Selection, this);
      radSingleFile.addListener(SWT.Selection, listener);
      txtSingleFile = new Text(composite, SWT.SINGLE | SWT.BORDER);
      txtSingleFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      txtSingleFile.addListener(SWT.Modify, this);
      txtSingleFile.addListener(SWT.Modify, listener);
      btnSingleFile = new Button(composite, SWT.PUSH);
      btnSingleFile.setText("&Browse...");
      btnSingleFile.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            File file = selectFile();
            if (file != null && file.isFile())
               txtSingleFile.setText(file.getPath());
         }

      });
   }

   private void updateWidgetEnablements() {
      boolean directorySelected = radDirectory.getSelection();

      txtDirectory.setEnabled(directorySelected);
      btnDirectory.setEnabled(directorySelected);

      txtSingleFile.setEnabled(!directorySelected);
      btnSingleFile.setEnabled(!directorySelected);
   }

   public File getFile() {
      File file;
      if (isDirectorySelected()) {
         file = new File(txtDirectory.getText());
      } else {
         file = new File(txtSingleFile.getText());
      }

      return file;
   }

   public String getText() {
      return (isDirectorySelected() ? txtDirectory : txtSingleFile).getText();
   }

   public boolean isDirectorySelected() {
      return radDirectory.getSelection();
   }

   public void setDirectorySelected(boolean selected) {
      radDirectory.setSelection(selected);
      radSingleFile.setSelection(!selected);

      updateWidgetEnablements();
   }

   public void setText(String text) {
      (isDirectorySelected() ? txtDirectory : txtSingleFile).setText(text);
   }

   public boolean validate(WizardDataTransferPage wizardPage) {
      if ((isDirectorySelected() && getFile().isDirectory()) || (!isDirectorySelected() && getFile().isFile()))
         return true;

      wizardPage.setErrorMessage(getText() + " is not a " + (isDirectorySelected() ? "directory" : "file"));
      return false;
   }

   private File selectFile() {
      FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SINGLE);
      File file = getFile();
      if (file != null && Strings.isValid(file.getAbsolutePath())) {
         dialog.setFilterPath(file.getAbsolutePath());
      } else {
         dialog.setFilterPath(AWorkspace.getWorkspacePath());
      }
      String path = dialog.open();

      if (path != null) {
         return new File(path);
      } else {
         return null;
      }
   }

   private File selectDirectory() {
      DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
      File file = getFile();
      if (file != null && Strings.isValid(file.getAbsolutePath())) {
         dialog.setFilterPath(file.getAbsolutePath());
      } else {
         dialog.setFilterPath(AWorkspace.getWorkspacePath());
      }
      String path = dialog.open();

      if (path != null) {
         return new File(path);
      } else {
         return null;
      }
   }

   public void handleEvent(Event event) {
      updateWidgetEnablements();
   }
}
