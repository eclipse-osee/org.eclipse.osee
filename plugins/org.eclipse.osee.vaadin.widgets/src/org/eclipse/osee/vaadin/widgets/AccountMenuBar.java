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
package org.eclipse.osee.vaadin.widgets;

import com.vaadin.Application;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Window;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("serial")
public class AccountMenuBar extends MenuBar {

   private final ClientDetails clientDetails;

   private boolean populated;

   public AccountMenuBar() {
      this.clientDetails = new ClientDetails();
   }

   @Override
   public void attach() {
      if (populated) {
         // Only populate the layout once
         return;
      }
      final MenuBar menuBar = this;

      String userName = getUserName(menuBar.getApplication());
      if (userName.equalsIgnoreCase("guest")) {
         menuBar.addItem("Log In", new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
               System.out.printf("Log In Page: %s\n", getLogInUrl());
            }
         });

         menuBar.addItem("Register", new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
               String registrationUrl = getRegistrationUrl();
               System.out.printf("Registration Page: %s \n", registrationUrl);
               showClientDetails(menuBar.getApplication());
            }
         });
      } else {
         MenuItem accountItem = menuBar.addItem("Account", null);

         accountItem.addItem("Change Password", new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
               System.out.printf("Change Password");
            }
         });

         accountItem.addItem("Log Off", new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
               String logoutUrl = getLogOutUrl(menuBar.getApplication());
               System.out.printf("LogOut: %s \n", logoutUrl);
            }
         });

         Resource settingsIcon = new ThemeResource("../runo/icons/16/settings.png");
         MenuItem item = accountItem.addItem("", settingsIcon, new MenuBar.Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
               System.out.println("Options");
            }
         });
         item.setDescription("Options");
      }
      populated = true;
   }

   private void showClientDetails(Application application) {
      Window subWindow = new Window("Client Details");
      subWindow.addComponent(clientDetails);
      subWindow.setModal(true);
      subWindow.setHeight("200");
      subWindow.setWidth("500");
      application.getMainWindow().addWindow(subWindow);
   }

   private String getUserName(Application application) {
      Object user = application.getUser();
      return user != null ? user.toString() : "Guest";
   }

   private String getRegistrationUrl() {
      String registrationUrl = "../register";
      return registrationUrl;
   }

   private String getLogInUrl() {
      String logInUrl = "../login";
      return logInUrl;
   }

   private String getLogOutUrl(Application application) {
      return application.getLogoutURL();
   }

}
