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
package org.eclipse.osee.vaadin;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.vaadin.internal.ApplicationState;
import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.ui.Window;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractApplication extends Application implements TransactionListener {

   private static final long serialVersionUID = -3069770596395717931L;

   private static final ApplicationState applicationState = new ApplicationState();

   protected AbstractApplication() {
      super();
   }

   protected Window getCurrentWindow() {
      return applicationState.getWindow();
   }

   @Override
   public final void init() {
      getContext().addTransactionListener(this);

      boolean isApplicationValid = applicationState.isApplicationValid();
      if (!isApplicationValid) {
         applicationState.setApplication(this);
      }

      Window currentWindow = createWindow();
      currentWindow.setName(getWindowName());
      setMainWindow(currentWindow);

      if (!isApplicationValid) {
         applicationState.removeApplication();
      }
   }

   @Override
   public final void transactionStart(Application application, Object transactionData) {
      if (this == application) {

         if (!applicationState.isApplicationValid()) {
            applicationState.setApplication(this);
         }

         HttpServletRequest request = (HttpServletRequest) transactionData;
         String paramValue = request.getParameter("fr");
         applicationState.setUriFragment(paramValue);
      }
   }

   @Override
   public final void transactionEnd(Application application, Object transactionData) {
      if (this == application) {
         applicationState.clearAll();
      }
   }

   //https://vaadin.com/web/joonas/wiki/-/wiki/Main/Supporting%20Multible%20Tabs
   @Override
   public final Window getWindow(String name) {
      Window toReturn = null;
      if (isRunning()) {
         toReturn = super.getWindow(name);

         Window mainWindow = getMainWindow();
         if (mainWindow != null && matchesWindowName(mainWindow.getName(), name)) {
            if (toReturn == null) {
               toReturn = createWindow();
               toReturn.setName(name);
               addWindow(toReturn);
            }
            applicationState.setWindow(toReturn);

            // SEE: http://vaadin.com/forum/-/message_boards/message/57240
            // Empty string is a call for home page.
            if ("".equals(applicationState.getUriFragment())) {
               if (mainWindow instanceof HasMultiplePages) {
                  HasMultiplePages multiPaged = (HasMultiplePages) mainWindow;
                  if (multiPaged.getCurrentPage() == null) {
                     multiPaged.setToDefault();
                  }
               }
            }
         }
      }
      return toReturn;
   }

   private Window createWindow() {
      try {
         Application application = applicationState.getApplication();
         return createApplicationWindow(application.getLocale());
      } catch (Exception ex) {
         throw new IllegalStateException(ex);
      }
   }

   private String getWindowName() {
      return getApplicationWindowName();
   }

   protected boolean matchesWindowName(String windowName, String name) {
      boolean result = name.equals(windowName);
      if (!result) {
         String[] nameParts = name.split("_");
         if (nameParts.length == 2) {
            result = windowName.equals(nameParts[0]);
         }
      }
      return result;
   }

   protected abstract Window createApplicationWindow(Locale locale) throws Exception;

   protected abstract String getApplicationWindowName();
}