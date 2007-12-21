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
package org.eclipse.osee.framework.ui.encrypter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.security.DESEncrypter;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Jeff C. Phillips
 */
public class EncrypterView extends ViewPart implements IActionable {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.encrypter.EncrypterView";
   private DESEncrypter encrypter;
   private Text applicationPath;
   private File input;
   private File encrypt;
   private File decrypt;
   private String pathResults;

   /**
    * The constructor.
    */
   public EncrypterView() {
      pathResults = "";
   }

   /**
    * This is a callback that will allow us to create the viewer and initialize it.
    */
   public void createPartControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NULL);
      GridLayout layout = new GridLayout(8, false);
      composite.setLayout(layout);

      Label description = new Label(composite, SWT.NONE);
      description.setText("Encrypt / Decrypt a file.");
      GridData gridData = new GridData();
      gridData.horizontalSpan = 8;
      description.setLayoutData(gridData);

      applicationPath = new Text(composite, SWT.BORDER);
      gridData = new GridData();
      gridData.widthHint = 450;
      gridData.horizontalSpan = 6;
      applicationPath.setLayoutData(gridData);

      Button browseButton = new Button(composite, SWT.PUSH);
      browseButton.setText("Browse");

      Button editButton = new Button(composite, SWT.PUSH);
      editButton.setText("Edit");

      Button encryptButton = new Button(composite, SWT.PUSH);
      encryptButton.setText("Encrypt");

      Button decryptButton = new Button(composite, SWT.PUSH);
      decryptButton.setText("Decrypt");

      browseButton.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent event) {
            browseForFile();
         }
      });

      editButton.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent event) {
            editFile();
         }
      });

      encryptButton.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent event) {
            encrypt();
         }
      });

      decryptButton.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent event) {
            decrypt();
         }
      });

      OseeAts.addBugToViewToolbar(this, this, EncrypterPlugin.getInstance(), VIEW_ID, "Admin");

   }

   protected void editFile() {
      if (!pathResults.equals("")) Program.launch(pathResults);
   }

   /**
    * Decrypts a file
    */
   protected void decrypt() {
      String password = getPassWord();
      try {

         if (password.equals("")) {
            invalidPassPhraseMsg();
         } else if (password.equals("Canceled")) {
            return;
         } else {
            if (pathResults.length() != 0) {
               encrypter = new DESEncrypter(password);
               encrypter.decrypt(new FileInputStream(input), new FileOutputStream(decrypt));
               cleanUpFiles(decrypt, input);
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Encrypts a file
    */
   protected void encrypt() {
      String password = getPassWord();
      try {

         if (password.equals("")) {
            invalidPassPhraseMsg();
         } else if (password.equals("Canceled")) {
            return;
         } else {
            if (pathResults.length() != 0) {
               encrypter = new DESEncrypter(password);
               encrypter.encrypt(new FileInputStream(input), new FileOutputStream(encrypt));
               cleanUpFiles(encrypt, input);
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private void browseForFile() {
      FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
      pathResults = fileDialog.open();

      if ((pathResults != null)) {
         applicationPath.setText(pathResults);
         input = new File(pathResults);
         encrypt = new File(input.getPath().substring(0, input.getPath().lastIndexOf("\\") + 1) + "encrpyt");
         decrypt = new File(input.getPath().substring(0, input.getPath().lastIndexOf("\\") + 1) + "descrypt");
      } else {
         pathResults = "";
      }
   }

   /**
    * copies source to destination file and then deletes source file
    */
   private void cleanUpFiles(File source, File destination) {
      try {
         FileInputStream streamIn = new FileInputStream(source);
         FileOutputStream streamOut = new FileOutputStream(destination);

         int c;
         try {
            while ((c = streamIn.read()) != -1) {
               streamOut.write(c);
            }
         } catch (IOException ex) {
            ex.printStackTrace();
         } finally {
            try {
               streamIn.close();
               streamOut.close();
            } catch (IOException ex) {
               ex.printStackTrace();
            }
         }
         source.delete();
      } catch (FileNotFoundException ex1) {
         ex1.printStackTrace();
      }
   }

   /**
    * @return String, pass phrase
    */
   private String getPassWord() {
      String passWord = "";
      int result = -1;
      EntryDialog ed =
            new EntryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Pass Phrase", null,
                  "Enter pass phrase", MessageDialog.QUESTION, new String[] {"Ok", "Cancel"}, 0);
      result = ed.open();

      if (result == 0) {
         if (!ed.getEntry().equals("")) {
            passWord = ed.getEntry();
         }
      } else {
         passWord = "Canceled";
      }
      return passWord;
   }

   /**
    * Passing the focus request to the viewer's control.
    */
   public void setFocus() {
   }

   /**
    * displays invalid pass phrase message
    */
   private void invalidPassPhraseMsg() {
      MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Pass phrase Error",
            "Invalid Pass phrase.");
   }

   public String getActionDescription() {
      return "";
   }
}