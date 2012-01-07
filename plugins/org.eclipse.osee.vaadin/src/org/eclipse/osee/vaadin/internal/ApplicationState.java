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
package org.eclipse.osee.vaadin.internal;

import com.vaadin.Application;
import com.vaadin.ui.Window;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationState {

   private final ThreadLocal<Application> application = new ThreadLocal<Application>();
   private final ThreadLocal<Window> window = new ThreadLocal<Window>();
   private final ThreadLocal<String> uriFragment = new ThreadLocal<String>();

   public Application getApplication() {
      return application.get();
   }

   public void setApplication(Application application) {
      this.application.set(application);
   }

   public boolean isApplicationValid() {
      return getApplication() != null;
   }

   public Window getWindow() {
      return window.get();
   }

   public void setWindow(Window window) {
      this.window.set(window);
   }

   public String getUriFragment() {
      return uriFragment.get();
   }

   public void setUriFragment(String fragment) {
      uriFragment.set(fragment);
   }

   public void removeApplication() {
      application.remove();
   }

   public void removeWindow() {
      window.remove();
   }

   public void removeUriFragment() {
      uriFragment.remove();
   }

   public void clearAll() {
      removeApplication();
      removeWindow();
      removeUriFragment();
   }
}
