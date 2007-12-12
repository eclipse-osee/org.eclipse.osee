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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class AuthenticationComposite extends Composite {

   private static final String LABEL_KEY = "label";
   private static final String WARNING_MESSAGE =
         "Saved passwords are stored in your computer in a file that is difficult, but not impossible, for an intruder to read.";
   private static final Image errorImage =
         PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

   private static final OseeAuthentication oseeAuthentication = OseeAuthentication.getInstance();

   private enum LabelEnum {
      UserId("Enter user name"),
      Password(true, "Enter a valid password"),
      Domain("Enter a valid domain [sw, nw, etc...]"),
      Remember_My_Password(WARNING_MESSAGE);

      boolean isHidden;
      String toolTipText;

      LabelEnum(boolean isHidden, String toolTipText) {
         this.isHidden = isHidden;
         this.toolTipText = toolTipText;
      }

      LabelEnum(String toolTipText) {
         this(false, toolTipText);
      }

      public String getToolTipText() {
         return toolTipText;
      }

      public boolean isHidden() {
         return isHidden;
      }
   }

   private Map<LabelEnum, Text> fieldMap;
   private Map<LabelEnum, String> dataMap;
   private Map<LabelEnum, Label> statusMap;
   private boolean buildSubmitButton;
   private SelectionListener listener;
   private boolean allValid;
   private Button memoButton;
   private Button guestButton;
   private Button userButton;
   private Composite mainComposite;
   private SashForm sash;
   private boolean isGuestLogin;

   public AuthenticationComposite(Composite parent, int style, boolean buildSubmitButton) {
      super(parent, style);
      this.buildSubmitButton = buildSubmitButton;
      fieldMap = new HashMap<LabelEnum, Text>();
      dataMap = new HashMap<LabelEnum, String>();
      statusMap = new HashMap<LabelEnum, Label>();
      createControl();
   }

   public AuthenticationComposite(Composite parent, int style) {
      this(parent, style, true);
   }

   private void createControl() {
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      sash = new SashForm(this, SWT.NONE);
      sash.setLayout(new GridLayout());
      sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      sash.setOrientation(SWT.VERTICAL);

      createLoginSelection(sash);

      mainComposite = new Group(sash, SWT.NONE);
      mainComposite.setLayout(new GridLayout());
      mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createFieldArea(mainComposite);
      handleLoginTypeSelection();
      sash.setWeights(new int[] {3, 7});
   }

   private void createLoginSelection(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      guestButton = new Button(composite, SWT.RADIO);
      guestButton.setText("Guest");
      guestButton.setSelection(isGuestLogin);
      guestButton.setToolTipText("Allows users to log into the system with guest priviledges.\n");
      guestButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            handleLoginTypeSelection();
            updateDefaultButtonStatus();
         }

      });

      userButton = new Button(composite, SWT.RADIO);
      userButton.setSelection(!isGuestLogin);
      userButton.setText("User Login");
      userButton.setToolTipText("Enables User to login");
      userButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            handleLoginTypeSelection();
            updateDefaultButtonStatus();
         }
      });

   }

   private void handleLoginTypeSelection() {
      boolean allowUserLogin = false;
      if (guestButton != null && !guestButton.isDisposed()) {
         if (guestButton.getSelection()) {
            allowUserLogin = false;
         }
      }

      if (userButton != null && !userButton.isDisposed()) {
         if (userButton.getSelection()) {
            allowUserLogin = true;
         }
      }

      isGuestLogin = !allowUserLogin;
      if (mainComposite != null && !mainComposite.isDisposed()) {
         setEnabledHelper(mainComposite, allowUserLogin);
      }
   }

   private void setEnabledHelper(Composite tempComposite, boolean setEnabled) {
      for (Control control : tempComposite.getChildren()) {
         if (control instanceof Composite) {
            setEnabledHelper((Composite) control, setEnabled);
         } else {
            control.setEnabled(setEnabled);
         }
      }
   }

   private void createFieldArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(3, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      for (LabelEnum labelEnum : LabelEnum.values()) {
         if (labelEnum.equals(LabelEnum.Remember_My_Password)) {
            createMementoButton(parent);
            if (memoButton != null && !memoButton.isDisposed()) {
               dataMap.put(labelEnum, Boolean.toString(memoButton.getSelection()));
            }
         } else {
            Label label = new Label(composite, SWT.NONE);
            label.setText(labelEnum.name() + ": ");

            int style = SWT.BORDER | SWT.SINGLE;
            Text field = new Text(composite, (labelEnum.isHidden() ? style |= SWT.PASSWORD : style));
            field.setData(LABEL_KEY, labelEnum);
            field.setToolTipText(labelEnum.getToolTipText());
            field.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

            Label statusLabel = new Label(composite, SWT.NONE);
            statusLabel.setImage(errorImage);
            statusLabel.setVisible(true);

            field.addModifyListener(new ModifyListener() {

               public void modifyText(ModifyEvent e) {
                  Object object = e.getSource();
                  if (object instanceof Text) {
                     Text field = (Text) object;
                     LabelEnum labelKey = (LabelEnum) field.getData(LABEL_KEY);

                     dataMap.put(labelKey, field.getText());
                     updateFieldStatus(labelKey, field);
                     updateDefaultButtonStatus();
                  }
               }
            });
            fieldMap.put(labelEnum, field);
            statusMap.put(labelEnum, statusLabel);
            dataMap.put(labelEnum, field.getText());
         }
      }

      if (buildSubmitButton) {
         Composite buttonComposite = new Composite(parent, SWT.NONE);
         buttonComposite.setLayout(new GridLayout());
         buttonComposite.setLayoutData(new GridData(SWT.END, SWT.END, true, false));

         Button authenticate = new Button(buttonComposite, SWT.PUSH);
         authenticate.setText("Submit");
         authenticate.setLayoutData(new GridData(SWT.END, SWT.END, false, false));
         authenticate.addSelectionListener(listener);
         authenticate.setEnabled(allValid);
         getShell().setDefaultButton(authenticate);
      }

      listener = new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            String user = dataMap.get(LabelEnum.UserId);
            String password = dataMap.get(LabelEnum.Password);
            String domain = dataMap.get(LabelEnum.Domain);
            String saveAllowed = dataMap.get(LabelEnum.Remember_My_Password);

            oseeAuthentication.setLogAsGuest(isGuestLogin());
            oseeAuthentication.authenticate(user, password, domain,
                  (saveAllowed != null ? Boolean.parseBoolean(saveAllowed) : false));

            oseeAuthentication.setLogAsGuest(false);
         }
      };

   }

   private void createMementoButton(Composite parent) {
      Composite tempComposite = new Composite(parent, SWT.NONE);
      tempComposite.setLayout(new GridLayout());
      tempComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      memoButton = new Button(tempComposite, SWT.CHECK);
      memoButton.setText("Remember my password");
      memoButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
      memoButton.setToolTipText(WARNING_MESSAGE);
      memoButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            dataMap.put(LabelEnum.Remember_My_Password, Boolean.toString(memoButton.getSelection()));
         }

      });
   }

   public SelectionListener getAuthenticateListener() {
      return listener;
   }

   public void setUserName(String user) {
      setAndStoreField(LabelEnum.UserId, user);
   }

   public void setPassword(String password) {
      setAndStoreField(LabelEnum.Password, password);
   }

   public void setDomain(String domain) {
      setAndStoreField(LabelEnum.Domain, domain);
   }

   public void setStorageAllowed(boolean isStorageAllowed) {
      setAndStoreField(LabelEnum.Remember_My_Password, Boolean.toString(isStorageAllowed));
   }

   private void setAndStoreField(LabelEnum fieldKey, String value) {
      if (fieldKey.equals(LabelEnum.Remember_My_Password)) {
         if (memoButton != null && !memoButton.isDisposed()) {
            memoButton.setSelection(Boolean.valueOf(value));
         }
      } else {
         Text textField = fieldMap.get(fieldKey);
         if (textField != null && !textField.isDisposed()) {
            textField.setText(value);
            updateFieldStatus(fieldKey, textField);
            updateDefaultButtonStatus();
         }
      }
      dataMap.put(fieldKey, value);
   }

   public String getUserName() {
      return dataMap.get(LabelEnum.UserId);
   }

   public String getPassword() {
      return dataMap.get(LabelEnum.Password);
   }

   public String getDomain() {
      return dataMap.get(LabelEnum.Domain);
   }

   public boolean isGuestLogin() {
      return isGuestLogin;
   }

   public void setGuestLogin(boolean isGuestLogin) {
      this.isGuestLogin = isGuestLogin;
   }

   public boolean isStorageAllowed() {
      String value = dataMap.get(LabelEnum.Remember_My_Password);
      return (value != null ? Boolean.parseBoolean(value) : false);
   }

   public boolean isValid() {
      return allValid;
   }

   private void updateFieldStatus(LabelEnum labelKey, Text field) {
      switch (labelKey) {
         case UserId:
            String temp = field.getText();
            statusMap.get(labelKey).setVisible(!(temp != null && temp.length() > 0));
            break;
         case Remember_My_Password:
            break;
         default:
            temp = field.getText();
            statusMap.get(labelKey).setVisible(!(temp != null && temp.length() > 0));
            break;
      }
   }

   private void updateDefaultButtonStatus() {
      allValid = true;

      if (!isGuestLogin) {
         for (LabelEnum key : LabelEnum.values()) {
            Label label = statusMap.get(key);
            if (label != null && !label.isDisposed()) {
               allValid &= !label.isVisible();
            }
         }
      }
      Button defaultButton = AuthenticationComposite.this.getShell().getDefaultButton();
      if (defaultButton != null) {
         defaultButton.setEnabled(allValid);
      }
   }
}
