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
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("serial")
public class ClientDetails extends VerticalLayout {

   private boolean populated;

   public ClientDetails() {
      this.populated = false;
   }

   @Override
   public void attach() {
      if (populated) {
         // Only populate the layout once
         return;
      }
      Application application = getApplication();
      WebApplicationContext context = (WebApplicationContext) application.getContext();
      WebBrowser webBrowser = context.getBrowser();

      String browserTypeAndVersion = getTypeAndVersion(webBrowser);
      browserTypeAndVersion = browserTypeAndVersion + " in " + getOperatingSystem(webBrowser);

      // Create labels for the information and add them to the application

      Label idlabel = new Label("SessionId: " + context.getHttpSession().getId(), Label.CONTENT_XHTML);

      Label ipAddresslabel =
         new Label("Hello " + application.getUser() + "  from <b>" + webBrowser.getAddress() + "</b>.",
            Label.CONTENT_XHTML);
      Label browser = new Label("You are running <b>" + browserTypeAndVersion + "</b>.", Label.CONTENT_XHTML);
      Label screenSize =
         new Label(
            "Your screen resolution is <b>" + webBrowser.getScreenWidth() + "x" + webBrowser.getScreenHeight() + "</b>.",
            Label.CONTENT_XHTML);
      Label locale =
         new Label("Your browser is set to primarily use the <b>" + webBrowser.getLocale() + "</b> locale.",
            Label.CONTENT_XHTML);
      Label secureConnection =
         new Label(webBrowser.isSecureConnection() ? "SecureConnection" : "Unsecure Channel", Label.CONTENT_XHTML);

      addComponent(idlabel);
      addComponent(ipAddresslabel);
      addComponent(browser);
      addComponent(screenSize);
      addComponent(locale);
      addComponent(secureConnection);

      populated = true;
   }

   private String getOperatingSystem(WebBrowser webBrowser) {
      if (webBrowser.isWindows()) {
         return "Windows";
      } else if (webBrowser.isMacOSX()) {
         return "Mac OSX";
      } else if (webBrowser.isLinux()) {
         return "Linux";
      } else {
         return "an unknown operating system";
      }
   }

   private String getTypeAndVersion(WebBrowser webBrowser) {
      if (webBrowser.isChrome()) {
         return "Chrome " + webBrowser.getBrowserMajorVersion() + "." + webBrowser.getBrowserMinorVersion();
      } else if (webBrowser.isOpera()) {
         return "Opera " + webBrowser.getBrowserMajorVersion() + "." + webBrowser.getBrowserMinorVersion();
      } else if (webBrowser.isFirefox()) {
         return "Firefox " + webBrowser.getBrowserMajorVersion() + "." + webBrowser.getBrowserMinorVersion();
      } else if (webBrowser.isSafari()) {
         return "Safari " + webBrowser.getBrowserMajorVersion() + "." + webBrowser.getBrowserMinorVersion();
      } else if (webBrowser.isIE()) {
         return "Internet Explorer " + webBrowser.getBrowserMajorVersion();
      } else {
         return "an unknown browser";
      }
   }
}
