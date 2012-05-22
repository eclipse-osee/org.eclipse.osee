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

import org.eclipse.osee.ats.ui.api.data.AtsSearchParameters;
import org.eclipse.osee.ats.ui.api.search.AtsPresenterFactory;
import org.eclipse.osee.ats.ui.api.search.AtsSearchPresenter;
import org.eclipse.osee.ats.ui.api.view.AtsSearchHeaderComponent;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.vaadin.AbstractApplication;
import org.eclipse.osee.vaadin.ApplicationFactory;

/**
 * @author Shawn F. Cook
 */
public class AtsUiApplicationFactory implements ApplicationFactory {

   private AtsPresenterFactory<AtsSearchHeaderComponent, AtsSearchParameters> presenterFactory;
   private Log logger;

   public void setPresenterFactory(AtsPresenterFactory<AtsSearchHeaderComponent, AtsSearchParameters> presenterFactory) {
      this.presenterFactory = presenterFactory;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   @Override
   public AbstractApplication createInstance() {
      AtsApplicationContext context = new AtsApplicationContext();

      AtsSearchPresenter<AtsSearchHeaderComponent, AtsSearchParameters> searchPresenter =
         presenterFactory.createInstance(context);

      AtsUiApplication<AtsSearchHeaderComponent, AtsSearchParameters> application =
         new AtsUiApplication<AtsSearchHeaderComponent, AtsSearchParameters>(searchPresenter, logger);

      // TODO attach context
      context.setUser(application.getUser());
      return application;
   }

   @Override
   public Class<? extends AbstractApplication> getApplicationClass() {
      return AtsUiApplication.class;
   }

   private final class AtsApplicationContext implements ApplicationContext {
      private Object user;

      @Override
      public String getSessionId() {
         return user != null ? user.toString() : "dummy";
      }

      public void setUser(Object user) {
         this.user = user;
      }
   }

}
