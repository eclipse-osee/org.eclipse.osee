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
package org.eclipse.osee.ats.view.web;

import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponent;
import org.eclipse.osee.ats.api.data.AtsSearchParameters;
import org.eclipse.osee.ats.api.search.AtsPresenterFactory;
import org.eclipse.osee.ats.api.search.AtsSearchPresenter;
import org.eclipse.osee.vaadin.ApplicationFactory;
import com.vaadin.Application;

/**
 * @author Shawn F. Cook
 */
public class AtsUiApplicationFactory implements ApplicationFactory {

   private AtsPresenterFactory<AtsSearchHeaderComponent, AtsSearchParameters> presenterFactory;

   @Override
   public Application createInstance() {
      AtsSearchPresenter<AtsSearchHeaderComponent, AtsSearchParameters> searchPresenter = null;
      if (presenterFactory != null) {
         searchPresenter = presenterFactory.createInstance();
      }
      return new AtsUiApplication(searchPresenter);
   }

   @Override
   public Class<? extends Application> getApplicationClass() {
      return AtsUiApplication.class;
   }

   public void setPresenterFactory(AtsPresenterFactory<AtsSearchHeaderComponent, AtsSearchParameters> presenterFactory) {
      this.presenterFactory = presenterFactory;
   }

}
