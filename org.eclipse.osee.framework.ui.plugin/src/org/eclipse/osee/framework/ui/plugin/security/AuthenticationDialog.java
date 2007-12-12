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
package org.eclipse.osee.framework.ui.plugin.security;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.ui.plugin.OseePluginUiActivator;
import org.eclipse.osee.framework.ui.plugin.security.OseeAuthentication.AuthenticationStatus;
import org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum;
import org.eclipse.osee.framework.ui.swt.OseeMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class AuthenticationDialog extends OseeMessageDialog {

   private Button okButton;
   private Button cancelButton;
   protected AuthenticationComposite authenticationComposite;
   private boolean selectionOk;
   private static final Image LOCK_AND_KEY = OseePluginUiActivator.getInstance().getImage("lockkey.gif");
   private static final int MAX_RETRIES = 3;

   public AuthenticationDialog(Shell parentShell) {
      super(parentShell, "OSEE Authenticate", null, "Enter your user id (email address), password, and domain.",
            LOCK_AND_KEY, new String[] {"Enter", "Cancel"}, 0);

      selectionOk = false;
      authenticationComposite = new AuthenticationComposite(parentShell, SWT.NONE, false);
   }

   @Override
   protected Control createCustomArea(Composite parent) {
      authenticationComposite.setParent(parent);
      return authenticationComposite;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control c = super.createButtonBar(parent);
      okButton = getButton(0);
      cancelButton = getButton(1);

      okButton.setEnabled(false);
      okButton.addSelectionListener(authenticationComposite.getAuthenticateListener());
      okButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            selectionOk = true;
         }
      });
      authenticationComposite.getShell().setDefaultButton(okButton);

      cancelButton.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            selectionOk = false;
         }
      });
      return c;
   }

   public boolean isValid() {
      return selectionOk;
   }

   private void setUserName(String user) {
      authenticationComposite.setUserName(user);
   }

   private void setPassword(String password) {
      authenticationComposite.setPassword(password);
   }

   private void setDomain(String domain) {
      authenticationComposite.setDomain(domain);
   }

   private void setStorageAllowed(boolean isStorageAllowed) {
      authenticationComposite.setStorageAllowed(isStorageAllowed);
   }

   private void setGuestLogin(boolean isGuestLogin) {
      authenticationComposite.setGuestLogin(isGuestLogin);
   }

   private String getUserName() {
      return authenticationComposite.getUserName();
   }

   private String getDomain() {
      return authenticationComposite.getDomain();
   }

   private boolean isStorageAllowed() {
      return authenticationComposite.isStorageAllowed();
   }

   private boolean isGuestLogin() {
      return authenticationComposite.isGuestLogin();
   }

   public static void openDialog() {
      Display.getDefault().syncExec(new Runnable() {

         private String getErrorMessage(AuthenticationStatus status) {
            String toReturn = "";
            switch (status) {
               case UserNotFound:
                  toReturn = "User Id not found.\n" + "Enter your user id.";
                  break;
               case InvalidPassword:
                  toReturn =
                        "Invalid Password.\n" + "Make sure <CAPS LOCK> is not enabled.\n" + "Enter a valid password.";
                  break;
               case NoResponse:
                  toReturn = "Please enter a valid user id and password.";
                  break;
               default:
                  break;
            }
            return toReturn;
         }

         public void run() {
            String dialogTitle = "Authentication Failed";
            String endMsg = "Shutting down the workbench.";
            String user = "";
            String domain = "";
            String message = "";
            boolean isStorageAllowed = false;
            boolean isGuestLogin = false;
            boolean shutdown = false;
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

            OseeAuthentication oseeAuthentication = OseeAuthentication.getInstance();

            for (int numberOfTries = 0; numberOfTries < MAX_RETRIES; numberOfTries++) {
               AuthenticationDialog dialog = new AuthenticationDialog(shell);
               if (numberOfTries != 0) {
                  dialog.setUserName(user);
                  dialog.setPassword("");
                  dialog.setDomain(domain);
                  dialog.setStorageAllowed(isStorageAllowed);
                  dialog.setGuestLogin(isGuestLogin);
               }
               int result = dialog.open();

               user = dialog.getUserName();
               domain = dialog.getDomain();
               isStorageAllowed = dialog.isStorageAllowed();
               isGuestLogin = dialog.isGuestLogin();

               if (result == Window.CANCEL) {
                  // TODO This was added because ATS requires a user to be logged in
                  // Non-Authentication is not an option --
                  if (numberOfTries >= MAX_RETRIES - 1) {
                     message = "Maximum number of Retries reached.\n" + endMsg;
                     shutdown = true;
                  } else {
                     message =
                           "Please log in as Guest or with your credentials.\n" + "A Log-in account is required to continue.";
                  }

                  MessageDialog.openError(shell, "Authentication Cancelled", message);
               }
               // else if (result != Window.OK ) {
               // numberOfTries = MAX_RETRIES;
               // }
               else {
                  if (dialog.isValid()) {
                     AuthenticationStatus status = oseeAuthentication.getAuthenticationStatus();
                     switch (status) {
                        case Success:
                           numberOfTries = MAX_RETRIES;
                           MessageDialog.openInformation(shell, "Authenticated",
                                 "Logged in as: " + oseeAuthentication.getCredentials().getField(
                                       UserCredentialEnum.Name));
                           break;
                        default:
                           if (numberOfTries >= MAX_RETRIES - 1) {
                              message = "Maximum number of Retries reached.\n" + endMsg;
                              shutdown = true;
                           } else {
                              message = getErrorMessage(status);
                           }
                           MessageDialog.openError(shell, dialogTitle, message);
                           break;
                     }
                  }
               }
            }

            if (shutdown) {
               PlatformUI.getWorkbench().close();
            }
         }
      });
   }
}
