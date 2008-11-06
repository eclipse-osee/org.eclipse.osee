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
package org.eclipse.osee.framework.ui.skynet.panels;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.BaseCredentialProvider;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
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

   private enum LabelEnum {
      UserId("Enter user name"),
      Password(true, "Enter a valid password"),
      Domain("Enter a valid domain [sw, nw, etc...]"),
      Protocol("Select authentication protocol"),
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

   private Map<LabelEnum, Control> fieldMap;
   private Map<LabelEnum, String> dataMap;
   private Map<LabelEnum, Label> statusMap;
   private boolean buildSubmitButton;
   private SelectionListener listener;
   private boolean allValid;
   private Button memoButton;
   private Button guestButton;
   private Button userButton;
   private Composite mainComposite;
   private boolean isGuestLogin;

   public AuthenticationComposite(Composite parent, int style, boolean buildSubmitButton) {
      super(parent, style);
      this.buildSubmitButton = buildSubmitButton;
      fieldMap = new HashMap<LabelEnum, Control>();
      dataMap = new HashMap<LabelEnum, String>();
      statusMap = new HashMap<LabelEnum, Label>();
      createControl();
   }

   public AuthenticationComposite(Composite parent, int style) {
      this(parent, style, true);
   }

   private void createControl() {
      GridLayout layout = new GridLayout();
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      this.setLayout(layout);
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createLoginSelection(this);

      mainComposite = new Group(this, SWT.NONE);
      GridLayout layout1 = new GridLayout();
      layout1.marginWidth = 0;
      layout1.marginHeight = 0;
      mainComposite.setLayout(layout1);
      mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createFieldArea(mainComposite);
      handleLoginTypeSelection();
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
      if (isWidgetAccessible(guestButton)) {
         if (guestButton.getSelection()) {
            allowUserLogin = false;
         }
      }

      if (isWidgetAccessible(userButton)) {
         if (userButton.getSelection()) {
            allowUserLogin = true;
         }
      }

      isGuestLogin = !allowUserLogin;
      if (isWidgetAccessible(mainComposite)) {
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
      tempComposite.setEnabled(setEnabled);
   }

   private void createFieldArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(3, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      for (LabelEnum labelEnum : LabelEnum.values()) {
         if (labelEnum.equals(LabelEnum.Remember_My_Password)) {
            createMementoButton(parent);
            if (isWidgetAccessible(memoButton)) {
               dataMap.put(labelEnum, Boolean.toString(memoButton.getSelection()));
            }
         } else {
            Label label = new Label(composite, SWT.NONE);
            label.setText(labelEnum.name() + ": ");

            int style = SWT.BORDER | SWT.SINGLE;
            Control control = null;
            if (labelEnum.equals(LabelEnum.Protocol)) {
               Combo combo = new Combo(composite, style);
               control = combo;
               combo.setItems(ClientSessionManager.getAuthenticationProtocols());
               dataMap.put(labelEnum, combo.getText());
            } else {
               Text field = new Text(composite, (labelEnum.isHidden() ? style |= SWT.PASSWORD : style));
               field.setData(LABEL_KEY, labelEnum);
               control = field;
               dataMap.put(labelEnum, field.getText());
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
            }
            control.setToolTipText(labelEnum.getToolTipText());
            control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

            Label statusLabel = new Label(composite, SWT.NONE);
            statusLabel.setImage(errorImage);
            statusLabel.setVisible(true);

            fieldMap.put(labelEnum, control);
            statusMap.put(labelEnum, statusLabel);

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
            try {
               if (isGuestLogin()) {
                  ClientSessionManager.authenticateAsGuest();
               } else {
                  ClientSessionManager.authenticate(new BaseCredentialProvider() {
                     @Override
                     public OseeCredential getCredential() throws OseeCoreException {
                        OseeCredential credential = super.getCredential();
                        credential.setUserName(dataMap.get(LabelEnum.UserId));
                        credential.setPassword(dataMap.get(LabelEnum.Password));
                        credential.setDomain(dataMap.get(LabelEnum.Domain));
                        credential.setAuthenticationProtocol(dataMap.get(LabelEnum.Protocol));
                        return credential;
                     }
                  });
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }

            if (ClientSessionManager.isSessionValid()) {
               boolean isSaveAllowed = Boolean.valueOf(dataMap.get(LabelEnum.Remember_My_Password));
               if (isSaveAllowed) {
                  //TODO: Store Password into KeyRing dataMap.get(LabelEnum.Password)
               }
            }
         }
      };

   }

   private void createMementoButton(Composite parent) {
      Composite tempComposite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      tempComposite.setLayout(layout);
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

   public void setProtocol(String protocol) {
      setAndStoreField(LabelEnum.Protocol, protocol);
   }

   private void setAndStoreField(LabelEnum fieldKey, String value) {
      if (fieldKey.equals(LabelEnum.Remember_My_Password)) {
         if (isWidgetAccessible(memoButton)) {
            memoButton.setSelection(Boolean.valueOf(value));
         }
      } else {
         Widget object = fieldMap.get(fieldKey);
         if (isWidgetAccessible(object)) {
            if (object instanceof Text) {
               Text textField = (Text) object;
               textField.setText(value);
            }
            if (object instanceof Combo) {
               Combo combo = (Combo) object;
               combo.setText(value);
            }
            updateFieldStatus(fieldKey, object);
            updateDefaultButtonStatus();
         }
      }
      dataMap.put(fieldKey, value);
   }

   private boolean isWidgetAccessible(Widget widget) {
      return widget != null && !widget.isDisposed();
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

   public String getProtocol() {
      return dataMap.get(LabelEnum.Protocol);
   }

   public boolean isValid() {
      return allValid;
   }

   private void updateFieldStatus(LabelEnum labelKey, Widget field) {
      switch (labelKey) {
         case Protocol:
            String temp = ((Combo) field).getText();
            statusMap.get(labelKey).setVisible(!Strings.isValid(temp));
            break;
         case Remember_My_Password:
            break;
         default:
            temp = ((Text) field).getText();
            statusMap.get(labelKey).setVisible(!Strings.isValid(temp));
            break;
      }
   }

   private void updateDefaultButtonStatus() {
      allValid = true;

      if (!isGuestLogin) {
         for (LabelEnum key : LabelEnum.values()) {
            Label label = statusMap.get(key);
            if (isWidgetAccessible(label)) {
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
