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
package org.eclipse.osee.framework.search.engine.servlet;

import org.eclipse.osee.framework.jdk.core.util.OseeApplicationServerContext;
import org.eclipse.osee.framework.resource.common.osgi.OseeHttpServiceTracker;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
   private static Activator instance;

   private OseeHttpServiceTracker searchEngineHttpTracker;
   private OseeHttpServiceTracker searchEngineTaggerHttpTracker;
   private ServiceTracker searchServiceTracker;
   private ServiceTracker taggerServiceTracker;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;

      searchServiceTracker = new ServiceTracker(context, ISearchEngine.class.getName(), null);
      searchServiceTracker.open();

      taggerServiceTracker = new ServiceTracker(context, ISearchEngineTagger.class.getName(), null);
      taggerServiceTracker.open();

      searchEngineHttpTracker =
            new OseeHttpServiceTracker(context, OseeApplicationServerContext.SEARCH_CONTEXT, SearchEngineServlet.class);
      searchEngineHttpTracker.open();

      searchEngineTaggerHttpTracker =
            new OseeHttpServiceTracker(context, OseeApplicationServerContext.SEARCH_TAGGING_CONTEXT,
                  SearchEngineTaggerServlet.class);
      searchEngineTaggerHttpTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      searchEngineHttpTracker.close();
      searchEngineHttpTracker = null;

      searchEngineTaggerHttpTracker.close();
      searchEngineTaggerHttpTracker = null;

      searchServiceTracker.close();
      searchServiceTracker = null;

      taggerServiceTracker.close();
      taggerServiceTracker = null;

      instance = null;
   }

   public static Activator getInstance() {
      return instance;
   }

   public ISearchEngine getSearchEngine() {
      return (ISearchEngine) searchServiceTracker.getService();
   }

   public ISearchEngineTagger getSearchTagger() {
      return (ISearchEngineTagger) taggerServiceTracker.getService();
   }

}
