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

import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponent;
import org.eclipse.osee.ats.api.data.AtsSearchParameters;
import org.eclipse.osee.ats.api.search.AtsSearchPresenter;
import org.eclipse.osee.display.view.web.HasNavigator;
import org.eclipse.osee.display.view.web.HasPresenter;
import org.eclipse.osee.display.view.web.HasUrl;
import org.eclipse.osee.vaadin.widgets.HasViews;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import com.vaadin.Application;
import com.vaadin.ui.Window;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class AtsUiApplication<T extends AtsSearchHeaderComponent, K extends AtsSearchParameters> extends Application implements HasUrl, HasNavigator, HasPresenter {

   private final AtsSearchPresenter<T, K> atsSearchPresenter;
   private AtsNavigator navigator;
   private String url = "";

   public AtsUiApplication(AtsSearchPresenter<T, K> searchPresenter) {
      this.atsSearchPresenter = searchPresenter;
   }

   @Override
   public void init() {
      setTheme("osee");

      AtsWindowFactory factory = new AtsWindowFactory();
      HasViews viewProvider = new AtsUiViews();
      Window mainWindow = factory.createNavigatableWindow(viewProvider, getNavigator());
      setMainWindow(mainWindow);
      mainWindow.setApplication(this);
   }

   @Override
   public AtsNavigator getNavigator() {
      if (navigator == null) {
         navigator = new AtsNavigator();
      }
      return navigator;
   }

   @Override
   public AtsSearchPresenter<T, K> getPresenter() {
      return atsSearchPresenter;
   }

   @Override
   public String getUrl() {
      return url;
   }

   @Override
   public void setUrl(String url) {
      this.url = url;
   }

   @Override
   public String getVersion() {
      Bundle bundle = FrameworkUtil.getBundle(this.getClass());
      return bundle.getVersion().toString();
   }

}
