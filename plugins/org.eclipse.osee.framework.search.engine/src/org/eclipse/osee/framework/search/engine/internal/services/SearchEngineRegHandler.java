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
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProviderManager;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.internal.search.SearchEngine;
import org.eclipse.osee.framework.search.engine.internal.search.SearchStatistics;
import org.eclipse.osee.framework.search.engine.utility.TagProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngineRegHandler extends AbstractTrackingHandler {

   //@formatter:off
   private static final Class<?>[] SERVICE_DEPENDENCIES = new Class<?>[] {
      IAttributeTaggerProviderManager.class,
      IOseeDatabaseService.class
      };
   //@formatter:on

   private ServiceRegistration serviceRegistration;
   private final TagProcessor processor;

   public SearchEngineRegHandler(TagProcessor processor) {
      this.processor = processor;
   }

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      //      IOseeDatabaseService databaseService = getService(IOseeDatabaseService.class, services);
      IAttributeTaggerProviderManager taggingManager = getService(IAttributeTaggerProviderManager.class, services);

      SearchStatistics searchStatistics = new SearchStatistics();

      ISearchEngine searchEngine = new SearchEngine(searchStatistics, processor, taggingManager);
      serviceRegistration = context.registerService(ISearchEngine.class.getName(), searchEngine, null);
   }

   @Override
   public void onDeActivate() {
      OsgiUtil.close(serviceRegistration);
   }

}
