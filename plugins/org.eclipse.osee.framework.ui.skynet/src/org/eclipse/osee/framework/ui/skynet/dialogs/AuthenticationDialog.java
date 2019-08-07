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
package org.eclipse.osee.framework.ui.skynet.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException.AuthenticationErrorCode;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.panels.AuthenticationComposite;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.OseeMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
   private static final int MAX_RETRIES = 3;

   public AuthenticationDialog(Shell parentShell) {
      super(parentShell, "OSEE Authenticate", null, "Enter your user id (email address), password, and domain.",
         ImageManager.getImage(FrameworkImage.LOCKED_KEY), new String[] {"Enter", "Cancel"}, 0);

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
         @Override
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

   private void setStorageAllowed(boolean isStorageAllowed) {
      authenticationComposite.setStorageAllowed(isStorageAllowed);
   }

   private void setAnonymousLogin(boolean isAnonymousLogin) {
      authenticationComposite.setAnonymousLogin(isAnonymousLogin);
   }

   private String getUserName() {
      return authenticationComposite.getUserName();
   }

   private boolean isStorageAllowed() {
      return authenticationComposite.isStorageAllowed();
   }

   private boolean isAnonymousLogin() {
      return authenticationComposite.isAnonymousLogin();
   }

   public static void openDialog() {
      Displays.pendInDisplayThread(new Runnable() {

         private String getErrorMessage(AuthenticationErrorCode status) {
            String toReturn = "";
            if (status == null) {
               status = AuthenticationErrorCode.Unknown;
            }
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
                  toReturn = "Unknown authentication error";
                  break;
            }
            return toReturn;
         }

         @Override
         public void run() {
            String dialogTitle = "Authentication Failed";
            String endMsg = "Shutting down the workbench.";
            String user = "";
            String message = "";
            boolean isStorageAllowed = false;
            boolean isAnonymousLogin = false;
            boolean shutdown = false;
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

            for (int numberOfTries = 0; numberOfTries < MAX_RETRIES; numberOfTries++) {
               AuthenticationDialog dialog = new AuthenticationDialog(shell);
               if (numberOfTries != 0) {
                  dialog.setUserName(user);
                  dialog.setPassword("");
                  dialog.setStorageAllowed(isStorageAllowed);
                  dialog.setAnonymousLogin(isAnonymousLogin);
               }
               int result = dialog.open();

               user = dialog.getUserName();
               isStorageAllowed = dialog.isStorageAllowed();
               isAnonymousLogin = dialog.isAnonymousLogin();

               if (result == Window.CANCEL) {
                  // TODO This was added because ATS requires a user to be logged in
                  // Non-Authentication is not an option --
                  if (numberOfTries > MAX_RETRIES) {
                     message = "Maximum number of Retries reached.\n" + endMsg;
                     shutdown = true;
                  } else {
                     message =
                        "Please log in as Anonymous or with your credentials.\n" + "A Log-in account is required to continue.";
                  }

                  MessageDialog.openError(shell, "Authentication Cancelled", message);
               }
               // else if (result != Window.OK ) {
               // numberOfTries = MAX_RETRIES;
               // }
               else {
                  if (dialog.isValid()) {
                     if (ClientSessionManager.isSessionValid()) {
                        numberOfTries = MAX_RETRIES;
                        String userText;
                        try {
                           userText = UserManager.getUser().toString();
                        } catch (OseeCoreException ex) {
                           userText = ex.getLocalizedMessage();
                        }
                        MessageDialog.openInformation(shell, "Authenticated", "Logged in as: " + userText);
                     } else {
                        if (numberOfTries >= MAX_RETRIES - 1) {
                           message = "Maximum number of Retries reached.\n" + endMsg;
                           shutdown = true;
                        } else {
                           IHealthStatus status = OseeLog.getStatusByName(ClientSessionManager.getStatusId());
                           if (status != null && status.getException() != null) {
                              Throwable ex = status.getException();
                              if (ex instanceof OseeAuthenticationException) {
                                 message = getErrorMessage(((OseeAuthenticationException) ex).getCode());
                              }
                              message = ex.getLocalizedMessage();
                           } else {
                              message = "Authentication error";
                           }
                        }
                        MessageDialog.openError(shell, dialogTitle, message);
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
