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
package org.eclipse.osee.ote.ui.test.manager.panels;

import java.io.File;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.ote.ui.test.manager.OteTestManagerImage;
import org.eclipse.osee.ote.ui.test.manager.util.Dialogs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class FileOrDirectorySelectionPanel extends Composite {

   private static final Image FILE_SELECT_IMAGE = ImageManager.getImage(OteTestManagerImage.FILE);
   private static final String DIRECTORY_ERROR_MESSAGE = "Directory should be blank or set an accessible directory.";
   private static final String FILE_ERROR_MESSAGE = "Unable to access file.";
   private static final String DEFAULT_FILE = "/dev/null";

   private StyledText textField;
   private Label labelField;
   private String labelText;
   private String toolTipText;
   private boolean isDirectory;

   public FileOrDirectorySelectionPanel(Composite parent, int style, String labelText, String toolTipText, boolean isDirectory) {
      super(parent, style);
      this.labelText = labelText;
      this.toolTipText = toolTipText;
      this.isDirectory = isDirectory;
      GridLayout gl = new GridLayout(3, false);
      gl.marginHeight = 0;
      gl.marginWidth = 0;
      this.setLayout(gl);
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      createControl(this);
   }

   private void createControl(Composite parent) {
      labelField = new Label(parent, SWT.NONE);
      labelField.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
      labelField.setText(labelText);
      labelField.setToolTipText(toolTipText);

      textField = new StyledText(parent, SWT.BORDER);
      textField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      if (isDirectory != true) {
         textField.setText(DEFAULT_FILE);
      }
      Button fileDialog = new Button(parent, SWT.NONE);
      fileDialog.setLayoutData(new GridData(SWT.FILL, SWT.END, false, false));
      fileDialog.setImage(FILE_SELECT_IMAGE);
      fileDialog.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            createDialog();
         }
      });

   }

   private void createDialog() {
      Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
      String selection = "";
      if (isDirectory == true) {
         DirectoryDialog directoryDialog = new DirectoryDialog(shell, SWT.OPEN);

         String defaultDir = textField.getText();
         File dir = new File(defaultDir);
         if (dir.isFile() || dir.isDirectory()) {
            directoryDialog.setFilterPath(defaultDir);
         } else {
            directoryDialog.setFilterPath("Y:\\");
         }
         selection = directoryDialog.open();
      } else {
         FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
         selection = fileDialog.open();
      }
      setSelected(selection);
   }

   private void verifySelection() {
      String text = getSelected();
      if (isValid() != true) {
         if (isDirectory == true) {
            text = "";
         } else {
            text = DEFAULT_FILE;
         }
         Dialogs.popupError("Error", getErrorMessage());
      }
      if (textField.getText().equals(text) != true) {
         textField.setText(text);
      }
   }

   private boolean isValidFile(String text) {
      File file = new File(text);
      return file != null && file.exists() != false && file.canWrite() != false;
   }

   public boolean isValid() {
      boolean toReturn = false;
      String text = getSelected();
      if (isDirectory == true) {
         toReturn = Strings.isValid(text) == true ? isValidFile(text) : true;
      } else {
         //         if (Strings.isValid(text) == true) {
         //            if (text.equals(DEFAULT_FILE) == true) {
         toReturn = true;
         //            } else {
         //               toReturn = isValidFile(text);
         //            }
         //         }
      }
      return toReturn;
   }

   public String getErrorMessage() {
      return isDirectory == true ? DIRECTORY_ERROR_MESSAGE : FILE_ERROR_MESSAGE;
   }

   public String getSelected() {
      return textField != null && textField.isDisposed() != true ? textField.getText() : "";
   }

   public void setSelected(String value) {
      if (textField != null && textField.isDisposed() != true) {
         if (Strings.isValid(value)) {
            textField.setText(value);
         }
         verifySelection();
      }
   }
}
