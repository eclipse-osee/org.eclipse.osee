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
package org.eclipse.osee.framework.search.engine.internal.services;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProviderManager;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.eclipse.osee.framework.search.engine.internal.tagger.SearchEngineTagger;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngineTaggerRegHandler extends AbstractTrackingHandler {

   //@formatter:off
   private static final Class<?>[] SERVICE_DEPENDENCIES = new Class<?>[] {
      IApplicationServerManager.class,
      IOseeDatabaseService.class, 
      IAttributeTaggerProviderManager.class
   };
   //@formatter:on

   private ServiceRegistration serviceRegistration;

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IOseeDatabaseService databaseService = getService(IOseeDatabaseService.class, services);
      IApplicationServerManager serverManager = getService(IApplicationServerManager.class, services);
      IAttributeTaggerProviderManager taggingManager = getService(IAttributeTaggerProviderManager.class, services);

      ThreadFactory threadFactory = serverManager.createNewThreadFactory("tagger.worker", Thread.NORM_PRIORITY);
      ExecutorService executorService = Executors.newFixedThreadPool(3, threadFactory);

      SearchTagDataStore dataStore = new SearchTagDataStore(databaseService);
      ISearchEngineTagger tagger = new SearchEngineTagger(executorService, dataStore, taggingManager);

      serviceRegistration = context.registerService(ISearchEngineTagger.class.getName(), tagger, null);
   }

   @Override
   public void onDeActivate() {
      OsgiUtil.close(serviceRegistration);
   }
}
