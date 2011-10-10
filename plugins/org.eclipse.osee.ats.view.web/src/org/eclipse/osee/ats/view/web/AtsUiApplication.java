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
package org.eclipse.osee.ats.view.web;

import org.eclipse.osee.ats.api.search.AtsSearchPresenter;
import org.eclipse.osee.vaadin.widgets.HasViews;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import com.vaadin.Application;
import com.vaadin.ui.Window;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class AtsUiApplication extends Application {

   private final AtsSearchPresenter atsBackend = AtsWebSearchPresenter_TestBackend.getInstance();

   @Override
   public void init() {
      setTheme("osee");
      @SuppressWarnings("unused")
      AtsAppData sessionData = new AtsAppData(this);
      AtsWindowFactory factory = new AtsWindowFactory();
      HasViews viewProvider = new AtsUiViews();
      Window mainWindow = factory.createNavigatableWindow(viewProvider);
      setMainWindow(mainWindow);
      mainWindow.setApplication(this);
   }

   @Override
   public String getVersion() {
      Bundle bundle = FrameworkUtil.getBundle(this.getClass());
      return bundle.getVersion().toString();
   }

   public AtsSearchPresenter getAtsWebSearchPresenter() {
      return atsBackend;
   }

   //   @Override
   //   public Window getWindow(String name) {
   //      // If the window is identified by name, we are good to go
   //      Window w = super.getWindow(name);
   //
   //      // If not, we must create a new window for this new browser window/tab
   //      if (w == null) {
   //
   //         // Use the random name given by the framework to identify this
   //         // window in future
   //         AtsWindowFactory factory = new AtsWindowFactory();
   //         HasViews viewProvider = new AtsUiViews();
   //         Window mainWindow = factory.createNavigatableWindow(viewProvider);
   //         mainWindow.setName(name);
   //         addWindow(mainWindow);
   //
   //         // Move to the url to remember the name in the future
   //         mainWindow.open(new ExternalResource(mainWindow.getURL()));
   //      }
   //
   //      return w;
   //   }
}
