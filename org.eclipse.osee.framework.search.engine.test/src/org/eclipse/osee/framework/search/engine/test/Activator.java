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
package org.eclipse.osee.framework.search.engine.test;

import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

   private static Activator instance;

   private BundleContext bundleContext;
   private ServiceTracker searchServiceTracker;
   private ServiceTracker taggerServiceTracker;

   public void start(BundleContext context) throws Exception {
      instance = this;
      this.bundleContext = context;
      searchServiceTracker = new ServiceTracker(context, ISearchEngine.class.getName(), null);
      searchServiceTracker.open();

      taggerServiceTracker = new ServiceTracker(context, ISearchEngineTagger.class.getName(), null);
      taggerServiceTracker.open();
   }

   public void stop(BundleContext context) throws Exception {
      searchServiceTracker.close();
      searchServiceTracker = null;

      taggerServiceTracker.close();
      taggerServiceTracker = null;

      bundleContext = null;

      instance = null;
   }

   public static Activator getInstance() {
      return instance;
   }

   public BundleContext getBundleContext() {
      return this.bundleContext;
   }

   public ISearchEngine getSearchEngine() {
      return (ISearchEngine) searchServiceTracker.getService();
   }

   public ISearchEngineTagger getSearchTagger() {
      return (ISearchEngineTagger) taggerServiceTracker.getService();
   }
}
