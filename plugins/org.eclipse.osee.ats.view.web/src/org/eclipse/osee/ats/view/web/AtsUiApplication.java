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
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.view.web.OseeUiApplication;
import org.eclipse.osee.vaadin.widgets.HasViews;
import com.vaadin.ui.Window;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class AtsUiApplication extends OseeUiApplication<AtsSearchHeaderComponent, AtsSearchParameters> {

   public AtsUiApplication(AtsSearchPresenter<AtsSearchHeaderComponent, AtsSearchParameters> searchPresenter) {
      super(searchPresenter);
   }

   @Override
   public void init() {
      super.init();

      AtsWindowFactory factory = new AtsWindowFactory();
      HasViews viewProvider = new AtsUiViews();
      Window mainWindow = factory.createNavigatableWindow(viewProvider, getAtsNavigator());
      setMainWindow(mainWindow);
      mainWindow.setApplication(this);
   }

   public AtsNavigator getAtsNavigator() {
      AtsNavigator nav = null;
      try {
         nav = (AtsNavigator) this.navigator;
      } catch (Exception e) {
         System.out.println("AtsUiApplication.getAtsNavigator() - CRITICAL ERROR: cast threw an exception.");
      }
      return nav;
   }

   public AtsSearchPresenter<AtsSearchHeaderComponent, AtsSearchParameters> getAtsWebSearchPresenter() {
      AtsSearchPresenter<AtsSearchHeaderComponent, AtsSearchParameters> pres = null;
      if (searchPresenter instanceof AtsSearchPresenter<?, ?>) {
         pres = (AtsSearchPresenter<AtsSearchHeaderComponent, AtsSearchParameters>) searchPresenter;
      } else {
         System.out.println("AtsUiApplication.getAtsWebSearchPresenter() - CRITICAL ERROR: cast threw an exception.");
      }
      return pres;
   }

   @Override
   protected SearchNavigator createNavigator() {
      return new AtsNavigator();
   }

}
