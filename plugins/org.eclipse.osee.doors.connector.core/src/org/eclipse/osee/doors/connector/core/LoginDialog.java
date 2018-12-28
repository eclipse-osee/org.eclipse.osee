/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.core;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Login dialog class to get the name and password
 *
 * @author Chandan Bandemutt
 */
public class LoginDialog extends TitleAreaDialog {

   private String name;

   private Text userName;
   private Text pwd;

   private String password;

   /**
    * Default constructor
    * 
    * @param parentShell :
    */
   public LoginDialog(final Shell parentShell) {
      super(parentShell);
   }

   @Override
   protected Control createContents(final Composite parent) {
      Control contents = super.createContents(parent);
      setTitle("Enter Credentials");
      parent.getShell().setText("Enter Credentials");
      getButton(IDialogConstants.OK_ID).setEnabled(false);
      return contents;
   }

   @Override
   protected Control createDialogArea(final Composite parent) {

      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout gridLayout = new GridLayout(2, false);
      composite.setLayout(gridLayout);
      GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
      composite.setLayoutData(layoutData);

      Label lblName = new Label(composite, SWT.NONE);
      lblName.setText("Name");
      lblName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

      this.userName = new Text(composite, SWT.BORDER);
      this.userName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      this.userName.addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(final ModifyEvent e) {
            validate();

         }
      });

      Label lblPassword = new Label(composite, SWT.NONE);
      lblPassword.setText("Password");
      lblPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

      this.pwd = new Text(composite, SWT.BORDER);
      char ch = '*';
      this.pwd.setEchoChar(ch);
      this.pwd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      this.pwd.addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(final ModifyEvent e) {
            validate();

         }
      });

      return parent;
   }

   /**
   *
   */
   protected void validate() {
      if (this.userName.getText() != null && !this.userName.getText().isEmpty() && this.pwd.getText() != null && !this.pwd.getText().isEmpty()) {
         getButton(IDialogConstants.OK_ID).setEnabled(true);
      } else {
         getButton(IDialogConstants.OK_ID).setEnabled(false);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void okPressed() {
      this.name = this.userName.getText();
      this.password = this.pwd.getText();
      super.okPressed();
   }

   /**
    * @return the name
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return the password
    */
   public String getPassword() {
      return this.password;
   }
}
