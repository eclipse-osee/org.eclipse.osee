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
package org.eclipse.osee.ats.presenter.internal;

import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponent;
import org.eclipse.osee.ats.api.data.AtsSearchParameters;
import org.eclipse.osee.ats.api.search.AtsArtifactProvider;
import org.eclipse.osee.ats.api.search.AtsPresenterFactory;
import org.eclipse.osee.ats.api.search.AtsSearchPresenter;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author John Misinco
 */
public class AtsPresenterFactoryImpl<T extends AtsSearchHeaderComponent, K extends AtsSearchParameters> implements AtsPresenterFactory<AtsSearchHeaderComponent, AtsSearchParameters> {

   private OrcsApi orcsApi;
   private Log logger;
   private ExecutorAdmin executorAdmin;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   @Override
   public AtsSearchPresenter<AtsSearchHeaderComponent, AtsSearchParameters> createInstance(ApplicationContext context) {
      AtsArtifactProvider provider = new AtsArtifactProviderImpl(logger, executorAdmin, orcsApi, context);
      AtsSearchPresenterImpl<AtsSearchHeaderComponent, AtsSearchParameters> instance =
         new AtsSearchPresenterImpl<AtsSearchHeaderComponent, AtsSearchParameters>(provider, logger);
      return instance;
   }

}
