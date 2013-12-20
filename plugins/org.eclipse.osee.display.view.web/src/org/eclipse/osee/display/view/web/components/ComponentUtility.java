/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.view.web.components;

import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.display.view.web.HasLogger;
import org.eclipse.osee.display.view.web.HasNavigator;
import org.eclipse.osee.display.view.web.HasPresenter;
import org.eclipse.osee.display.view.web.HasUrl;
import com.vaadin.Application;
import com.vaadin.ui.Component;

/**
 * @author Roberto E. Escobar
 */
public final class ComponentUtility {

   private ComponentUtility() {
      // Utility Class
   }

   public static boolean isAccessible(Component... components) {
      boolean result = true;
      if (components == null) {
         result = false;
      } else {
         for (Component component : components) {
            result &= component != null;
         }
      }
      return result;
   }

   public static SearchNavigator getNavigator(Component component) {
      SearchNavigator navigator = null;
      if (isAccessible(component)) {
         Application app = component.getApplication();
         if (app instanceof HasNavigator) {
            navigator = ((HasNavigator) app).getNavigator();
         }
      }
      return navigator;
   }

   public static String getUrl(Component component) {
      String url = null;
      if (isAccessible(component)) {
         Application app = component.getApplication();
         if (app instanceof HasUrl) {
            url = ((HasUrl) app).getUrl();
         }
      }
      return url;
   }

   public static void setUrl(Component component, String url) {
      if (isAccessible(component)) {
         Application app = component.getApplication();
         if (app instanceof HasUrl) {
            ((HasUrl) app).setUrl(url);
         }
      }
   }

   public static SearchPresenter<?, ?> getPresenter(Component component) {
      SearchPresenter<?, ?> presenter = null;
      if (isAccessible(component)) {
         Application app = component.getApplication();
         if (app instanceof HasPresenter) {
            presenter = ((HasPresenter) app).getPresenter();
         }
      }
      return presenter;
   }

   public static void logError(String format, Component component, Object... args) {
      if (isAccessible(component)) {
         Application app = component.getApplication();
         if (app instanceof HasLogger) {
            ((HasLogger) app).logError(format, args);
         }
      }
   }

   public static void logWarn(String format, Component component, Object... args) {
      if (isAccessible(component)) {
         Application app = component.getApplication();
         if (app instanceof HasLogger) {
            ((HasLogger) app).logWarn(format, args);
         }
      }
   }

   public static void logInfo(String format, Component component, Object... args) {
      if (isAccessible(component)) {
         Application app = component.getApplication();
         if (app instanceof HasLogger) {
            ((HasLogger) app).logInfo(format, args);
         }
      }
   }

   public static void logDebug(String format, Component component, Object... args) {
      if (isAccessible(component)) {
         Application app = component.getApplication();
         if (app instanceof HasLogger) {
            ((HasLogger) app).logDebug(format, args);
         }
      }
   }
}
